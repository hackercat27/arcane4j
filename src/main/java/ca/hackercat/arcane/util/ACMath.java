package ca.hackercat.arcane.util;

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

}
