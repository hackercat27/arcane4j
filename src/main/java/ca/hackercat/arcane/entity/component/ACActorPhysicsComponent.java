package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.entity.ACCollisionBody;
import ca.hackercat.arcane.entity.ACCollisionType;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.joml.primitives.Rectangled;

public class ACActorPhysicsComponent implements ACComponent {

    boolean onGround = false;

    @Override
    public void update(ACEntity parent, double deltaTime) {

        parent.setVelocity(parent.getVelocity().add(ACEntity.getGravity().mul(deltaTime)));

    }

    @Override
    public void updateCollision(ACEntity parent, ACCollisionBody body, double deltaTime) {
        Vector2d p = parent.getPosition();

        if (parent.getBody() == null || body == null) {
            return;
        }

        ACCollisionBody parentBody = new ACCollisionBody(ACCollisionType.ENTITY, new Rectangled(0, 0, 1, 1));
        parentBody.setTranslation(p);

        if (parentBody.intersects(body)) {
            onGround = true;
            parent.setPosition(0, parent.getPosition().y + 1);
            parent.setVelocity(0, 0);
        }

    }

    public boolean isOnGround() {
        return onGround;
    }

}
