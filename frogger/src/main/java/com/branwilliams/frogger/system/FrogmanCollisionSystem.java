package com.branwilliams.frogger.system;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.NameMatcher;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shape.AABB2f;
import com.branwilliams.bundi.engine.shape.SeparatingAxis;
import com.branwilliams.frogger.tilemap.Tilemap;

import java.util.function.Supplier;

import static com.branwilliams.frogger.FroggerConstants.FROGMAN_NAME;

public class FrogmanCollisionSystem extends AbstractSystem {

    private final Supplier<Tilemap> tilemap;

    public FrogmanCollisionSystem(Supplier<Tilemap> tilemap) {
        super(new NameMatcher(FROGMAN_NAME));
        this.tilemap = tilemap;
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        Tilemap tilemap = this.tilemap.get();
        for (IEntity frogman : entitySystemManager.getEntities(this)) {
            Transformable frogmanTransform = frogman.getComponent(Transformable.class);
            AABB2f frogmanAABB = frogman.getComponent(AABB2f.class);
            tilemap.forTilesInRange(frogmanAABB, handleTileCollision(tilemap, frogmanTransform, frogmanAABB));
        }
    }

    private Tilemap.TileConsumer handleTileCollision(Tilemap tilemap, Transformable frogmanTransform, AABB2f frogmanAABB) {
        return (x, y, tile) -> {
//            System.out.println("tile at x: " + x + ", y: " + y);
            float pX = x * tilemap.getTileWidth();
            float pY = y * tilemap.getTileHeight();
            AABB2f tileAABB = new AABB2f(-tilemap.getTileWidth() * 0.5F, -tilemap.getTileHeight() * 0.5F,
                    tilemap.getTileWidth() * 0.5F, tilemap.getTileHeight() * 0.5F);
            tileAABB.center(pX + tilemap.getTileWidth() * 0.5F, pY + tilemap.getTileHeight() * 0.5F);

            if (SeparatingAxis.collide(tileAABB, frogmanAABB, (push) -> frogmanTransform.move(push.x, push.y, 0))) {
                frogmanAABB.center(frogmanTransform.x(), frogmanTransform.y());
            }
        };
    }
}
