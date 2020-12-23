package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.frogger.Tilemap;
import com.branwilliams.frogger.builder.TilemapMeshBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author Brandon
 * @since September 08, 2019
 */
public class TilemapUpdateSystem extends AbstractSystem {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Supplier<Tilemap> tilemap;

    private final TilemapMeshBuilder tilemapMeshBuilder;

    public TilemapUpdateSystem(Supplier<Tilemap> tilemap) {
        super(new ClassComponentMatcher(Transformable.class));
        this.tilemap = tilemap;

        this.tilemapMeshBuilder = new TilemapMeshBuilder();
    }


    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {
        this.tilemapMeshBuilder.buildTilemapMesh(tilemap.get());
    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Tilemap tilemap = this.tilemap.get();
        if (tilemap.getTiles().isDirty()) {
            this.tilemapMeshBuilder.rebuildTilemapMesh(tilemap, tilemap.getMesh());
            tilemap.getTiles().setDirty(false);
        }
    }
}
