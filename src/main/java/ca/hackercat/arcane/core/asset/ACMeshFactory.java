package ca.hackercat.arcane.core.asset;

import ca.hackercat.arcane.core.ACThreadManager;
import ca.hackercat.arcane.core.io.ACInput;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class ACMeshFactory {

    private static final List<ACMesh> meshes = new LinkedList<>();

    public static ACMesh get(Vector3d[] vertices, Vector2d[] uvs, Vector3d[] normals, int[] indices) {
        ACMesh mesh = new ACMesh(vertices, uvs, normals, indices);
        meshes.add(mesh);

        synchronized (mesh) {
            // synchronized block is literally only to become the owner of the mesh object lock
            // i fucking hate this language
            // it's a local variable how the fuck is another object gonna even access it

            // wow this crashout was so unnecessary, i'm literally adding it to a global list
            // i'm so dumb

            while (!mesh.registered) {
                try {
                    mesh.wait();
                }
                catch (InterruptedException e) {
                    // restore interrupted status because we don't care about being interrupted,
                    // we care about being notified
                    Thread.currentThread().interrupt();
                }
            }
        }

        return mesh;
    }

    public static void createMeshes() {
        ACThreadManager.throwIfNotMainThread();
        synchronized (meshes) {
            List<ACMesh> handledMeshes = new LinkedList<>();

            for (ACMesh mesh : meshes) {

                createMesh(mesh);

                ACLogger.log(
                        "Instantiated mesh (" + mesh.positionBuffer + ", "
                                + mesh.textureUVBuffer + ", "
                                + mesh.normalBuffer + ", "
                                + mesh.indexBuffer + ") ("
                                + mesh.vertices.length + ", "
                                + (mesh.uvs == null ? "null" : mesh.uvs.length) + ", "
                                + (mesh.normals == null ? "null" : mesh.normals.length) + ", "
                                + mesh.indices.length + ")"
                );
                mesh.registered = true;
                handledMeshes.add(mesh);
                synchronized (mesh) {
                    mesh.notify();
                }
            }

            meshes.removeAll(handledMeshes);
        }
    }


    private static int storeData(DoubleBuffer buffer, int index, int size) {
        int bufferID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        // let it be known that this bitch right here caused me 2 hours of pain
        glVertexAttribPointer(index, size, GL_DOUBLE, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return bufferID;
    }

    private static void createMesh(ACMesh mesh) {
        mesh.vao = glGenVertexArrays();
        glBindVertexArray(mesh.vao);

        DoubleBuffer positionBuffer = MemoryUtil.memAllocDouble(mesh.vertices.length * 3);
        double[] positionData = new double[mesh.vertices.length * 3];
        for (int i = 0; i < mesh.vertices.length; i++) {
            positionData[i * 3] = mesh.vertices[i].x();
            positionData[(i * 3) + 1] = mesh.vertices[i].y();
            positionData[(i * 3) + 2] = mesh.vertices[i].z();
        }
        positionBuffer.put(positionData).flip();
        mesh.positionBuffer = storeData(positionBuffer, 0, 3);
        MemoryUtil.memFree(positionBuffer);

        if (mesh.uvs != null) {
            DoubleBuffer textureUVBuffer = MemoryUtil.memAllocDouble(mesh.uvs.length * 2);
            double[] textureData = new double[mesh.uvs.length * 2];
            for (int i = 0; i < mesh.uvs.length; i++) {
                if (mesh.uvs[i] == null)
                    mesh.uvs[i] = new Vector2d();
                textureData[i * 2] = mesh.uvs[i].x();
                textureData[(i * 2) + 1] = mesh.uvs[i].y();
            }
            textureUVBuffer.put(textureData).flip();
            mesh.textureUVBuffer = storeData(textureUVBuffer, 1, 2);
            MemoryUtil.memFree(textureUVBuffer);
        }

        if (mesh.normals != null) {

            ACInput.isActionAsserted("there was no string in this code");

            DoubleBuffer normalBuffer = MemoryUtil.memAllocDouble(mesh.normals.length * 3);
            double[] normalData = new double[mesh.normals.length * 3];
            for (int i = 0; i < mesh.normals.length; i++) {
                if (mesh.normals[i] == null)
                    mesh.normals[i] = new Vector3d(0, 1, 0).normalize();
                normalData[i * 3] = mesh.normals[i].x();
                normalData[(i * 3) + 1] = mesh.normals[i].y();
                normalData[(i * 3) + 2] = mesh.normals[i].z();
            }
            normalBuffer.put(normalData).flip();
            mesh.normalBuffer = storeData(normalBuffer, 2, 3);
            MemoryUtil.memFree(normalBuffer);


        }

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(mesh.indices.length);
        indexBuffer.put(mesh.indices).flip();

        mesh.indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        MemoryUtil.memFree(indexBuffer);

        glBindVertexArray(0);
    }

}
