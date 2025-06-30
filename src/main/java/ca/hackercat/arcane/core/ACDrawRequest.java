package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACTexture;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

public class ACDrawRequest {



    public enum Type {
        RECT,
        ROUND_RECT, // to be added eventually
        OVAL,
        TEXTURE;
    }

    public Type type;
    public Vector3d position;
    public ACTexture texture;
    public Vector2d size;
    public Vector4d color;
    public boolean fill;
    public double rotation;
    public double cornerRadius;
    public Matrix4d projection;
    public Matrix4d camera;

    public ACDrawRequest(Type type) {
        this.type = type;
    }
}
