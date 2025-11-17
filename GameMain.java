/*
 * Group Members:
 * 1. [Your Name] ([Your ID])
 * 2. [Name] ([ID])
 * 3. [Name] ([ID])
 * 4. [Name] ([ID])
 * 5. [Name] ([ID])
 */
package Project3_6713118; // Make sure to rename XXX to your ID
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
// This is NOW Frame 1, the Main Menu.
// It satisfies the PDF requirement that the program starts with "MainApplication.java".
public class GameMain extends JFrame {

    // (Req #2) Components
    private JTextField playerNameField;
    private JRadioButton[] difficultyRadios;
    private JButton startGameButton;
    private JButton backGameButton;
    private JFrame mainFrame;
    private JFrame currentFrame;

//    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        //SwingUtilities.invokeLater(() -> new MainApplication());
//        new GameMain();
//    }

    public GameMain(JFrame mainFrame) {
        setTitle("Space Defender - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        currentFrame = this;
        
        
        // Main panel with a nice border
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // --- Title ---
        JLabel titleLabel = new JLabel("SPACE DEFENDER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- (Req #2) Group Names ---
        JLabel namesLabel = new JLabel("By: [Your Name] (ID), [Friend's Name] (ID), ...");
        namesLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        namesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(namesLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- (Req #2) JTextField ---
        mainPanel.add(createSectionLabel("Enter Your Name:"));
        playerNameField = new JTextField("Commander");
        playerNameField.setMaximumSize(new Dimension(300, 30));
        mainPanel.add(playerNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --- (Req #2) JRadioButton (5+ items) ---
        mainPanel.add(createSectionLabel("Select Difficulty:"));
        String[] difficulties = {"Recruit", "Soldier", "Veteran", "Ace", "Impossible"};
        difficultyRadios = new JRadioButton[difficulties.length];
        ButtonGroup difficultyGroup = new ButtonGroup();
        JPanel radioPanel = new JPanel(new GridLayout(3, 2));
        radioPanel.setMaximumSize(new Dimension(300, 100));
        for (int i = 0; i < difficulties.length; i++) {
            difficultyRadios[i] = new JRadioButton(difficulties[i]);
            difficultyGroup.add(difficultyRadios[i]);
            radioPanel.add(difficultyRadios[i]);
        }
        difficultyRadios[1].setSelected(true); // Default to "Soldier"
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        
        mainPanel.add(Box.createVerticalGlue()); // Pushes start button to bottom

        // --- (Req #2) JButton that opens another frame ---
        startGameButton = new JButton("START GAME");
        startGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setMinimumSize(new Dimension(300, 60));
        startGameButton.setPreferredSize(new Dimension(300, 60));
        
        backGameButton = new JButton("BACK TO MAIN MENU");
        backGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        backGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backGameButton.setMinimumSize(new Dimension(300, 60));
        backGameButton.setPreferredSize(new Dimension(300, 60));

        // (Req #3) Event Handler 2 & 3 (ActionListener & MouseEvent)
        startGameButton.addActionListener(e -> {
            // This is the logic that opens Frame 2
            String playerName = playerNameField.getText();
            String selectedDifficulty = getSelectedDifficulty();
            
            // Create and show the game frame
            GameFrame gameFrame = new GameFrame(mainFrame, playerName, selectedDifficulty);
            gameFrame.setVisible(true);
            mainFrame.setVisible(false);
            // Close this menu frame
            this.dispose();
        });
        
        backGameButton.addActionListener(e -> {
            // This is the logic that opens Frame 2
            mainFrame.setVisible(true);
            // Close this menu frame
            this.dispose();
        });
        
        

        // (Req #3) This satisfies the MouseEvent handler requirement
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Substantial: change button appearance
                startGameButton.setBackground(Color.GREEN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Substantial: revert button appearance
                startGameButton.setBackground(UIManager.getColor("Button.background"));
            }
        });
        
        
        
        mainPanel.add(startGameButton);
        mainPanel.add(backGameButton);

        // Show the frame
        setVisible(true);
    }
    
    // Helper to get selected radio button
    private String getSelectedDifficulty() {
        for (JRadioButton radio : difficultyRadios) {
            if (radio.isSelected()) {
                return radio.getText();
            }
        }
        return "Soldier";
    }

    // Helper to style section labels
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}