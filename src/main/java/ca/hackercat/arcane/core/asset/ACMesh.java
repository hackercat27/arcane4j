package ca.hackercat.arcane.core.asset;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
    }

    @Override
    public boolean registered() {
        return registered;
    }

    @Override
    public void register() {
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(this.vertices.length * 3);
        float[] positionData = new float[this.vertices.length * 3];
        for (int i = 0; i < this.vertices.length; i++) {
            positionData[i * 3] = (float) this.vertices[i].x();
            positionData[(i * 3) + 1] = (float) this.vertices[i].y();
            positionData[(i * 3) + 2] = (float) this.vertices[i].z();
        }
        positionBuffer.put(positionData).flip();
        this.positionBuffer = storeData(positionBuffer, 0, 3);
        MemoryUtil.memFree(positionBuffer);

        if (this.uvs != null) {
            FloatBuffer textureUVBuffer = MemoryUtil.memAllocFloat(this.uvs.length * 2);
            float[] textureData = new float[this.uvs.length * 2];
            for (int i = 0; i < this.uvs.length; i++) {
                if (this.uvs[i] == null)
                    this.uvs[i] = new Vector2d();
                textureData[i * 2] = (float) this.uvs[i].x();
                textureData[(i * 2) + 1] = (float) this.uvs[i].y();
            }
            textureUVBuffer.put(textureData).flip();
            this.textureUVBuffer = storeData(textureUVBuffer, 1, 2);
            MemoryUtil.memFree(textureUVBuffer);
        }

        if (this.normals != null) {

            FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(this.normals.length * 3);
            float[] normalData = new float[this.normals.length * 3];
            for (int i = 0; i < this.normals.length; i++) {
                if (this.normals[i] == null)
                    this.normals[i] = new Vector3d(0, 1, 0).normalize();
                normalData[i * 3] = (float) this.normals[i].x();
                normalData[(i * 3) + 1] = (float) this.normals[i].y();
                normalData[(i * 3) + 2] = (float) this.normals[i].z();
            }
            normalBuffer.put(normalData).flip();
            this.normalBuffer = storeData(normalBuffer, 2, 3);
            MemoryUtil.memFree(normalBuffer);


        }

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(this.indices.length);
        indexBuffer.put(this.indices).flip();

        this.indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(indexBuffer);

        glBindVertexArray(0);

        registered = true;
    }

    private static int storeData(FloatBuffer buffer, int index, int size) {
        int bufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return bufferID;
    }
}
