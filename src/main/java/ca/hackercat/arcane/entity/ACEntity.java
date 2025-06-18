package ca.hackercat.arcane.entity;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.entity.component.ACComponent;
import ca.hackercat.arcane.entity.component.ACActorPhysicsComponent;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class ACEntity {

    private static Vector2d gravity = new Vector2d(0, -72);

    private Vector2d position = new Vector2d();
    private Vector2d lastPosition = new Vector2d();
    private Vector2d velocity = new Vector2d();

    private ACCollisionBody body = new ACCollisionBody();

    private ACTeam team;

    private final List<ACComponent> components = new ArrayList<>();

    public ACEntity(ACTeam team) {
        this.team = team;
    }

    public ACEntity(ACTeam team, ACComponent... components) {
        this.team = team;
        synchronized (this.components) {
            this.components.addAll(List.of(components));
        }
    }

    public ACEntity addComponent(ACComponent component) {
        if (component == null) {
            return this;
        }
        if (hasComponentOfType(component.getClass())) {
            ACLogger.log(ACLevel.ERROR, "Entity already has component '%s'", component.getClass().getTypeName());
            return this;
        }
        synchronized (this.components) {
            this.components.add(component);
        }
        return this;
    }

    public <T> boolean hasComponentOfType(Class<T> clazz) {
        return getComponentOfType(clazz) != null;
    }

    public <T> ACComponent getComponentOfType(Class<T> clazz) {
        synchronized (this.components) {
            for (ACComponent component : components) {
                if (component.getClass() == clazz) {
                    return component;
                }
            }
        }
        return null;
    }

    public void update(double deltaTime) {
        lastPosition.set(position);
        position.add(new Vector2d(velocity).mul(deltaTime));
        synchronized (components) {
            for (ACComponent component : components) {
                component.update(this, deltaTime);
            }
        }
    }

    public void updateCollision(ACCollisionBody body, double deltaTime) {
        synchronized (components) {
            for (ACComponent component : components) {
                component.updateCollision(this, body, deltaTime);
            }
        }
    }

    public void render(ACRenderer renderer, double interp) {
        synchronized (components) {
            for (ACComponent component : components) {
                component.render(this, renderer, interp);
            }
        }
    }

    public boolean onGround() {
        synchronized (components) {
            for (ACComponent component : components) {
                if (component instanceof ACActorPhysicsComponent c) {
                    return c.isOnGround();
                }
            }
        }
        return false;
    }

    public static Vector2d getGravity() {
        return new Vector2d(gravity);
    }

    public Vector2d getPositionI() {
        return position.get(new Vector2d());
    }

    public Vector2d getPosition() {
        return position;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public Vector2d getPositionI(double interp) {
        return new Vector2d(lastPosition).lerp(position, interp);
    }

    public Vector2d getVelocityI() {
        return velocity.get(new Vector2d());
    }

    public ACCollisionBody getBody() {
        return this.body;
    }

    public void setPosition(Vector2d position) {
        this.position.set(position);
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setX(double x) {
        this.position.x = x;
    }

    public void setY(double y) {
        this.position.y = y;
    }

    public void setVelocity(Vector2d velocity) {
        this.velocity.set(velocity);
    }

    public void setVelocity(double x, double y) {
        this.velocity.set(x, y);
    }
}