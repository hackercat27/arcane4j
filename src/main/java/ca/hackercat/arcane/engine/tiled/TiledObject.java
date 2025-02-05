package ca.hackercat.arcane.engine.tiled;

import org.joml.Vector2d;

public class TiledObject {

    public boolean ellipse;
    public int gid;
    public double width;
    public double height;
    public int id;
    public String name;
    public boolean point;
    public Vector2d[] polygon;
    public Vector2d[] polyline;
    public TiledProperty[] properties;
    public double rotation;
    public String template;
    public TiledText text;
    public String type;
    public boolean visible; // only for editor
    public double x;
    public double y;

}
