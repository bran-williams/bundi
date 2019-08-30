package com.branwilliams.bundi.engine.sprite;

import com.branwilliams.bundi.engine.shader.VertexArrayObject;
import com.branwilliams.bundi.engine.shape.AABB;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * Created by Brandon Williams on 6/26/2018.
 */
public class Sprite {

    private final SpriteSheet spriteSheet;

    private final VertexFormat vertexFormat;

    private final VertexArrayObject vao;

    private final int vertexCount;

    private final int index;

    private final float width, height;

    private boolean centered;

    private final AABB aabb;

    public Sprite(SpriteSheet spriteSheet, VertexFormat vertexFormat, VertexArrayObject vao, int vertexCount, int index, float width, float height, boolean centered) {
        this.spriteSheet = spriteSheet;
        this.vertexFormat = vertexFormat;
        this.vao = vao;
        this.vertexCount = vertexCount;
        this.index = index;
        this.width = width;
        this.height = height;
        this.centered = centered;
        if (centered) {
            this.aabb = new AABB(-width/2, -height/2, width/2, height/2);
        } else {
            this.aabb = new AABB(0, 0, width, height);
        }
    }

    /**
     * Draws this sprite object.
     * */
    public void draw() {
        this.spriteSheet.getTexture().bind();
        this.vao.bind();

        DynamicVAO.draw(GL_TRIANGLES, vertexFormat, vertexCount);
        this.vao.unbind();
    }

    /**
     * Destroys the vao created for this sprite object.
     * */
    public void destroy() {
        this.vao.destroy();
    }

    public boolean hasIndex() {
        return index < 0;
    }

    public int getIndex() {
        return index;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isCentered() {
        return centered;
    }

    public AABB getAABB() {
        return aabb;
    }

    @Override
    public String toString() {
        return "Sprite{" +
                "index=" + index +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
