package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACComponent {

    private double speed = 1;

    @Override
    public void update(ACEntity parent, double deltaTime) {

        Vector2d desiredVelocity = new Vector2d();

        if (ACInput.isActionAsserted("left")) {
            desiredVelocity.x -= 7;
        }
        if (ACInput.isActionAsserted("right")) {
            desiredVelocity.x += 7;
        }
        desiredVelocity.mul(speed);

        if (ACInput.isActionJustPressed("jump")) {
            Vector2d v = parent.getVelocity();
            v.y = 4;
            parent.setVelocity(v);
        }

        Vector2d v = parent.getVelocity();
        v.x = desiredVelocity.x;
        parent.setVelocity(v);

    }
}
