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

            String p = System.getProperty("hackercat.logging.level");

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

        stream.printf("[%s] [%s/%s]: %s\n", getTime(), Thread.currentThread().getName(), level.name(), message);
    }
}
