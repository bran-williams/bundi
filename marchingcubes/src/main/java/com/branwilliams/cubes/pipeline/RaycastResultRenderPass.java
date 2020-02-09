package com.branwilliams.cubes.pipeline;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.DynamicShaderProgram;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.cubes.CubesScene;
import com.branwilliams.cubes.GridCell;
import com.branwilliams.cubes.math.RaycastResult;
import com.branwilliams.cubes.world.MarchingCubeChunk;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.MeshUtils.calculateNormal;

public class RaycastResultRenderPass extends RenderPass<RenderContext> {

    private final Transformable transformable = new Transformation();

    private final Vector4f red = new Vector4f(0F, 1F, 0F, 1F);

    private final Vector4f green = new Vector4f(0F, 1F, 0F, 1F);

    private final Vector4f blue = new Vector4f(0F, 0F, 1F, 1F);

    private final CubesScene scene;

    private final Supplier<Camera> camera;

    private DynamicShaderProgram shaderProgram;

    private SphereMesh sphereMesh;

    public RaycastResultRenderPass(CubesScene scene, Supplier<Camera> camera) {
        this.scene = scene;
        this.camera = camera;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            shaderProgram = new DynamicShaderProgram(VertexFormat.POSITION, DynamicShaderProgram.VIEW_MATRIX);
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }
        this.sphereMesh = new SphereMesh(1F, 90, 90, VertexFormat.POSITION);
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());
//            shaderProgram.setModelMatrix(transformable.position(scene.getRaycast().position));

        RaycastResult raycast = raycast(scene.getCamera().getPosition(), scene.getCamera().getDirection(), scene.getRaycastDistance());
        if (raycast != null) {
            scene.setRaycast(raycast);
            shaderProgram.setModelMatrix(transformable.position(raycast.position).scale(0.25F));
            shaderProgram.setColor(green);
            MeshRenderer.render(sphereMesh, null);
//            System.out.println("Drawing raycast as a green sphere!");
        }
        ShaderProgram.unbind();
    }

    /**
     * ray-triangle intersection test from
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/ray-tracing-rendering-a-triangle/ray-triangle-intersection-geometric-solution
     *
     * */
    public Vector3f intersectPoint(Vector3f origin, Vector3f direction, float distance, float x, float y, float z) {
        int chunkX = scene.getWorld().toChunkX(x);
        int chunkY = scene.getWorld().toChunkY(y);
        int chunkZ = scene.getWorld().toChunkZ(z);

        MarchingCubeChunk chunk = scene.getWorld().getChunks().getValue(chunkX, chunkY, chunkZ);
        if (chunk == null) {
            return null;
        }

        Vector3f offset = chunk.getOffset();
        GridCell gridCell = chunk.getGridCell(
                (int) (x - offset.x),
                (int) (y - offset.y),
                (int) (z - offset.z));

        if (gridCell == null) {
            return null;
        }

        List<Vector3f> triangles = new ArrayList<>();
        int triangleCount = gridCell.getTriangles(scene.getWorld().getIsoLevel(), triangles);

        if (triangleCount > 0) {
//            shaderProgram.setModelMatrix(transformable.position(x, y, z).scale(0.25F));
//            shaderProgram.setColor(blue);
//            MeshRenderer.render(sphereMesh, null);
//            System.out.println("rendering blue sphere for cube with triangles!");
            return new Vector3f(x, y, z);
        }

        for (int i = 0; i < triangleCount; i++) {
            Vector3f p1 = triangles.get(i * 3);
            Vector3f p2 = triangles.get(i * 3 + 1);
            Vector3f p3 = triangles.get(i * 3 + 2);
            Vector3f normal = calculateNormal(p1, p2, p3);

            float nDotRayDirection = normal.dot(direction);

            // check if ray and triangle plane are parallel?
            if (Math.abs(nDotRayDirection) < 0.00001F) {
                return null; // they are almost parallel so they don't intersect!
            }

            // distance
            float d = normal.dot(p1);

            // t
            float t = (normal.dot(origin) + d) / nDotRayDirection;

            // if the triangle is behind the ray, then no intersection...
            if (t < 0) {
                return null;
            }

            Vector3f point = direction.mul(t, new Vector3f()).add(origin);

            Vector3f c = new Vector3f();
            Vector3f edge = new Vector3f();
            Vector3f vp = new Vector3f();

            p2.sub(p1, edge);
            point.sub(p1, vp);
            edge.cross(vp, c);
            if (normal.dot(c) < 0)
                return null;

            p3.sub(p2, edge);
            point.sub(p2, vp);
            edge.cross(vp, c);
            if (normal.dot(c) < 0)
                return null;

            p1.sub(p3, edge);
            point.sub(p3, vp);
            edge.cross(vp, c);
            if (normal.dot(c) < 0)
                return null;

            return point;
        }
        // no triangles means an empty chunk position...
        return null;
    }

    private RaycastResult raycast(Vector3f origin, Vector3f direction, float distance) {
        float directionLength = direction.length();

        if (directionLength == 0F)
            return null;

        float x = Mathf.floor(origin.x - scene.getWorld().getCubeSize() * 0.5F);
        float y = Mathf.floor(origin.y - scene.getWorld().getCubeSize() * 0.5F);
        float z = Mathf.floor(origin.z - scene.getWorld().getCubeSize() * 0.5F);

        float stepX = Math.signum(direction.x);
        float stepY = Math.signum(direction.y);
        float stepZ = Math.signum(direction.z);

        float tDeltaX = Mathf.abs(1F / direction.x);
        float tDeltaY = Mathf.abs(1F / direction.y);
        float tDeltaZ = Mathf.abs(1F / direction.z);

        float xdist = stepX > 0 ? (x + 1 - origin.x) : (origin.x - x);
        float ydist = stepY > 0 ? (y + 1 - origin.y) : (origin.y - y);
        float zdist = stepZ > 0 ? (z + 1 - origin.z) : (origin.z - z);

        float tMaxX = tDeltaX * xdist;
        float tMaxY = tDeltaY * ydist;
        float tMaxZ = tDeltaZ * zdist;

        Vector3f point = null;
        Vector3f face = new Vector3f();

        float t = 0F;
        boolean found = false;
        while (t < distance) {
            if ((point = intersectPoint(origin, direction, distance, x, y, z)) != null) {
                found = true;
                break;
            }

            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    x += stepX;
                    t = tMaxX;
                    tMaxX += tDeltaX;

                    face.x = -stepX;
                    face.y = 0;
                    face.z = 0;
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;

                    face.x = 0;
                    face.y = 0;
                    face.z = -stepZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    y += stepY;
                    t = tMaxY;
                    tMaxY += tDeltaY;

                    face.x = 0;
                    face.y = -stepY;
                    face.z = 0;
                } else {
                    z += stepZ;
                    t = tMaxZ;
                    tMaxZ += tDeltaZ;

                    face.x = 0;
                    face.y = 0;
                    face.z = -stepZ;
                }
            }
        }

        if (found) {
            Vector3f hitPosition = new Vector3f(origin);
            hitPosition.add(direction.x * t, direction.y * t, direction.z * t);

            Vector3f blockPosition = new Vector3f(Mathf.floor(hitPosition.x), Mathf.floor(hitPosition.y), Mathf.floor(hitPosition.z));
            // Small hack to fix the block position being offset wrongly.
            if (face.x > 0)
                blockPosition.x -= 1;
            if (face.y > 0)
                blockPosition.y -= 1;
            if (face.z > 0)
                blockPosition.z -= 1;

            return new RaycastResult(point, hitPosition, face);
        } else {
            return null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.shaderProgram.destroy();
        this.sphereMesh.destroy();
    }
}
