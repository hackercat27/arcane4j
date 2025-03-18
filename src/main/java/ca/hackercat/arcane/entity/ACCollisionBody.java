package ca.hackercat.arcane.entity;

import org.joml.primitives.Rectangled;

public class ACCollisionBody {

    public ACCollisionType type;
    public Rectangled hull;

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
        return hull.intersectsRectangle(other.hull);
    }

}
