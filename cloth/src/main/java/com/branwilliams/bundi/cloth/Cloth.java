package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class Cloth {

    private final ClothPhysicsParameters parameters;

    private final int width;

    private final int height;

    private ClothParticle[] particles;

    private List<ClothConstraint> constraints;

    private Mesh mesh;

    public Cloth(ClothPhysicsParameters parameters, int width, int height) {
        this.parameters = parameters;
        this.width = width;
        this.height = height;
        this.particles = new ClothParticle[width * height];
        this.constraints = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector3f position = new Vector3f(width * (x / (float) width), -height * (y / (float) height), 0);
                particles[x + y * width] = (new ClothParticle(parameters, position));
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < width - 1) makeConstraint(getParticle(x, y), getParticle(x + 1, y));
                if (y < height - 1) makeConstraint(getParticle(x, y), getParticle(x, y + 1));
                if (x < width - 1 && y < height - 1) {
                    makeConstraint(getParticle(x, y), getParticle(x + 1, y + 1));
                    makeConstraint(getParticle(x + 1, y), getParticle(x, y + 1));
                }

            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x < width - 2) makeConstraint(getParticle(x, y), getParticle(x + 2, y));
                if (y < height - 2) makeConstraint(getParticle(x, y), getParticle(x, y + 2));
                if (x < width - 2 && y < height - 2) {
                    makeConstraint(getParticle(x, y), getParticle(x + 2, y + 2));
                    makeConstraint(getParticle(x + 2, y), getParticle(x, y + 2));
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            getParticle(i, 0).offsetPosition(new Vector3f(0.5F, 0F, 0F));
            getParticle(i, 0).setMovable(false);

            getParticle(i, 0).offsetPosition(new Vector3f(-0.5F, 0F, 0F));
            getParticle(width - 1 - i, 0).setMovable(false);

        }
    }

    public void update() {
        for (int i = 0; i < parameters.getConstraintIterations(); i++) {
            for (ClothConstraint constraint : constraints) {
                constraint.satisfyConstraint();
            }
        }

        for (ClothParticle particle : particles) {
            particle.update();
        }
    }

    public void addForce(Vector3f force) {
        for (ClothParticle particle : particles) {
            particle.addForce(force);
        }
    }

    public void addWindForce(Vector3f direction) {
        for (int x = 0; x < width - 1; x++) {
            for (int y = 0; y < height - 1; y++) {
                addWindForcesForTriangle(getParticle(x + 1, y),getParticle(x, y),getParticle(x, y + 1),direction);
                addWindForcesForTriangle(getParticle(x + 1, y + 1),getParticle(x + 1, y),getParticle(x, y + 1),direction);
            }
        }
    }

    public ClothParticle getParticle(int x, int y) {
        return particles[x + y * width];
    }

    public Vector3f calculateTriangleNormal(ClothParticle particle1, ClothParticle particle2, ClothParticle particle3) {
        Vector3f v1 = particle2.getPosition().sub(particle1.getPosition(), new Vector3f());
        Vector3f v2 = particle3.getPosition().sub(particle1.getPosition(), new Vector3f());
        return v1.cross(v2);
    }

    private void addWindForcesForTriangle(ClothParticle particle1, ClothParticle particle2, ClothParticle particle3,
                                         Vector3f direction) {
        Vector3f normal = calculateTriangleNormal(particle1, particle2, particle3);
        Vector3f d = normal.normalize(new Vector3f());
        Vector3f force = normal.mul(d.dot(direction));
        particle1.addForce(force);
        particle2.addForce(force);
        particle3.addForce(force);
    }

    private void makeConstraint(ClothParticle particle1, ClothParticle particle2) {
        constraints.add(new ClothConstraint(particle1, particle2));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ClothParticle[] getParticles() {
        return particles;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
