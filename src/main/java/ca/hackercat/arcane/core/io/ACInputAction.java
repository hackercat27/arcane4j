package ca.hackercat.arcane.core.io;

public enum ACInputAction {
    RIGHT("d"),
    LEFT("a"),
    FAST_FALL("s"),
    CROUCH("s"),
    JUMP("space");

    private final String defaultValue;

    ACInputAction(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
