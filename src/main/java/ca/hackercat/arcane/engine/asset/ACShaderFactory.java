package ca.hackercat.arcane.engine.asset;

import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.LinkedList;
import java.util.List;

public class ACShaderFactory {

    private static final List<ACMesh> shaders = new LinkedList<>();

    public static ACMesh get(Vector3d[] vertices, Vector2d[] uvs, Vector3d[] normals, int[] indices) {
        ACMesh mesh = new ACMesh(vertices, uvs, normals, indices);
        shaders.add(mesh);

        while (!mesh.registered) {
            try {
                mesh.wait();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return mesh;
    }

    public static void createShaders() {
        synchronized (shaders) {

        }
    }

}
