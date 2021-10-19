package com.branwilliams.fog.pipeline.passes;

import com.branwilliams.bundi.engine.core.Engine;
import com.branwilliams.bundi.engine.core.Scene;
import com.branwilliams.bundi.engine.core.pipeline.InitializationException;
import com.branwilliams.bundi.engine.core.pipeline.RenderContext;
import com.branwilliams.bundi.engine.core.pipeline.RenderPass;
import com.branwilliams.bundi.engine.core.window.Window;
import com.branwilliams.bundi.engine.ecs.IComponentMatcher;
import com.branwilliams.bundi.engine.ecs.IEntity;
import com.branwilliams.bundi.engine.material.MaterialFormat;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.mesh.MeshRenderer;
import com.branwilliams.bundi.engine.shader.*;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.shader.modular.AbstractShaderModule;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderConstants;
import com.branwilliams.bundi.engine.shader.modular.ModularShaderProgram;
import com.branwilliams.bundi.engine.shader.modular.module.BillboardShaderModule;
import com.branwilliams.bundi.engine.shader.modular.patches.FragUniformPatch;
import com.branwilliams.bundi.engine.shader.patching.CommentShaderPatch;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.fog.ParticleEmitter;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.branwilliams.bundi.engine.util.Mathf.toCylindricalBillboardedModelMatrix;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * @author Brandon
 * @since April 17, 2019
 */
public class ParticleRenderPass extends RenderPass<RenderContext> {

    private static final float[] VERTICES = {
            -0.5F, 0.5F,
            0.5F, 0.5F,
            -0.5F, -0.5F,
            0.5F, -0.5F
    };

    private static final float[] UVS = {
            0, 1,
            1, 1,
            0, 0,
            1, 0
    };

    private static final int[] INDICES = {
            0, 2, 1, 1, 2, 3
    };

    private static final VertexFormat<?> PARTICLE_VERTEX_FORMAT = VertexFormat.POSITION_2D_UV;

    private final Scene scene;

    private final Supplier<Camera> camera;

    private final Supplier<Environment> environment;

    private final IComponentMatcher particleEmitterMatcher;

    private final Transformable tempTransform = new Transformation();

    private ModularShaderProgram shaderProgram;

    private Mesh particleMesh;

    public ParticleRenderPass(Scene scene, Supplier<Camera> camera, Supplier<Environment> environment) {
        this.scene = scene;
        this.camera = camera;
        this.environment = environment;
        this.particleEmitterMatcher = scene.getEs().matcher(Transformable.class, ParticleEmitter.class);
    }

    @Override
    public void init(RenderContext renderContext, Engine engine, Window window) throws InitializationException {
        try {
//            Environment environment = new Environment(null, null, null, null);
//            EnvironmentShaderModule environmentShaderModule = new EnvironmentShaderModule(() -> environment,
//                    PARTICLE_VERTEX_FORMAT, MaterialFormat.DIFFUSE_SAMPLER2D, DEFAULT_MATERIAL_NAME);
            shaderProgram = new ModularShaderProgram(PARTICLE_VERTEX_FORMAT,
                    MaterialFormat.DIFFUSE_SAMPLER2D,
                    Arrays.asList(new BillboardShaderModule(), new ParticleColorShaderModule()));
        } catch (ShaderInitializationException | ShaderUniformException e) {
            e.printStackTrace();
        }

        createParticleMesh();
    }

    private void createParticleMesh() {
        particleMesh = new Mesh();
        particleMesh.setRenderMode(GL_TRIANGLES);
        particleMesh.bind();
        particleMesh.storeAttribute(PARTICLE_VERTEX_FORMAT.getElementIndex(VertexElements.POSITION_2D), VERTICES,
                VertexElements.POSITION_2D.getSize());
        particleMesh.storeAttribute(PARTICLE_VERTEX_FORMAT.getElementIndex(VertexElements.UV), UVS,
                VertexElements.UV.getSize());
        particleMesh.storeIndices(INDICES);
        particleMesh.setVertexFormat(PARTICLE_VERTEX_FORMAT);
        particleMesh.unbind();
    }

    @Override
    public void render(RenderContext renderContext, Engine engine, Window window, double deltaTime) {
//        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
//        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_DEPTH_TEST);
        shaderProgram.bind();
        shaderProgram.setProjectionMatrix(renderContext.getProjection());
        shaderProgram.setViewMatrix(camera.get());

        glActiveTexture(GL_TEXTURE0);
        for (IEntity entity : scene.getEs().getEntities(particleEmitterMatcher)) {
            Transformable entityTransform = entity.getComponent(Transformable.class);
            ParticleEmitter particleEmitter = entity.getComponent(ParticleEmitter.class);
            tempTransform.scale(particleEmitter.getScale());
            tempTransform.setRotation(entityTransform.getRotation());
            particleEmitter.getParticles().stream().sorted(this::compareParticleDistance).forEach(particle -> {
                tempTransform.position(particle.getX(), particle.getY(), particle.getZ());
                shaderProgram.setModelMatrix(tempTransform);

                float minDistanceForFade = 10F;
                float alpha = Mathf.sin((particle.getLife() / particle.getMaxLife()) * Mathf.PI);
                float distanceFadeFactor = Math.min(particle.getDistance(camera.get().getPosition()),
                        minDistanceForFade) / minDistanceForFade;
                alpha = Math.min(alpha, someEasingFunc(distanceFadeFactor));

                shaderProgram.setUniform(ParticleColorShaderModule.ALPHA_UNIFORM,
                        Mathf.clamp(alpha, 0F, 1F));

                particle.texture.bind();
                MeshRenderer.render(particleMesh, null);
            });
        }



//        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ShaderProgram.unbind();
    }

    private static float someEasingFunc(float t) {
        return t < 0.5F ? 16F * t * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
    }

    private int compareParticleDistance(ParticleEmitter.Particle p1, ParticleEmitter.Particle p2) {
        Vector3f cameraPos = camera.get().getPosition();
        return -Float.compare(p1.getDistance(cameraPos), p2.getDistance(cameraPos));
    }

    public static class ParticleColorShaderModule extends AbstractShaderModule {

        public static final String ALPHA_UNIFORM = "alpha";

        public ParticleColorShaderModule() {
            this.addShaderPatches(new CommentShaderPatch(ModularShaderConstants.FRAG_MAIN_COMMENT,
                    (s) -> "pixelColor = materialDiffuse;\n" +
                            "pixelColor.a = min(pixelColor.a, " + ALPHA_UNIFORM + ");\n",
                            CommentShaderPatch.ModificationType.PREPEND),
                    new FragUniformPatch("float " + ALPHA_UNIFORM));
        }

        @Override
        public void createUniforms(ShaderProgram shaderProgram, VertexFormat<?> vertexFormat,
                                   MaterialFormat materialFormat, String materialName) throws ShaderUniformException {
            shaderProgram.createUniform(ALPHA_UNIFORM);
        }

        @Override
        public void update(ShaderProgram shaderProgram, Projection projection, Camera camera) {

        }
    }

    // TODO instancing for particles
    public class ParticleContainer {

        private VertexArrayObject particleVao;

        public ParticleContainer() {
            this.particleVao = new VertexArrayObject();
            VertexBufferObject meshBuffer = new VertexBufferObject();
            VertexBufferObject positionBuffer = new VertexBufferObject();
            VertexBufferObject colorBuffer = new VertexBufferObject();
        }
    }
}
