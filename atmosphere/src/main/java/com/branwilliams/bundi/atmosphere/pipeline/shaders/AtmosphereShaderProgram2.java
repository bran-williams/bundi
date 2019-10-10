package com.branwilliams.bundi.atmosphere.pipeline.shaders;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.file.Path;

/**
 * Created by Brandon Williams on 1/5/2018.
 */
public class AtmosphereShaderProgram2 extends ShaderProgram {

    // Used to create a copy of the view matrix
    private final Matrix4f copy = new Matrix4f();

    public AtmosphereShaderProgram2(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "atmosphere/shaders/atmosphere2/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "atmosphere/shaders/atmosphere2/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("viewMatrix");

        this.createUniform("sunPos");
//        this.createUniform("rotStars");

//        this.createUniform("time");
        this.createUniform("weather");

        this.createUniform("tint");
        this.createUniform("tint2");
        this.createUniform("sun");

        this.bind();
        this.setUniform("tint", 0);
        this.setUniform("tint2", 1);
        this.setUniform("sun", 2);
        ShaderProgram.unbind();
        this.validate();
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

    public void setSunPos(Vector3f sunPos) {
        this.setUniform("sunPos", sunPos);
    }

    public void setRotStars(Matrix3f rotStars) {
//        this.setUniform("rotStars", rotStars);
    }

    public void setTime(float time) {
//        this.setUniform("time", time);
    }

    public void setWeather(float weather) {
        this.setUniform("weather", weather);
    }

}
