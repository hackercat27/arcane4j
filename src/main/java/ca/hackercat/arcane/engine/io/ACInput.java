package ca.hackercat.arcane.engine.io;

import ca.hackercat.arcane.engine.ACThreadManager;
import ca.hackercat.arcane.engine.asset.ACDisposable;
import ca.hackercat.arcane.logging.ACLogger;
import org.lwjgl.glfw.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class ACInput implements ACDisposable {

    private static class Bind {
        String name;
        String key;
        int keyNum;
        int lastPressed;
        int lastReleased;
    }

    private static int tick = 0;

    private static final List<Bind> binds = new ArrayList<>();

    private static GLFWKeyCallback callback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int code, int action, int mods, int scancode) {
            synchronized (binds) {
                for (Bind bind : binds) {
                    if (bind.keyNum == code) {
                        if (action == GLFW_PRESS) {
                            bind.lastPressed = tick;
                        }
                        if (action == GLFW_RELEASE) {
                            bind.lastReleased = tick;
                        }
                    }
                }
            }
        }
    };

    public static void init(long window) {
        ACThreadManager.throwIfNotMainThread();
        glfwSetKeyCallback(window, callback);
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    @Override
    public void dispose() {
        callback.free();
    }

    public static void update() {
        if (!ACThreadManager.isMainThread()) {
            return;
        }
        tick++;
    }

    public static void addAction(String name, String key) {
        Bind bind = new Bind();
        bind.name = name;
        bind.key = key;
        bind.keyNum = getGLFWKeyCode(key);
        synchronized (binds) {
            binds.add(bind);
        }
    }

    public static boolean isActionAsserted(String action) {
        Bind bind = getBindByName(action);
        if (bind == null) {
            return false;
        }
        return bind.lastPressed > bind.lastReleased;
    }

    public static boolean isActionJustPressed(String action) {
        Bind bind = getBindByName(action);
        if (bind == null) {
            return false;
        }
        return bind.lastPressed == tick;
    }

    public static boolean isActionJustReleased(String action) {
        Bind bind = getBindByName(action);
        if (bind == null) {
            return false;
        }
        return bind.lastReleased == tick;
    }

    private static Bind getBindByName(String action) {
        synchronized (binds) {
            for (Bind bind : binds) {
                if (bind.name.equals(action)) {
                    return bind;
                }
            }
        }
        return null;
    }

    public static int getGLFWKeyCode(String keyName) {
        if (keyName == null) {
            return -1;
        }

        for (Field field : GLFW.class.getFields()) {
            String fieldName = field.getName();
            if (!fieldName.matches("GLFW_KEY_.*")) {
                continue;
            }

            if (fieldName.equalsIgnoreCase(keyName)) {
                try {
                    return (int) field.get(null);
                }
                catch (IllegalAccessException ignored) {}
            }
        }

        ACLogger.error("Couldn't find key %s", keyName);
        return -1;
    }
}