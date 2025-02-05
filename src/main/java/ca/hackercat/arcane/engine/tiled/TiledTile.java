package ca.hackercat.arcane.engine.tiled;

public class TiledTile {

    public TiledFrame[] animation;
    public int id;
    public String image;
    public int imagewidth;
    public int imageheight;
    public int x;
    public int y;
    public int width;
    public int height;
    public TiledLayer objectgroup;
    public double probability; // editor only
    public TiledProperty[] properties;
    public TiledTerrain[] terrain;
    public String type;

}
