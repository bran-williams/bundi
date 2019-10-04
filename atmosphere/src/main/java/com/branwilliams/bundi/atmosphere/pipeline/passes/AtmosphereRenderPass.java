package com.branwilliams.bundi.atmosphere.pipeline.passes;

import com.branwilliams.bundi.atmosphere.pipeline.shaders.AtmosphereShaderProgram;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Window;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.ShaderInitializationException;
import com.branwilliams.bundi.engine.shader.ShaderUniformException;
import com.branwilliams.bundi.engine.util.Mathf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Brandon Williams on 11/28/2018.
 */
public class AtmosphereRenderPass extends RenderPass<RenderContext> {

    private AtmosphereShaderProgram atmosphereShaderProgram;

    private Mesh renderPassMesh;

    private Vector3f sunPosition;

    private float theta = 0F;

    public AtmosphereRenderPass() {
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
            this.atmosphereShaderProgram = new AtmosphereShaderProgram(engine.getContext());
        } catch (ShaderUniformException | ShaderInitializationException e) {
            System.err.println("Unable to create scene shader program!");
            throw new InitializationException(e);
        }
        this.sunPosition = new Vector3f();

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
//        this.renderPassMesh.storeIndices(new int[] { 0, 2, 1, 1, 2, 3 });
        this.renderPassMesh.unbind();
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {

        theta += 0.0125F;

        sunPosition.y = Mathf.cos(theta) * 0.4F + 0.2F;
        sunPosition.z = -1F;

        glEnable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Bind skydome shader program and render skydome.
        this.atmosphereShaderProgram.bind();
        this.atmosphereShaderProgram.setSunPos(sunPosition);
        MeshRenderer.render(renderPassMesh, null);
    }

}
