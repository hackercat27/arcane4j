package ca.hackercat.arcane.logging;

import java.io.PrintStream;

public class ACLogger {

    public static final PrintStream err;
    public static final PrintStream warn;
    public static final PrintStream out;

    static {
        out = System.out;
        warn = System.out;
        err = System.out;
    }

    public enum Level {
        INFO,
        WARN,
        ERROR
    }

    public static void error(Object o, Object... args) {
        write(Level.ERROR, String.valueOf(o), args);
    }

    public static void error(String message, Object... args) {
        write(Level.ERROR, message, args);
    }

    public static void warn(Object o, Object... args) {
        write(Level.WARN, String.valueOf(o), args);
    }

    public static void warn(String message, Object... args) {
        write(Level.WARN, message, args);
    }

    public static void log(Object o, Object... args) {
        write(Level.INFO, String.valueOf(o), args);
    }

    public static void log(String message, Object... args) {
        write(Level.INFO, message, args);
    }

    private static void write(Level level, String message, Object... args) {
        out.printf(message, args);
        out.println();
    }

}
