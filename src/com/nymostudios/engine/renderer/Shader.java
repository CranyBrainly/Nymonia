package com.nymostudios.engine.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*; // Shaders

public class Shader {

    private int shaderProgram;
    private String vertexSrc, fragmentSrc;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;

        try {
            // *                  Documentation
            // *    This script allows the user to input a Shader
            // *       file containing both the Vertex and the
            // *     fragment shader without having to write the
            // *             two in two seperate files.

            // Open the shader file //
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern //
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second patten //
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSrc = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSrc = splitString[1];
            } else {
                throw new IOException("Error: Unexpected token '" + firstPattern + "'.");
            }
            
            if (secondPattern.equals("vertex")) {
                vertexSrc = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSrc = splitString[2];
            } else {
                throw new IOException("Error: Unexpected token '" + secondPattern + "'.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Failed to open shader '" + this.filepath + "'.";
        }

        // System.out.println(vertexSrc);
        // System.out.println(fragmentSrc);
    }

    public void compile() {
        int vertexID, fragmentID;

        // Load and compile vertex shader //
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Give shader source code to the GPU to be compiled //
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);

        // Check for errors //
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: There was an error compile the Vertex shader from shader '" + filepath + "'.");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Load and compile fragment shader //
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Give shader source code to the GPU to be compiled //
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);

        // Check for errors //
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: There was an error compile the Fragment shader from shader '" + filepath + "'.");
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
            System.out.println("Error: There was an error linking the shaders from '" + filepath + "'.");
            System.out.println(glGetProgramInfoLog(shaderProgram, length));
            assert false : "";
        }
    }

    public void use() {
        glUseProgram(shaderProgram);
    }

    public void detach() {
        glUseProgram(0);
    }
}
