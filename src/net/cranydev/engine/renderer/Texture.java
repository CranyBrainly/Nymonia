package net.cranydev.engine.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.stb.STBImage.*; 
import static org.lwjgl.opengl.GL11.*;

public class Texture {
    
    private String filepath;
    private int textureID;

    public Texture(String filepath) {
        this.filepath = filepath;

        // Generate texture with GPU //
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Texture params //
        // Repeat in both directions //
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        // Pixilate //
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Stretching
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Shrinking
        
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0),
                            height.get(0), 0, GL_RGB,
                            GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0),
                            height.get(0), 0, GL_RGBA,
                            GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: Unkown number of channels in texture '" + "'.";
            }
        } else {
            assert false : "Error: Failed to open Texture '" + this.filepath + "'";
        }

        stbi_image_free(image);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
