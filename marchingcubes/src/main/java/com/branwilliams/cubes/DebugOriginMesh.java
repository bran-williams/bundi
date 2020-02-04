package com.branwilliams.cubes;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

import static com.branwilliams.bundi.engine.util.ColorUtils.toVector4;
import static org.lwjgl.opengl.GL11.GL_LINES;

/**
 * @author Brandon
 * @since February 04, 2020
 */
public class DebugOriginMesh implements Destructible {

    private Vector3f origin;

    private Vector4f xAxisColor;

    private Vector4f yAxisColor;

    private Vector4f zAxisColor;

    private float alpha;

    private Mesh mesh;

    public DebugOriginMesh() {
        this(new Vector3f());
    }

    public DebugOriginMesh(Vector3f origin) {
        this(origin, toVector4(Color.RED), toVector4(Color.GREEN), toVector4(Color.BLUE), 0.4F);
    }

    public DebugOriginMesh(Vector3f origin, Vector4f xAxisColor, Vector4f yAxisColor, Vector4f zAxisColor, float alpha) {
        this.origin = origin;
        this.xAxisColor = xAxisColor;
        this.xAxisColor.w = alpha;

        this.yAxisColor = yAxisColor;
        this.yAxisColor.w = alpha;

        this.zAxisColor = zAxisColor;
        this.zAxisColor.w = alpha;
        this.alpha = alpha;
    }

    public void init() {
        this.mesh = new Mesh();
        this.mesh.bind();
        this.mesh.initializeAttribute(0, 3, 3 * 6);
        this.mesh.initializeAttribute(1, 4, 4 * 6);
        this.mesh.setRenderMode(GL_LINES);
        this.mesh.unbind();
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    public Vector4f getxAxisColor() {
        return xAxisColor;
    }

    public void setxAxisColor(Vector4f xAxisColor) {
        this.xAxisColor = xAxisColor;
    }

    public Vector4f getyAxisColor() {
        return yAxisColor;
    }

    public void setyAxisColor(Vector4f yAxisColor) {
        this.yAxisColor = yAxisColor;
    }

    public Vector4f getzAxisColor() {
        return zAxisColor;
    }

    public void setzAxisColor(Vector4f zAxisColor) {
        this.zAxisColor = zAxisColor;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void destroy() {
        mesh.destroy();
    }
}
