package com.mycompany.flappybird;

/**
 *
 * @author tayog
 */

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Background {
    private Image dayBackground;
    private Image nightBackground;
    private boolean nightMode = false;

    public Background() {
        try {
            // Load day background
            URL dayResource = getClass().getResource("/assets/background.png");
            dayBackground = ImageIO.read(dayResource);
            
            // Load night background
            URL nightResource = getClass().getResource("/assets/backgroundNight.png");
            nightBackground = ImageIO.read(nightResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public void draw(Graphics g, int width, int height) {
        Image currentBackground = nightMode ? nightBackground : dayBackground;
        if (currentBackground != null) {
            g.drawImage(currentBackground, 0, 0, width, height, null);
        }
    }
}