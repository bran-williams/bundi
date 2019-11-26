package com.branwilliams.mcskin.steve;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public final class TexturedQuad {

    public SteveVertex[] vertices;

    private TexturedQuad(SteveVertex[] vertices) {
        this.vertices = vertices;
    }

    public TexturedQuad(SteveVertex[] vertices, int u, int v, int s, int t) {
        this(vertices);
        float uOffset = 0.0015625F;
        float vOffset = 0.003125F;
        float u_ = (float) u / 64.0F + uOffset;
        float v_ = (float) v / 32.0F + vOffset;
        float s_ = (float) s / 64.0F - uOffset;
        float t_ = (float) t / 32.0F - vOffset;

        // no more quads, triangles..
        vertices[0] = vertices[0].create(s_, v_);
        vertices[1] = vertices[1].create(u_, v_);
        vertices[2] = vertices[2].create(u_, t_);
        vertices[3] = vertices[3].create(s_, t_);
    }

    public TexturedQuad(SteveVertex[] vertices, float u, float v, float s, float t) {
        this(vertices);
        vertices[0] = vertices[0].create(s, v);
        vertices[1] = vertices[1].create(u, v);
        vertices[2] = vertices[2].create(u, t);
        vertices[3] = vertices[3].create(s, t);
    }
}