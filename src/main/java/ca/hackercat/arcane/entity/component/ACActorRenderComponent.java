package ca.hackercat.arcane.entity.component;

import ca.hackercat.arcane.core.ACRenderer;
import ca.hackercat.arcane.entity.ACEntity;
import org.joml.Vector2d;

import java.awt.Color;

public class ACActorRenderComponent implements ACComponent {

    @Override
    public void render(ACEntity parent, ACRenderer r, double t) {
        r.setColor(Color.WHITE);
        r.drawRect(parent.getPositionI(t), new Vector2d(1, 1));
    }

}
