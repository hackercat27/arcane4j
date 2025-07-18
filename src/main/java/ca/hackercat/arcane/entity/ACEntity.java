package ca.hackercat.arcane.entity;

import ca.hackercat.arcane.core.ACGameManager;
import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.entity.component.ACActorPhysicsComponent;
import ca.hackercat.arcane.entity.component.ACComponent;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import ca.hackercat.arcane.util.ACMath;
import ca.hackercat.arcane.util.ACCoroutine;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2d;

public class ACEntity implements ACCoroutine {

    private static Vector2d gravity = new Vector2d(0, -72);

    private Vector2d position = new Vector2d();
    private Vector2d lastPosition = new Vector2d();
    private Vector2d velocity = new Vector2d();
    private double rotation;
    private double lastRotation;

    private ACCollisionBody body = new ACCollisionBody();

    private final List<ACComponent> components = new ArrayList<>();

    private final ACGameManager gameManager;

    public ACEntity(ACGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public ACEntity(ACGameManager gameManager, ACComponent... components) {
        this.gameManager = gameManager;
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

    public boolean hasComponentOfType(Class<? extends ACComponent> clazz) {
        return getComponentOfType(clazz) != null;
    }

    public ACComponent getComponentOfType(Class<? extends ACComponent> clazz) {
        synchronized (this.components) {
            for (ACComponent component : components) {
                if (component.getClass() == clazz) {
                    return component;
                }
            }
        }
        return null;
    }

    @Override
    public void update(double deltaTime) {
        lastRotation = rotation;
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

    @Override
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

    public Vector2d getPositionCopy() {
        return position.get(new Vector2d());
    }

    public Vector2d getPosition() {
        return position;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public Vector2d getPositionCopyInterpolated(double interp) {
        return new Vector2d(lastPosition).lerp(position, interp);
    }

    public Vector2d getVelocityCopy() {
        return velocity.get(new Vector2d());
    }

    public ACCollisionBody getBody() {
        return this.body;
    }

    public Vector2d getOrigin() {
        if (body.hull == null) {
            return getPositionCopy();
        }
        return new Vector2d((body.hull.minX + body.hull.maxX) / 2, (body.hull.minY + body.hull.maxY) / 2);
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

    public double getRotation() {
        return rotation;
    }

    public double getRotationCopy(double t) {
        return ACMath.lerpMod(lastRotation, rotation, t, 0, Math.TAU);
    }

    public void setRotation(double rotation) {
        this.rotation = ((rotation % Math.TAU) + Math.TAU) % Math.TAU;
    }

    public ACGameManager getGameManager() {
        return gameManager;
    }
}