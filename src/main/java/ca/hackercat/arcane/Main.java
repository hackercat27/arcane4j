package ca.hackercat.arcane;

import ca.hackercat.arcane.core.ACWindowManager;

public class Main {
    public static void main(String[] args) {

        ACWindowManager m = new ACWindowManager();
        int ret = m.startWindow();

        System.out.println("Main thread finished with exit code " + ret);
    }
}
