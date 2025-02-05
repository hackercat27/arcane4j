package ca.hackercat.arcane.engine.tiled;

public class TiledMap {

    public String backgroundcolor;
    public int compressionlevel;
    public int width;
    public int height;
    public boolean infinite;
    public TiledLayer[] layers;

    public int nextlayerid;
    public int nextobjectid;

    public String orientation;
    public double parallaxoriginx;
    public double parallaxoriginy;
    public TiledProperty[] properties;
    public String renderorder;
    public String tiledversion;
    public int tilewidth;
    public int tileheight;
    public TiledTileset[] tilesets;
    public String type;
    public String version;
}
