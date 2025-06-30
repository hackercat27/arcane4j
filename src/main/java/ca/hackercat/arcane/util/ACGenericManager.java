package ca.hackercat.arcane.util;

import java.util.ArrayList;
import java.util.List;

public class ACGenericManager {

    private static final List<ACUpdatable> updatables = new ArrayList<>();

    private ACGenericManager() {}

    public static void update(double deltaTime) {

        synchronized (updatables) {
            for (ACUpdatable updatable : updatables) {
                updatable.update(deltaTime);
            }
        }

    }

    public static void register(ACUpdatable updatable) {
        synchronized (updatables) {
            updatables.add(updatable);
        }
    }
}
