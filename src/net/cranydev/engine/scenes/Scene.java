package net.cranydev.engine.scenes;

import net.cranydev.engine.renderer.Camera;

public abstract class Scene {

    protected Camera camera;

    public Scene() {

    }

    public void init() {}

    public abstract void update(float dt);
}
