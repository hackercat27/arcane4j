package ca.hackercat.arcane.engine;

import java.util.ArrayList;
import java.util.List;

public class ACThreadManager {

    // "struct"
    private static class ACThread {
        Thread thread;
        int id;
        boolean started;

        public ACThread(Thread thread, int id) {
            this.thread = thread;
            this.id = id;
        }

        public boolean halted() {
            return started && !thread.isAlive();
        }
    }

    private static int gid = 0;
    private static final List<ACThread> threads = new ArrayList<>();

    public static Thread execute(Runnable runnable) {
        int id = gid++;
        Thread t = new Thread(runnable, String.format("arcane-worker%d", id));
        ACThread threadStruct = new ACThread(t, id);
        threads.add(threadStruct);
        t.start();
        threadStruct.started = true;
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
            throw new RuntimeException(new IllegalCallerException("Cannot run on non-main thread."));
        }
    }

    public static void throwIfMainThread() {
        if (isMainThread()) {
            throw new RuntimeException(new IllegalCallerException("Not allowed to run on main thread."));
        }
    }

    public static boolean isMainThread() {
        String threadName = Thread.currentThread().getName();
        return threadName.matches("main");
    }

}
