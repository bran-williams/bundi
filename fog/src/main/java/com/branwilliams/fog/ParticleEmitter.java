package com.branwilliams.fog;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.texture.Texture;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleEmitter implements Destructible {

    private final Random random = new Random();

    private final List<Particle> particles;

    private final boolean randomDespawn;

    private final Texture[] textures;

    private Vector3f spawnPosition = new Vector3f();

    private Vector3f dispurseAmount = new Vector3f();

    private Vector3f particleVelocity = new Vector3f();

    private float gravity;

    private float scale = 1F;

    public ParticleEmitter(int particleCount, int particleLife, Texture... textures) {
        this(false, particleCount, particleLife, textures);
    }

    public ParticleEmitter(boolean randomDespawn, int particleCount, int particleLife, Texture... textures) {
        this.randomDespawn = randomDespawn;
        this.textures = textures;
        this.particles = new ArrayList<>(particleCount);
        for (int i = 0; i < particleCount; i++) {
            Particle particle = new Particle(textures[random.nextInt(textures.length)], particleLife);
            particles.add(particle);
        }
    }

    /**
     * Updates the list of particles.
     * */
    public void updateParticles(double deltaTime) {
        particles.forEach(particle -> {
            particle.kill();
            particle.applyPhysics(deltaTime);

            if (particle.isDead()) {
                particle.respawn();
            }
        });
    }

    public void respawnParticles() {
        particles.forEach(Particle::respawn);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Vector3f spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public Vector3f getDispurseAmount() {
        return dispurseAmount;
    }

    public void setDispurseAmount(Vector3f dispurseAmount) {
        this.dispurseAmount = dispurseAmount;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public Vector3f getVelocity() {
        return particleVelocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.particleVelocity = velocity;
    }

    @Override
    public void destroy() {
        for (Texture texture : textures) {
            texture.destroy();
        }
    }

    public class Particle {

        private float maxLife;

        private final Vector3f position;

        private final Vector3f velocity;

        private float life;

        public final Texture texture;

        private Particle(Texture texture, float maxLife) {
            this.texture = texture;
            this.maxLife = maxLife;
            this.position = new Vector3f();
            this.velocity = new Vector3f();
            respawn();
//            this.life = maxLife * random.nextFloat();
        }

        private void respawn() {
//            this.life = maxLife * (0.8F + random.nextFloat() * 0.2F);
            this.life = maxLife * random.nextFloat();

            this.position.x = spawnPosition.x + (random.nextFloat() - 0.5F) * dispurseAmount.x;
            this.position.y = spawnPosition.y + (random.nextFloat() - 0.5F) * dispurseAmount.y;
            this.position.z = spawnPosition.z + (random.nextFloat() - 0.5F) * dispurseAmount.z;

            this.velocity.x = (random.nextFloat() - 0.5F) * particleVelocity.x;
            this.velocity.y = (random.nextFloat() - 0.5F) * particleVelocity.y;
            this.velocity.z = (random.nextFloat() - 0.5F) * particleVelocity.z;
        }

        private void applyPhysics(double deltaTime) {
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
            position.z += velocity.z * deltaTime;

            velocity.x *= 0.99F;
            velocity.y *= 0.99F;
            velocity.z *= 0.99F;

            velocity.y += gravity * deltaTime;
        }

        /**
         * Kills this particle, aka decrements the life value by some random amount between 0~2.
         * */
        private void kill() {
            if (!randomDespawn || random.nextBoolean()) {
                life -= random.nextFloat() * 2;
            }
        }

        public float getDistance(Vector3f position) {
            return position.distance(this.position);
        }

        /**
         * @return True if this particles life is <= 0.
         * */
        public boolean isDead() {
            return life <= 0;
        }

        /**
         *
         * */
        public float getMaxLife() {
            return maxLife;
        }

        public float getLife() {
            return life;
        }

        public float getX() {
            return position.x;
        }

        public float getY() {
            return position.y;
        }

        public float getZ() {
            return position.z;
        }
    }

}