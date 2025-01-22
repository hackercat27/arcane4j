package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.engine.asset.ACMesh;
import ca.hackercat.arcane.engine.asset.ACTexture;
import org.joml.Vector2d;

import java.util.List;

public class ACRenderer {

    private final List<ACDrawRequest> drawQueue;

    private ACMesh quad;

    public ACRenderer(List<ACDrawRequest> drawQueue) {
        this.drawQueue = drawQueue;

    }

    public void drawRect(Vector2d position, Vector2d size) {
        ACDrawRequest request = new ACDrawRequest(ACDrawRequest.Type.RECT);
        request.position = position;
        request.size = size;
        synchronized (drawQueue) {
            drawQueue.add(request);
        }
    }


    public void drawTexture(ACTexture texture, Vector2d position) {

    }
    public void drawTexture(ACTexture texture, Vector2d position, Vector2d size) {

    }

}
