package com.mycompany.flappybird;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javax.imageio.ImageIO;

public class Pipe {
    private static final int SPEED = 2;
    private static final int GAP = 200; // Space for the bird to pass
    private static final int PIPE_WIDTH = 70;
    private static final int PIPE_HEIGHT = (int)(PIPE_WIDTH * (793.0 / 138.0)); // keep aspect ratio

    private int x;
    private int topPipeHeight;  // y position of bottom of top pipe (gap start)
    private Image pipeTopImage;
    private Image pipeBottomImage;
    private Random rand;
    private boolean scored = false; // Track if this pipe has been scored

    public Pipe(int startX) {
        x = startX;
        rand = new Random();
        topPipeHeight = rand.nextInt(200) + 150; // random vertical gap position

        try {
            URL topResource = getClass().getResource("/assets/images/pipe_top.png");  // flipped pipe image
            pipeTopImage = ImageIO.read(topResource);

            URL bottomResource = getClass().getResource("/assets/images/pipe_bottom.png");  // normal pipe image
            pipeBottomImage = ImageIO.read(bottomResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        x -= SPEED;
    }

    public void draw(Graphics g) {
        if (pipeTopImage == null || pipeBottomImage == null) return;

        // Draw top pipe
        g.drawImage(pipeTopImage, x, topPipeHeight - PIPE_HEIGHT, PIPE_WIDTH, PIPE_HEIGHT, null);

        // Draw bottom pipe
        g.drawImage(pipeBottomImage, x, topPipeHeight + GAP, PIPE_WIDTH, PIPE_HEIGHT, null);
    }
    
    public Rectangle[] getBounds() {
        Rectangle topPipe = new Rectangle(x, topPipeHeight - PIPE_HEIGHT, PIPE_WIDTH, PIPE_HEIGHT);
        Rectangle bottomPipe = new Rectangle(x, topPipeHeight + GAP, PIPE_WIDTH, PIPE_HEIGHT);
        return new Rectangle[]{topPipe, bottomPipe};
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return PIPE_WIDTH;
    }
    
    public boolean isScored() {
        return scored;
    }
    
    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public void reset(int newX) {
        x = newX;
        topPipeHeight = rand.nextInt(200) + 150;  // new random vertical gap position
        scored = false; // Reset scoring flag
    }
}