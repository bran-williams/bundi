package com.branwilliams.bundi.engine.texture;

import static java.lang.Integer.max;
import static java.lang.Math.log;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 *
 * */
public class ArrayTexture extends Texture {

    public ArrayTexture(TextureType type, TextureData... images) {
        super(GL_TEXTURE_2D_ARRAY);
        int width = images[0].getWidth(), height = images[0].getHeight();

        // Ensure same dimensions for each image.
        for (int i = 1; i < images.length; i++) {
            if (images[i].getWidth() != width || images[i].getHeight() != height) {
                destroy();
                throw new IllegalArgumentException("All images must have the same dimensions!");
            }
        }
        bind();

        //glPixelStorei(GL_UNPACK_ALIGNMENT,1);

        // old
        //glTexImage3D(GL_TEXTURE_2D_ARRAY, 1, type.glInternalFormat, width, height, images.length, 0,
        //        type.dataFormat, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        // opengl 4.3 and above
        int maxMips = (int) log(max(width,height)) + 1;
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, maxMips, type.glInternalFormat, width, height, images.length);

        for (int i = 0; i < images.length; i++) {
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY,
                    0,
                    0,0, i, // update array slice i
                    width, height,1, // only one array slice updated
                    images[i].getFormat(),
                    type.dataType,
                    images[i].getData());
        }

        repeatEdges();

        unbind(this);
    }
}
