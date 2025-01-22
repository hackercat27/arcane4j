package ca.hackercat.arcane.engine.asset;

import ca.hackercat.arcane.engine.ACThreadManager;
import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL30.*;

public class ACMesh implements ACAsset {

    private static ACShader shader;

    static {
        ACThreadManager.execute(() -> shader = ACShaderFactory.get("generic",
                "res:/shaders/core/generic.vsh",
                "res:/shaders/core/generic.fsh"));
    }

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
        if (!registered || shader == null || !shader.registered) {
            // silently fail instead of spamming log
            return;
        }

        ACThreadManager.throwIfNotMainThread();

        glBindVertexArray(vao);

        glEnableVertexAttribArray(0); // position

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

        glUseProgram(shader.programID);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glUseProgram(0);
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
