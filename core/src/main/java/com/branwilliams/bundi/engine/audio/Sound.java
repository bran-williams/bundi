package com.branwilliams.bundi.engine.audio;

import com.branwilliams.bundi.engine.core.Destructible;

import static org.lwjgl.openal.AL10.*;

/**
 * @author Brandon
 * @since May 05, 2019
 */
public class Sound implements Destructible {

    private final int id;

    public Sound(AudioData audioData) {
        id = alGenBuffers();
        alBufferData(id, audioData.getFormat(), audioData.getData(), audioData.getSampleRate());
    }

    @Override
    public void destroy() {
        alDeleteBuffers(id);
    }

    public int getId() {
        return id;
    }
}
