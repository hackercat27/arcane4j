package ca.hackercat.arcane.engine;

import java.util.ArrayList;
import java.util.List;

public class ACThreadManager {

    // "struct"
    private static class ACThread {
        Thread thread;
        int id;

        public ACThread(Thread thread, int id) {
            this.thread = thread;
            this.id = id;
        }
    }

    private static int gid = 0;
    private static final List<ACThread> threads = new ArrayList<>();

    public static Thread execute(Runnable runnable) {
        int id = gid++;
        Thread t = new Thread(runnable, String.format("arcane-worker%d", id));
        threads.add(new ACThread(t, id));
        t.start();
        return t;
    }

    public static void blockUntilTermination(Thread... threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void throwIfNotMainThread() {
        if (!isMainThread()) {
            throw new RuntimeException(new IllegalCallerException("Cannot run on main thread."));
        }
    }

    public static boolean isMainThread() {
        String threadName = Thread.currentThread().getName();
        return threadName.matches("main");
    }

}
