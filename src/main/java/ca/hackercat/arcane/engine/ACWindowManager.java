package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.engine.asset.ACAssetManager;
import ca.hackercat.arcane.engine.asset.ACMeshFactory;
import ca.hackercat.arcane.engine.asset.ACShaderFactory;
import ca.hackercat.arcane.engine.io.ACWindow;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ACWindowManager {

    private long windowPtr;

    private final List<ACDrawRequest> queue;


    public ACWindowManager() {
        // the ultimate question of linked list or array list...?
        queue = new LinkedList<>();
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

        ACRenderer renderer = new ACRenderer(queue, windowObj);

        while (!glfwWindowShouldClose(windowPtr)) {
            glfwPollEvents();
            glClearColor(0f, 0f, 0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            glViewport(0, 0, windowObj.getWidth(), windowObj.getHeight());

            renderer.drawRect(new Vector2d(0, 0), new Vector2d(1, 1));

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
