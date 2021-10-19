package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.core.window.WindowListener;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.frogger.Camera2D;
import com.branwilliams.frogger.tilemap.Tilemap;
import com.branwilliams.frogger.builder.TilemapMeshBuilder;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class TilemapUpdateSystem extends AbstractSystem implements WindowListener {

    private final Supplier<Tilemap> tilemap;

    private final Supplier<Camera2D> camera;

    private final TilemapMeshBuilder tilemapMeshBuilder;

    public TilemapUpdateSystem(Scene scene, Supplier<Tilemap> tilemap, Supplier<Camera2D> camera) {
        super(new ClassComponentMatcher(Transformable.class));
        scene.addWindowListener(this);
        this.tilemap = tilemap;
        this.camera = camera;
        this.tilemapMeshBuilder = new TilemapMeshBuilder();
    }


    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        this.tilemapMeshBuilder.buildTilemapMesh(tilemap.get(), camera.get());
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Tilemap tilemap = this.tilemap.get();
        Camera2D camera = this.camera.get();
        if (tilemap.getTiles().isDirty() || camera.isMoving()) {
            this.tilemapMeshBuilder.rebuildTilemapMesh(tilemap, camera, tilemap.getMesh());
            tilemap.getTiles().setDirty(false);
        }
    }

    @Override
    public void resize(Window window, int width, int height) {
        this.tilemap.get().getTiles().setDirty(true);
    }
}
