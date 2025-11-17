/*
 * Group Members:
 * 1. [Your Name] ([Your ID])
 * 2. [Name] ([ID])
 * 3. [Name] ([ID])
 * 4. [Name] ([ID])
 * 5. [Name] ([ID])
 */

package Project3_6713118;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import javax.swing.border.EmptyBorder;

public class SettingApplication extends JFrame{
    private MyImageIcon main_backgroundImg;
    private JLabel drawpane;
    private JPanel contentpane;
    private int framewidth  = MyConstants.FRAME_WIDTH;
    private int frameheight = MyConstants.FRAME_HEIGHT;    
    private MySoundEffect  ownerBackgroundMusic;
    private MySoundEffect  clickedSound;    
    private String[] backgroundMusicThemes = { "Retro Theme", "8-bit Theme", "Spaceship Shooter Theme", "Cyber Theme", "Happy Theme"};
    private JSlider volumeSlider;
    private VolumeManagement   vmBackground;
    private JRadioButton  mute, unmute;

    private JFrame currentFrame;
    private JFrame ownerFrame;
    
    public SettingApplication(JFrame ownerFrame, MySoundEffect ownerBackgroundMusic, VolumeManagement vm) {
        this.vmBackground = vm;
        this.ownerBackgroundMusic = ownerBackgroundMusic;
        this.clickedSound = new MySoundEffect();
        clickedSound.setSound(MyConstants.FILE_CLICKED);
        this.ownerFrame = ownerFrame;
        setTitle("Setting Menu");
        setSize(framewidth, frameheight); 
        setLocationRelativeTo(null);
	setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                ownerFrame.setVisible(true);
                currentFrame.dispose();
            }
        });
        
        setVisible(true);
        currentFrame = this;
        contentpane = (JPanel)getContentPane();
	contentpane.setLayout(new BorderLayout()); 
        
        AddComponent();
    } 
    public void AddComponent() {
        
        Font largeFont = new Font("Arial", Font.BOLD, 20);
        main_backgroundImg  = new MyImageIcon(MyConstants.FILE_SettingMenu);
	drawpane = new JLabel();
	drawpane.setIcon(main_backgroundImg);
        drawpane.setLayout(new BoxLayout(drawpane, BoxLayout.Y_AXIS));
        
        
        // --------------- Box inside setting menu
        JLabel boxSettingMenu = new JLabel();
        boxSettingMenu.setLayout(new BoxLayout(boxSettingMenu, BoxLayout.Y_AXIS));
//        EmptyBorder paddingBorder = new EmptyBorder(0, 50, 0, 50);
//        boxSettingMenu.setBorder(paddingBorder);
        boxSettingMenu.setIcon(new MyImageIcon(MyConstants.FILE_BoxSettingMenu));
        
        
        // --------------- Select Music Themes using JComboBox 
        JPanel selectMusicPanel = new JPanel();
        selectMusicPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel musicThemeText = new JLabel();
        musicThemeText.setIcon(new MyImageIcon(MyConstants.FILE_MusicTheme));
        selectMusicPanel.add(musicThemeText);
        
        JComboBox boxMusicThemes = new JComboBox(backgroundMusicThemes);
//        boxMusicThemes.setPreferredSize(new Dimension(200, 40));
        if(ownerBackgroundMusic.getCurrentMusic() == null) {
            boxMusicThemes.setSelectedItem("Retro");
        } else {
            if((ownerBackgroundMusic.getCurrentMusic()).equals(MyConstants.FILE_RetroSound))
                boxMusicThemes.setSelectedItem("Retro Theme");
            else if((ownerBackgroundMusic.getCurrentMusic()).equals(MyConstants.FILE_8bitSound))
                boxMusicThemes.setSelectedItem("8bit Theme");
            else if((ownerBackgroundMusic.getCurrentMusic()).equals(MyConstants.FILE_SpaceshipShooterSound))
                boxMusicThemes.setSelectedItem("Spaceship Shooter Theme");
            else if((ownerBackgroundMusic.getCurrentMusic()).equals(MyConstants.FILE_CyberSound))
                boxMusicThemes.setSelectedItem("Cyber Theme");
            else if((ownerBackgroundMusic.getCurrentMusic()).equals(MyConstants.FILE_HappySound))
                boxMusicThemes.setSelectedItem("Happy Theme");
        }
        boxMusicThemes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox source = (JComboBox) e.getSource();
                String selectedThemes = (String) source.getSelectedItem();
                ownerBackgroundMusic.stop();
                if(selectedThemes.equals("Retro Theme") && unmute.isSelected()) {
                    ownerBackgroundMusic.setSound(MyConstants.FILE_RetroSound);
                    ownerBackgroundMusic.setVolume(vmBackground.getGain());
                    ownerBackgroundMusic.playLoop();
                } else if(selectedThemes.equals("8-bit Theme") && unmute.isSelected()) {
                    ownerBackgroundMusic.setSound(MyConstants.FILE_8bitSound);
                    ownerBackgroundMusic.setVolume(vmBackground.getGain());
                    ownerBackgroundMusic.playLoop();
                } else if(selectedThemes.equals("Spaceship Shooter Theme") && unmute.isSelected()) {
                    ownerBackgroundMusic.setSound(MyConstants.FILE_SpaceshipShooterSound);
                    ownerBackgroundMusic.setVolume(vmBackground.getGain());
                    ownerBackgroundMusic.playLoop();
                } else if(selectedThemes.equals("Cyber Theme") && unmute.isSelected()) {
                    ownerBackgroundMusic.setSound(MyConstants.FILE_CyberSound);
                    ownerBackgroundMusic.setVolume(vmBackground.getGain());
                    ownerBackgroundMusic.playLoop();
                } else if(selectedThemes.equals("Happy Theme") && unmute.isSelected()) {
                    ownerBackgroundMusic.setSound(MyConstants.FILE_HappySound);
                    ownerBackgroundMusic.setVolume(vmBackground.getGain());
                    ownerBackgroundMusic.playLoop();
                }
                
            }
        });        
        selectMusicPanel.add(boxMusicThemes);
                
        
        // ------------- Adjust Volumne using JSlider
        JPanel volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int) (vmBackground.getVolumeSliderBar())); 
        volumeSlider.setPreferredSize(new Dimension(150, 15));
        volumeSlider.setBackground(Color.WHITE);
        volumeSlider.setOpaque(true);
        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int sliderValue = volumeSlider.getValue();
                float gain = sliderValue / 100.0f;
                ownerBackgroundMusic.setVolume(gain);
                vmBackground.updateGain(gain);
            }
        });
        JLabel volumeText = new JLabel();
        volumeText.setIcon(new MyImageIcon(MyConstants.FILE_Volume));
        volumePanel.add(volumeText);
        volumePanel.add(volumeSlider);
        volumePanel.setOpaque(false);

        
        
        // --------------- Mute Unmute using RadioButton
        JPanel mutePanel = new JPanel();
        mutePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ButtonGroup buttongroup = new ButtonGroup();
        mute = new JRadioButton("Mute");
        unmute = new JRadioButton("Unmute");
        
        mute.setFont(largeFont);
        mute.setForeground(Color.WHITE);
        mute.setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
        
        unmute.setFont(largeFont);
        unmute.setForeground(Color.WHITE);
        unmute.setIcon(new MyImageIcon(MyConstants.FILE_Selected));
        
        buttongroup.add(mute);
        buttongroup.add(unmute);
        
        JLabel speaker = new JLabel();
        speaker.setIcon(new MyImageIcon(MyConstants.FILE_Speaker));
        mutePanel.add(speaker);
        mutePanel.add(mute);
        mutePanel.add(unmute);
        
        unmute.setSelected(true);
        mute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mute.isSelected()) {
                    ownerBackgroundMusic.stop();
                    mute.setIcon(new MyImageIcon(MyConstants.FILE_Selected));
                    unmute.setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
                }
            }
        });
        unmute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (unmute.isSelected()) {
                    ownerBackgroundMusic.playLoop();
                    mute.setIcon(new MyImageIcon(MyConstants.FILE_Unselected));
                    unmute.setIcon(new MyImageIcon(MyConstants.FILE_Selected));
                }
            }
        });
        
        
        
        /*
        JButton saveBtn = new JButton();
        saveBtn.setIcon(new MyImageIcon(MyConstants.FILE_Save));
        saveBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                saveBtn.setIcon(new MyImageIcon(MyConstants.FILE_Save_Glow));
            }
            public void mouseExited(MouseEvent e) {
                saveBtn.setIcon(new MyImageIcon(MyConstants.FILE_Save));
            }
        });
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
            }
        });
        */
        
        // ------------- Back Button 
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backBtn = new JButton();
        backBtn.setIcon(new MyImageIcon(MyConstants.FILE_Back));

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backBtn.setIcon(new MyImageIcon(MyConstants.FILE_Back_Glow));
            }
            public void mouseExited(MouseEvent e) {
                backBtn.setIcon(new MyImageIcon(MyConstants.FILE_Back));
            }
        });
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
                ownerFrame.setVisible(true);
                currentFrame.dispose();
            }
        });
        // Remove white bg of back button
        RemoveBG.removeBgBtn(backBtn);
        backPanel.add(backBtn);
        

        // --------------- setOpaque
        backPanel.setOpaque(false);
        boxSettingMenu.setOpaque(false);
        mutePanel.setOpaque(false);
        selectMusicPanel.setOpaque(false);
        
        // --------------- boxSettingMenu.add
        boxSettingMenu.add(Box.createRigidArea(new Dimension(0, 60)));
        
        boxSettingMenu.add(selectMusicPanel);
        boxSettingMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        
        boxSettingMenu.add(mutePanel);
        boxSettingMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        
        boxSettingMenu.add(volumePanel);
     
        
        // --------------- drawpane.add
        drawpane.add(Box.createRigidArea(new Dimension(0, 90)));
        drawpane.add(boxSettingMenu);
        drawpane.add(Box.createRigidArea(new Dimension(0, 20)));
        drawpane.add(backPanel);
        drawpane.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --------------- setAlignment
        boxSettingMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentpane.add(drawpane);
    }
}
