package com.nymostudios.engine.renderer;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL20.*; // Shaders

public class Shader {

    private int shaderProgram;
    private String vertexSrc, fragmentSrc;
    private String filepath;

    private boolean beingUsed = false;

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
        if (!beingUsed) {
            glUseProgram(shaderProgram);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4f) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4f.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3f) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3f.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3f) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2f) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        glUniform2f(varLocation, vec2f.x, vec2f.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgram, varName);
        use();
        glUniform1i(varLocation, val);
    }
}
