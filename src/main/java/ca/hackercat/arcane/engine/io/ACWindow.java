package ca.hackercat.arcane.engine.io;

import ca.hackercat.arcane.engine.ACThreadManager;
import ca.hackercat.arcane.engine.asset.ACAsset;
import ca.hackercat.arcane.engine.asset.ACAssetManager;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class ACWindow implements ACAsset {

    private long window;
    private int width;
    private int height;

    public GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long l, int width, int height) {
            ACWindow.this.width = width;
            ACWindow.this.height = height;
        }
    };

    public ACWindow(long window) {
        ACThreadManager.throwIfNotMainThread();
        this.window = window;
        glfwSetWindowSizeCallback(window, sizeCallback);
        ACAssetManager.register(this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {

    }
}
