package ca.hackercat.arcane.logging;

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

    private static String getTime() {
        return "time here"; // TODO: implement
    }

    private static void write(Level level, String message, Object... args) {
        out.println(
                ansi().a(String.format("[%s] [%s/", getTime(), Thread.currentThread().getName()))
                      .fg(level.color)
                      .a(level.name())
                      .reset()
                      .a("]: ")
                      .a(String.format(message, args)));
    }

}
