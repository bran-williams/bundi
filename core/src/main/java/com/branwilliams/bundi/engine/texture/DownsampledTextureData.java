package com.branwilliams.bundi.engine.texture;


import com.branwilliams.bundi.engine.core.Destructible;

/**
 *
 * @author Brandon
 * @since August 17, 2019
 */
public class DownsampledTextureData implements Destructible {

    private TextureData original;

    private TextureData[] downsampled;

    public DownsampledTextureData(TextureData original, TextureData... downsampled) {
        this.original = original;
        this.downsampled = downsampled;
    }

    public TextureData getSample(int sample) {
        return downsampled[sample];
    }

    public int getSamples() {
        return downsampled.length;
    }

    public TextureData getOriginal() {
        return original;
    }

    public void setOriginal(TextureData original) {
        this.original = original;
    }

    public TextureData[] getDownsampled() {
        return downsampled;
    }

    public void setDownsampled(TextureData[] downsampled) {
        this.downsampled = downsampled;
    }

    @Override
    public void destroy() {
        original.destroy();

        for (TextureData downsampledData : downsampled) {
            downsampledData.destroy();
        }
    }

}
