import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.event.ActionListener;

import java.io.InputStream;
import java.net.URL;

public class Screen extends JPanel implements Runnable {
    public Thread thread = new Thread(this);
    public static Image[] tileset_groundGrass = new Image[100];
    public static Image[] tileset_air = new Image[100];
    public static Image[] tileset_res = new Image[100];
    public static Image[] tileset_mob = new Image[100];
    public static int myWidth;
    public static int myHeight;
    JButton pauseButton;
    private volatile boolean isPaused = false;
    JPanel bottomPanel;
    JFrame pauseFrame;

    public static final int mobWarrior = 3;
    private boolean showScoreboard = false; // Flag to control scoreboard
    public static boolean isFirst = true;
    public static boolean isDebug = false;
    public static int killCount = 0;
    public static Room room;
    public static Save save;
    public static Point mse = new Point(0, 0);
    public static Store store;
    public static Mob[] mobs = new Mob[100];
    public static int coinage = 100;
    public static int health = 100;
    public static int level = 1;
    public int mobNumber;
    public int mobSpawned;
    public static int fpsFrame = 0;
    public static int fps = 1000000;
    public int spawnTime;
    public int spawnFrame;
    private ScoreBoard scoreBoard;

    public Screen(Frame var1) {
        this.setLayout(null);
        this.mobNumber = level * 20; // at first 3 // mob number
        this.mobSpawned = 0;
        this.spawnTime = 2000;
        this.spawnFrame = 0;
        var1.addMouseListener(new KeyHandel());
        var1.addMouseMotionListener(new KeyHandel());

        JPanel buttonPanel = new JPanel();
        pauseButton = new JButton("Pause");
        int buttonX = 595; // X position of the button on the screen
        int buttonY = 470; // Y position of the button on the screen
        int buttonWidth = 70; // Width of the button
        int buttonHeight = 30; // Height of the button

        // Set the position and size of the pauseButton
        pauseButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        pauseButton.addActionListener(e -> showPauseMenu());
        ActionListener pauseListener = e -> {
            togglePauseGame();
        };
        pauseButton.addActionListener(pauseListener);
        // buttonPanel.add(pauseButton);

        this.add(pauseButton);
        this.setPreferredSize(new Dimension(800, 600));
        this.thread.start();
        startCoinageIncrementTimer();
    }

    // Toggle pause game method using a boolean flag
    private void togglePauseGame() {
        isPaused = !isPaused;
        if (!isPaused) {
            // Resume the game logic
            synchronized (this) {
                notify();
            }
        }
    }

    private void showPauseMenu() {

        // Create a frame for the pause menu
        pauseFrame = new JFrame("Pause Menu");
        pauseFrame.setSize(300, 200);
        pauseFrame.setLayout(null);
        pauseFrame.setLocationRelativeTo(null);
        pauseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create buttons for the pause menu
        JButton resumeButton = new JButton("Resume");
        resumeButton.setBounds(50, 30, 200, 30);
        resumeButton.addActionListener(e -> {
            pauseFrame.setVisible(false);
            togglePauseGame();
            // Code to resume the game
        });

        JButton startMenuButton = new JButton("Starting Menu");
        startMenuButton.setBounds(50, 70, 200, 30);
        startMenuButton.addActionListener(e -> {
            // Dispose of the pause frame and the game frame immediately
            pauseFrame.dispose();
            Frame.getFrames()[0].dispose(); // Assuming the first frame is the game frame

            // Use SwingUtilities.invokeLater to ensure this runs on the EDT
            SwingUtilities.invokeLater(() -> {
                // Here you should implement the logic to create a new game frame
                // For example, if you have a method called createAndShowGameFrame(), call it
                // here
                createAndShowGameFrame();
            });
        });

        JButton leaveGameButton = new JButton("Leave Game");
        leaveGameButton.setBounds(50, 110, 200, 30);
        leaveGameButton.addActionListener(e -> {
            pauseFrame.setVisible(false);
            System.exit(0);
        });

        // Add buttons to the pause frame
        pauseFrame.add(resumeButton);
        pauseFrame.add(startMenuButton);
        pauseFrame.add(leaveGameButton);
        pauseFrame.setVisible(true);

    }

    private void createAndShowGameFrame() {
        // Create a new frame to start the game again
        JFrame newGameFrame = new Frame(); // Replace 'Frame' with your game frame class
        newGameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newGameFrame.setSize(800, 600); // Set the size of the frame
        newGameFrame.setLocationRelativeTo(null); // Center the frame
        newGameFrame.setVisible(true); // Make the frame visible
    }

    // Call this method from your game logic when needed
    public void closeCurrentGameFrame() {
        SwingUtilities.invokeLater(() -> {
            // Assuming the first frame is your game frame
            if (Frame.getFrames().length > 0) {
                Frame.getFrames()[0].dispose();
            }
        });
    }

    public void define() {
        room = new Room();
        save = new Save();
        store = new Store();
        URL var1 = this.getClass().getResource("Textures/Tile_GroundGrass.png");

        for (int var2 = 0; var2 < tileset_groundGrass.length; ++var2) {
            tileset_groundGrass[var2] = (new ImageIcon(var1)).getImage();
            tileset_groundGrass[var2] = this.createImage(new FilteredImageSource(tileset_groundGrass[var2].getSource(),
                    new CropImageFilter(0, 25 * var2, 25, 25)));
        }

        URL var12 = this.getClass().getResource("Textures/Tile_airAir.png");

        for (int var3 = 0; var3 < tileset_air.length; ++var3) {
            tileset_air[var3] = (new ImageIcon(var12)).getImage();
            tileset_air[var3] = this.createImage(
                    new FilteredImageSource(tileset_air[var3].getSource(), new CropImageFilter(0, 25 * var3, 25, 25)));
        }

        URL var13 = this.getClass().getResource("Textures/cell.png");
        tileset_res[0] = (new ImageIcon(var13)).getImage();

        URL var4 = this.getClass().getResource("Textures/coin.png");
        tileset_res[1] = (new ImageIcon(var4)).getImage();

        URL var5 = this.getClass().getResource("Textures/heart.png");
        tileset_res[2] = (new ImageIcon(var5)).getImage();

        URL var6 = this.getClass().getResource("Textures/mob1.png");
        tileset_mob[0] = (new ImageIcon(var6)).getImage();

        URL var7 = this.getClass().getResource("Textures/mob2.png");
        tileset_mob[2] = (new ImageIcon(var7)).getImage();

        URL var8 = this.getClass().getResource("Textures/boss1.png");
        tileset_mob[1] = (new ImageIcon(var8)).getImage();

        ClassLoader var9 = this.getClass().getClassLoader();
        InputStream var10 = var9.getResourceAsStream("Mission");
        save.loadSave(var10);

        for (int var11 = 0; var11 < mobs.length; ++var11) {
            mobs[var11] = new Mob();
        }
    }

    public void paintComponent(Graphics var1) {
        if (isFirst) {
            myWidth = this.getWidth();
            myHeight = this.getHeight();
            this.define();
            isFirst = false;
        }

        var1.setColor(new Color(60, 60, 60));
        var1.fillRect(0, 0, this.getWidth(), this.getHeight());
        var1.setColor(new Color(0, 0, 0));
        var1.drawLine(room.block[0][0].x - 1, 0, room.block[0][0].x - 1,
                room.block[room.worldHeight - 1][0].y + room.blockSize);
        var1.drawLine(room.block[0][room.worldWidth - 1].x + room.blockSize, 0,
                room.block[0][room.worldWidth - 1].x + room.blockSize,
                room.block[room.worldHeight - 1][0].y + room.blockSize);
        var1.drawLine(room.block[0][0].x, room.block[room.worldHeight - 1][0].y + room.blockSize,
                room.block[0][room.worldWidth - 1].x + room.blockSize,
                room.block[room.worldHeight - 1][0].y + room.blockSize);
        room.draw(var1);

        for (int var2 = 0; var2 < mobs.length; ++var2) {
            if (mobs[var2].inGame) {
                mobs[var2].draw(var1);
            }

            store.draw(var1);
            if (health < 1) {
                // Remove the pauseButton from the panel
                this.remove(pauseButton);
                var1.setColor(new Color(240, 20, 20));
                var1.fillRect(0, 0, myWidth, myHeight);
                var1.setColor(new Color(255, 255, 255));

                // Set the font to be bold and larger so it's more visible
                Font gameOverFont = new Font("Arial", Font.BOLD, 100);
                var1.setFont(gameOverFont);

                // Get metrics from the graphics
                FontMetrics metrics = var1.getFontMetrics(gameOverFont);
                // Determine the X coordinate for the text
                int x = (myWidth - metrics.stringWidth("Game Over!")) / 2;
                // Determine the Y coordinate for the text (note we add the ascent, as in java
                // 2d 0 is top of the screen)
                int y = ((myHeight - metrics.getHeight()) / 2) + metrics.getAscent();

                // Draw the string
                var1.drawString("Game Over!", x, y);
                if (scoreBoard != null) {
                    scoreBoard.displayScoreBoard();
                } else {
                    scoreBoard = new ScoreBoard(coinage, health, killCount, level);
                    scoreBoard.displayScoreBoard();
                }
            }

        }

    }

    public void startCoinageIncrementTimer() {
        Timer coinageTimer = new Timer(4000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                coinage += 1;
                // Increment the coinage by 2 every 4 seconds
                // System.out.println("Coinage incremented to: " + coinage);
            }
        });
        coinageTimer.start(); // Start the timer
    }

    public synchronized void run() {

        while (true) {
            // The outer loop should run indefinitely
            if (!isPaused) { // Check if the game is not paused
                if (!isFirst && health > 0) {
                    room.physic();
                    this.mobSpawner();

                    for (int var1 = 0; var1 < mobs.length; ++var1) {
                        if (mobs[var1].inGame) {
                            mobs[var1].physic();
                        }
                    }

                    if (killCount == this.mobNumber && health > 0) {
                        // ++level;
                        // killCount = 0;
                        // this.mobSpawned = 0;
                        scoreBoard = new ScoreBoard(coinage, health, killCount, level);
                        System.exit(0);
                        try {
                            Thread.sleep(1);
                            // System.exit(0);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                    }

                }
            } else {
                synchronized (this) {
                    try {
                        wait(); // Use wait() to release the lock and pause
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            this.repaint();

            try {
                Thread.sleep(1); // was first to 1
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public void mobSpawner() { //how the 20 mobs are calculated 
        if (this.mobSpawned < this.mobNumber) {
            if (this.spawnFrame >= this.spawnTime) {
                for (int var1 = 0; var1 < mobs.length; ++var1) {
                    if (!mobs[var1].inGame) {
                        mobs[var1] = new Mob();

                        // Spawn normal mobs
                        if (this.mobSpawned == this.mobNumber - 1) { // Ensure we leave space for the boss
                            // Spawn boss mob
                            mobs[this.mobNumber].towerDamage = 5;
                            mobs[var1].spawnMob(Value.mobBoss, 2000 * level, 22 * level, 100, 14 / level);
                            // System.out.println("Boss Spawned! Health: " + mobs[this.mobNumber].health + "
                            // Tower Damage: " + mobs[this.mobNumber].towerDamage + " Count: " +
                            // this.mobSpawned + " At Speed " + mobs[this.mobNumber].walkSpeed);
                        } else {

                            // Alternate between spawning mob1 and mobWarrior
                            if (this.mobSpawned % 2 == 0) {
                                mobs[var1].towerDamage = 10;
                                mobs[var1].spawnMob(Value.mobAngry, 1000 * level, 22 * level, 7, 22 / level); // First
                                                                                                              // type of
                                                                                                              // normal
                                                                                                              // mob
                                // System.out.println("Normal Spawned! Health: " + mobs[var1].health + " Tower
                                // Damage: " + mobs[var1].towerDamage + " Count: " + this.mobSpawned + " At
                                // Speed " + mobs[var1].walkSpeed);
                            } else {
                                mobs[var1].towerDamage = 15;
                                mobs[var1].spawnMob(Value.mobWarrior, 1200 * level, 22 * level, 10, 16 / level); // Second
                                                                                                                 // type
                                                                                                                 // of
                                                                                                                 // normal
                                                                                                                 // mob
                                                                                                                 // (mobWarrior)
                                // System.out.println("Normal Spawned! Health: " + mobs[var1].health + " Tower
                                // Damage: " + mobs[var1].towerDamage + " Count: " + this.mobSpawned + " At
                                // Speed " + mobs[var1].walkSpeed);
                            }

                        }

                        ++this.mobSpawned;
                        break;
                    }
                }

                this.spawnFrame = 0;
            } else {
                ++this.spawnFrame;
            }
        }
    }

}
