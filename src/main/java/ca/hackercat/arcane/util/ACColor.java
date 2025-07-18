package ca.hackercat.arcane.util;

import org.joml.Vector3d;
import org.joml.Vector4d;

public class ACColor {

    private final Vector4d argb = new Vector4d();

    public static ACColor WHITE = new ACColor(0xFFFFFF);
    public static ACColor RED = new ACColor(0xFF0000);
    public static ACColor GREEN = new ACColor(0x00FF00);
    public static ACColor BLUE = new ACColor(0x0000FF);
    public static ACColor CYAN = new ACColor(0x00FFFF);
    public static ACColor YELLOW = new ACColor(0xFFFF00);
    public static ACColor MAGENTA = new ACColor(0xFF00FF);
    public static ACColor BLACK = new ACColor(0x000000);

    public ACColor(Vector4d argb) {
        this.argb.set(argb);
    }

    public ACColor(Vector3d rgb) {
        this.argb.set(rgb, 1);
    }

    public ACColor(int argb, boolean useAlpha) {
        double a = ((argb >> 24) & 0xFF) / 255d;
        double r = ((argb >> 16) & 0xFF) / 255d;
        double g = ((argb >> 8 ) & 0xFF) / 255d;
        double b = ((argb      ) & 0xFF) / 255d;
        this.argb.x = r;
        this.argb.y = g;
        this.argb.z = b;
        this.argb.w = useAlpha? a : 1;
    }

    public ACColor(int rgb) {
        double r = ((rgb >> 16) & 0xFF) / 255d;
        double g = ((rgb >> 8 ) & 0xFF) / 255d;
        double b = ((rgb      ) & 0xFF) / 255d;
        argb.x = r;
        argb.y = g;
        argb.z = b;
        argb.w = 1;
    }

    public double getRed() {
        return argb.x;
    }

    public double getGreen() {
        return argb.y;
    }

    public double getBlue() {
        return argb.z;
    }

    public double getAlpha() {
        return argb.w;
    }

    public Vector3d getRGB() {
        return new Vector3d(getRed(), getGreen(), getBlue());
    }

    public Vector4d getARGB() {
        return new Vector4d(getRed(), getGreen(), getBlue(), getAlpha());
    }
}
