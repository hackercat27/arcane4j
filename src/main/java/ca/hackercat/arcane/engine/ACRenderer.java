package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.engine.asset.ACMesh;
import ca.hackercat.arcane.engine.asset.ACMeshFactory;
import ca.hackercat.arcane.engine.asset.ACShader;
import ca.hackercat.arcane.engine.asset.ACTexture;
import ca.hackercat.arcane.engine.io.ACFileUtils;
import ca.hackercat.arcane.engine.io.ACWindow;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.logging.ACLogger;
import ca.hackercat.arcane.util.ACMath;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.primitives.Rectangled;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ACRenderer {

    private final List<ACDrawRequest> drawQueue = new LinkedList<>();
    private ACWindow window;
    private ACMesh quad;
    private ACEntity camera;

    private Vector2d scale = new Vector2d(1, 1);
    private Vector2d translation = new Vector2d();

    private ACShader shaderGeneric;
    private ACShader shaderColorable;

    private Vector4d color = new Vector4d(1, 1, 1, 1);

    public ACRenderer(ACWindow window) {
        this.window = window;

        // execute in new thread to not block the thread that just
        // so happened to load this class (also known as shit design)

        // also hardcoding this quad isn't that shit but its not very far from it
        // but fuck writing code to load arbitrary meshes when this
        // is probably gonna be the only mesh thats used
        ACThreadManager.execute(() -> {
            quad = ACMeshFactory.get(new Vector3d[] { // positions
                    new Vector3d(0, 0, 0),
                    new Vector3d(1, 0, 0),
                    new Vector3d(1, 1, 0),
                    new Vector3d(0, 1, 0)
            }, new Vector2d[] { // uvs
                    new Vector2d(0, 0),
                    new Vector2d(1, 0),
                    new Vector2d(1, 1),
                    new Vector2d(0, 1)
            }, new Vector3d[] { // normals
                    new Vector3d(0, 0, 1),
                    new Vector3d(0, 0, 1),
                    new Vector3d(0, 0, 1),
                    new Vector3d(0, 0, 1)
            }, new int[] { // indices
                    0, 1, 2, 0, 2, 3
            });
            shaderGeneric = (ACShader) ACFileUtils.getAsset("arcane.shader.generic");
            shaderColorable = (ACShader) ACFileUtils.getAsset("arcane.shader.colorable");
        });
    }

    public void handleDrawQueue() {
        ACThreadManager.throwIfNotMainThread();

        synchronized (drawQueue) {
            for (ACDrawRequest request : drawQueue) {
                switch (request.type) {
                    case RECT -> handleDrawRect(request.position, request.size,
                                                request.color, request.fill,
                                                shaderColorable);
                }
            }
        }

    }

    public void setColor(Color color) {
        this.color = new Vector4d(color.getRed() / 255d,
                                  color.getGreen() / 255d,
                                  color.getBlue() / 255d,
                                  color.getAlpha() / 255d);
    }

    public Matrix4d getTransform() {
        return ACMath.getTransform(this.translation, this.scale);
    }

    public Rectangled getScreenBounds() {
        double scale = (double) window.getWidth() / window.getHeight();
        return new Rectangled(0, 0, scale, 1);
    }

    public void drawRect(Vector2d position, Vector2d size) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.RECT);
        // adjust to have origin in top left
        request.position = position.add(0, -size.y);
        request.size = size;
        request.color = new Vector4d(this.color);
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    private void handleDrawRect(Vector2d position, Vector2d size, Vector4d color, boolean fill, ACShader shader) {

        if (quad == null || !quad.registered || shader == null || !shader.registered) {
            // silently fail instead of crashing
            return;
        }

        ACThreadManager.throwIfNotMainThread();

        Matrix4d transform = ACMath.getTransform(position, size);

        glBindVertexArray(quad.vao);

        glEnableVertexAttribArray(0); // position

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quad.indexBuffer);

        glUseProgram(shader.programID);
        shader.setUniform("transform", transform);
        shader.setUniform("projection", ACMath.getOrthographicMatrix(camera, window));
        shader.setUniform("camera", getTransform());

        shader.setUniform("color", color);


        glDrawElements(GL_TRIANGLES, quad.indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glUseProgram(0);

        int err = glGetError();
        if (err != 0) {
            ACLogger.error(err);
        }
    }


    public void drawTexture(ACTexture texture, Vector2d position) {

    }
    public void drawTexture(ACTexture texture, Vector2d position, Vector2d size) {

    }

}
