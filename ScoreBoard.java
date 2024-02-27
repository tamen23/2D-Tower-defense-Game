import javax.swing.*;
import java.awt.*;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;

public class ScoreBoard {

            // Sample data to display
    private static int coins;
    private static int bloodLevel;
    private static int mobsKilled;
    private static int level;
    private static double totalScore;
    private static double bestScore;
    private JPanel panel;

    public ScoreBoard(int coins, int bloodLevel, int mobsKilled, int level) {
        this.coins = coins;
        this.bloodLevel = bloodLevel;
        this.mobsKilled = mobsKilled;
        this.level = level;
        this.totalScore = coins + bloodLevel + mobsKilled + level;
        //this.totalScore = coins * bloodLevel;

        panel = new JPanel(new BorderLayout());

        // Read the best score from the file
        bestScore = readBestScore();
        

        // Check if the new score is a high score
        if (totalScore > bestScore) {
            bestScore = totalScore;
            writeBestScore((int) bestScore);
            JOptionPane.showMessageDialog(null, "YOU WIN \nNew High Score! Score: " + bestScore);
        } else {
            JOptionPane.showMessageDialog(null, "YOU WIN \nScore: " + totalScore);
        }

        
        // Display the score board with buttons
        displayScoreBoard();

    }

    public void displayScoreBoard() {
        // Column Names
        String[] columnNames = {"Description", "Value"};

        // Data to be displayed in the JTable
        Object[][] data = {
                {"Coins Left", coins},
                {"Blood Level", bloodLevel},
                {"Mobs Killed", mobsKilled},
                {"Level", level},
                {"Total Score", totalScore},
                {"Best Score", bestScore}
        };

        // Create table model
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create JTable with the model
        JTable table = new JTable(model);

        // Panel to hold the table and buttons
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton mainMenuButton = new JButton("Main Menu");
        JButton restartButton = new JButton("Restart");
        JButton exitButton = new JButton("Exit Game");

        // Add action listeners to buttons
        mainMenuButton.addActionListener(e -> goToMainMenu());
        restartButton.addActionListener(e -> restartGame());
        exitButton.addActionListener(e -> exitGame());

        // Add buttons to button panel
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        // Add button panel to the main panel
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Show the panel in a JOptionPane dialog
        //JOptionPane.showMessageDialog(null, panel, "Score Board", JOptionPane.PLAIN_MESSAGE);
        
        panel.setPreferredSize(new Dimension(300, 159));
        // Show the panel in a JOptionPane dialog without the default "Ok" button
        JOptionPane.showOptionDialog(null, panel, "Score Board", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{}, null);
    }

    private void goToMainMenu() {
        Window window = SwingUtilities.getWindowAncestor(panel);
        window.dispose();

        // Code to go to the main menu goes here
       new Frame();

    }

    private void restartGame() {

        Window window = SwingUtilities.getWindowAncestor(panel);
        window.dispose();

        // Code to restart the game goes here
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Frame frame = new Frame();
                frame.init(); // Set up the initial UI state.
                frame.startGame(); // Start the game logic.
                frame.setVisible(true); // Make sure to set the frame visible.
            }
        });

    }

    private void exitGame() {
        System.exit(0);
    }

    private int readBestScore() {
        try {
            Path filePath = Paths.get("score.txt");
            if (Files.exists(filePath)) {
                String content = new String(Files.readAllBytes(filePath));
                return Integer.parseInt(content.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void writeBestScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("score.txt"))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
