package ca.hackercat.arcane.util;

import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * This class does NOT redefine math functions, only small helper functions.
 */
public class ACMath {

    public static double PI = 3.141592653589793;
    public static double TAU = 6.283185307179586;

    public static double approach(double value, double target, double maxDelta) {
        double delta = target - value;

        if (Math.abs(maxDelta) < delta) {
            return value + Math.copySign(delta, Math.abs(maxDelta));
        }
        return value + delta;
    }

    public static Matrix4d getTransform(Vector2d pos, Vector2d scale) {
        return getTransform(new Vector3d(pos, 0), new Vector3d(scale, 1));
    }

    public static Matrix4d getTransform(Vector3d pos, Vector2d scale) {
        return getTransform(pos, new Vector3d(scale, 1));
    }

    public static Matrix4d getTransform(Vector3d pos, Vector3d scale) {
        return new Matrix4d()
                .translate(pos)
                .scale(scale);
    }

    public static Matrix4d getOrthographicMatrix(ACEntity camera, ACWindow window) {
        Matrix4d mat = new Matrix4d();

        double near = -50d;
        double far = 50d;

        double ratio = (double) window.getWidth() / window.getHeight();

        double scale = 0.5d;

        return mat.ortho(-scale * ratio, scale * ratio, -scale, scale, near, far);
    }
}
