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

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ACRenderer {

    private final List<ACDrawRequest> drawQueue;
    private ACWindow window;
    private ACMesh quad;
    private ACEntity camera;
    private Matrix4d globalTransform = new Matrix4d();

    private ACShader genericShader;

    public ACRenderer(List<ACDrawRequest> drawQueue, ACWindow window) {
        this.drawQueue = drawQueue;
        this.window = window;

        // execute in new thread to not block the thread that just
        // so happened to load this class (also known as shit design)

        // also hardcoding this quad isn't that shit but its not very far from it
        // but fuck writing code to load arbitrary meshes when this
        // is probably gonna be the only mesh thats used
        ACThreadManager.execute(() -> quad = ACMeshFactory.get(new Vector3d[] {
                new Vector3d(0, 0, 0), new Vector3d(1, 0, 0), new Vector3d(1, 1, 0), new Vector3d(0, 1, 0)
        }, new Vector2d[] {
                new Vector2d(0, 0), new Vector2d(1, 0), new Vector2d(1, 1), new Vector2d(0, 1)
        }, new Vector3d[] {
                new Vector3d(0, 0, 1), new Vector3d(0, 0, 1), new Vector3d(0, 0, 1), new Vector3d(0, 0, 1)
        }, new int[] {
                0, 1, 2, 0, 2, 3
        }));

        ACThreadManager.execute(() -> genericShader = (ACShader) ACFileUtils.getAsset("arcane.shader.generic"));
    }

    public void handleDrawQueue() {
        ACThreadManager.throwIfNotMainThread();

        synchronized (drawQueue) {
            for (ACDrawRequest request : drawQueue) {
                switch (request.type) {
                    case RECT -> handleDrawRect(request.position, request.size, genericShader);
                }
            }
        }


    }

    public void drawRect(Vector2d position, Vector2d size) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.RECT);
        request.position = position;
        request.size = size;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }

    private void handleDrawRect(Vector2d position, Vector2d size, ACShader shader) {

        if (quad == null || !quad.registered || shader == null || !shader.registered) {
            // silently fail instead of crashing
            return;
        }

        ACThreadManager.throwIfNotMainThread();

        Matrix4d transform = ACMath.getTransform(position, size)
                                   .mul(globalTransform);

        glBindVertexArray(quad.vao);

        glEnableVertexAttribArray(0); // position

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quad.indexBuffer);



        glUseProgram(shader.programID);
        shader.setUniform("transform", transform);
//        shader.setUniform("projection", ACMath.getOrthographicMatrix(camera, window));
//        shader.setUniform("camera", new Matrix4d());

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
