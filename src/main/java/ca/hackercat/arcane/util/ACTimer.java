package ca.hackercat.arcane.util;

public class ACTimer implements ACUpdatable {

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
    
    public void reset() {
        this.seconds = 0;
    }
    
    public boolean triggered() {
        return seconds >= activationThreshold;
    }

}
