package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;

public interface ACGameManager {

    void update(double deltaTime);

    void render(ACRenderer r, double t);
}
