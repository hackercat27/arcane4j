package ca.hackercat.arcane.engine;

import ca.hackercat.arcane.core.ACRenderer;

public class ACGameManager {


    private ACStageManager stageManager;

    public ACGameManager() {
        stageManager = new ACStageManager();
    }

    public void update(double deltaTime) {
        stageManager.update(deltaTime);
    }

    public void render(ACRenderer r, double t) {
        stageManager.render(r, t);
    }

}
