package com.branwilliams.bundi.atmosphere.pipeline.passes;

import com.branwilliams.bundi.atmosphere.Skydome;
import com.branwilliams.bundi.atmosphere.pipeline.shaders.AtmosphereShaderProgram2;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.function.Supplier;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class AtmosphereRenderPass2 extends RenderPass<RenderContext> {

    private final Supplier<Camera> camera;

    private final Supplier<Skydome> skydome;

    private final Supplier<Material> atmosphereMaterial;

    private final Supplier<Vector3f> sunPosition;

    private final Supplier<Float> weather;

    private AtmosphereShaderProgram2 atmosphereShaderProgram;

    private Mesh renderPassMesh;

    private Vector3f sunPos;

    private Matrix3f rotStars;

    private float time = 0F;

    public AtmosphereRenderPass2(Supplier<Material> atmosphereMaterial, Supplier<Camera> camera,
                                 Supplier<Skydome> skydome, Supplier<Vector3f> sunPosition, Supplier<Float> weather) {
        this.atmosphereMaterial = atmosphereMaterial;
        this.camera = camera;
        this.skydome = skydome;
        this.sunPosition = sunPosition;
        this.weather = weather;
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.atmosphereShaderProgram = new AtmosphereShaderProgram2(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create scene shader program!");
            throw new InitializationException(e);
        }
        sunPos = new Vector3f();
        rotStars = new Matrix3f();

        this.renderPassMesh = new Mesh();
        this.renderPassMesh.bind();
        this.renderPassMesh.storeAttribute(0,
                new float[] {
                        -1, -1, -1,
                        1, -1, -1,
                        1,  1, -1,
                        -1, -1, -1,
                        1,  1, -1,
                        -1,  1, -1 }, 3);
        this.renderPassMesh.setVertexCount(6);
        this.renderPassMesh.unbind();
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
        time += 0.0125F;

//        float stacks = 360;
//        float slices = 360;

//        float rho = Mathf.PI * 0.5F;
//        float theta = 2F * Mathf.PI * 0.25F;
//        float radius = 250;
//        sunPos.x = (-Mathf.sin(theta) * Mathf.sin(rho)) * radius;
//        sunPos.y = (Mathf.cos(theta) * Mathf.sin(rho)) * radius;
//        sunPos.z = (Mathf.cos(rho)) * radius;
//        float sunY = Mathf.sin(0.1F);
//        sunPos.set(-0.1625F, sunY, 0.3F);
//        System.out.println("sunY=" + sunY);
        // Bind skydome shader program and render skydome.
        this.atmosphereShaderProgram.bind();
        this.atmosphereShaderProgram.setProjectionMatrix(renderContext.getProjection());
        this.atmosphereShaderProgram.setViewMatrix(camera.get());
        this.atmosphereShaderProgram.setTime((float) engine.getTime());
        this.atmosphereShaderProgram.setWeather(weather.get());
        this.atmosphereShaderProgram.setSunPos(sunPosition.get());
        this.atmosphereShaderProgram.setRotStars(rotStars);

        MeshRenderer.render(skydome.get().getSkydomeMesh(), atmosphereMaterial.get());

        ShaderProgram.unbind();
    }

}
