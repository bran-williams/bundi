package com.branwilliams.bundi.engine.audio;

import com.branwilliams.bundi.engine.core.Destructible;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

/**
 * @author Brandon
 * @since August 08, 2019
 */
public class AudioData implements Destructible {

    private final int format;

    private final int sampleRate;

    private final ShortBuffer data;

    public AudioData(int format, int sampleRate, ShortBuffer data) {
        this.format = format;
        this.sampleRate = sampleRate;
        this.data = data;
    }

    public int getFormat() {
        return format;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public ShortBuffer getData() {
        return data;
    }

    @Override
    public void destroy() {
//            LibCStdlib.free(data);
        MemoryUtil.memFree(data);
    }
}
