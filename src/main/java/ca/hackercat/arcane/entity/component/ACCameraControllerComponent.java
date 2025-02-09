package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACCameraControllerComponent implements ACComponent {

    private ACEntity tracked;

    public ACCameraControllerComponent(ACEntity tracked) {
        this.tracked = tracked;
    }

    @Override
    public void update(ACEntity parent, double deltaTime) {
        Vector2d pos = parent.getPosition();
        Vector2d targetPos = tracked.getPosition();

        Vector2d delta = targetPos.sub(pos, new Vector2d());

        parent.setPosition(targetPos.sub(delta.mul(0.9995)));
    }
}
