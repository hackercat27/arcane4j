package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.entity.ACCollisionBody;
import ca.hackercat.arcane.entity.ACCollisionType;
import ca.hackercat.arcane.entity.ACEntity;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2d;
import org.joml.primitives.Rectangled;

public class ACActorPhysicsComponent implements ACComponent {

    private boolean onGround = false;

    private boolean foundCollisionThisTick = false;
    private boolean experiencesGravity;

    public ACActorPhysicsComponent(boolean experiencesGravity) {
        this.experiencesGravity = experiencesGravity;
    }

    @Override
    public void update(ACEntity parent, double deltaTime) {

        if (experiencesGravity && !parent.onGround()) {
            parent.setVelocity(parent.getVelocityI().add(ACEntity.getGravity().mul(deltaTime)));
        }

        foundCollisionThisTick = false;
    }

    @Override
    public void updateCollision(ACEntity parent, ACCollisionBody body, double deltaTime) {
        switch (body.type) {
            case ACCollisionType.SOLID -> updateSolidColliion(parent, body, deltaTime);
        }
    }

    private void updateSolidColliion(ACEntity parent, ACCollisionBody body, double deltaTime) {
        Vector2d p = parent.getPositionI();

        final double GROUND_EPSILON = 0.1;

        if (parent.getBody() == null || body == null) {
            return;
        }

        Vector2d velocity = parent.getVelocityI();

        ACCollisionBody parentBody = new ACCollisionBody(ACCollisionType.ENTITY, new Rectangled(0, 0, 1, 1));
        parentBody.translate(p);

        if (experiencesGravity && parent.getVelocity().y < 0) {
            parentBody.translate(new Vector2d(0, -GROUND_EPSILON));
            Vector2d d = getDisplacement(parentBody.hull, body.hull);
            if (d.y < 0) {
                foundCollisionThisTick = true;
            }
            parentBody.translate(new Vector2d(0, GROUND_EPSILON));
        }

        if (parentBody.intersects(body)) {
            final double DISPLACEMENT_EPSILON = 0.0001;

            Vector2d displacement = getDisplacement(parentBody.hull, body.hull);

            if (displacement.y < -DISPLACEMENT_EPSILON) {
                velocity.y = Math.max(velocity.y, 0);
            }
            if (displacement.y > DISPLACEMENT_EPSILON) {
                velocity.y = Math.min(velocity.y, 0);
            }

            if (displacement.x < -DISPLACEMENT_EPSILON) {
                velocity.x = Math.max(velocity.x, 0);
            }
            if (displacement.x > DISPLACEMENT_EPSILON) {
                velocity.x = Math.min(velocity.x, 0);
            }

            parent.getPosition().sub(displacement);

        }

        parent.setVelocity(velocity);
        onGround = foundCollisionThisTick;
    }

    private Vector2d getDisplacement(Rectangled obj, Rectangled collider) {
        Vector2d objPos = new Vector2d(obj.minX, obj.minY);
        Vector2d objSize = new Vector2d(obj.maxX - obj.minX, obj.maxY - obj.minY);
        Vector2d colliderPos = new Vector2d(collider.minX, collider.minY);
        Vector2d colliderSize = new Vector2d(collider.maxX - collider.minX, collider.maxY - collider.minY);

        List<Vector2d> hPushPoints = new ArrayList<>();
        List<Vector2d> vPushPoints = new ArrayList<>();

        final double margin_x = 0.3;
        final double margin_y = 0.3;

        // TODO: make this generic and dont hardcode the points

        hPushPoints.add(new Vector2d(0, margin_y).add(objPos));
        hPushPoints.add(new Vector2d(objSize.x, margin_y).add(objPos));
        hPushPoints.add(new Vector2d(0, objSize.y - margin_y).add(objPos));
        hPushPoints.add(new Vector2d(objSize.x, objSize.y - margin_y).add(objPos));

        vPushPoints.add(new Vector2d(margin_x, 0).add(objPos));
        vPushPoints.add(new Vector2d(margin_x, objSize.y).add(objPos));
        vPushPoints.add(new Vector2d(objSize.x - margin_x, 0).add(objPos));
        vPushPoints.add(new Vector2d(objSize.x - margin_x, objSize.y).add(objPos));

        boolean moved = false;

        Vector2d displacement = new Vector2d();

        for (Vector2d point : vPushPoints) {
            if (moved || !collider.containsPoint(point)) {
                continue;
            }

            double dUp = collider.maxY - point.y;
            double dDown = collider.minY - point.y;

            double delta;

            if (Math.abs(dDown) < Math.abs(dUp))
                delta = dDown;
            else delta = dUp;

            displacement.add(0, delta);
            moved = true;
        }

        moved = false;
        for (Vector2d point : hPushPoints) {
            if (moved || !collider.containsPoint(point)) {
                continue;
            }

            double dRight = collider.maxX - point.x;
            double dLeft = collider.minX - point.x;

            double delta;

            if (Math.abs(dLeft) < Math.abs(dRight)) {
                delta = dLeft;
            }
            else delta = dRight;

            displacement.add(delta, 0);
            moved = true;
        }


        return displacement;
    }

    public boolean isOnGround() {
        return onGround || !experiencesGravity;
    }

}
