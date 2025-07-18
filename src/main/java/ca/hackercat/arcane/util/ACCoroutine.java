package ca.hackercat.arcane.util;

import ca.hackercat.arcane.core.ACRenderer;

public interface ACCoroutine {
    void update(double deltaTime);
    void render(ACRenderer r, double t);
}
