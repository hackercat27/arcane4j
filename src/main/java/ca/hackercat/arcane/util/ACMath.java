package ca.hackercat.arcane.util;

import ca.hackercat.arcane.core.io.ACWindow;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * This class does NOT redefine math functions, only small helper functions.
 */
public class ACMath {

    public static double PI = 3.141592653589793;
    public static double TAU = 6.283185307179586;
    public static double EPSILON = 0.0001;

    public static double approach(double value, double target, double delta) {
        double trueDelta = target - value;
        if (Math.abs(value - target) < EPSILON) {
            return value;
        }

        // is delta too big?
        if (Math.abs(delta) > Math.abs(trueDelta)) {
            return value + trueDelta;
        }
        // else its smaller anyways so just add it
        return value + Math.copySign(delta, trueDelta);
    }

    public static Matrix4d getTransform(Vector2d pos, Vector2d scale) {
        return getTransform(new Vector3d(pos, 0), new Vector3d(scale, 1));
    }

    public static Matrix4d getTransform(Vector3d pos, Vector2d scale) {
        return getTransform(pos, new Vector3d(scale, 1));
    }

    public static Matrix4d getTransform(Vector3d pos, Vector3d scale) {
        return new Matrix4d()
                .scale(scale)
                .translate(pos);
    }

    public static Matrix4d getOrthographicMatrix(ACEntity camera, ACWindow window) {
        Matrix4d mat = new Matrix4d();

        double near = -50d;
        double far = 50d;

        double ratio = (double) window.getWidth() / window.getHeight();

        double scale = 0.5d;

        return mat.ortho(-scale * ratio, scale * ratio, -scale, scale, near, far);
    }

    public static Matrix4d getPerspectiveMatrix(ACEntity camera, ACWindow window, double fov) {
        // i'll bet you 20 bucks this code never gets used for anything
        Matrix4d mat = new Matrix4d();

        double near = 0.1;
        double far = 1000d;

        double ratio = (double) window.getWidth() / window.getHeight();

        double scale = 0.5d;

        return mat.perspective(fov, ratio, near, far)
                  .translate(new Vector3d(0, 0, -1))
                  .rotate(new Quaterniond().rotateAxis(0.7, 0, 1, 0));
    }
}
