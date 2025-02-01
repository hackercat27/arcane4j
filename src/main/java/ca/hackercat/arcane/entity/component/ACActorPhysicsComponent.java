package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.entity.ACCollisionBody;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

public class ACActorPhysicsComponent implements ACEntityComponent {

    boolean onGround = false;

    @Override
    public void update(ACEntity parent, double deltaTime) {

        parent.setVelocity(parent.getVelocity().add(ACEntity.getGravity().mul(deltaTime)));
    }


    @Override
    public void updateCollision(ACEntity parent, ACCollisionBody body, double deltaTime) {
        Vector2d p = parent.getPosition();
        if (p.y <= -3) {
            p.y = -3;
            parent.setPosition(p);
            onGround = true;
        }
    }

    public boolean isOnGround() {
        return onGround;
    }

}
