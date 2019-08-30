package com.branwilliams.bundi.engine.texture;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.branwilliams.bundi.engine.util.TextureUtils.getChannelsFromFormat;
import static com.branwilliams.bundi.engine.util.TextureUtils.toByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

/**
 * Wrapper class for OpenGL textures. <br/>
 *
 * Created by Brandon Williams on 11/10/2016.
 */
public class Texture implements Destructible {

    /**
     * Used to determine a texture's
     * <ul>
     * <li>internal format</li>
     * <li>data format</li>
     * <li>data type</li>
     * </ul>
     * */
    public enum TextureType {
        /**
         * This texture type is a high precision texture (16 bit precision per component)
         * */
        COLOR8(GL_RGBA8, GL_RGBA, GL_UNSIGNED_BYTE),
        /**
         * This texture type is a high precision texture (16 bit precision per component)
         * */
        COLOR16F(GL_RGBA16F, GL_RGBA, GL_FLOAT),
        /**
         * This texture type is a high precision texture (16 bit precision per component)
         * */
        COLOR32F(GL_RGBA32F, GL_RGBA, GL_FLOAT),
        /**
         * Default precision for textures.
         * */
        COLOR(GL_RGBA, GL_RGBA, GL_UNSIGNED_BYTE),
        /**
         * This is for depth textures.
         * */
        DEPTH24(GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_FLOAT),
        /**
         * This is for depth textures.
         * */
        DEPTH32F(GL_DEPTH_COMPONENT32F, GL_DEPTH_COMPONENT, GL_FLOAT),
        /**
         *
         * */
        DEPTH32F_STENCIL8(GL_DEPTH32F_STENCIL8, GL_DEPTH_COMPONENT, GL_FLOAT)
        ;

        /** This is the internal format OpenGL uses to store the texture data. */
        public final int glInternalFormat;

        /** This is the format OpenGL should expect the texture data to be uploaded in. */
        public final int dataFormat;

         /** This is the type of data stored inside of the buffer that is stored in the texture. */
        public final int dataType;

        TextureType(int glInternalFormat, int dataFormat, int dataType) {
            this.glInternalFormat = glInternalFormat;
            this.dataFormat = dataFormat;
            this.dataType = dataType;
        }
    }

    private final int id, target;

    private int width, height;

    /**
     * Creates a texture with the provided target.
     *
     * @param target The texture target. One of:<br>
     *               <table>
     *                   <tr>
     *                       <td>{@link GL11#GL_TEXTURE_1D}</td>
     *                       <td>{@link GL11#GL_TEXTURE_2D}</td>
     *                       <td>{@link GL12#GL_TEXTURE_3D}</td>
     *                       <td>{@link GL31#GL_TEXTURE_RECTANGLE}</td>
     *                   </tr>
     *                   <tr>
     *                       <td>{@link GL30#GL_TEXTURE_CUBE_MAP}</td>
     *                       <td>{@link GL30#GL_TEXTURE_1D_ARRAY}</td>
     *                       <td>{@link GL30#GL_TEXTURE_2D_ARRAY}</td>
     *                       <td>{@link GL45#GL_TEXTURE_CUBE_MAP_ARRAY}</td>
     *                   </tr>
     *               </table>
     * */
    public Texture(int target) {
        this.id = glGenTextures();
        this.target = target;
    }

    /**
     * Creates a texture object from the provided {@link ByteBuffer} with the provided width and height.
     * Mipmaps will be generated if specified and the {@link TextureType} specifies the internal format, expected buffer
     * format, and data type.
     * @param buffer The buffer containing the data of the image.
     * @param width The width of this texture.
     * @param height The height of this texture.
     * @param mipmaps Creates mip maps for this image if true.
     * @param textureType Determines the internal format OpenGL uses to store this image data.
     * */
    public Texture(ByteBuffer buffer, int width, int height, boolean mipmaps, TextureType textureType) {
        this(GL_TEXTURE_2D);

        this.width = width;
        this.height = height;

        bind();

        glTexImage2D(target, 0, textureType.glInternalFormat, width, height, 0, textureType.dataFormat, textureType.dataType, buffer);

        if (mipmaps) {
            generateMipmaps();
        }

        // Scale the images linearly
        linearFilter(mipmaps);

        unbind(this);

        // be gone, demon
        MemoryUtil.memFree(buffer);
    }

    public Texture(ByteBuffer buffer, int width, int height, boolean mipmaps) {
        this(buffer, width, height, mipmaps, TextureType.COLOR);
    }

    public Texture(int[] pixels, int width, int height, boolean mipmaps, TextureType textureType) {
        this(toByteBuffer(pixels, width, height), width, height, mipmaps, textureType);
    }

    public Texture(int[] pixels, int width, int height, boolean mipmaps) {
        this(pixels, width, height, mipmaps, TextureType.COLOR);
    }

    /**
     * Creates an RGBA texture from the provided color vector.
     * The color values are expected to be between 0F ~ 1F.
     * @param color A vector whose xyzw values represent the rgba values of a color.
     * @param width The width of this texture.
     * @param height The height of this texture.
     * @param mipmaps Creates mip maps for this image if true.
     * */
    public Texture(Vector4f color, int width, int height, boolean mipmaps) {
        this(toByteBuffer((int) (Mathf.clamp(color.x * 255F, 255F, 0F)),
                (int) (Mathf.clamp(color.y * 255F, 255F, 0F)),
                (int) (Mathf.clamp(color.z * 255F, 255F, 0F)),
                (int) (Mathf.clamp(color.w * 255F, 255F, 0F)), width, height),
                width, height, mipmaps, TextureType.COLOR);
    }

    /**
     * Creates an RGBA texture with the provided rgba values.
     * The RGBA values are expected to be between 0 ~ 255.
     * @param r The red value for this texture.
     * @param g The green value for this texture.
     * @param b The blue value for this texture.
     * @param a The alpha value for this texture.
     * @param width The width of this texture.
     * @param height The height of this texture.
     * @param mipmaps Creates mip maps for this image if true.
     * */
    public Texture(int r, int g, int b, int a, int width, int height, boolean mipmaps) {
        this(toByteBuffer(r, g, b, a, width, height), width, height, mipmaps, TextureType.COLOR);
    }

    /**
     * Constructs an empty texture object of the given texture type.
     * */
    public Texture(int width, int height, boolean mipmaps, TextureType textureType) {
        this((ByteBuffer) null, width, height, mipmaps, textureType);
    }

    /**
     * Constructs an empty texture object with internal type of RGBA.
     * @param width The width of this texture.
     * @param height The height of this texture.
     * @param mipmaps Creates mip maps for this image if true.
     * */
    public Texture(int width, int height, boolean mipmaps) {
        this((ByteBuffer) null, width, height, mipmaps, TextureType.COLOR);
    }

    /**
     * Creates a texture from the provided {@link TextureData} using the texture type
     * of {@link TextureType#COLOR}.
     * See {@link Texture#Texture(TextureData, TextureType, boolean)}.
     * */
    public Texture(TextureData textureData, boolean mipmaps) {
        this(textureData, TextureType.COLOR, mipmaps);
    }

    /**
     * Uploads the data from the provided {@link TextureData} into an OpenGL texture.
     * @param textureData The image data used to create this texture.
     * @param textureType Determines the internal format OpenGL uses to store this image data.
     * @param mipmaps Creates mip maps for this image if true.
     * */
    public Texture(TextureData textureData, TextureType textureType, boolean mipmaps) {
        this(GL_TEXTURE_2D);

        this.width = textureData.getWidth();
        this.height = textureData.getHeight();

        bind();

        // TODO hmmm?
        if (textureData.getChannels() == 3 && (width & 3) != 0) {
            glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
        }
        //glPixelStorei(GL_UNPACK_ALIGNMENT,1);

        glTexImage2D(target, 0, textureType.glInternalFormat, width, height, 0, textureData.getFormat(),
                textureType.dataType, textureData.getData());

        if (mipmaps) {
            generateMipmaps();
        }

        // Scale the images linearly
        linearFilter(mipmaps);

        unbind(this);

        // destroy the image data so it doesn't take up memory.
        textureData.destroy();
    }

    /**
     * Binds this texture for use.
     * */
    public Texture bind() {
        glBindTexture(target, id);
        return this;
    }

    /**
     * Sets this textures wrapping mode to {@link GL12#GL_REPEAT}.
     * */
    public Texture repeatEdges() {
        // Allow edges to repeat
        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_REPEAT);
        return this;
    }

    /**
     * Sets this textures wrapping mode to {@link GL12#GL_CLAMP_TO_EDGE}.
     * */
    public Texture clampToEdges() {
        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        return this;
    }

    /**
     * Sets this textures filter mode to {@link GL11#GL_LINEAR}.
     * */
    public Texture linearFilter(boolean mipmaps) {
        // Scale the images linearly
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, mipmaps ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
        return this;
    }

    /**
     * Sets this textures filter mode to {@link GL11#GL_NEAREST}.
     * */
    public Texture nearestFilter() {
        return nearestFilter(false);
    }

    /**
     * Sets this textures filter mode to {@link GL11#GL_NEAREST}.
     * */
    public Texture nearestFilter(boolean mipmaps) {
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, mipmaps ? GL_NEAREST_MIPMAP_NEAREST : GL_NEAREST);
        return this;
    }

    public Texture magFilter(int filter) {
        glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filter);
        return this;
    }

    public Texture minFilter(int filter) {
        glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filter);
        return this;
    }

    /**
     * Generates mipmaps for this texture.
     * */
    public void generateMipmaps() {
        glGenerateMipmap(this.target);
    }

    public void setMaxMipMapLevel(int level) {
        glTexParameteri(this.target, GL_TEXTURE_MAX_LEVEL, level);
    }

    public void uploadMipMap(int level, TextureData textureData) {
//        glTexSubImage2D(target,
//                level,
//                xoffset, yoffset, textureData.getWidth(), textureData.getHeight(),
//                textureData.getFormat(), TextureType.COLOR.dataType, textureData.getData());
        glTexImage2D(target, level, TextureType.COLOR.glInternalFormat, textureData.getWidth(), textureData.getHeight(),
                0, textureData.getFormat(), TextureType.COLOR.dataType, textureData.getData());

//        glTexImage2D(target, level, textureData.getFormat(), textureData.getWidth(), textureData.getHeight(), 0,
//                TextureType.COLOR.glInternalFormat, TextureType.COLOR.dataType, textureData.getData());
    }

    /**
     * Unbinds this texture.
     * */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Unbinds the provided texture.
     * */
    public static void unbind(Texture texture) {
        glBindTexture(texture.target, 0);
    }

    public int getTarget() {
        return target;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TextureData getBuffer() {
        return getBuffer(0);
    }

    /**
     * @return A ByteBuffer containing the data of this texture.
     * */
    public TextureData getBuffer(int mip) {

        bind();
        int format = glGetTexLevelParameteri(target, mip, GL_TEXTURE_INTERNAL_FORMAT);
        int width  = glGetTexLevelParameteri(target, mip, GL_TEXTURE_WIDTH);
        int height = glGetTexLevelParameteri(target, mip, GL_TEXTURE_HEIGHT);
        int channels = getChannelsFromFormat(format);

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * channels);
        glGetTexImage(GL_TEXTURE_2D, mip, format, GL_UNSIGNED_BYTE, buffer);
        unbind(this);

        return new TextureData(width, height, channels, format, buffer);
    }

    /**
     * @return This textures internal format.
     * */
    public int getFormat() {
        return glGetTexLevelParameteri(target, 0, GL_TEXTURE_INTERNAL_FORMAT);
    }

    @Override
    public void destroy() {
        glDeleteTextures(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, width, height);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Texture) {
            Texture texture = (Texture) object;
            // Comparing the width and height for the two textures seems a bit redundant when their ids match...
            return texture.id == this.id;
        } else
            return super.equals(object);
    }

    @Override
    public String toString() {
        return "[target=" + getTarget(target) + ", id=" + id + ", width=" + width + ", height=" + height + "]";
    }

    /**
     * @return A String representation of a textures target.
     * */
    private static String getTarget(int target) {
        switch (target) {
            case GL_TEXTURE_CUBE_MAP:
                return "cubemap";
            case GL_TEXTURE_2D:
                return "texture2D";
            case GL_TEXTURE_2D_ARRAY:
                return "arrayTexture";
            default:
                return "unknown";
        }
    }
}