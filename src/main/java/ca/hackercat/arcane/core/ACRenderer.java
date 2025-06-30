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
    private double rotation;

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
                                                request.position, request.size, request.rotation,
                                                request.color, request.fill, 0,
                                                getShader("arcane.shader.colorable"));
                    case OVAL -> handleDrawRect(request.projection == null? getProjection() : request.projection,
                                                request.camera == null? getTransform() : request.camera,
                                                request.position, request.size, request.rotation,
                                                request.color, request.fill, 1,
                                                getShader("arcane.shader.colorable"));
                    case TEXTURE -> handleDrawTextureRect(request.projection == null? getProjection() : request.projection,
                                                          request.camera == null? getTransform() : request.camera,
                                                          request.position, request.size, request.rotation,
                                                          request.texture,
                                                          getShader("arcane.shader.texture_cutout"));
                }
            }
            drawQueue.clear();

        }
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

    public void drawRect(double posX, double posY, double sizeX, double sizeY, double rotation) {
        drawRect(new Vector2d(posX, posY), new Vector2d(sizeX, sizeY), rotation);
    }

    public void drawRect(Vector2d position, Vector2d size, double rotation) {
        drawRect(position, size, 0, rotation);
    }

    public void drawRect(Rectangled rect, double rotation) {
        drawRect(rect, 0, rotation);
    }

    public void drawRect(Rectangled rect, double depth, double rotation) {
        drawRect(new Vector2d(rect.minX, rect.minY), rect.lengths(new Vector2d()), depth, rotation);
    }

    public void drawRect(Vector2d position, Vector2d size, double depth, double rotation) {
        drawRect(new Vector3d(position, depth), size, rotation);
    }

    public void drawRect(Vector3d position, Vector2d size, double rotation) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.RECT);
        request.position = new Vector3d().set(position);
        request.size = new Vector2d().set(size);
        request.color = new Vector4d().set(this.color);
        request.rotation = rotation;
        request.camera = new Matrix4d().set(getTransform());
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    public void drawOval(double posX, double posY, double sizeX, double sizeY, double rotation) {
        drawOval(posX, posY, sizeX, sizeY, 0, rotation);
    }

    public void drawOval(double posX, double posY, double sizeX, double sizeY, double depth, double rotation) {
        drawOval(new Vector2d(posX, posY), new Vector2d(sizeX, sizeY), depth, rotation);
    }

    public void drawOval(Vector2d position, Vector2d size, double rotation) {
        drawOval(position, size, 0, rotation);
    }

    public void drawOval(Rectangled rect, double rotation) {
        drawOval(rect, 0, rotation);
    }

    public void drawOval(Rectangled rect, double depth, double rotation) {
        drawOval(new Vector2d(rect.minX, rect.minY), rect.lengths(new Vector2d()), depth, rotation);
    }

    public void drawOval(Vector2d position, Vector2d size, double depth, double rotation) {
        drawOval(new Vector3d(position, depth), size, rotation);
    }

    public void drawOval(Vector3d position, Vector2d size, double rotation) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.OVAL);
        request.position = new Vector3d().set(position);
        request.size = new Vector2d().set(size);
        request.color = new Vector4d().set(this.color);
        request.rotation = rotation;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    public void drawTexture(ACTexture texture, Vector2d position, Vector2d size) {
        drawTexture(texture, new Vector3d(position, 0), size);
    }

    public void drawTexture(ACTexture texture, Vector2d position, Vector2d size, double depth) {
        drawTexture(texture, new Vector3d(position, depth), size);
    }

    public void drawTexture(ACTexture texture, Vector3d position, Vector2d size) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.TEXTURE);
        request.position = new Vector3d().set(position);
        request.size = new Vector2d().set(size);
        request.texture = texture;
        request.rotation = rotation;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    private void handleDrawRect(Matrix4d projection, Matrix4d camera,
                                Vector3d position, Vector2d size, double rotation,
                                Vector4d color, boolean fill, double cornerRadius, ACShader shader) {

        if (quad == null || !quad.registered()
                || shader == null || !shader.registered()) {
            // silently fail instead of crashing
            return;
        }

        ACThreadManager.throwIfNotMainThread();

        Matrix4d transform = ACMath.getOBJTransform(position, size, rotation);

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
        if (err != 0) {
            ACLogger.log(ACLevel.ERROR, err);
        }
    }

    private void handleDrawTextureRect(Matrix4d projection, Matrix4d camera,
                                       Vector3d position, Vector2d size, double rotation,
                                       ACTexture texture, ACShader shader) {

        if (quad == null || !quad.registered()
                || shader == null || !shader.registered()
                || texture == null || !texture.registered()) {
            // silently fail instead of crashing
            return;
        }

        texture.setFilter(GL_NEAREST);

        ACThreadManager.throwIfNotMainThread();

        Matrix4d transform = ACMath.getOBJTransform(position, size, rotation);

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

        shader.setUniform("sampler", 0);

        glDrawElements(GL_TRIANGLES, quad.indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glUseProgram(0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);

        int err = glGetError();
        if (err != 0) {
            ACLogger.log(ACLevel.ERROR, err);
        }

    }
}
