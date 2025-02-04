package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACInputAction;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.util.ACMath;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACComponent {

    private double speed = 1;
    private double accel = 1;

    private boolean canSlowFall = false;

    @Override
    public void update(ACEntity parent, double deltaTime) {

        Vector2d desiredVelocity = new Vector2d();
        Vector2d v = parent.getVelocity();

        if (ACInput.isActionHeld(ACInputAction.LEFT)) {
            desiredVelocity.x -= 12;
        }
        if (ACInput.isActionHeld(ACInputAction.RIGHT)) {
            desiredVelocity.x += 12;
        }
        desiredVelocity.mul(speed);

        if (ACInput.isActionJustPressed(ACInputAction.JUMP)) {
            v.y = 16;
            canSlowFall = true;
        }
        else if (canSlowFall && v.y > 0 && ACInput.isActionHeld(ACInputAction.JUMP)) {
            v.add(ACEntity.getGravity().mul(-0.6 * deltaTime));
        }
        else {
            canSlowFall = false;
        }

        v.x = ACMath.approach(desiredVelocity.x, v.x, accel / deltaTime);
        parent.setVelocity(v);

    }
}
