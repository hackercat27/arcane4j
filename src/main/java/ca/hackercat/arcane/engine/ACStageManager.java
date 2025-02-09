package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.entity.component.ACActorPhysicsComponent;
import ca.hackercat.arcane.entity.component.ACActorRenderComponent;
import ca.hackercat.arcane.entity.component.ACCameraControllerComponent;
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
    private ACMap map;

    private Vector2d lastCameraPos = new Vector2d();
    private Vector2d cameraPos = new Vector2d();

    public ACStageManager() {
        ACEntity player = new ACEntity()
                             .addComponent(new ACPlayerControllerComponent())
                             .addComponent(new ACActorRenderComponent())
                             .addComponent(new ACActorPhysicsComponent());
        ACEntity camera = new ACEntity()
                .addComponent(new ACCameraControllerComponent(player));

        entities.add(player);
        entities.add(camera);

        changeStage("res:/maps/map.json");
    }

    public void changeStage(String path) {
        map = new ACMap(path);
    }

    public void update(double deltaTime) {
        lastCameraPos.set(cameraPos);
        synchronized (entities) {
            for (ACEntity e : entities) {
                e.update(deltaTime);
                e.updateCollision(deltaTime);

                if (e.getComponentOfType(ACCameraControllerComponent.class) != null) {
                    cameraPos.set(e.getPosition());
                }
            }
        }
        if (map != null) {
            map.update(deltaTime);
        }
    }

    public void render(ACRenderer r, double t) {


        r.setColor(Color.WHITE);
        r.setTransform(new Matrix4d());
        r.setTranslation(new Vector2d().sub(cameraPos));

        r.scale(new Vector2d(0.1, 0.1));
        synchronized (entities) {
            for (ACEntity e : entities) {
                e.render(r, t);
            }
        }
        if (map != null) {
            map.render(r, t);
        }
    }

}
