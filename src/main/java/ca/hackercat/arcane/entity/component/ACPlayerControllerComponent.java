package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACInputAction;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.util.ACMath;
import ca.hackercat.arcane.util.ACTimer;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACComponent {

    private double speed = 1;
    private double accel = 160;
    private double airAccel = 60;

    private boolean canSlowFall = false;
    private boolean canFastFall = false;

    private double slowFallGravityMul = 0.4;
    private double fastFallGravityMul = 1.6;

    private double jumpVelocity = 16;

    private double fastFallMaxYVelocity = 2;
    private ACTimer fastFallDelayTimer = new ACTimer(0.06);

    @Override
    public void update(ACEntity parent, double deltaTime) {

        Vector2d targetVelocity = new Vector2d();
        Vector2d velocity = parent.getVelocity();

        boolean moved = false;

        if (ACInput.isActionHeld(ACInputAction.LEFT)) {
            targetVelocity.x -= 12;
            moved = true;
        }
        if (ACInput.isActionHeld(ACInputAction.RIGHT)) {
            targetVelocity.x += 12;
            moved = true;
        }
        targetVelocity.mul(speed);

        if (ACInput.isActionJustPressed(ACInputAction.JUMP) && parent.onGround()) {
            velocity.y = Math.max(velocity.y + jumpVelocity, jumpVelocity);
            canSlowFall = true;
            fastFallDelayTimer.reset();
        }
        if (canSlowFall && velocity.y > 0 && ACInput.isActionHeld(ACInputAction.JUMP)) {
            velocity.add(ACEntity.getGravity().mul((slowFallGravityMul - 1) * deltaTime));
            canFastFall = true;
        }
        else {
            canSlowFall = false;
        }

        if (fastFallDelayTimer.triggered() && canFastFall
                && ACInput.isActionHeld(ACInputAction.FAST_FALL)) {
            canSlowFall = false;
            velocity.add(ACEntity.getGravity().mul((fastFallGravityMul - 1) * deltaTime));
            velocity.y = Math.min(velocity.y, fastFallMaxYVelocity);
        }

        if (parent.onGround()) {
            canFastFall = false;
        }

        if (moved || parent.onGround()) {
            velocity.x = ACMath.approach(velocity.x, targetVelocity.x,
                                         (parent.onGround()? accel : airAccel) * deltaTime);
        }

        parent.setVelocity(velocity);

    }
}
