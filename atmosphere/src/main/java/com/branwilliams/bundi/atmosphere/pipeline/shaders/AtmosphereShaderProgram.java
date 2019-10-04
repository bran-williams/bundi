package com.branwilliams.bundi.atmosphere.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 1/5/2018.
 */
public class AtmosphereShaderProgram extends ShaderProgram {

    public AtmosphereShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "atmosphere/shaders/atmosphere/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "atmosphere/shaders/atmosphere/fragmentShader.frag", null));
        this.link();

        this.createUniform("sunPos");
        this.bind();
        ShaderProgram.unbind();
        this.validate();
    }

    public void setSunPos(Vector3f sunPos) {
        this.setUniform("sunPos", sunPos);
    }

}
