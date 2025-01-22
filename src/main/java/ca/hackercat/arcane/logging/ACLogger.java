package ca.hackercat.arcane.logging;

import java.io.PrintStream;

public class ACLogger {

    public static PrintStream out = System.out;
    public static PrintStream err = System.out;

    public static void error(String message, Object... args) {
        err.printf(message + "\n", args);
    }

    public static void log(Object o, Object... args) {
        log(String.valueOf(o));
    }
    public static void log(String message, Object... args) {
        out.printf(message + "\n", args);
    }

}
