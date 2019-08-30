package com.branwilliams.bundi.voxel.util;

import com.branwilliams.bundi.engine.shader.dynamic.DynamicVAO;
import com.branwilliams.bundi.voxel.math.AABB;
import org.joml.Vector4f;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class RenderUtils {

    public static void addAABB(DynamicVAO dynamicVAO, AABB aabb, Vector4f color) {
        addAABB(dynamicVAO, aabb, color.x, color.y, color.z, color.w);
    }

    public static void addAABB(DynamicVAO dynamicVAO, AABB aabb, float r, float g, float b, float a) {
        float offset = 0.02F;
        float minx = aabb.getMinX() - offset;
        float miny = aabb.getMinY() - offset;
        float minz = aabb.getMinZ() - offset;
        float maxx = aabb.getMaxX() + offset;
        float maxy = aabb.getMaxY() + offset;
        float maxz = aabb.getMaxZ() + offset;

        // ---------- x axis ----------
        dynamicVAO.position(minx, miny, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, miny, minz).color(r, g, b, a).endVertex();

        dynamicVAO.position(minx, maxy, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, maxy, minz).color(r, g, b, a).endVertex();

        dynamicVAO.position(minx, miny, maxz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, miny, maxz).color(r, g, b, a).endVertex();

        dynamicVAO.position(minx, maxy, maxz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, maxy, maxz).color(r, g, b, a).endVertex();

        // ---------- z axis ----------
        dynamicVAO.position(minx, miny, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(minx, miny, maxz).color(r, g, b, a).endVertex();

        dynamicVAO.position(maxx, miny, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, miny, maxz).color(r, g, b, a).endVertex();

        dynamicVAO.position(minx, maxy, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(minx, maxy, maxz).color(r, g, b, a).endVertex();

        dynamicVAO.position(maxx, maxy, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, maxy, maxz).color(r, g, b, a).endVertex();

        // ---------- y axis ----------
        dynamicVAO.position(minx, miny, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(minx, maxy, minz).color(r, g, b, a).endVertex();

        dynamicVAO.position(maxx, miny, minz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, maxy, minz).color(r, g, b, a).endVertex();

        dynamicVAO.position(minx, miny, maxz).color(r, g, b, a).endVertex();
        dynamicVAO.position(minx, maxy, maxz).color(r, g, b, a).endVertex();

        dynamicVAO.position(maxx, miny, maxz).color(r, g, b, a).endVertex();
        dynamicVAO.position(maxx, maxy, maxz).color(r, g, b, a).endVertex();
    }
}
