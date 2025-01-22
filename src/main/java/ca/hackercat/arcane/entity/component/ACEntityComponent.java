package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.engine.ACRenderer;
import ca.hackercat.arcane.entity.ACCollisionBody;
import ca.hackercat.arcane.entity.ACEntity;

public interface ACEntityComponent {
    default void update(ACEntity parent, double deltaTime) {}
    default void updateCollision(ACEntity parent, ACCollisionBody body, double deltaTime) {}
    default void render(ACRenderer r, double t) {}
}
