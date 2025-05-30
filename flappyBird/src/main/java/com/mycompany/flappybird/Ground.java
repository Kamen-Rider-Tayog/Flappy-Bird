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

public class Ground {
    private Image groundImageDay;
    private Image groundImageNight;
    private int[] xPositions;
    private int tileWidth;
    private int numTiles;
    private double speed = 4;
    private int yPosition;
    private boolean nightMode = false;

    public Ground(int screenWidth, int screenHeight) {
        try {
            // Load day ground
            URL dayResource = getClass().getResource("/assets/ground.png");
            groundImageDay = ImageIO.read(dayResource);
            
            // Load night ground
            URL nightResource = getClass().getResource("/assets/groundNight.png");
            groundImageNight = ImageIO.read(nightResource);
            
            // Use day ground as default
            tileWidth = groundImageDay.getWidth(null);
            yPosition = screenHeight - groundImageDay.getHeight(null) - 35;
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize tile positions
        numTiles = (screenWidth / tileWidth) + 4;
        xPositions = new int[numTiles];
        for (int i = 0; i < numTiles; i++) {
            xPositions[i] = i * tileWidth;
        }
    }
    
    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public void update() {
        for (int i = 0; i < numTiles; i++) {
            xPositions[i] -= speed;
        }

        // Wrap tiles that move off-screen
        for (int i = 0; i < numTiles; i++) {
            if (xPositions[i] + tileWidth <= 0) {
                int maxX = Integer.MIN_VALUE;
                for (int x : xPositions) {
                    if (x > maxX) {
                        maxX = x;
                    }
                }
                xPositions[i] = maxX + tileWidth;
            }
        }
    }

    public void draw(Graphics g) {
        Image currentGround = nightMode ? groundImageNight : groundImageDay;
        if (currentGround != null) {
            for (int i = 0; i < numTiles; i++) {
                g.drawImage(currentGround, xPositions[i], yPosition, null);
            }
        }
    }
}