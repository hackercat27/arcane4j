package ca.hackercat.arcane.entity;

import ca.hackercat.arcane.entity.component.ACEntityComponent;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class ACEntity {

    public Vector2d lastPosition;
    public Vector2d position;
    public Vector2d velocity;
    public boolean stuck;

    private final List<ACEntityComponent> components = new ArrayList<>();

    public ACEntity(ACEntityComponent... components) {
        this.components.addAll(List.of(components));
    }

    public ACEntity addComponent(ACEntityComponent component) {
        this.components.add(component);
        return this;
    }

}