package com.branwilliams.bundi.voxel;

import com.branwilliams.bundi.engine.audio.AudioData;
import com.branwilliams.bundi.engine.audio.AudioLoader;
import com.branwilliams.bundi.engine.audio.AudioSource;
import com.branwilliams.bundi.engine.audio.Sound;
import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.core.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class VoxelSoundManager implements Destructible {

    private static final Logger LOG = LoggerFactory.getLogger(VoxelSoundManager.class);

    private final VoxelScene scene;

    private final Map<String, Sound> sounds;

    private final Map<String, AudioSource> sources;

    private AudioLoader audioLoader;

    private Sound buttonSound;

    private AudioSource buttonSoundSource;

    private Sound backgroundMusic;

    private AudioSource backgroundMusicSource;

    public VoxelSoundManager(VoxelScene scene) {
        this.scene = scene;
        this.sounds = new HashMap<>();
        this.sources = new HashMap<>();
    }

    public void initialize(Engine engine) {
        audioLoader = new AudioLoader(engine.getContext().getAssetDirectory());

        buttonSound = loadSound("sounds/hit1.ogg");

        buttonSoundSource = loadAudioSource("button_source");
        buttonSoundSource.setPlayback(buttonSound);

        backgroundMusic = loadSound("music/ffblitz.ogg");

        backgroundMusicSource = loadAudioSource("music_source");
        backgroundMusicSource.setLooping(true);
        backgroundMusicSource.setPlayback(backgroundMusic);
    }

    public AudioSource loadAudioSource(String sourceName) {
        AudioSource audioSource = sources.get(sourceName);

        if (audioSource == null) {
            audioSource = new AudioSource();
            sources.put(sourceName, audioSource);
        }

        return audioSource;
    }

    public Sound loadSound(String soundFile) {
        Sound sound = sounds.get(soundFile);

        if (sound == null) {
            AudioData audioData = loadSoundFromFile(soundFile);
            sound = putSound(soundFile, audioData);
        }

        return sound;
    }

    protected AudioData loadSoundFromFile(String soundFile) {
        AudioData audioData = audioLoader.loadAudio(soundFile);
        if (audioData == null) {
            LOG.error("Unable to find soundFile: {}", soundFile);
            return null;
        }
        return audioData;
    }

    public AudioSource putAudioSource(String soundFile, AudioSource audioSource) {
        sources.put(soundFile, audioSource);
        return audioSource;
    }

    public Sound putSound(String soundFile, AudioData audioData) {
        Sound sound = new Sound(audioData);
        sounds.put(soundFile, sound);
        return sound;
    }

    public void playButtonSoundEffect() {
        buttonSoundSource.play();
    }

    public AudioSource getBackgroundMusicSource() {
        return backgroundMusicSource;
    }

    public AudioSource getButtonSoundSource() {
        return buttonSoundSource;
    }

    @Override
    public void destroy() {
        backgroundMusicSource.destroy();
        buttonSoundSource.destroy();

        sounds.forEach((soundFile, sound) -> sound.destroy());
        sounds.clear();
        sources.forEach((sourceFile, source) -> source.destroy());
        sources.clear();
    }
}
