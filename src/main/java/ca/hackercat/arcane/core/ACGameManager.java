package ca.hackercat.arcane.core;

import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.entity.component.ACComponent;

public interface ACGameManager {

    void update(double deltaTime);

    void render(ACRenderer r, double t);

    ACEntity[] getEntities();

    ACEntity[] getEntitiesWithComponent(Class<? extends ACComponent> componentType);

    default double getTime() {
        return System.currentTimeMillis() / 1000d;
    }
}
