package com.mycompany.flappybird;

/**
 *
 * @author tayog
 */

import javax.swing.JFrame;
import java.awt.CardLayout;
import javax.swing.JPanel;

public class FlappyBird {
    private static JFrame window;
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static HomePanel homePanel;
    private static GamePanel gamePanel;

    public static void main(String[] args) {
        initializeSounds();
        window = new JFrame("Flappy Bird");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create panels
        homePanel = new HomePanel(() -> showGamePanel());
        gamePanel = new GamePanel(() -> showHomePanel());
        
        // Add panels to card layout
        mainPanel.add(homePanel, "home");
        mainPanel.add(gamePanel, "game");
        
        // Window setup
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(480, 854);
        window.add(mainPanel);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false);
        
        System.out.println("Java start.");
        // Start with home panel
        showHomePanel();
    }
    
    private static void showHomePanel() {
        cardLayout.show(mainPanel, "home");
        homePanel.resumeAnimation(); // Ensure animation restarts
        homePanel.requestFocusInWindow();
    }
    
    private static void showGamePanel() {
        cardLayout.show(mainPanel, "game");
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
    }
    
    private static void initializeSounds() {
        SoundManager.loadSound("background", "/assets/sounds/background.wav");
        SoundManager.loadSound("flap", "/assets/sounds/flap.wav");
        SoundManager.loadSound("die", "/assets/sounds/die.wav");
        SoundManager.loadSound("point", "/assets/sounds/point.wav");
        SoundManager.loadSound("hit", "/assets/sounds/hit.wav");
    }
}