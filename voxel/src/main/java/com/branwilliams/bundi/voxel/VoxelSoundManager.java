package com.branwilliams.bundi.voxel;

import com.branwilliams.bundi.engine.audio.AudioData;
import com.branwilliams.bundi.engine.audio.AudioLoader;
import com.branwilliams.bundi.engine.audio.AudioSource;
import com.branwilliams.bundi.engine.audio.Sound;
import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;

import java.util.HashMap;
import java.util.Map;

public class VoxelSoundManager implements Destructible {

    private final VoxelScene scene;

    private final Map<String, Sound> sounds;

    private AudioLoader audioLoader;

    private AudioSource musicSource;

    private AudioSource buttonSoundSource;

    public VoxelSoundManager(VoxelScene scene) {
        this.scene = scene;
        this.sounds = new HashMap<>();
    }

    public void initialize(Engine engine) {
        audioLoader = new AudioLoader(engine.getContext().getAssetDirectory());

        Sound buttonSound = getSound("sounds/hit1.ogg");

        buttonSoundSource = new AudioSource();
        buttonSoundSource.setPlayback(buttonSound);


        Sound backgroundMusic = getSound("music/ffblitz.ogg");

        musicSource = new AudioSource();
        musicSource.setLooping(true);
        musicSource.setGain(scene.getGameSettings().getMusicVolume());
        musicSource.setPlayback(backgroundMusic);
    }

    public Sound getSound(String soundFile) {
        return sounds.computeIfAbsent(soundFile, (s -> createSoundFromFile(soundFile)));
    }

    protected Sound createSoundFromFile(String soundFile) {
        AudioData audioData = audioLoader.loadAudio(soundFile);
        Sound sound = new Sound(audioData);
        return sound;
    }

    public void playButtonSoundEffect() {
        buttonSoundSource.play();
    }

    public AudioSource getMusicSource() {
        return musicSource;
    }

    public AudioSource getButtonSoundSource() {
        return buttonSoundSource;
    }

    @Override
    public void destroy() {
        musicSource.destroy();
        buttonSoundSource.destroy();

        sounds.forEach((soundFile, sound) -> sound.destroy());
        sounds.clear();
    }
}
