package com.branwilliams.bundi.cloth;

import com.branwilliams.bundi.engine.mesh.Mesh;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class Cloth {

    private final ClothPhysicsParameters parameters;

    private final int particleSizeX;

    private final int particleSizeY;

    private ClothParticle[] particles;

    private List<ClothConstraint> constraints;

    private Mesh mesh;

    public Cloth(ClothPhysicsParameters parameters, int width, int height, int particleSizeX, int particleSizeY) {
        this.parameters = parameters;
        this.particleSizeX = particleSizeX;
        this.particleSizeY = particleSizeY;
        this.particles = new ClothParticle[particleSizeX * particleSizeY];
        this.constraints = new ArrayList<>();

        for (int x = 0; x < particleSizeX; x++) {
            for (int y = 0; y < particleSizeY; y++) {
                Vector3f position = new Vector3f(width * (x / (float) particleSizeX), -height * (y / (float) particleSizeY), 0);
                particles[x + y * particleSizeX] = (new ClothParticle(parameters, position));
            }
        }

        for (int x = 0; x < particleSizeX; x++) {
            for (int y = 0; y < particleSizeY; y++) {
                if (x < particleSizeX - 1) makeConstraint(getParticle(x, y), getParticle(x + 1, y));
                if (y < particleSizeY - 1) makeConstraint(getParticle(x, y), getParticle(x, y + 1));
                if (x < particleSizeX - 1 && y < particleSizeY - 1) {
                    makeConstraint(getParticle(x, y), getParticle(x + 1, y + 1));
                    makeConstraint(getParticle(x + 1, y), getParticle(x, y + 1));
                }
            }
        }

        for (int x = 0; x < particleSizeX; x++) {
            for (int y = 0; y < particleSizeY; y++) {
                if (x < particleSizeX - 2) makeConstraint(getParticle(x, y), getParticle(x + 2, y));
                if (y < particleSizeY - 2) makeConstraint(getParticle(x, y), getParticle(x, y + 2));
                if (x < particleSizeX - 2 && y < particleSizeY - 2) {
                    makeConstraint(getParticle(x, y), getParticle(x + 2, y + 2));
                    makeConstraint(getParticle(x + 2, y), getParticle(x, y + 2));
                }
            }
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
            particle.addForce(force.mul(parameters.getTimeStepSize2()));
        }
    }

    public void addWindForce(Vector3f direction) {
        direction.mul(parameters.getTimeStepSize2());
        for (int x = 0; x < particleSizeX - 1; x++) {
            for (int y = 0; y < particleSizeY - 1; y++) {
                addWindForcesForTriangle(getParticle(x + 1, y),getParticle(x, y),getParticle(x, y + 1), direction);
                addWindForcesForTriangle(getParticle(x + 1, y + 1),getParticle(x + 1, y),getParticle(x, y + 1), direction);
            }
        }
    }

    public ClothParticle getParticle(int x, int y) {
        return particles[x + y * particleSizeX];
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

    public void collideWithSphere(Vector3f position, float radius) {
        for (ClothParticle particle : particles) {
            Vector3f difference = particle.getPosition().sub(position, new Vector3f());
            float distance = difference.length();
            if (distance < radius) {
                particle.offsetPosition(difference.normalize().mul(radius - distance));
            }
        }
    }

    public int getParticleSizeX() {
        return particleSizeX;
    }

    public int getParticleSizeY() {
        return particleSizeY;
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

    public ClothPhysicsParameters getParameters() {
        return parameters;
    }
}
