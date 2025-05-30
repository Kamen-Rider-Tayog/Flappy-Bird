package com.mycompany.flappybird;

/**
 *
 * @author tayog
 */

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class HomePanel extends JPanel implements KeyListener, ActionListener {
    private Background background;
    private Ground ground;
    private Bird bird;
    private Runnable startGameCallback;
    private Image titleImage;
    private Timer animationTimer;
    private Font pixelFont; // Added pixel font
    
    private static final int PANEL_WIDTH = 480;
    private static final int PANEL_HEIGHT = 854;

    public HomePanel(Runnable startGameCallback) {
        this.startGameCallback = startGameCallback;
        
        background = new Background();
        ground = new Ground(PANEL_WIDTH, PANEL_HEIGHT);
        bird = new Bird(PANEL_WIDTH, PANEL_HEIGHT);
        
        // Load custom pixel font
        try {
            URL fontUrl = getClass().getResource("/assets/fonts/pixel-font.ttf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            pixelFont = baseFont.deriveFont(32f); // Base size for title
        } catch (Exception e) {
            System.err.println("Error loading pixel font, using fallback");
            pixelFont = new Font("Arial", Font.BOLD, 32); // Fallback font
        }
        
        setFocusable(true);
        addKeyListener(this);
        
        animationTimer = new Timer(30, this);
        animationTimer.start();
        
        loadImages();
        SoundManager.startBackgroundMusic();
    }
    
    private void loadImages() {
        try {
            URL logoResource = getClass().getResource("/assets/images/logo.png");
            if (logoResource != null) {
                titleImage = ImageIO.read(logoResource);
            }
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        bird.animate();
        ground.update();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // General anti-aliasing for graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        background.draw(g, getWidth(), getHeight());
        ground.draw(g);
        bird.draw(g);

        if (titleImage != null) {
            int originalWidth = titleImage.getWidth(null);
            int originalHeight = titleImage.getHeight(null);
            int logoWidth = 280;
            int logoHeight = (int)((double)logoWidth * originalHeight / originalWidth);
            int logoX = (getWidth() - logoWidth) / 2;
            int logoY = getHeight() / 5;
            g.drawImage(titleImage, logoX, logoY, logoWidth, logoHeight, null);
        } else {
            drawFallbackTitle(g2d);
        }
        
        drawInstructions(g2d);
    }
    
    private void drawFallbackTitle(Graphics2D g2d) {
        // Set pixel font with custom size
        Font titleFont = pixelFont.deriveFont(48f);
        g2d.setFont(titleFont);
        
        // Configure text rendering for pixel perfection
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                           RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        String title = "FLAPPY BIRD";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        
        // Text shadow
        g2d.setColor(Color.BLACK);
        g2d.drawString(title, (getWidth() - titleWidth)/2 + 3, getHeight()/4 + 3);
        
        // Main text
        g2d.setColor(new Color(255, 215, 0)); // Gold color
        g2d.drawString(title, (getWidth() - titleWidth)/2, getHeight()/4);
    }
    
    private void drawInstructions(Graphics2D g2d) {
        // Set smaller pixel font size
        Font instructionFont = pixelFont.deriveFont(16f);
        g2d.setFont(instructionFont);
        
        // Maintain pixel-perfect text rendering
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        String instruction = "PRESS SPACE TO START";
        int textWidth = g2d.getFontMetrics().stringWidth(instruction);
        int yPosition = PANEL_HEIGHT - 120; // Adjusted position
        
        // Text shadow
        g2d.setColor(Color.BLACK);
        g2d.drawString(instruction, (getWidth() - textWidth)/2 + 2, yPosition + 2);
        
        // Main text
        g2d.setColor(Color.WHITE);
        g2d.drawString(instruction, (getWidth() - textWidth)/2, yPosition);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            startGame();
        }
    }

    private void startGame() {
        animationTimer.stop();
        SoundManager.stopBackgroundMusic();
        if (startGameCallback != null) {
            startGameCallback.run();
        }
    }

    public void resumeAnimation() {
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
        SoundManager.startBackgroundMusic();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}