package com.branwilliams.bundi.engine.shader.dynamic;

import com.branwilliams.bundi.engine.shader.VertexArrayObject;
import com.branwilliams.bundi.engine.shader.VertexBufferObject;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

/**
 * Updates a {@link VertexArrayObject VAO} with an interleaved {@link VertexBufferObject VBO} that follows a given
 * {@link VertexFormat}. This useful for any rendering that is dynamic.
 * */
public class DynamicVAO {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

    private final int BUFFER_USAGE = GL_DYNAMIC_DRAW;

    // Default number of elements allocated for the temporary buffer.
    private final int DEFAULT_BUFFER_ELEMENT_COUNT = 6;

    // Factor used to resize the temp buffer when it needs to be resized.
    private final float RESIZE_FACTOR = 2;

    /**
     * The vertex format used by this dynamicVao.
     * */
    private final VertexFormat vertexFormat;

    // default buffer size for the temporary buffer.
    private int defaultBufferSize;

    /**
     * Vao used to render by this dynamicVao.
     * */
    private VertexArrayObject vao;

    /**
     * Vbo used to store vertices, texture coords, and colors within the vao of this dynamicVao.
     * */
    private VertexBufferObject vbo;

    // Each vertex is stored temporarily in this array.
    private FloatBuffer tempBuffer;

    // The index this dynamicVao is at within the vertices array.
    private int vertexCount = 0;

    // The index of the vertex element that this dynamicVao is at.
    private int vertexFormatIndex = 0;

    // true when the temp buffer has been modified and needs to re-upload to the vbo.
    private boolean dirty = false;

    // true if the size of the temp buffer has been changed.
    private boolean resized = false;

    /**
     * Creates a DynamicVAO with the vertex format of {@link VertexFormat#POSITION_UV_COLOR}.
     * <br/>
     * See {@link DynamicVAO#DynamicVAO(VertexFormat)}.
     * */
    public DynamicVAO() {
        this(VertexFormat.POSITION_UV_COLOR);
    }

    /**
     * Creates the {@link VertexArrayObject VAO} and {@link VertexBufferObject VBO} that this DynamicVAO will use.
     * @param vertexFormat The {@link VertexFormat} which this dynamicVao will follow.
     * */
    public DynamicVAO(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        initVao();
        reset();
    }

    /**
     * Creates the VAO and VBO for this dynamicVao and assigns the attributes (to the vao) specified by this vertex
     * format.
     * */
    private void initVao() {
        vao = new VertexArrayObject();
        vbo = new VertexBufferObject();

        defaultBufferSize = DEFAULT_BUFFER_ELEMENT_COUNT * vertexFormat.getElementSize();

        // Create the temp buffer and initially store it within the vbo.
        tempBuffer = createFloatBuffer(null, defaultBufferSize, false);
        vbo.bind();
        vbo.storeBuffer(tempBuffer.capacity() * FLOAT_SIZE, BUFFER_USAGE);
        vbo.unbind();

        log.info("Temp buffer initialized with size: " + defaultBufferSize);
        vao.bind();

        // calculate the stride and offsets for each vertex element. Store the offset/strides within their corresponding
        // index of the vertex format.
        int stride = vertexFormat.getElementSize() * FLOAT_SIZE;
        long offset = 0;
        for (int i = 0; i < vertexFormat.getElementCount(); i++) {
            vao.storeAttribute(i, vertexFormat.getElement(i).getSize(), vbo, stride, offset);
            offset += vertexFormat.getElement(i).getSize() * FLOAT_SIZE;
        }

        VertexArrayObject.unbind();
    }

    /**
     * Creates a new float buffer with the specified size. Will destory the provided float buffer and copy data if
     * specified.
     * */
    private FloatBuffer createFloatBuffer(FloatBuffer floatBuffer, int size, boolean copyData) {
        FloatBuffer newBuffer = MemoryUtil.memAllocFloat(size);
        if (floatBuffer != null) {
            if (copyData) {
                newBuffer.put(floatBuffer);
            }
            MemoryUtil.memFree(floatBuffer);
        }
        return newBuffer;
    }

    /**
     * Prepares this dynamicVao for rendering.
     * */
    public void begin() {
        if (dirty)
            throw new IllegalStateException("Cannot prepare this dynamicVao when it is currently dirty! Try reset()");
        this.dirty = true;
        this.vertexCount = 0;
        this.vertexFormatIndex = 0;
    }

    /**
     * Resets this tessellators temporary values.
     * */
    public void reset() {
        this.dirty = false;
        this.vertexCount = 0;
        this.vertexFormatIndex = 0;
        tempBuffer.clear();
    }

    /**
     * Resets the temporary buffer to the default buffer size.
     * */
    public void resetBuffer() {
        if (dirty)
            throw new IllegalStateException("Cannot reset the temporary buffer when it is currently dirty! Try reset()");
        this.tempBuffer = createFloatBuffer(this.tempBuffer, defaultBufferSize, false);
        this.vbo.bind();
        this.vbo.storeBuffer(this.tempBuffer, BUFFER_USAGE);
        this.vbo.unbind();
    }

    /**
     * Resizes the temp buffer if necessary.
     * */
    private void resizeBuffer(int index) {
        // If there is not room for the next vertex, double the space.
        if (index + vertexFormat.getElementSize() >= tempBuffer.capacity()) {
            tempBuffer = createFloatBuffer(tempBuffer, (int) (tempBuffer.capacity() * RESIZE_FACTOR), true);
            resized = true;
            log.info("Resized temp buffer to: " + tempBuffer.capacity() + ", " + (tempBuffer.capacity() / vertexFormat.getElementSize()) + " elements.");
        }
    }

    /**
     * Adds the provided x, y, z vertex information to this dynamicVao.
     * */
    public DynamicVAO position(float x, float y, float z) {
        if (!dirty)
            throw new IllegalStateException("The begin() function must be called before assigning position data!");
        int index = vertexCount * vertexFormat.getElementSize() + vertexFormat.getElementOffset(vertexFormatIndex);

        tempBuffer.put(index, x);
        tempBuffer.put(index + 1, y);
        tempBuffer.put(index + 2, z);

        nextVertexElement();
        return this;
    }

    /**
     * Adds the provided x, y vertex information to this dynamicVao.
     * */
    public DynamicVAO position(float x, float y) {
        if (!dirty)
            throw new IllegalStateException("The begin() function must be called before assigning position data!");
        int index = vertexCount * vertexFormat.getElementSize() + vertexFormat.getElementOffset(vertexFormatIndex);

        tempBuffer.put(index, x);
        tempBuffer.put(index + 1, y);

        nextVertexElement();
        return this;
    }

    /**
     * Adds the u, v texture coordinates to this dynamicVao.
     * */
    public DynamicVAO texture(float u, float v) {
        if (!dirty)
            throw new IllegalStateException("The begin() function must be called before assigning texture data!");
        int index = vertexCount * vertexFormat.getElementSize() + vertexFormat.getElementOffset(vertexFormatIndex);

        tempBuffer.put(index, u);
        tempBuffer.put(index + 1, v);

        nextVertexElement();
        return this;
    }

    public DynamicVAO color(float r, float g, float b, float a) {
        if (!dirty)
            throw new IllegalStateException("The begin() function must be called before assigning vertex data!");
        int index = vertexCount * vertexFormat.getElementSize() + vertexFormat.getElementOffset(vertexFormatIndex);

        tempBuffer.put(index, r);
        tempBuffer.put(index + 1, g);
        tempBuffer.put(index + 2, b);
        tempBuffer.put(index + 3, a);

        nextVertexElement();
        return this;
    }

    /**
     * Increments the current vertex element index in order to calculate the offsets properly.
     * */
    private void nextVertexElement() {
        this.vertexFormatIndex++;
        this.vertexFormatIndex %= vertexFormat.getElementCount();
    }

    /**
     * Ends the current vertex.
     * */
    public DynamicVAO endVertex() {
        if (vertexFormatIndex != 0) {
            log.error("Vertex was ended without reaching the rest of its elements!");
        }
        vertexCount++;
        resizeBuffer(vertexCount * vertexFormat.getElementSize());
        return this;
    }

    /**
     * Loads the vertex data to this tessellators vao.
     * */
    public void compile() {
        vao.bind();
        loadVertexData();
        vao.unbind();
    }

    /**
     * Draws this dynamicVao with the draw mode of {@link org.lwjgl.opengl.GL11#GL_TRIANGLES}.
     * */
    public void draw() {
        draw(GL_TRIANGLES);
    }


    /**
     * Draws the vao created with this dynamicVao.
     * */
    public void draw(int mode) {
        vao.bind();
        loadVertexData();
        DynamicVAO.draw(mode, vertexFormat, vertexCount);
        VertexArrayObject.unbind();
    }

    /**
     *
     * */
    public static void draw(int mode, VertexFormat vertexFormat, int vertexCount) {
        for (int i = 0; i < vertexFormat.getElementCount(); i++) {
            glEnableVertexAttribArray(i);
        }

        glDrawArrays(mode, 0, vertexCount);

        for (int i = 0; i < vertexFormat.getElementCount(); i++) {
            glDisableVertexAttribArray(i);
        }
    }

    /**
     * Loads the current vertex data into the vao and clears the temporary list of vertices. Does nothing if there are
     * no values to load into the vbo. (aka not dirty)
     * */
    protected void loadVertexData() {
        // The dirty variable is used to mark whether or not this dynamicVao has vertices that need to be loaded into
        // the vao.
        if (dirty) {
            this.vbo.bind();

            // Set temp buffer's position to zero. This is done since the absolute put methods are used rather than the
            // regular put method. The buffer's position is not updated and so buffer.flip() will not work properly.
            tempBuffer.position(0);

            if (resized) {
                resized = false;
                // We want the vbo to be updated with the entire capacity of the buffer instead of the number of
                // elements that is being drawn.
                tempBuffer.limit(tempBuffer.capacity());
                this.vbo.storeBuffer(this.tempBuffer, BUFFER_USAGE);
            } else {
                // We want the vbo to be updated with only the vertices updated in the buffer.
                tempBuffer.limit(vertexCount * vertexFormat.getElementSize());
                this.vbo.updateBuffer(tempBuffer, 0);
            }
            this.vbo.unbind();

            // Reset the buffer.
            this.tempBuffer.clear();
            this.dirty = false;
        }
    }

    /**
     * This function replaces the vertex array object and also returns it's current vao without deleting it.
     * @Returns This dynamicVao's vao.
     * */
    public VertexArrayObject pop() {
        if (dirty) {
            vao.bind();
            loadVertexData();
            VertexArrayObject.unbind();
        }
        // Store a copy of the current vao
        VertexArrayObject vao = this.vao;
        // Initialize a new VAO and VBO(s)
        initVao();
        // Return the old one
        return vao;
    }

    /**
     * Draws a textured rectangle at the provided positions.
     * */
    public void drawRect(float x, float y, float x1, float y1,
                         float u, float v, float s, float t,
                         float r, float g, float b, float a) {
        begin();
        addRect(x, y, x1, y1, u, v, s, t, r, g, b, a);
        draw();
    }

    public void drawRect(float x, float y, float x1, float y1,
                         float u, float v, float s, float t,
                         Color color) {
        begin();
        addRect(x, y, x1, y1, u, v, s, t, color);
        draw();
    }

    public void drawRect(float x, float y, float x1, float y1,
                         float u, float v, float s, float t,
                         int color) {
        begin();
        addRect(x, y, x1, y1, u, v, s, t, color);
        draw();
    }

    public void drawRect(float x, float y, float x1, float y1, int color) {
        begin();
        addRect(x, y, x1, y1, color);
        draw();
    }

    public void drawLine(float x, float y, float x1, float y1) {
        begin();
        addLine(x, y, x1, y1);
        draw(GL_LINES);
    }
    public void addLine(float x, float y, float x1, float y1,
                        float u, float v, float s, float t,
                        int color) {
        float z = 0F;

        position(x, y, z).texture(u, v).color(color).endVertex();
        position(x1, y1, z).texture(s, t).color(color).endVertex();
    }


    public void addLine(float x, float y, float x1, float y1, int color) {
        float z = 0F;

        position(x, y, z).color(color).endVertex();
        position(x1, y1, z).color(color).endVertex();
    }

    public void addLine(float x, float y, float x1, float y1) {
        float z = 0F;

        position(x, y, z).endVertex();
        position(x1, y1, z).endVertex();
    }

    public void addLine(float x, float y, float x1, float y1,
                        float r, float g, float b, float a) {
        float z = 0F;

        position(x, y, z).color(r, g, b, a).endVertex();
        position(x1, y1, z).color(r, g, b, a).endVertex();
    }

    public void addRect(float x, float y, float x1, float y1, int color) {
        float z = 0F;

        position(x1, y, z).color(color).endVertex();
        position(x, y, z).color(color).endVertex();
        position(x, y1, z).color(color).endVertex();
        position(x, y1, z).color(color).endVertex();
        position(x1, y1, z).color(color).endVertex();
        position(x1, y, z).color(color).endVertex();
    }

    public void addRect(float x, float y, float x1, float y1,
                        float u, float v, float s, float t) {
        float z = 0F;

        position(x1, y, z).texture(s, v).endVertex();
        position(x, y, z).texture(u, v).endVertex();
        position(x, y1, z).texture(u, t).endVertex();
        position(x, y1, z).texture(u, t).endVertex();
        position(x1, y1, z).texture(s, t).endVertex();
        position(x1, y, z).texture(s, v).endVertex();
    }

    public void addRect(float x, float y, float x1, float y1,
                        float u, float v, float s, float t,
                        Color color) {
        addRect(x, y, x1, y1, u, v, s, t, color.getRGB());
    }

        /**
         * Adds a textured rectangle at the provided positions to this dynamicVao.
         * */
    public void addRect(float x, float y, float x1, float y1,
                        float u, float v, float s, float t,
                        int color) {
        float z = 0F;
        position(x1, y, z).texture(s, v).color(color).endVertex();
        position(x, y, z).texture(u, v).color(color).endVertex();
        position(x, y1, z).texture(u, t).color(color).endVertex();
        position(x, y1, z).texture(u, t).color(color).endVertex();
        position(x1, y1, z).texture(s, t).color(color).endVertex();
        position(x1, y, z).texture(s, v).color(color).endVertex();
    }

    /**
     * Adds a textured rectangle at the provided positions to this dynamicVao.
     * */
    public void addRect(float x, float y, float x1, float y1,
                        float u, float v, float s, float t,
                        float r, float g, float b, float a) {
        float z = 0F;
        position(x1, y, z).texture(s, v).color(r, g, b, a).endVertex();
        position(x, y, z).texture(u, v).color(r, g, b, a).endVertex();
        position(x, y1, z).texture(u, t).color(r, g, b, a).endVertex();
        position(x, y1, z).texture(u, t).color(r, g, b, a).endVertex();
        position(x1, y1, z).texture(s, t).color(r, g, b, a).endVertex();
        position(x1, y, z).texture(s, v).color(r, g, b, a).endVertex();
    }


    /**
     * Adds a textured rectangle at the provided positions to this dynamicVao.
     * */
    public void addGradientRect(float x, float y, float x1, float y1,
                                float u, float v, float s, float t,
                                int topColor, int bottomColor) {
        float z = 0F;
        position(x1, y, z).texture(s, v).color(topColor).endVertex();
        position(x, y, z).texture(u, v).color(topColor).endVertex();
        position(x, y1, z).texture(u, t).color(bottomColor).endVertex();
        position(x, y1, z).texture(u, t).color(bottomColor).endVertex();
        position(x1, y1, z).texture(s, t).color(bottomColor).endVertex();
        position(x1, y, z).texture(s, v).color(topColor).endVertex();
    }

    /**
     * Destroys this dynamicVao.
     * */
    public void destroy() {
        MemoryUtil.memFree(tempBuffer);
        vbo.destroy();
        vao.destroy();
    }

    public DynamicVAO color(Vector4f color) {
        return this.color(color.x, color.y, color.z, color.w);
    }

    public DynamicVAO color(Vector3f color, float alpha) {
        return this.color(color.x, color.y, color.z, alpha);
    }

    /**
     * Each vertex is assigned a color when provided to this dynamicVao, this function modifies that color.
     * */
    public DynamicVAO color(Color color) {
        return this.color((float) color.getRed() / 255F,
                (float) color.getGreen() / 255F,
                (float) color.getBlue() / 255F,
                (float) color.getAlpha() / 255F);
    }

    /**
     * Each vertex is assigned a color when provided to this dynamicVao, this function modifies that color.
     * */
    public DynamicVAO color(Color color, float alpha) {
        return this.color((float) color.getRed() / 255F,
                (float) color.getGreen() / 255F,
                (float) color.getBlue() / 255F, alpha);
    }

    /**
     * Each vertex is assigned a color when provided to this dynamicVao, this function modifies that color.
     * */
    public DynamicVAO color(int color) {
        return this.color((float) (color >> 16 & 255) / 255F,
                (float) (color >> 8 & 255) / 255F,
                (float) (color & 255) / 255F,
                (color >> 24 & 0xff) / 255F);
    }

    /**
     * Each vertex is assigned a color when provided to this dynamicVao, this function modifies that color.
     * */
    public DynamicVAO color(int color, float alpha) {
        return this.color((float) (color >> 16 & 255) / 255F,
                (float) (color >> 8 & 255) / 255F,
                (float) (color & 255) / 255F, alpha);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }
}
