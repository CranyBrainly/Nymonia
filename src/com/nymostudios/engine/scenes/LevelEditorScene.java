package com.nymostudios.engine.scenes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL20.*; // Shaders
import static org.lwjgl.opengl.GL30.*; // Rendering

public class LevelEditorScene extends Scene{

    private String vertexShaderSrc = "#version 330 core\n" +
    "\n" +
    "layout (location = 0) in vec3 aPos;\n" +
    "layout (location = 1) in vec4 aColor;\n" +
    "\n" +
    "out vec4 fColor;\n" +
    "\n" +
    "void main() {\n" +
        "fColor = aColor;\n" +
        "gl_Position = vec4(aPos, 1.0);\n" +
    "}";

    private String fragmentShaderSrc = "#version 330 core\n" +
    "\n" +
    "in vec4 fColor;\n" +
    "\n" +
    "out vec4 color;\n" +
    "\n" + 
    "void main() {\n" +
        "color = fColor;\n" +
    "}";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        // Positions          // Colour
        0.5f, -0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f, // Bottom Right  // 0
       -0.5f,  0.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f, // Top Left      // 1
        0.5f,  0.5f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f, // Top Right     // 2
       -0.5f, -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f, // Bottom Left   //3
    };

    // NTS: Counter clockwise order //
    private int[] elementArray = {
        2, 1, 0, // Top Right
        0, 1, 3, // Bottom Left
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        
    }

    @Override
    public void init() {
        //// Compile and link shaders ////

        // Load and compile vertex shader //
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Give shader source code to the GPU to be compiled //
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        // Check for errors //
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: There was an error compile the Vertex shader from shader 'default.glsl'");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Load and compile fragment shader //
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Give shader source code to the GPU to be compiled //
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        // Check for errors //
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: There was an error compile the Fragment shader from shader 'default.glsl'");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // Link the shaders //
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        // Check for errors //
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: There was an error linking the shaders from 'default.glsl'");
            System.out.println(glGetProgramInfoLog(shaderProgram, length));
            assert false : "";
        }

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
        // Bind shader program //
        glUseProgram(shaderProgram);

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
        
        glUseProgram(0);
    }
}
