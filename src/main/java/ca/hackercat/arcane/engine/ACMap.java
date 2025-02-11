package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.core.io.ACFileUtils;
import ca.hackercat.arcane.engine.tiled.TiledLayer;
import ca.hackercat.arcane.engine.tiled.TiledMap;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;
import org.joml.Vector2i;

import java.awt.Color;

public class ACMap {

    private Vector2i offset = new Vector2i();

    private int width;
    private int height;

    private int[] tileIDs;

    public ACMap(String path) {

        ACLogger.log("Loading map from '%s'", path);

        TiledMap map = ACFileUtils.fromJson(TiledMap.class, ACFileUtils.readStringFromPath(path));

        TiledLayer layer = map.getContentLayer();

        tileIDs = layer.getData();
        width = layer.getWidth();
        height = layer.getHeight();

        offset.set(layer.getOffset());

    }

    public void update(double deltaTime) {

    }

    public void render(ACRenderer r, double t) {

        r.setColor(Color.MAGENTA);

        if (tileIDs != null) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int i = y * width + x;

                    if (tileIDs[i] != 0) {
                        r.drawRect(new Vector2d(x - offset.x, y - offset.y), new Vector2d(1, 1), -2);
                    }
                }
            }
        }

    }

}
