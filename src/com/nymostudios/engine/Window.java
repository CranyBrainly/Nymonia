package com.nymostudios.engine;

import com.nymostudios.engine.listeners.KeyListener;
import com.nymostudios.engine.listeners.MouseListener;
import com.nymostudios.engine.scenes.LevelEditorScene;
import com.nymostudios.engine.scenes.LevelScene;
import com.nymostudios.engine.scenes.Scene;
import com.nymostudios.util.Time;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryStack.*;

public class Window {

    public static Window instance;

    private int width, height;
    private String title;

    private long window;

    private static Scene currentScene = null;

    public float r, b, g;

    public Window() {
        this.width = 1000;
        this.height = 500;
        this.title = "Nymoina v1.0.0";

        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                // currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                // currentScene.init();
                break;
            default:
                assert false : "Unknown Scene '" + newScene + "'.";
                break;
        }
    }

    public static Window get() {
        if (Window.instance == null) {
            Window.instance = new Window();
        }

        return Window.instance;
    }

    public void run() {
        System.out.println("Hello, LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window's callbacks and destroy it //
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // End GLFW and free the error callbacks //
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup error callback //
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialise GLFW //
        if (!glfwInit()) throw new IllegalStateException("Unable to initialise GLFW.");

        // Configure GLFW //
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // Create the window //
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Could not create glfw window.");
        
        // Create listener callbacks //
        glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(window, KeyListener::keyCallback);

        // Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}

        // Make the OpenGL context current //
        glfwMakeContextCurrent(window);
        // Enable v-sync //
        glfwSwapInterval(1);

        // Finally, show the window //
        glfwShowWindow(window);
        
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;


        while(!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
            glClearColor(r, g, b, 1f);
			glfwSwapBuffers(window); // Swap the color buffers

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                System.out.println("MouseX: " + MouseListener.getX());
                System.out.println("MouseY: " + MouseListener.getY());
                System.out.println();
            }

            if (dt >= 0) {
                currentScene.update(dt);
            }

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

    }
}