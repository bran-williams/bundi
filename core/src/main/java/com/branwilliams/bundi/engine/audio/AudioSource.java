package com.branwilliams.bundi.engine.audio;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.shader.Transformable;
import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 * @author Brandon
 * @since May 05, 2019
 */
public class AudioSource implements Destructible {

    private final int id;

    public AudioSource() {
        id = alGenSources();
    }

    public AudioSource(Sound sound) {
        this();
        setPlayback(sound);
    }

    /**
     * Sets this audio sources buffer to the sound buffer specified.
     * */
    public void setPlayback(Sound sound) {
        alSourcei(id, AL_BUFFER, sound.getId());
    }

    public void setGain(float gain) {
        alSourcef(id, AL_GAIN, gain);
    }

    public void setPitch(float pitch) {
        alSourcef(id, AL_PITCH, pitch);
    }

    public void setPosition(Transformable transformable) {
        setPosition(transformable.getPosition());
    }

    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void setPosition(float x, float y, float z) {
        alSource3f(id, AL_POSITION, x, y, z);
    }

    public void setVelocity(Vector3f velocity) {
        setVelocity(velocity.x, velocity.y, velocity.z);
    }

    public void setVelocity(float x, float y, float z) {
        alSource3f(id, AL_VELOCITY, x, y, z);
    }

    public void play() {
        alSourcePlay(id);
    }

    public void pause() {
        alSourcePause(id);
    }

    public void stop() {
        alSourceStop(id);
    }

    public void setLooping(boolean looping) {
        alSourcei(id, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
    }

    public void rewind() {
        alSourceRewind(id);
    }

    @Override
    public void destroy() {
        alDeleteSources(id);
    }
}
