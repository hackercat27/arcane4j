package ca.hackercat.arcane.engine.asset;

import ca.hackercat.arcane.engine.ACThreadManager;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL30.*;

public class ACMesh implements ACAsset {

    public Vector3d[] vertices;
    public Vector2d[] uvs;
    public Vector3d[] normals;
    public int[] indices;

    public int vao;
    public int indexBuffer;
    public int positionBuffer;
    public int textureUVBuffer;
    public int normalBuffer;

    private boolean disposable;
    public boolean registered;

    public ACMesh(Vector3d[] vertices, Vector2d[] uvs, Vector3d[] normals, int[] indices) {
        this.vertices = vertices;
        this.uvs = uvs;
        this.normals = normals;
        this.indices = indices;

        ACAssetManager.register(this);
    }

    public void render() {
        if (!registered) {
            ACLogger.error("%o is not initialized!", this);
            return;
        }
        ACThreadManager.throwIfNotMainThread();

        glBindVertexArray(vao);

        glEnableVertexAttribArray(0); // position
        glEnableVertexAttribArray(1); // UVs
        glEnableVertexAttribArray(2); // normals

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    @Override
    public boolean isDisposable() {
        return disposable;
    }

    @Override
    public void dispose() {
        glDeleteBuffers(positionBuffer);
        glDeleteBuffers(textureUVBuffer);
        glDeleteBuffers(normalBuffer);
        glDeleteBuffers(indexBuffer);
        glDeleteVertexArrays(vao);
        registered = false;
        // mark as unregistered after disposal,
        // although this is probably unnecessary - but good practice nonetheless
    }
}
