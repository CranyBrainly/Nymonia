package com.nymostudios.engine.scenes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import com.nymostudios.engine.renderer.Camera;
import com.nymostudios.engine.renderer.Shader;
import com.nymostudios.util.Time;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private float[] vertexArray = {
        // Positions             // Colour
        100.5f,  -0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f, // Bottom Right  // 0
         -0.5f, 100.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f, // Top Left      // 1
        100.5f, 100.5f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f, // Top Right     // 2
         -0.5f,  -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f, // Bottom Left   // 3
    };

    // NTS: Counter clockwise order //
    private int[] elementArray = {
        2, 1, 0, // Top Right
        0, 1, 3, // Bottom Left
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    public LevelEditorScene() {
    }

    @Override
    public void init() {

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("src/assets/shaders/default.glsl");
        defaultShader.compile();

        //// Generate VAO, VBO, and EBO buffer objects ////

        // Generate VAO buffer object //
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices //
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip(); // Upload buffer

        // Create VBO buffer object //
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Upload buffer

        // Create indicies buffer //
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // Create EBO buffer object //
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Vertex Attrib pointers //
        int positionsSize = 3;
        int colourSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colourSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colourSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // System.out.println("" + (1 / dt) + "FPS"); // FPS counter

        this.camera.position.x -= dt * 50f;
        this.camera.position.y -= dt * 50f;

        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind VAO //
        glBindVertexArray(vaoID);

        // Enable vertex attrib pointer //
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything //
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        
        defaultShader.detach();
    }
}
