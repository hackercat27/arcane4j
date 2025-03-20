package ca.hackercat.arcane.logging;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class ACLogger {

    public static final PrintStream err;
    public static final PrintStream warn;
    public static final PrintStream out;

    static {
        AnsiConsole.systemInstall();
        out = System.out;
        warn = System.out;
        err = System.out;
    }

    public enum Level {
        INFO(BLUE),
        WARN(YELLOW),
        ERROR(RED);
        public final Ansi.Color color;
        Level(Ansi.Color color) {
            this.color = color;
        }
    }

    public static void error(Object o) {
        write(Level.ERROR, String.valueOf(o));
    }

    public static void error(String message, Object... args) {
        error(String.format(message, args));
    }

    public static void warn(Object o) {
        write(Level.WARN, String.valueOf(o));
    }

    public static void warn(String message, Object... args) {
        warn(String.format(message, args));
    }

    public static void log(Object o) {
        write(Level.INFO, String.valueOf(o));
    }

    public static void log(String message, Object... args) {
        log(String.format(message, args));
    }

    private static String getTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    private static void write(Level level, String message) {
        out.println(
                ansi().a(String.format("[%s] [%s/", getTime(), Thread.currentThread().getName()))
                      .fg(level.color)
                      .a(level.name())
                      .reset()
                      .a("]: ")
                      .a(message));
    }

}
