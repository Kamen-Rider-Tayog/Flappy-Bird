package com.mycompany.flappybird;

/**
 *
 * @author tayog
 */

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Clip> soundClips = new HashMap<>();
    private static Clip backgroundMusic;

    static {
        loadSound("background", "/assets/sounds/background.wav");
        loadSound("flap", "/assets/sounds/flap.wav");
        loadSound("die", "/assets/sounds/die.wav");
        loadSound("point", "/assets/sounds/point.wav");
        loadSound("hit", "/assets/sounds/hit.wav");
    }

    public static void loadSound(String name, String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            if (url == null) {
                System.err.println("Sound file not found: " + path);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundClips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound: " + name);
            e.printStackTrace();
        }
    }

    public static void playSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + name);
            }
        }
    }

    public static void startBackgroundMusic() {
        if (backgroundMusic == null) {
            backgroundMusic = soundClips.get("background");
        }
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
}