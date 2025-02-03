package ca.hackercat.arcane.core;

import ca.hackercat.arcane.util.ACStringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

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

    private static final List<ACThread> threads = new ArrayList<>();

    private static int getFirstAvailableID() {
        threads.sort(Comparator.comparingInt(o -> o.id));
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).id != i) {
                return i;
            }
        }
        return threads.size();
    }

    public static void clean() {

        List<ACThread> terminated = new ArrayList<>();
        synchronized (threads) {
            for (ACThread thread : threads) {
                if (thread.halted()) {
                    terminated.add(thread);
                }
            }
            threads.removeAll(terminated);
        }

    }

    public static <T> Thread execute(T value, Consumer<T> consumer) {
        return execute(() -> consumer.accept(value));
    }

    public static Thread execute(Runnable runnable) {
        return execute(runnable, "arcane-worker${custom.thread_id}");
    }

    public static Thread execute(Runnable runnable, String name) {
        Thread t;
        synchronized (threads) {
            int id = 1;
            t = new Thread(runnable, ACStringUtils.resolve(name, String.format("custom.thread_id=%d", id)));
            ACThread threadStruct = new ACThread(t, id);
            threads.add(threadStruct);
            t.start();
            threadStruct.started = true;
        }
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

    public static void parallelFor(int iterationCount, Consumer<Integer> body) {
        Thread[] threads = new Thread[iterationCount];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = execute(i, body);
        }

        blockUntilTermination(threads);
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
