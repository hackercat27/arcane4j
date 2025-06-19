package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACAssetManager;
import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import ca.hackercat.arcane.util.ACGenericManager;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ACWindowManager {

    private long windowPtr;
    private boolean closeRequested;

    private double targetTPS = 66;

    private long lastUpdateTimestampNanos;
    private long lastUpdateDurationNanos;
    private long lastFrameDurationNanos;

    private ACGameManager gameManager;

    public ACWindowManager() {
    }

    public void setGameManager(ACGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean closeRequested() {
        return this.closeRequested;
    }

    public int startWindow() {

        int initialWidth = 854;
        int initialHeight = 480;

        GLFWErrorCallback.createPrint(ACLogger.err).set();

        ACThreadManager.setMainThread();

        ACLogger.log(ACLevel.VERBOSE, "Initializing GLFW");

        if (!glfwInit()) {
            ACLogger.log(ACLevel.FATAL, "Failed to initialize GLFW");
            return -1;
        }

        ACLogger.log(ACLevel.VERBOSE, "Initialized GLFW");

        glfwWindowHint(GLFW_RED_BITS, 8);
        glfwWindowHint(GLFW_GREEN_BITS, 8);
        glfwWindowHint(GLFW_BLUE_BITS, 8);
        glfwWindowHint(GLFW_ALPHA_BITS, 8);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);
        glfwWindowHint(GLFW_STENCIL_BITS, 8);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowPtr = glfwCreateWindow(initialWidth, initialHeight, "Arcane", 0, 0);

        if (windowPtr == 0) {
            ACLogger.log(ACLevel.FATAL, "Failed to create window!");
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

        Thread updateThread = ACThreadManager.execute(() -> {
            while (!closeRequested()) {
                long start = System.nanoTime();
                long targetTimeNanos = (long) (1e9 / targetTPS);
                double deltaTime = lastUpdateDurationNanos / 1e9;
                if (gameManager != null) {
                    gameManager.update(deltaTime);
                }
                ACInput.update();
                ACGenericManager.update(deltaTime);
                lastUpdateTimestampNanos = System.nanoTime();


                long extraTimeNanos = System.nanoTime() - start;

                try {
                    ACThreadManager.sleepNanos(extraTimeNanos);
                }
                catch (InterruptedException ignored) {}
                lastUpdateDurationNanos = System.nanoTime() - start;
            }
            ACLogger.log(ACLevel.INFO, "Update thread exited");
        }, "arcane-update");

        glEnable(GL_DEPTH_TEST);

        while (!closeRequested) {
            long frameStartNanos = System.nanoTime();
            glfwPollEvents();
            closeRequested = glfwWindowShouldClose(windowPtr);

            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            double interp = (double) (System.nanoTime() - lastUpdateTimestampNanos) / lastUpdateDurationNanos;

            if (gameManager != null) {
                gameManager.render(renderer, interp);
            }

            ACAssetManager.registerAssets();
            ACAssetManager.clean();
            ACThreadManager.clean();
            renderer.handleDrawQueue();

            glfwSwapBuffers(windowPtr);
            lastFrameDurationNanos = System.nanoTime() - frameStartNanos;

        }

        ACThreadManager.blockUntilTermination(updateThread);
        ACInput.dispose();
        ACAssetManager.forceDisposeAll();

        GL.destroy();

        glfwMakeContextCurrent(0);
        glfwDestroyWindow(windowPtr);
        glfwTerminate();

        ACLogger.log(ACLevel.VERBOSE, "Released system resources");

        return 0;
    }

}
