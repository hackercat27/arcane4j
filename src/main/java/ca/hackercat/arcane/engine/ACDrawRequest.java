package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.engine.asset.ACMesh;
import ca.hackercat.arcane.engine.asset.ACMeshFactory;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class ACDrawRequest {

    public enum Type {
        RECT,
        TEXTURE
    }

    private static ACMesh quad;

    static {
        // execute in new thread to not block the thread that just
        // so happened to load this class (also known as shit design)
        ACThreadManager.execute(() -> quad = ACMeshFactory.get(new Vector3d[] {
                new Vector3d(0, 0, 0), new Vector3d(1, 0, 0), new Vector3d(1, 1, 0), new Vector3d(0, 1, 0)
        }, new Vector2d[] {
                new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1), new Vector2d(0, 1)
        }, new Vector3d[] {
                new Vector3d(0, 0, 1), new Vector3d(0, 0, 1), new Vector3d(0, 0, 1), new Vector3d(0, 0, 1)
        }, new int[] {
                0, 1, 2, 0, 2, 3
        }));
    }

    public Type type;
    public Vector2d position;
    public Vector2d size;

    public ACDrawRequest(Type type) {
        this.type = type;
    }

    public void render() {
        if (quad != null) {
            quad.render();
        }
    }
}
