package ca.hackercat.arcane.core;

public interface ACGameManager {

    void update(double deltaTime);

    void render(ACRenderer r, double t);
}
