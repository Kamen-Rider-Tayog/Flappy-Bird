package com.mycompany.flappybird;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Bird {
    private static final int BIRD_WIDTH = 50;
    private static final int BIRD_HEIGHT = 35;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -10;
    private static final double TERMINAL_VELOCITY = 12;
    
    // Animation constants - corrected for 276x64 sprite sheet
    private static final int SPRITE_WIDTH = 92;   // Width of each sprite frame (276/3 = 92)
    private static final int SPRITE_HEIGHT = 64;  // Height of each sprite frame  
    private static final int FRAME_COUNT = 3;     // Number of animation frames
    private static final int ANIMATION_SPEED = 8; // Frames to wait between sprite changes (faster animation)
    
    private double x;
    private double y;
    private double velocityY;
    private BufferedImage spriteSheet;
    private BufferedImage[] birdFrames;
    private int currentFrame = 0;
    private int animationCounter = 0;
    private int screenHeight;
    
    public Bird(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        x = screenWidth / 4.0; // Position bird 1/4 from left
        y = screenHeight / 2.0; // Start in middle of screen
        velocityY = 0;
        
        loadSpriteSheet();
    }
    
    private void loadSpriteSheet() {
        try {
            URL resource = getClass().getResource("/assets/images/bird.png");
            if (resource != null) {
                spriteSheet = ImageIO.read(resource);
                
                // Extract individual frames from sprite sheet (horizontal layout)
                birdFrames = new BufferedImage[FRAME_COUNT];
                for (int i = 0; i < FRAME_COUNT; i++) {
                    // Make sure we don't go out of bounds
                    int frameX = i * SPRITE_WIDTH;
                    if (frameX + SPRITE_WIDTH <= spriteSheet.getWidth()) {
                        birdFrames[i] = spriteSheet.getSubimage(
                            frameX, 0, 
                            SPRITE_WIDTH, SPRITE_HEIGHT
                        );
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load bird sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void update() {
        // Apply gravity
        velocityY += GRAVITY;
        
        // Cap falling speed
        if (velocityY > TERMINAL_VELOCITY) {
            velocityY = TERMINAL_VELOCITY;
        }
        
        // Update position
        y += velocityY;
        
        // Keep bird on screen (basic bounds)
        if (y < 0) {
            y = 0;
            velocityY = 0;
        }
        if (y > screenHeight - BIRD_HEIGHT) {
            y = screenHeight - BIRD_HEIGHT;
            velocityY = 0;
        }
        
        // Update animation
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            animationCounter = 0;
        }
    }
    
    public void jump() {
        velocityY = JUMP_STRENGTH;
        SoundManager.playSound("flap");
        // Don't reset animation frame when jumping to keep smooth animation
    }
    
    public void draw(Graphics g) {
        if (birdFrames != null && currentFrame < birdFrames.length && birdFrames[currentFrame] != null) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Save the original transform
            AffineTransform originalTransform = g2d.getTransform();
            
            // Calculate rotation angle based on velocity for more realistic movement
            double angle = Math.toRadians(Math.max(-30, Math.min(30, velocityY * 3)));
            
            // Rotate around the center of the bird
            g2d.rotate(angle, x + BIRD_WIDTH/2, y + BIRD_HEIGHT/2);
            
            // Draw the bird
            g2d.drawImage(birdFrames[currentFrame], (int)x, (int)y, BIRD_WIDTH, BIRD_HEIGHT, null);
            
            // Restore the original transform
            g2d.setTransform(originalTransform);
        } else {
            // Fallback drawing if sprites fail to load
            g.setColor(java.awt.Color.YELLOW);
            g.fillOval((int)x, (int)y, BIRD_WIDTH, BIRD_HEIGHT);
            g.setColor(java.awt.Color.ORANGE);
            g.fillOval((int)x + 5, (int)y + 8, 8, 6); // Simple beak
        }
    }
    
    public Rectangle getBounds() {
        // Use slightly smaller bounds for more forgiving collision detection
        int margin = 3;
        return new Rectangle((int)x + margin, (int)y + margin, 
                           BIRD_WIDTH - 2*margin, BIRD_HEIGHT - 2*margin);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getWidth() {
        return BIRD_WIDTH;
    }
    
    public int getHeight() {
        return BIRD_HEIGHT;
    }
    
    public void reset(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        x = screenWidth / 4.0;
        y = screenHeight / 2.0;
        velocityY = 0;
        currentFrame = 0;
        animationCounter = 0;
    }
    
    public void animate() {
        // Update animation only
        animationCounter++;
        if (animationCounter >= ANIMATION_SPEED) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            animationCounter = 0;
        }
    }
}