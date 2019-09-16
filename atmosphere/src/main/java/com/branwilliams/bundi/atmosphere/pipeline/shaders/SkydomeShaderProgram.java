package com.branwilliams.bundi.atmosphere.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 1/5/2018.
 */
public class SkydomeShaderProgram extends ShaderProgram {

    // Used to create a copy of the view matrix
    private final Matrix4f copy = new Matrix4f();

    public SkydomeShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/deferred/phong/skydome/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/deferred/phong/skydome/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");
        this.createUniform("apexColor");
        this.createUniform("centerColor");
        this.bind();
        ShaderProgram.unbind();
        this.validate();
    }

    public void setColors(Vector4f apexColor, Vector4f centerColor) {
        this.setUniform("apexColor", apexColor);
        this.setUniform("centerColor", centerColor);
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setViewMatrix(Camera camera) {
        // Put contents of 'view matrix' into 'copy'.
        copy.set(camera.toViewMatrix());
        // Remove the translations, making it only the rotations.
        copy.m30 = 0;
        copy.m31 = 0;
        copy.m32 = 0;
        this.setUniform("viewMatrix", copy);
    }
}
