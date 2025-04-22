package ca.hackercat.arcane.entity;

import org.joml.Vector2d;
import org.joml.primitives.Rectangled;

public class ACCollisionBody {

    public ACCollisionType type;
    public Rectangled hull;

    private Vector2d translation = new Vector2d();

    public ACCollisionBody(ACCollisionType type, Rectangled hull) {
        this.type = type;
        this.hull = hull;
    }

    public ACCollisionBody() {
        hull = new Rectangled();
    }

    public boolean intersects(ACCollisionBody other) {
        if (other == null || hull == null || other.hull == null) {
            return false;
        }
        Rectangled thisHull = new Rectangled(hull);
        thisHull.translate(translation);
        return thisHull.intersectsRectangle(other.hull);
    }

    public void setTranslation(Vector2d translation) {
        this.translation.set(translation);
    }
}
