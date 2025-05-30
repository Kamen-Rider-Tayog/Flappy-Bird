package com.mycompany.flappybird;

/**
 *
 * @author tayog
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Game components
    private final Background background;
    private final Ground ground;
    private final Bird bird;
    private final List<Pipe> pipes;
    private final Timer timer;
    private final Runnable returnToHomeCallback;
    private Font pixelFont;
    
    // Game state
    private int score = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private boolean nightModeActive = false;
    
    // Constants
    private static final int PANEL_WIDTH = 480;
    private static final int PANEL_HEIGHT = 854;
    private static final int PIPE_SPACING = 250;
    private static final int NUM_PIPES = 3;
    private static final Color TEXT_SHADOW = new Color(0, 0, 0, 150);
    private static final Color PRIMARY_TEXT = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(150, 255, 150);
    private long nightModeActivatedTime = 0;
    private final int NIGHT_MODE_MESSAGE_DURATION = 3000;

    public GamePanel(Runnable returnToHomeCallback) {
        this.returnToHomeCallback = returnToHomeCallback;
        
        // Initialize font
        initializeFont();
        
        // Game entities
        background = new Background();
        ground = new Ground(PANEL_WIDTH, PANEL_HEIGHT);
        bird = new Bird(PANEL_WIDTH, PANEL_HEIGHT);
        pipes = initializePipes();

        // Setup panel
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(30, this);
    }

    private void initializeFont() {
        try {
            URL fontUrl = getClass().getResource("/assets/fonts/pixel-font.ttf");
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
            pixelFont = baseFont.deriveFont(36f);
        } catch (Exception e) {
            System.err.println("Error loading pixel font, using fallback");
            pixelFont = new Font("Arial", Font.BOLD, 36);
        }
    }

    private List<Pipe> initializePipes() {
        List<Pipe> pipes = new ArrayList<>();
        for (int i = 0; i < NUM_PIPES; i++) {
            pipes.add(new Pipe(PANEL_WIDTH + i * PIPE_SPACING));
        }
        return pipes;
    }

    public void startGame() {
        if (!timer.isRunning()) {
            timer.start();
        }
        gameStarted = true;
        SoundManager.playSound("flap"); // Initial flap sound
        requestFocusInWindow();
        System.out.println("Game start.");
    }

    public void stopGame() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Configure rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // Draw game elements
        background.draw(g, getWidth(), getHeight());
        pipes.forEach(pipe -> pipe.draw(g));
        ground.draw(g);
        bird.draw(g);
        
        // Draw UI elements
        drawUI(g2d);
    }

    private void drawUI(Graphics2D g2d) {
        drawScore(g2d);

        // Draw achievement message
        if (nightModeActive) {
            long elapsed = System.currentTimeMillis() - nightModeActivatedTime;
            if (elapsed <= NIGHT_MODE_MESSAGE_DURATION) {
                drawAchievementMessage(g2d);
            }
        }


        if (gameOver) {
            drawGameOverScreen(g2d);
        } else if (!gameStarted) {
            drawStartInstruction(g2d);
        }
    }
    
    private void drawAchievementMessage(Graphics2D g2d) {
        g2d.setFont(pixelFont.deriveFont(18f));
        String message = "NIGHT MODE UNLOCKED!";
        int textWidth = g2d.getFontMetrics().stringWidth(message);
        int yPosition = 120; // Position near the top

        // Text
        g2d.setColor(new Color(255, 215, 0)); // Gold color
        g2d.drawString(message, (getWidth() - textWidth)/2, yPosition);
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setFont(pixelFont.deriveFont(36f));
        String scoreText = Integer.toString(score);
        int textWidth = g2d.getFontMetrics().stringWidth(scoreText);
        
        // Shadow
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(scoreText, (getWidth() - textWidth)/2 + 3, 65);
        
        // Main text
        g2d.setColor(PRIMARY_TEXT);
        g2d.drawString(scoreText, (getWidth() - textWidth)/2, 62);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        // Dark overlay
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Over text
        drawTextWithShadow(g2d, "GAME OVER", 48f, new Color(255, 50, 50), getHeight()/2 - 48);
        
        // Final score
        drawTextWithShadow(g2d, "SCORE: " + score, 24f, PRIMARY_TEXT, getHeight()/2 + 3);
        
        // Restart instruction
        drawTextWithShadow(g2d, "PRESS SPACE TO RESTART", 18f, ACCENT_COLOR, getHeight()/2 + 43);
    }

    private void drawStartInstruction(Graphics2D g2d) {
        drawTextWithShadow(g2d, "PRESS SPACE TO START", 24f, ACCENT_COLOR, getHeight()/2 + 3);
    }

    private void drawTextWithShadow(Graphics2D g2d, String text, float size, Color color, int yPos) {
        g2d.setFont(pixelFont.deriveFont(size));
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int xPos = (getWidth() - textWidth) / 2;
        
        // Shadow
        g2d.setColor(TEXT_SHADOW);
        g2d.drawString(text, xPos + 2, yPos + 2);
        
        // Main text
        g2d.setColor(color);
        g2d.drawString(text, xPos, yPos);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && gameStarted) {
            bird.update();

            for (Pipe pipe : pipes) {
                pipe.update();

                if (pipe.getX() + pipe.getWidth() < 0) {
                    int maxX = pipes.stream().mapToInt(Pipe::getX).max().orElse(PANEL_WIDTH);
                    pipe.reset(maxX + PIPE_SPACING);
                }

                // Check for scoring
                if (pipe.getX() + pipe.getWidth() < bird.getX() && !pipe.isScored()) {
                    score++;
                    pipe.setScored(true);
                    SoundManager.playSound("point");
                    System.out.println("Score: " + score);
                    
                    // Activate night mode 
                    if (score >= 20 && !nightModeActive) {
                        nightModeActive = true;
                        nightModeActivatedTime = System.currentTimeMillis(); // Record activation time
                        background.setNightMode(true);
                        ground.setNightMode(true);
                        SoundManager.playSound("point"); // Play extra sound
                    }

                }
            }

            ground.update();
            checkCollisions();
        }
        repaint();
    }
    
    private void checkCollisions() {
        Rectangle birdBounds = bird.getBounds();
        
        // Ground collision
        if (bird.getY() + bird.getHeight() >= PANEL_HEIGHT - 35 - 130) {
            if (!gameOver) {
                SoundManager.playSound("die"); // Death sound
                System.out.println("Bird died.");
            }
            gameOver = true;
            return;
        }
        
        // Ceiling collision
        if (bird.getY() <= 0) {
            if (!gameOver) {
                SoundManager.playSound("die"); // Death sound
                System.out.println("Bird died.");
            }
            gameOver = true;
            return;
        }
        
        // Pipe collision
        for (Pipe pipe : pipes) {
            Rectangle[] pipeBounds = pipe.getBounds();
            if (birdBounds.intersects(pipeBounds[0]) || birdBounds.intersects(pipeBounds[1])) {
                if (!gameOver) {
                    SoundManager.playSound("hit"); // Hit sound
                    System.out.println("Bird died.");
                }
                gameOver = true;
                return;
            }
        }
    }
    
    private void returnToHome() {
        stopGame();
        restartGame();
        if (returnToHomeCallback != null) {
            returnToHomeCallback.run();
        }
    }

    private void restartGame() {
        gameOver = false;
        gameStarted = false;
        nightModeActive = false;
        background.setNightMode(false);
        ground.setNightMode(false);
        score = 0;
        bird.reset(PANEL_WIDTH, PANEL_HEIGHT);
        for (int i = 0; i < pipes.size(); i++) {
            pipes.get(i).reset(PANEL_WIDTH + i * PIPE_SPACING);
        }
        System.out.println("Game restarted.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                returnToHome();
            } else {
                if (!gameStarted) {
                    gameStarted = true;
                }
                bird.jump();
                SoundManager.playSound("flap"); // Flap sound
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}