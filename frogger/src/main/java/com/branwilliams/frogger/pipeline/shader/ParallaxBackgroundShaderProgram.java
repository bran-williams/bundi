package com.branwilliams.frogger.pipeline.shader;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.parallax.ParallaxObject;
import org.joml.Matrix4f;

import java.nio.file.Path;

public class ParallaxBackgroundShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    public ParallaxBackgroundShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/parallax/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/parallax/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("offsetUV");
        this.createUniform("objectSize");
        this.createUniform("diffuse");

        this.validate();

        this.bind();
        this.setUniform("diffuse", 0);
        ShaderProgram.unbind();
    }

    public void setParallaxObject(ParallaxObject<?> parallaxObject) {
        this.setUniform("offsetUV", parallaxObject.getOffset());
        this.setUniform("objectSize", parallaxObject.getSize());
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }


    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(worldMatrix, transformable));
    }
}
