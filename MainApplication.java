/*
Group Members:
Thanakrit Jomhong 6713118
Phurinut Wongwatcharapaiboon 6713245
Jitchaya Hirunsri 6713222
Tanop Udomkanaruck 6713233
 */

package Project3_6713118;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainApplication extends JFrame 
{
    // components
    private JPanel             contentpane;
    private JLabel             drawpane;
    private MyImageIcon        main_backgroundImg;    
    private MySoundEffect      clickedSound;    
    private MainApplication    currentFrame;
    private MySoundEffect      backgroundMusic;
    private VolumeManagement   vmBackground;
//    private VolumeManagement   vmClicked;

    private int framewidth  = MyConstants.FRAME_WIDTH;
    private int frameheight = MyConstants.FRAME_HEIGHT;

    public static void main(String[] args) 
    {
        new MainApplication();
    }  
    

    //--------------------------------------------------------------------------
    public MainApplication()
    {   
        vmBackground = new VolumeManagement();
        backgroundMusic = new MySoundEffect();
        backgroundMusic.setSound(MyConstants.FILE_RetroSound);
        backgroundMusic.playLoop();
        backgroundMusic.setVolume(0.5f);
        
        setTitle("Space Fighter");
	setSize(framewidth, frameheight); 
        setLocationRelativeTo(null);
	setVisible(true);
	setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        currentFrame = this;

	contentpane = (JPanel)getContentPane();
	contentpane.setLayout(new BorderLayout());        
        AddComponents();
    } 

    
    //--------------------------------------------------------------------------
    public void AddComponents()
    {    
        clickedSound = new MySoundEffect();
        clickedSound.setSound(MyConstants.FILE_CLICKED);
        
	main_backgroundImg  = new MyImageIcon(MyConstants.FILE_BG);
	drawpane = new JLabel();
	drawpane.setIcon(main_backgroundImg);
        drawpane.setLayout(new BorderLayout());
        
        JLabel logo = new JLabel();
        JButton playBtn = new JButton();
        JButton settingBtn = new JButton();
        JButton aboutusBtn = new JButton();
        JButton howtoplayBtn = new JButton();
        
        logo.setIcon(new MyImageIcon(MyConstants.FILE_GameLogo));
        playBtn.setIcon(new MyImageIcon(MyConstants.FILE_Play));
        settingBtn.setIcon(new MyImageIcon(MyConstants.FILE_Setting));
        aboutusBtn.setIcon(new MyImageIcon(MyConstants.FILE_Credits));
        howtoplayBtn.setIcon(new MyImageIcon(MyConstants.FILE_Howto));
        
        RemoveBG.removeBgBtn(playBtn); RemoveBG.removeBgBtn(settingBtn); RemoveBG.removeBgBtn(aboutusBtn); RemoveBG.removeBgBtn(howtoplayBtn);
              
        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.Y_AXIS));
        mainMenu.add(Box.createRigidArea(new Dimension(0, 100)));
        mainMenu.add(logo);
        mainMenu.add(Box.createRigidArea(new Dimension(0, 20)));
        mainMenu.add(playBtn);
        mainMenu.add(Box.createRigidArea(new Dimension(0, 20)));
        mainMenu.add(settingBtn);
        mainMenu.add(Box.createRigidArea(new Dimension(0, 20)));
        mainMenu.add(howtoplayBtn);
        mainMenu.add(Box.createRigidArea(new Dimension(0, 20)));
        mainMenu.add(aboutusBtn);
        
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        playBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        howtoplayBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        aboutusBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        playBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playBtn.setIcon(new MyImageIcon(MyConstants.FILE_Play_Glow));
            }
            public void mouseExited(MouseEvent e) {
                playBtn.setIcon(new MyImageIcon(MyConstants.FILE_Play));
            }
        });
        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
                currentFrame.setVisible(false);
                new GameMain(currentFrame, backgroundMusic, vmBackground);
            }
        });
        
        settingBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                settingBtn.setIcon(new MyImageIcon(MyConstants.FILE_Setting_Glow));
            }
            public void mouseExited(MouseEvent e) {
                settingBtn.setIcon(new MyImageIcon(MyConstants.FILE_Setting));
            }
        });
        settingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
                currentFrame.setVisible(false);
                new SettingApplication(currentFrame, backgroundMusic, vmBackground);
            }
        });
        
        howtoplayBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                howtoplayBtn.setIcon(new MyImageIcon(MyConstants.FILE_Howto_Glow));
            }
            public void mouseExited(MouseEvent e) {
                howtoplayBtn.setIcon(new MyImageIcon(MyConstants.FILE_Howto));
            }
        });
        howtoplayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
            }
        });
        
        aboutusBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                aboutusBtn.setIcon(new MyImageIcon(MyConstants.FILE_Credits_Glow));
            }
            public void mouseExited(MouseEvent e) {
                aboutusBtn.setIcon(new MyImageIcon(MyConstants.FILE_Credits));
            }
        });
        aboutusBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickedSound.playOnce();
            }
        });
        mainMenu.setOpaque(false);
        
        drawpane.add(mainMenu, BorderLayout.CENTER);
        contentpane.add(drawpane, BorderLayout.CENTER);
             
        validate();       


    }    

} // end class MainApplication
