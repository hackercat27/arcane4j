package ca.hackercat.arcane.logging;

import java.io.PrintStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ACLogger {

    public static final PrintStream err;
    public static final PrintStream warn;
    public static final PrintStream out;

    static {
        out = System.out;
        warn = System.out;
        err = System.out;
    }

    public static void log(ACLevel level, String message, Object... args) {
        log(level, String.format(message, args));
    }

    public static void log(ACLevel level, Object o) {
        write(level, String.valueOf(o));
    }

    private static String getTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    private static void write(ACLevel level, String message) {

        ACLevel minLevel;
        try {

            String p = System.getProperty("hackercat.arcane.loglevel");

            if (p == null) {
                throw new IllegalArgumentException("Property not set");
            }

            minLevel = ACLevel.valueOf(p.toUpperCase());
        }
        catch (IllegalArgumentException ignored) {
            minLevel = ACLevel.INFO; // default to just info
        }

        PrintStream stream = switch (level) {
            case ACLevel.ERROR -> err;
            case ACLevel.WARN -> warn;
            default -> out;
        };

        if (level.getPriority() < minLevel.getPriority()) {
            return;
        }

        String ansi = switch (level) {
            case ACLevel.DEBUG -> "\u001b[90m";
            case ACLevel.VERBOSE -> "\u001b[90m";
            case ACLevel.WARN -> "\u001b[93m";
            case ACLevel.ERROR -> "\u001b[91m";
            case ACLevel.FATAL -> "\u001b[91;41m";
            default -> "\u001b[0m";
        };

        stream.print(ansi);
        stream.printf("[%s] [%s/%s]: %s\n", getTime(), Thread.currentThread().getName(), level.name(), message);
    }

    public static String exceptionString(Exception e) {
        return e.getMessage();
    }
}
