package net.cranydev.engine.entity;

import net.cranydev.engine.entity.Component;
import java.util.List;

public class GameObject {

    public String name;
    private List<Component> components;


    public GameObject(String name) {
        this.name = name;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (c.getClass().isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);    
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting Component";
                }
            } 
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> compoenentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (compoenentClass.isAssignableFrom(components.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }
}
