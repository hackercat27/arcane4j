package ca.hackercat.arcane.core.io;

import ca.hackercat.arcane.core.ACThreadManager;
import ca.hackercat.arcane.core.asset.ACAsset;
import ca.hackercat.arcane.core.asset.ACAssetManager;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ACWindow implements ACAsset {

    private long window;
    private int width;
    private int height;

    public GLFWWindowSizeCallback sizeCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long l, int width, int height) {
            ACWindow.this.width = width;
            ACWindow.this.height = height;

            glViewport(0, 0, width, height);
        }
    };

    public ACWindow(long window, int width, int height) {
        ACThreadManager.throwIfNotMainThread();
        this.window = window;
        this.width = width;
        this.height = height;
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
    public boolean registered() {
        return true;
    }

    @Override
    public void register() {

    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {

    }
}
