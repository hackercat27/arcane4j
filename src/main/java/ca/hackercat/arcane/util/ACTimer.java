package ca.hackercat.arcane.util;

import ca.hackercat.arcane.core.ACRenderer;

public class ACTimer implements ACCoroutine {

    private double seconds;
    private double activationThreshold;

    public ACTimer() {
        this(-1);
    }

    public ACTimer(double activationThreshold) {
        this.activationThreshold = activationThreshold;

        ACGenericManager.register(this);
    }

    public double get() {
        return seconds;
    }

    @Override
    public void update(double deltaTime) {
        seconds += deltaTime;
    }

    @Override
    public void render(ACRenderer r, double t) {}

    public void reset() {
        this.seconds = 0;
    }
    
    public boolean triggered() {
        return seconds >= activationThreshold;
    }

}
