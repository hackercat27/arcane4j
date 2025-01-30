package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACAssetManager;
import ca.hackercat.arcane.core.asset.ACMeshFactory;
import ca.hackercat.arcane.core.asset.ACShaderFactory;
import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.Color;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ACWindowManager {

    private long windowPtr;


    public ACWindowManager() {

    }

    public int startWindow() {

        int initialWidth = 854;
        int initialHeight = 480;

        GLFWErrorCallback.createPrint(ACLogger.err).set();

        if (!ACThreadManager.isMainThread()) {
            ACLogger.error("Cannot run %s.startWindow() on non-main thread!", getClass());
            return -1;
        }

        ACLogger.log("Initializing GLFW");

        if (!glfwInit()) {
            ACLogger.error("Failed to initialize GLFW");
            return -1;
        }

        ACLogger.log("Initialized GLFW");

        windowPtr = glfwCreateWindow(initialWidth, initialHeight, "Arcane", 0, 0);

        if (windowPtr == 0) {
            ACLogger.error("Failed to create window!");
            return -1;
        }

        ACWindow windowObj = new ACWindow(windowPtr, initialWidth, initialHeight);

        glfwMakeContextCurrent(windowPtr);
        glfwSwapInterval(1);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwShowWindow(windowPtr);

        GLCapabilities capabilities = GL.createCapabilities();

        ACRenderer renderer = new ACRenderer(windowObj);
        ACInput.init(windowPtr);

        while (!glfwWindowShouldClose(windowPtr)) {

            // MUST update before polling events
            ACInput.update();

            glfwPollEvents();
//            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_DEPTH_BUFFER_BIT);


            double z = System.currentTimeMillis() / 1000d;

            double x = Math.cos(z) / 2d + 0.5;
            double y = Math.sin(z) / 2d + 0.5;

            renderer.setScale(new Vector2d(0.5d, 0.5d));

            renderer.setColor(Color.BLACK);
            renderer.drawRect(renderer.getScreenBounds(), 2);
            renderer.setColor(Color.WHITE);
            renderer.drawRect(new Vector2d(x, y - 0.5), new Vector2d(0.5, 0.5), 1);
            renderer.setColor(Color.RED);
            renderer.drawRect(new Vector2d(-1, 0), new Vector2d(1, 1), 1.5);

            ACMeshFactory.createMeshes();
            ACShaderFactory.createShaders();
            renderer.handleDrawQueue();

            glfwSwapBuffers(windowPtr);

            ACAssetManager.clean();
        }

        ACAssetManager.forceDisposeAll();

        GL.destroy();

        glfwMakeContextCurrent(0);
        glfwDestroyWindow(windowPtr);
        glfwTerminate();

        return 0;
    }

}
