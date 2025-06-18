package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACTexture;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class ACDrawRequest {

    public enum Type {
        RECT,
        OVAL,
        TEXTURE
    }

    public Type type;
    public Vector3d position;
    public ACTexture texture;
    public Vector2d size;
    public Vector4d color;
    public boolean fill;

    public ACDrawRequest(Type type) {
        this.type = type;
    }
}
