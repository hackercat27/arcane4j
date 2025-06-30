package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACCameraControllerComponent implements ACComponent {

    private Vector2d position;
    private ACEntity entity;

    public ACCameraControllerComponent(ACEntity tracked) {
        setTracked(tracked);
    }
    public ACCameraControllerComponent(Vector2d tracked) {
        setTracked(tracked);
    }

    @Override
    public void update(ACEntity parent, double deltaTime) {
        if (position == null) {
            return;
        }

        Vector2d pos = parent.getPositionCopy();

        // TODO: center on bounding box instead of hardcoded constant
        Vector2d targetPos = new Vector2d(position).add(0.5, 0.5);

        Vector2d delta = targetPos.sub(pos, new Vector2d());

        final double smoothing = 10;

        double alpha = 1 - Math.exp(-smoothing * deltaTime);

        parent.setPosition(pos.add(delta.mul(alpha)));
    }

    public void setTracked(ACEntity tracked) {
        if (tracked != null) {
            position = tracked.getPosition();
            entity = tracked;
        }
    }

    public void setTracked(Vector2d position) {
        this.position = position;
        entity = null;
    }
}
