package ca.hackercat.arcane.engine.tiled;

import org.joml.Vector2i;

public class TiledLayer {

    public TiledChunk[] chunks;
    public String compression;
    public int[] data;
    public String draworder;
    public String encoding;
    public int width;
    public int height;
    public int id;
    public String image;
    public int imagewidth;
    public int imageheight;
    public TiledLayer[] layers;
    public boolean locked;
    public String name;
    public TiledObject[] objects;
    public double offsetx;
    public double offsety;
    public double opacity = 1;
    public double parallaxx = 1;
    public double parallaxy = 1;
    public TiledProperty[] properties;
    public boolean repeatx;
    public boolean repeaty;
    public int startx;
    public int starty;
    public String tintcolor;
    public String transparentcolor;
    public String type;
    public boolean visible; // only intended for editor
    public int x;
    public int y;

    public int[] getData() {

        if (chunks == null) {
            return data;
        }

        Vector2i offset = getOffset();

        int[] chunkData = new int[width * height];
        for (TiledChunk chunk : chunks) {

            for (int y = 0; y < chunk.height; y++) {
                for (int x = 0; x < chunk.width; x++) {
                    int gx = (chunk.x + offset.x) + x;
                    int gy = (chunk.y + offset.y) + y;

                    int gi = gx + (height - 1 - gy) * width;
                    int i = x + y * chunk.width;

                    chunkData[gi] = chunk.data[i];
                }
            }
        }

        return chunkData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Vector2i getOffset() {
        if (chunks == null) {
            return new Vector2i();
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        for (TiledChunk chunk : chunks) {
            minX = Math.min(chunk.x, minX);
            minY = Math.min(chunk.y, minY);
        }

        return new Vector2i(-minX, -minY);
    }
}
