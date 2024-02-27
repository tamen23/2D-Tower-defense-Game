import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Frame extends JFrame {
    private Clip clip;
    public static String title = "Leo Tower Defense Game";
    public static Dimension size = new Dimension(700, 550);
    private JPanel mainMenu; // Declare mainMenu as an instance variable
    private Screen screen; // Maintain a reference to the Screen

    public Frame() {
        title = "Leo Tower Defense Game";
        this.setTitle(title);
        this.setSize(size);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.init();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMusic();
                System.exit(0);
            }
        });
    }

    public void init() {
        this.setLayout(new GridLayout(1, 1, 0, 0));
        mainMenu = new JPanel(new GridBagLayout()); // Initialize mainMenu
        GridBagConstraints c = new GridBagConstraints();
    
        // Button size and padding
        int buttonWidth = 150;
        int buttonHeight = 50;
        int padding = 10; // padding between buttons
    
        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    
        JButton howToButton = new JButton("How to play");
        howToButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        howToButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 // Define the instructions message
                 String message = "Instructions on how to play the game:\n\n" +
                 "1. Place your towers along the path to defeat oncoming enemies.\n" +
                 "2. Upgrade your towers to increase their effectiveness.\n" +
                 "3. Manage your resources carefully to survive as many waves as possible.\n" +
                 "4. If an enemy reaches the end of the path, you will lose a life.\n" +
                 "5. The game is over when you run out of lives or complete all the waves.\n\n" +
                 "Good luck and have fun!";

                // Display the instructions in a message dialog
                JOptionPane.showMessageDialog(null, message, "How to Play", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    
        JButton quitButton = new JButton("Quit Game");
        quitButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    
        // Layout constraints for the buttons
        c.insets = new Insets(padding, padding, padding, padding); // Add padding around components
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL; // Make the button expand to fill the space
        mainMenu.add(startButton, c);
    
        c.gridy++; // Increment the y position for the next button
        mainMenu.add(howToButton, c);
    
        c.gridy++; // Increment the y position for the next button
        mainMenu.add(quitButton, c);
    
        this.add(mainMenu);
        this.setVisible(true);
    }   

    public void startGame() {
        playMusic("Music/music.wav"); // Replace with the path to your audio file
        this.remove(mainMenu);
        screen = new Screen(this);
        this.add(screen);
        this.revalidate();
        this.repaint();
    }
    
    public static void main(String[] args) {
        Frame frame = new Frame();
    }


   
    
    

    private void playMusic(String path) {
        try {
            // Open an audio input stream.
            URL url = this.getClass().getClassLoader().getResource(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously.
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
