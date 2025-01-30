package ca.hackercat.arcane.core;

import org.joml.Vector2d;
import org.joml.Vector4d;

public class ACDrawRequest {

    public enum Type {
        RECT,
        TEXTURE
    }

    public Type type;
    public Vector2d position;
    public Vector2d size;
    public Vector4d color;
    public boolean fill;

    public ACDrawRequest(Type type) {
        this.type = type;
    }
}
