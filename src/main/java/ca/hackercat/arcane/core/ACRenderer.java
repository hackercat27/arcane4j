package ca.hackercat.arcane.core;

import ca.hackercat.arcane.core.asset.ACMesh;
import ca.hackercat.arcane.core.asset.ACShader;
import ca.hackercat.arcane.core.asset.ACTexture;
import ca.hackercat.arcane.core.io.ACFileUtils;
import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.entity.ACEntity;
import ca.hackercat.arcane.logging.ACLevel;
import ca.hackercat.arcane.logging.ACLogger;
import ca.hackercat.arcane.util.ACMath;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

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
    private double rotation;

    private ACShader customShader;
    private final Map<String, ACShader> shaders = new HashMap<>();

    private Vector4d color = new Vector4d(1, 1, 1, 1);

    public ACRenderer(ACWindow window) {
        this.window = window;

        // execute in new thread to not block the thread that just
        // so happened to load this class (also known as shit design)

        // also hardcoding this quad isn't that shit but its not very far from it
        // but fuck writing code to load arbitrary meshes when this
        // is probably gonna be the only mesh thats used
        ACThreadManager.execute(() -> {
            quad = new ACMesh(new Vector3d[] { // positions
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

            final String[] shadersToLoad = new String[] {
                    "arcane.shader.generic",
                    "arcane.shader.colorable",
                    "arcane.shader.texture_cutout"
            };

            for (String shader : shadersToLoad) {
                shaders.put(shader, (ACShader) ACFileUtils.getAsset(shader));
            }
        });
    }

    private ACShader getShader(String assetName) {
        return shaders.get(assetName);
    }

    public void handleDrawQueue() {
        ACThreadManager.throwIfNotMainThread();

        synchronized (drawQueue) {
            // somehow i was never clearing these and never noticed
            // and i literally just thought the depth buffer was never getting cleared
            // like how does that even happen
            for (ACDrawRequest request : drawQueue) {
                switch (request.type) {
                    case RECT -> handleDrawRect(request.projection == null? getProjection() : request.projection,
                                                request.camera == null? getTransform() : request.camera,
                                                request.transform,
                                                request.color, request.fill, 0,
                                                request.customShader == null? getShader("arcane.shader.colorable") : request.customShader);
                    case OVAL -> handleDrawRect(request.projection == null? getProjection() : request.projection,
                                                request.camera == null? getTransform() : request.camera,
                                                request.transform,
                                                request.color, request.fill, 1,
                                                request.customShader == null? getShader("arcane.shader.colorable") : request.customShader);
                    case TEXTURE -> handleDrawTextureRect(request.projection == null? getProjection() : request.projection,
                                                          request.camera == null? getTransform() : request.camera,
                                                          request.transform,
                                                          request.texture,
                                                          request.customShader == null? getShader("arcane.shader.texture_cutout") : request.customShader);
                }
            }
            drawQueue.clear();

        }
    }

    public void setShader(ACShader shader) {
        this.customShader = shader;
    }

    public void setColor(double r, double g, double b) {
        this.color.set(r, g, b);
    }

    public void setColor(double r, double g, double b, double a) {
        this.color.set(r, g, b, a);
    }

    public void setColor(Color color) {
        this.color.set(color.getRed() / 255d,
                       color.getGreen() / 255d,
                       color.getBlue() / 255d,
                       color.getAlpha() / 255d);
    }

    public void setColor(Vector3d color) {
        this.color.set(color, 1d);
    }

    public void setColor(Vector4d color) {
        this.color.set(color);
    }

    public void setTranslation(Vector2d translation) {
        this.translation.set(translation);
    }

    public void setTranslation(double x, double y) {
        translation.set(x, y);
    }

    public void translate(Vector2d translation) {
        this.translation.add(translation);
    }

    public void translate(double x, double y) {
        translation.add(x, y);
    }

    public void setScale(Vector2d scale) {
        this.scale.set(scale);
    }

    public void setScale(double x, double y) {
        scale.set(x, y);
    }

    public void scale(Vector2d scale) {
        this.scale.mul(scale);
    }

    public void scale(double x, double y) {
        scale.mul(x, y);
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setTransform(Matrix4d transform) {
        Vector3d t = transform.getTranslation(new Vector3d());
        Vector3d s = transform.getScale(new Vector3d());
        Quaterniond rotation = transform.getNormalizedRotation(new Quaterniond());
        setTranslation(new Vector2d(t.x, t.y));
        setScale(new Vector2d(s.x, s.y));
        setRotation(rotation.getEulerAnglesXYZ(new Vector3d()).z);
    }

    public Matrix4d getProjection() {
        return ACMath.getOrthographicMatrix(camera, window);
    }

    public Matrix4d getTransform() {
        return ACMath.getCameraTransform(this.translation, this.scale, this.rotation);
    }

    public Vector2d screenspaceToWorldspace(Vector2d screenspace) {

        screenspace.x -= window.getWidth() / 2d;
        screenspace.y -= window.getHeight() / 2d;

        screenspace.x /= window.getHeight();
        screenspace.y /= -window.getHeight();

        screenspace.x /= scale.x;
        screenspace.y /= scale.y;

        screenspace.x -= translation.x;
        screenspace.y -= translation.y;

        return screenspace;
    }

    public void drawRect(Matrix4d transform) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.RECT);
        request.transform = new Matrix4d().set(transform);
        request.color = new Vector4d().set(this.color);
        request.camera = new Matrix4d().set(getTransform());
        request.customShader = customShader;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    public void drawOval(Matrix4d transform) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.OVAL);
        request.transform = new Matrix4d().set(transform);
        request.color = new Vector4d().set(this.color);
        request.customShader = customShader;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    public void drawTexture(ACTexture texture, Matrix4d transform) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.TEXTURE);
        request.transform = new Matrix4d().set(transform);
        request.texture = texture;
        request.customShader = customShader;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    private void handleDrawRect(Matrix4d projection, Matrix4d camera,
                                Matrix4d transform,
                                Vector4d color, boolean fill, double cornerRadius, ACShader shader) {

        if (quad == null || !quad.registered()
                || shader == null || !shader.registered()) {
            // silently fail instead of crashing
            return;
        }

        ACThreadManager.throwIfNotMainThread();

        glBindVertexArray(quad.vao);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnableVertexAttribArray(0); // position
        glEnableVertexAttribArray(1); // uvs

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quad.indexBuffer);

        glUseProgram(shader.programID);
        shader.setUniform("transform", transform);
        shader.setUniform("projection", projection);
        shader.setUniform("camera", camera);

        shader.setUniform("color", color);
        shader.setUniform("cornerRadius", cornerRadius);

        glDrawElements(GL_TRIANGLES, quad.indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glUseProgram(0);

        int err = glGetError();
        if (err != GL_NO_ERROR) {
            ACLogger.log(ACLevel.ERROR, "OpenGL error %d", err);
        }
    }

    private void handleDrawTextureRect(Matrix4d projection, Matrix4d camera,
                                       Matrix4d transform,
                                       ACTexture texture, ACShader shader) {

        if (quad == null || !quad.registered()
                || shader == null || !shader.registered()
                || texture == null || !texture.registered()) {
            // silently fail instead of crashing
            return;
        }

        texture.setFilter(GL_NEAREST);

        ACThreadManager.throwIfNotMainThread();

        glBindVertexArray(quad.vao);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnableVertexAttribArray(0); // position
        glEnableVertexAttribArray(1); // texturecoords

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quad.indexBuffer);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getID());

        glUseProgram(shader.programID);
        shader.setUniform("transform", transform);
        shader.setUniform("projection", projection);
        shader.setUniform("camera", camera);

        shader.setUniform("diffuseMap", 0);

        glDrawElements(GL_TRIANGLES, quad.indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glUseProgram(0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);

        int err = glGetError();
        if (err != GL_NO_ERROR) {
            ACLogger.log(ACLevel.ERROR, "OpenGL error %d", err);
        }

    }
}
