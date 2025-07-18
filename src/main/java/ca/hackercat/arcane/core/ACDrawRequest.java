package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACShader;
import ca.hackercat.arcane.core.asset.ACTexture;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class ACDrawRequest {

    public enum Type {
        RECT,
        ROUND_RECT, // to be added eventually
        OVAL,
        TEXTURE;
    }

    public Type type;


    public Vector4d color;
    public ACTexture texture;
    public double cornerRadius;
    public boolean fill;

    public Matrix4d transform;

    public Matrix4d projection;
    public Matrix4d camera;
    public ACShader customShader;

    public ACDrawRequest(Type type) {
        this.type = type;
    }
}
