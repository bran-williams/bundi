package com.branwilliams.bundi.cloth.system;

import com.branwilliams.bundi.cloth.Cloth;
import com.branwilliams.bundi.cloth.builder.ClothMeshBuilder;
import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.AbstractSystem;
import com.branwilliams.bundi.engine.ecs.EntitySystemManager;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.ecs.matchers.ClassComponentMatcher;
import com.branwilliams.bundi.engine.mesh.Mesh;


public class ClothUpdateSystem extends AbstractSystem {

    private final ClothMeshBuilder clothMeshBuilder;

//    private final IComponentMatcher windMatcher;

    public ClothUpdateSystem(Scene scene) {
        super(new ClassComponentMatcher(Mesh.class, Cloth.class));
//        windMatcher = scene.getEs().matcher(ClothWind.class);
        this.clothMeshBuilder = new ClothMeshBuilder();
    }

    @Override
    public void init(Engine engine, EntitySystemManager entitySystemManager, Window window) {

    }

    @Override
    public void update(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {

    }

    @Override
    public void fixedUpdate(Engine engine, EntitySystemManager entitySystemManager, double deltaTime) {
        for (IEntity entity : entitySystemManager.getEntities(this)) {
            Cloth cloth = entity.getComponent(Cloth.class);
            Mesh mesh = entity.getComponent(Mesh.class);

            cloth.update();
            clothMeshBuilder.rebuildMesh(mesh, cloth);
        }

    }
}
