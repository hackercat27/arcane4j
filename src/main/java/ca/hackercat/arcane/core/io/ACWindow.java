package ca.hackercat.arcane.core.io;

import ca.hackercat.arcane.core.ACThreadManager;
import ca.hackercat.arcane.core.asset.ACAsset;
import ca.hackercat.arcane.core.asset.ACAssetManager;
import java.util.function.Consumer;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ACWindow implements ACAsset {

    private long window;
    private int width;
    private int height;
    private boolean registered;
    private Consumer<Long> onClose;

    public GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            ACWindow.this.width = width;
            ACWindow.this.height = height;

            glViewport(0, 0, width, height);
        }
    };

    public GLFWWindowCloseCallback closeCallback = new GLFWWindowCloseCallback() {
        @Override
        public void invoke(long window) {
            if (onClose != null) {
                onClose.accept(window);
            }
        }
    };

    public ACWindow(long window, int width, int height, Consumer<Long> onClose) {
        ACThreadManager.throwIfNotMainThread();
        this.window = window;
        this.width = width;
        this.height = height;
        this.onClose = onClose;
        ACAssetManager.register(this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean registered() {
        return registered;
    }

    @Override
    public void register() {
        glfwSetWindowSizeCallback(window, sizeCallback);
        glfwSetWindowCloseCallback(window, closeCallback);
        registered = true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {
        sizeCallback.free();
        closeCallback.free();
        registered = false;
    }
}
