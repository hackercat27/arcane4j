package ca.hackercat.arcane.entity;

import org.joml.primitives.Rectangled;

public class ACCollisionBody {

    public ACCollisionType type;
    public Rectangled hull;

    public ACCollisionBody(ACCollisionType type, Rectangled hull) {
        this.type = type;
        this.hull = hull;
    }

    public boolean intersects(ACCollisionBody other) {
        return hull.intersectsRectangle(other.hull);
    }

}
