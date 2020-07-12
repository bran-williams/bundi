package com.branwilliams.bundi.engine.core;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.openal.ALC10.*;

public class AudioManager implements Destructible {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ALCapabilities alCapabilities;

    private long alDevice;

    private long alContext;

    /**
     * Creates the OpenAL context using the default audio device.
     *
     * */
    public void createALContext() {
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);

        alDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        alContext = alcCreateContext(alDevice, attributes);
        if (!alcMakeContextCurrent(alContext)) {
            throw new RuntimeException("Unable to create OpenAL context");
        }

        ALCCapabilities alcCapabilities = ALC.createCapabilities(alDevice);
        alCapabilities = AL.createCapabilities(alcCapabilities);

        log.info("OpenAL context created using default device");
    }

    public ALCapabilities getAlCapabilities() {
        return alCapabilities;
    }

    public long getAlDevice() {
        return alDevice;
    }

    public long getAlContext() {
        return alContext;
    }

    @Override
    public void destroy() {
        alcDestroyContext(alContext);
        alcCloseDevice(alDevice);
    }
}
