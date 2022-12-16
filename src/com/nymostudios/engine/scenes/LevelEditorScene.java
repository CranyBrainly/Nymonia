package com.nymostudios.engine.scenes;

import com.nymostudios.engine.Window;
import com.nymostudios.engine.listeners.KeyListener;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene{
    private boolean changingScene = false;
    private float timeToChangeScene = 2f;

    public LevelEditorScene() {
        System.out.println("In LevelEditorScene.");
    }

    @Override
    public void update(float dt) {
        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }

        if (changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.get().r -= dt * 5f;
            Window.get().g -= dt * 5f;
            Window.get().b -= dt * 5f;
        } else if (changingScene) {
            Window.changeScene(1);
        }
    }
}
