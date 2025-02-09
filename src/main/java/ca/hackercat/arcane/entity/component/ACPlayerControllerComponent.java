package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.core.io.ACInputAction;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.util.ACTimer;
import org.joml.Vector2d;

public class ACPlayerControllerComponent implements ACComponent {

    private double maxSpeed = 14;
    private double hardGroundSpeedCap = 30;
    private double groundAccel = 200;
    private double airAccel = 60;

    private double airFriction = 4;
    private double groundFriction = 120;

    private boolean canSlowFall = false;
    private boolean canFastFall = false;

    private double slowFallGravityMul = 0.4;
    private double fastFallGravityMul = 1.6;

    private double jumpVelocity = 14;
    private double jumpVelocityOffset = 4;
    private double jumpVelocitySpeedMul = 0.2;
    private double maxJumpVelocitySpeed = 2;

    private double bunnyHopMul = 1.2;

    private double fastFallMaxYVelocity = 0;
    private ACTimer fastFallDelayTimer = new ACTimer(0.08);

    @Override
    public void update(ACEntity parent, double deltaTime) {

        Vector2d desiredVelocity = new Vector2d();
        Vector2d velocity = parent.getVelocity();


        if (ACInput.isActionHeld(ACInputAction.LEFT)) {
            desiredVelocity.x -= 12;
        }
        if (ACInput.isActionHeld(ACInputAction.RIGHT)) {
            desiredVelocity.x += 12;
        }
        desiredVelocity.mul(maxSpeed);

        if (ACInput.isActionJustPressed(ACInputAction.JUMP) && parent.onGround() && velocity.y < 0) {
            double jumpVelocity = this.jumpVelocity +
                    Math.max(0, Math.min((Math.abs(velocity.x) - jumpVelocityOffset) * jumpVelocitySpeedMul,
                                         maxJumpVelocitySpeed));
            velocity.y = Math.max(velocity.y + jumpVelocity, jumpVelocity);
            velocity.x *= bunnyHopMul;
            canSlowFall = true;
            fastFallDelayTimer.reset();
            canFastFall = false;
        }
        if (canSlowFall && velocity.y > 0 && ACInput.isActionHeld(ACInputAction.JUMP)) {
            velocity.add(ACEntity.getGravity().mul((slowFallGravityMul - 1) * deltaTime));
        }
        else {
            canSlowFall = false;
        }

        if (fastFallDelayTimer.triggered() && ACInput.isActionJustPressed(ACInputAction.FAST_FALL)) {
            canFastFall = true;
        }

        if (canFastFall && ACInput.isActionHeld(ACInputAction.FAST_FALL)) {
            canSlowFall = false;
            velocity.add(ACEntity.getGravity().mul((fastFallGravityMul - 1) * deltaTime));
            velocity.y = Math.min(velocity.y, fastFallMaxYVelocity);
        }

        if (parent.onGround()) {
            canFastFall = false;
        }

        handleMovement(velocity, desiredVelocity, parent.onGround(), deltaTime);
        handleFriction(velocity, parent.onGround(), deltaTime);

        if (Math.abs(velocity.x) > Math.abs(hardGroundSpeedCap)) {
            velocity.x = Math.copySign(hardGroundSpeedCap, velocity.x);
        }

        parent.setVelocity(velocity);
    }

    private void handleMovement(Vector2d velocity, Vector2d desiredVelocity, boolean onGround, double deltaTime) {
        Vector2d movementDirection = new Vector2d(desiredVelocity.x, 0).normalize();
        if (Double.isNaN(movementDirection.x)) {
            movementDirection.x = 0;
        }
        movementDirection.y = 0;

        boolean gainingSpeed = velocity.x * movementDirection.x > 0;
        boolean belowSpeedCap = Math.abs(velocity.x) < maxSpeed;
        boolean canAccelerate = belowSpeedCap || !gainingSpeed;

        if (canAccelerate) {
            velocity.add(movementDirection.mul(onGround? groundAccel : airAccel).mul(deltaTime));
        }
    }

    private void handleFriction(Vector2d velocity, boolean onGround, double deltaTime) {
        Vector2d dir = new Vector2d(velocity).normalize();
        if (!dir.isFinite()) {
            return;
        }
        Vector2d frictionDir = new Vector2d().sub(dir);
        frictionDir.mul(onGround? groundFriction : airFriction);

        velocity.add(frictionDir.mul(deltaTime));
    }

}
