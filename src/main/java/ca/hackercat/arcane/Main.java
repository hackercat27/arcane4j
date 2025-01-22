package ca.hackercat.arcane;

import ca.hackercat.arcane.engine.ACWindowManager;

public class Main {
    public static void main(String[] args) {

        ACWindowManager m = new ACWindowManager();
        int ret = m.startWindow();

        System.out.println("Exited with exit code " + ret);
    }
}
