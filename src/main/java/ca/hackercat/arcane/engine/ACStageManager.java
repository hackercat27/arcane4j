package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.entity.component.ACActorRenderComponent;
import ca.hackercat.arcane.entity.component.ACActorPhysicsComponent;
import ca.hackercat.arcane.entity.component.ACPlayerControllerComponent;
import org.joml.Matrix4d;
import org.joml.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * "stage" is defined by the stuff "in the world" on the screen
 */
public class ACStageManager {

    private final List<ACEntity> entities = new ArrayList<>();

    public ACStageManager() {
        entities.add(new ACEntity()
                             .addComponent(new ACPlayerControllerComponent())
                             .addComponent(new ACActorRenderComponent())
                             .addComponent(new ACActorPhysicsComponent()));
    }

    public void update(double deltaTime) {
        synchronized (entities) {
            for (ACEntity e : entities) {
                e.update(deltaTime);
                e.updateCollision(deltaTime);
            }
        }
    }

    public void render(ACRenderer r, double t) {
        r.setColor(Color.WHITE);
        r.setTransform(new Matrix4d());

        r.scale(new Vector2d(0.1, 0.1));
        synchronized (entities) {
            for (ACEntity e : entities) {
                e.render(r, t);
            }
        }
    }

}
