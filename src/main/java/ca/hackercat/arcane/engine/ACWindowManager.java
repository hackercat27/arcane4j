package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.engine.asset.ACAssetManager;
import ca.hackercat.arcane.engine.asset.ACMeshFactory;
import ca.hackercat.arcane.engine.asset.ACShaderFactory;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ACWindowManager {

    private long window;

    private final List<ACDrawRequest> queue;

    public ACWindowManager() {
        // the ultimate question of linked list or array list...?
        queue = new LinkedList<>();
    }

    private void handleDrawQueue() {
        ACThreadManager.throwIfNotMainThread();
        synchronized (queue) {
            for (ACDrawRequest request : queue) {
                request.render();
            }
        }
    }

    public int startWindow() {

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

        window = glfwCreateWindow(854, 480, "Arcane", 0, 0);

        if (window == 0) {
            ACLogger.error("Failed to create window!");
            return -1;
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GLCapabilities capabilities = GL.createCapabilities();

        ACRenderer renderer = new ACRenderer(queue);

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glViewport(0, 0, 854, 480);
            glClearColor(0f, 0f, 0f, 0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.drawRect(new Vector2d(0, 0), new Vector2d(1, 1));

            ACMeshFactory.createMeshes();
            ACShaderFactory.createShaders();
            handleDrawQueue();

            glfwSwapBuffers(window);

            ACAssetManager.clean();
        }

        GL.destroy();

        glfwMakeContextCurrent(0);
        glfwDestroyWindow(window);
        glfwTerminate();

        return 0;
    }

}
