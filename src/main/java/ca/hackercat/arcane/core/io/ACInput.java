package ca.hackercat.arcane.core.io;

import ca.hackercat.arcane.core.ACThreadManager;
import ca.hackercat.arcane.logging.ACLogger;
import org.lwjgl.glfw.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class ACInput {

    private static class Bind {
        String action;
        String keyName;
        int keyNum;
        int lastPressed = -2; // init so that it doesnt think it was pressed on the first tick of adding it
        int lastReleased = -1;
    }

    private static int tick = 0;

    private static final List<Bind> binds = new ArrayList<>();

    private static GLFWKeyCallback callback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int code, int mods, int action, int scancode) {
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

    public static void dispose() {
        if (!ACThreadManager.isMainThread()) {
            return;
        }
        callback.free();
    }

    public static void update() {
        tick++;
    }

    public static void addAction(String action, String keyName) {
        Bind bind = new Bind();
        bind.action = action;
        bind.keyName = keyName;
        bind.keyNum = getGLFWKeyCode(keyName);
        synchronized (binds) {
            binds.add(bind);
        }
        ACLogger.log("Added bind '%s' to key '%s'", action, keyName);
    }

    public static boolean isActionHeld(String action) {
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
                if (bind.action.equals(action)) {
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

        String prefix = "GLFW_KEY_";
        String qualifiedName = prefix + keyName.toUpperCase();

        for (Field field : GLFW.class.getFields()) {
            String fieldName = field.getName();
            if (!fieldName.matches(prefix+".*")) {
                continue;
            }

            if (fieldName.equalsIgnoreCase(qualifiedName)) {
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