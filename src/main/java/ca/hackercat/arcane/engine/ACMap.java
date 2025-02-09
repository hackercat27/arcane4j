package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.core.io.ACFileUtils;
import ca.hackercat.arcane.engine.tiled.TiledMap;
import ca.hackercat.arcane.logging.ACLogger;
import org.joml.Vector2d;

import java.awt.Color;

public class ACMap {

    private int width;
    private int height;

    private int[] tileIDs;

    public ACMap(String path) {

        ACLogger.log("Loading map from '%s'", path);

        TiledMap map = ACFileUtils.fromJson(TiledMap.class, ACFileUtils.readStringFromPath(path));

        width = 10;
        height = 10;
        tileIDs = new int[width * height];

        tileIDs[0] = 1;
        tileIDs[2] = 1;
        tileIDs[4] = 1;
        tileIDs[6] = 1;
        tileIDs[width + 1] = 1;
        tileIDs[width + 3] = 1;
        tileIDs[width + 5] = 1;
        tileIDs[width + 7] = 1;


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
                        r.drawRect(new Vector2d(x, y), new Vector2d(1, 1), -2);
                    }
                }
            }
        }

    }

}
