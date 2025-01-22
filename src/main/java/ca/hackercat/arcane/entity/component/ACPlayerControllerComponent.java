package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.engine.io.ACInput;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACEntityComponent {

    private double speed = 1;


    @Override
    public void update(ACEntity parent, double deltaTime) {

        Vector2d desiredVelocity = new Vector2d();

        if (ACInput.isActionAsserted("left")) {
            desiredVelocity.x -= 1;
        }
        if (ACInput.isActionAsserted("right")) {
            desiredVelocity.x += 1;
        }
        desiredVelocity.mul(speed);



    }
}
