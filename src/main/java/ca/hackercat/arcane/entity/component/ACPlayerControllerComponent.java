package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACEntityComponent {

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


        parent.getVelocity().set(desiredVelocity);

    }
}
