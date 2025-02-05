package ca.hackercat.arcane.engine.tiled;

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
}
