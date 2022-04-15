package com.branwilliams.bundi.engine.audio;

import com.branwilliams.bundi.engine.core.context.EngineContext;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

/**
 * @author Brandon
 * @since May 05, 2019
 */
public class AudioLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Path directory;

    public AudioLoader(EngineContext context) {
        this(context.getAssetDirectory());
    }

    public AudioLoader(Path directory) {
        this.directory = directory;
    }

    public AudioData loadAudio(String location) {
        Path audioLocation = directory.resolve(location);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channelsBuffer = stack.stackMallocInt(1);
            IntBuffer sampleRateBuffer = stack.stackMallocInt(1);

            ShortBuffer audioData = stb_vorbis_decode_filename(audioLocation.toString(), channelsBuffer, sampleRateBuffer);

            // Find the correct OpenAL format
            int channels = channelsBuffer.get();
            int format;
            if(channels == 1) {
                format = AL_FORMAT_MONO16;
            } else {
                format = AL_FORMAT_STEREO16;
            }

            return new AudioData(format, sampleRateBuffer.get(), audioData);
        }
    }

}
