/*
Thanakrit Jomhong 6713118
Phurinut Wongwatcharapaiboon 6713245
Jitchaya Hirunsri 6713222
Tanop Udomkanaruck 6713233
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
    private JFrame mainFrame;
    private JFrame currentFrame;
    private JLabel drawpane;
    private JPanel contentpane;
    private MyImageIcon frameIcon;
    private MySoundEffect backgroundMusic;
    private VolumeManagement vm;
    private MySoundEffect clickedSound;
    
//    private int i;
    

//    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread
        //SwingUtilities.invokeLater(() -> new MainApplication());
//        new GameMain();
//    }

    public GameMain(JFrame mainFrame, MySoundEffect backgroundMusic, VolumeManagement vm) {
        
        contentpane = (JPanel)getContentPane();
	contentpane.setLayout(new BorderLayout());   
        
        this.backgroundMusic = backgroundMusic;
        this.vm = vm;
        this.mainFrame = mainFrame;
        this.clickedSound = new MySoundEffect();
        clickedSound.setSound(MyConstants.FILE_CLICKED);
        frameIcon = new MyImageIcon(MyConstants.FILE_Selected);
        
        setTitle("Space Fighter - Set Game Play");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(MyConstants.FRAME_WIDTH, MyConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());  
        setIconImage(frameIcon.getImage());
        currentFrame = this;
        setVisible(true);
        
        AddComponent();
    }
    
    
    private void AddComponent() {
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        
        drawpane = new JLabel();
        drawpane.setIcon(new MyImageIcon(MyConstants.FILE_BG)); 
        drawpane.setLayout(new BorderLayout());
        
        // --- Title ---
        JLabel titleLabel = new JLabel();
        titleLabel.setIcon(new MyImageIcon(MyConstants.FILE_GameLogo));
//        titleLabel.add(Box.createRigidArea(new Dimension(0, 100)));
        

        // --- (Req #2) JTextField ---
//        mainPanel.add(createSectionLabel("Enter Your Name:"));
        playerNameField = new JTextField("Player Name");
        playerNameField.setFont(new Font("Arial", Font.PLAIN, 25));
        playerNameField.setMaximumSize(new Dimension(300, 30));
        
        
        // --- (Req #2) JRadioButton (5+ items) ---
        String[] difficulties = {"Recruit", "Soldier", "Veteran", "Ace", "Impossible"};
        difficultyRadios = new JRadioButton[difficulties.length];
        ButtonGroup difficultyGroup = new ButtonGroup();
        JPanel radioPanel = new JPanel(new GridLayout(3, 2));
        radioPanel.setMaximumSize(new Dimension(300, 100));
        for (int i = 0; i < difficulties.length; i++) {
            difficultyRadios[i] = new JRadioButton(difficulties[i]);
            difficultyRadios[i].setFont(new Font("Arial", Font.PLAIN, 20));
            difficultyRadios[i].setForeground(Color.WHITE);
            difficultyRadios[i].setContentAreaFilled(false);
            difficultyRadios[i].setOpaque(false);
            difficultyRadios[i].setFocusPainted(false);
            difficultyGroup.add(difficultyRadios[i]);
            radioPanel.add(difficultyRadios[i]);
        }
        
        for(int i = 0; i < difficulties.length; i++) {
            if(i == 1) {
                difficultyRadios[i].setIcon(new MyImageIcon(MyConstants.FILE_Selected));
                difficultyRadios[i].setSelected(true);
            } else {
                difficultyRadios[i].setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
            }
            difficultyRadios[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int j = 0; j < difficulties.length; j++) {
                        if(difficultyRadios[j].isSelected()) 
                            difficultyRadios[j].setIcon(new MyImageIcon(MyConstants.FILE_Selected));
                        else 
                            difficultyRadios[j].setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
                    }
//                    difficultyRadios[i].setIcon(new MyImageIcon(MyConstants.FILE_Selected));
//                    for(int j = 0; j < difficulties.length; i++) {
//                        if (j == i) continue;
//                        else difficultyRadios[i].setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
//                    }
                }
            });
        }
        difficultyRadios[1].setSelected(true); // Default to "Soldier"
        radioPanel.setOpaque(false);
//        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
//        mainPanel.add(Box.createVerticalGlue()); // Pushes start button to bottom

        
        // --- (Req #2) JButton that opens another frame ---
        JButton startGameButton = new JButton();
        JButton backGameButton = new JButton();
        
        startGameButton.setIcon(new MyImageIcon(MyConstants.FILE_Start));
        startGameButton.setOpaque(false);
        backGameButton.setIcon(new MyImageIcon(MyConstants.FILE_BackMenu));
        backGameButton.setOpaque(false);
        
        RemoveBG.removeBgBtn(startGameButton); 
        RemoveBG.removeBgBtn(backGameButton);

        
        startGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startGameButton.setIcon(new MyImageIcon(MyConstants.FILE_Start_Glow));
            }
            public void mouseExited(MouseEvent e) {
                startGameButton.setIcon(new MyImageIcon(MyConstants.FILE_Start));
            }
        });
        startGameButton.addActionListener(e -> {
            // This is the logic that opens Frame 2
            clickedSound.playOnce();
            String playerName = playerNameField.getText();
            String selectedDifficulty = getSelectedDifficulty();
            
            // Create and show the game frame
            GameFrame gameFrame = new GameFrame(mainFrame, playerName, selectedDifficulty, backgroundMusic, vm);
            gameFrame.setVisible(true);
            mainFrame.setVisible(false);
            // Close this menu frame
            this.dispose();
        });

        
        backGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backGameButton.setIcon(new MyImageIcon(MyConstants.FILE_BackMenu_Glow));
            }
            public void mouseExited(MouseEvent e) {
                backGameButton.setIcon(new MyImageIcon(MyConstants.FILE_BackMenu));
            }
        });
        backGameButton.addActionListener(e -> {
            clickedSound.playOnce();
            mainFrame.setVisible(true);
            this.dispose();
        });

        
        JLabel textDifficult = new JLabel("Select Difficulty");
        textDifficult.setForeground(Color.WHITE);
        textDifficult.setFont(new Font("Arial", Font.BOLD, 20));
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(playerNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(textDifficult);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(startGameButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(backGameButton);
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textDifficult.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        drawpane.add(mainPanel, BorderLayout.CENTER);
        contentpane.add(drawpane, BorderLayout.CENTER);
        validate();       
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