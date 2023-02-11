package net.cranydev.engine.scenes;

import net.cranydev.engine.entity.GameObject;
import net.cranydev.engine.renderer.Camera;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    private List<GameObject> gameObjects = new ArrayList<>();

    public Scene() {

    }

    public void init() {}

    public abstract void update(float dt);

    public void start() {
        for (GameObject go : gameObjects) {

        }
    }

    public void addGameObjectToScene() {

    }
}
