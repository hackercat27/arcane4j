package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACAssetManager;
import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.engine.ACGameManager;
import ca.hackercat.arcane.logging.ACLogger;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class ACWindowManager {

    private long windowPtr;
    private boolean closeRequested;

    private ACGameManager gameManager;

    public ACWindowManager() {
        gameManager = new ACGameManager();
    }

    public boolean closeRequested() {
        return this.closeRequested;
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

        Thread updateThread = ACThreadManager.execute(new Runnable() {
            @Override
            public void run() {

                double targetTime = 1 / 60d;

                while (!closeRequested()) {
                    gameManager.update(targetTime);
                    try {
                        //noinspection BusyWait
                        Thread.sleep((long) (targetTime * 1000));
                    }
                    catch (InterruptedException ignored) {}
                }
                ACLogger.log("Update thread exited");
            }
        }, "arcane-update");

        while (!closeRequested) {

            // MUST update before polling events
            ACInput.update();

            glfwPollEvents();
            closeRequested = glfwWindowShouldClose(windowPtr);

            glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

            gameManager.render(renderer, 0);

            ACAssetManager.registerAssets();
            ACAssetManager.clean();
            ACThreadManager.clean();
            renderer.handleDrawQueue();

            glfwSwapBuffers(windowPtr);

        }

        ACThreadManager.blockUntilTermination(updateThread);

        ACAssetManager.forceDisposeAll();

        GL.destroy();

        glfwMakeContextCurrent(0);
        glfwDestroyWindow(windowPtr);
        glfwTerminate();

        ACLogger.log("Released system resources");

        return 0;
    }

}
