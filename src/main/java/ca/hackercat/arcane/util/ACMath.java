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

    public static final double PI = 3.141592653589793;
    public static final double TAU = 6.283185307179586;
    public static final double EPSILON = 0.0001;

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

    public static double smooth(double a, double b, double t, double deltaTime) {
        double s = 1 - Math.pow(1 - Math.clamp(t, 0, 1), deltaTime);
        return a + (b - a) * s;
    }

    public static double lerp(double a, double b, double t) {
        return t * (b - a) + a;
    }

    public static Matrix4d getCameraTransform(Vector2d pos, Vector2d scale, double rotation) {
        return getCameraTransform(new Vector3d(pos, 0), new Vector3d(scale, 1), rotation);
    }

    public static Matrix4d getCameraTransform(Vector3d pos, Vector2d scale, double rotation) {
        return getCameraTransform(pos, new Vector3d(scale, 1), rotation);
    }

    public static Matrix4d getCameraTransform(Vector3d pos, Vector3d scale, double rotation) {
        return new Matrix4d()
                .rotateZ(-rotation)
                .scale(scale)
                .translate(pos);
    }


    public static Matrix4d getOBJTransform(Vector2d pos, Vector2d scale, double rotation) {
        return getOBJTransform(new Vector3d(pos, 0), new Vector3d(scale, 1), rotation);
    }

    public static Matrix4d getOBJTransform(Vector3d pos, Vector2d scale, double rotation) {
        return getOBJTransform(pos, new Vector3d(scale, 1), rotation);
    }

    public static Matrix4d getOBJTransform(Vector3d pos, Vector3d scale, double rotation) {

        // kindof a game specific hack but i think i'll just call this intended behaviour
        Vector3d rotationOrigin = new Vector3d(scale).div(2);

        return new Matrix4d()
                .translate(pos)
                .translate(rotationOrigin)
                .rotateZ(rotation)
                .translate(new Vector3d().sub(rotationOrigin))
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

    public static Matrix4d getPerspectiveMatrix(ACEntity camera, ACWindow window, double fov) {
        // i'll bet you 20 bucks this code never gets used for anything
        Matrix4d mat = new Matrix4d();

        double near = 0.1;
        double far = 1000d;

        double ratio = (double) window.getWidth() / window.getHeight();

        double scale = 0.5d;

        return mat.perspective(fov, ratio, near, far)
                  .translate(new Vector3d(0, 0, -1))
                  .rotate(new Quaterniond().rotateAxis(Math.sin(System.currentTimeMillis() / 4000d), 0, 1, 0));
    }

    public static double lerpMod(double a, double b, double t, double min, double max) {
        double range = max - min;

        double c = ((a - min) % range + range) % range;
        double d = ((b - min) % range + range) % range;

        double delta = d - c;
        if (delta > range / 2) {
            delta -= range;
        }
        else if (delta < -range / 2) {
            delta += range;
        }

        double result = c + delta * t;

        return ((result % range + range) % range) + min;
    }
}
