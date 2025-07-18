package ca.hackercat.arcane.util;

import java.util.ArrayList;
import java.util.List;

public class ACGenericManager {

    private static final List<ACCoroutine> updatables = new ArrayList<>();

    private ACGenericManager() {}

    public static void update(double deltaTime) {

        synchronized (updatables) {
            for (ACCoroutine updatable : updatables) {
                updatable.update(deltaTime);
            }
        }

    }

    public static void register(ACCoroutine updatable) {
        synchronized (updatables) {
            updatables.add(updatable);
        }
    }
}
