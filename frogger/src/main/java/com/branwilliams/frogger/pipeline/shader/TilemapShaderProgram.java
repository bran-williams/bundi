package com.branwilliams.frogger.pipeline.shader;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.util.IOUtils;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.frogger.tilemap.Tilemap;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.file.Path;

public class TilemapShaderProgram extends ShaderProgram {

    private final Matrix4f worldMatrix = new Matrix4f();

    private final Vector2f spriteSize = new Vector2f();

    public TilemapShaderProgram(EngineContext engineContext) throws ShaderInitializationException, ShaderUniformException {
        super();
        Path directory = engineContext.getAssetDirectory();
        this.setVertexShader(IOUtils.readFile(directory, "shaders/tilemap/vertexShader.vert", null));
        this.setFragmentShader(IOUtils.readFile(directory, "shaders/tilemap/fragmentShader.frag", null));
        this.link();

        this.createUniform("projectionMatrix");
        this.createUniform("modelMatrix");

        this.createUniform("spriteSize");
        this.createUniform("diffuse");

        this.validate();

        this.bind();
        this.setUniform("diffuse", 0);
        ShaderProgram.unbind();
    }

    public void setProjectionMatrix(Projection projection) {
        this.setUniform("projectionMatrix", projection.toProjectionMatrix());
    }

    public void setModelMatrix(Transformable transformable) {
        this.setUniform("modelMatrix", Mathf.toModelMatrix(worldMatrix, transformable));
    }

    public void setTilemap(Tilemap tilemap) {
        this.setUniform("spriteSize", spriteSize.set(tilemap.getTileWidth(), tilemap.getTileHeight()));
    }
}
