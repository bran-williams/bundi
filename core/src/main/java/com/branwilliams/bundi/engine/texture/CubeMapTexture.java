package com.branwilliams.bundi.engine.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

/**
 * Created by Brandon Williams on 1/4/2018.
 */
public class CubeMapTexture extends Texture {

    public CubeMapTexture(int width, int height, TextureData... textureData) {
        this(width, height, null, textureData);
    }


    public CubeMapTexture(int width, int height, CubeMapPosition[] positions, TextureData... textureData) {
        super(GL_TEXTURE_CUBE_MAP);

        if (textureData == null) {
            destroy();
            throw new NullPointerException("image data cannot be null!");
        }

        if (positions == null)
            positions = CubeMapPosition.values();

        //glActiveTexture(GL_TEXTURE0);
        bind();

        for (int i = 0; i < textureData.length; i++) {
            glTexImage2D(positions[i].glTarget, 0, GL_RGBA8, width, height, 0, textureData[i].getFormat(), GL_UNSIGNED_BYTE, textureData[i].getData());
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_LEVEL, 0);
        unbind(this);
    }
    
    public enum CubeMapPosition {
        RIGHT_FACE(GL_TEXTURE_CUBE_MAP_POSITIVE_X),
        LEFT_FACE(GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
        TOP_FACE(GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
        BOTTOM_FACE(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
        FRONT_FACE(GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
        BACK_FACE(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z);

        private final int glTarget;

        CubeMapPosition(int glTarget) {
            this.glTarget = glTarget;
        }
    }
}
