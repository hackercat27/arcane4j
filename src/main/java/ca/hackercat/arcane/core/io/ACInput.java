package ca.hackercat.arcane.core.io;

import ca.hackercat.arcane.core.ACThreadManager;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.lwjgl.glfw.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class ACInput {

    private static class Bind {
        public final String action;
        public final String keyName;
        public final int keyNum;
        public int lastPressed = -2; // init so that it doesnt think it was pressed on the first tick of adding it
        public int lastReleased = -1;

        public Bind(String action, String keyName, int keyNum) {
            this.action = action;
            this.keyName = keyName;
            this.keyNum = keyNum;
        }
    }

    private static Vector2d cursorPos = new Vector2d();
    private static boolean cursorOnScreen;

    private static int tick = 0;

    private static final List<Bind> binds = new ArrayList<>();

    private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
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

    private static GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int code, int action, int mods) {
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

    private static GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            cursorPos.x = x;
            cursorPos.y = y;
        }
    };

    private static GLFWCursorEnterCallback cursorEnterCallback = new GLFWCursorEnterCallback() {
        @Override
        public void invoke(long window, boolean onScreen) {
            cursorOnScreen = onScreen;
        }
    };

    public static void init(long window) {
        ACThreadManager.throwIfNotMainThread();
        glfwSetKeyCallback(window, keyCallback);
        glfwSetCursorEnterCallback(window, cursorEnterCallback);
        glfwSetCursorPosCallback(window, cursorPosCallback);
        glfwSetMouseButtonCallback(window, mouseButtonCallback);
    }

    public static void dispose() {
        if (!ACThreadManager.isMainThread()) {
            return;
        }
        keyCallback.free();
        cursorEnterCallback.free();
        cursorPosCallback.free();
        mouseButtonCallback.free();
    }

    public static void update() {
        tick++;
    }

    public static void addAction(String action, String keyName) {
        Bind bind = new Bind(action, keyName, getGLFWKeyCode(keyName));
        if (bind.keyNum < 0) {
            return;
        }
        synchronized (binds) {
            binds.add(bind);
        }
        ACLogger.log(ACLevel.INFO, "Added bind '%s' to key '%s'", action, keyName);
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

        String prefixKey = "GLFW_KEY_";
        String prefixMouse = "GLFW_MOUSE_";

        String qualifiedKeyName = prefixKey + keyName.toUpperCase();
        String qualifiedMouseName = prefixMouse + keyName.toUpperCase();

        for (Field field : GLFW.class.getFields()) {
            String fieldName = field.getName();
            if (!(fieldName.matches("^("+prefixKey+"|"+prefixMouse+").*$"))) {
                continue;
            }

            boolean isKey = fieldName.matches(qualifiedKeyName);
            boolean isButton = fieldName.matches(qualifiedMouseName);

            if (isKey || isButton) {
                try {
                    return (int) field.get(null);
                }
                catch (IllegalAccessException ignored) {}
            }
        }

        ACLogger.log(ACLevel.ERROR, "Couldn't find key %s", keyName);
        return -1;
    }

    public static Vector2d getCursorPos() {
        if (cursorPos == null || !cursorOnScreen) {
            return null;
        }
        return new Vector2d().set(cursorPos);
    }
}