package ca.hackercat.arcane.engine;

import org.joml.Vector2d;

public class ACDrawRequest {

    public enum Type {
        RECT,
        TEXTURE
    }

    public Type type;
    public Vector2d position;
    public Vector2d size;

    public ACDrawRequest(Type type) {
        this.type = type;
    }
}
