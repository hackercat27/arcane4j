package ca.hackercat.arcane.core.asset;

public interface ACAsset {
    boolean registered();
    void register();
    boolean isDisposable();
    void dispose();
}
