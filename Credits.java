/*
Group Members:
Thanakrit Jomhong 6713118
Phurinut Wongwatcharapaiboon 6713245
Jitchaya Hirunsri 6713222
Tanop Udomkanaruck 6713233
 */

package Project3_6713118;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.prefs.BackingStoreException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import javax.swing.border.EmptyBorder;

public class Credits extends JFrame{
    private MyImageIcon main_backgroundImg;
    private MyImageIcon frameIcon;
    private JLabel drawpane;
    private JPanel contentpane;
    private int framewidth  = MyConstants.FRAME_WIDTH;
    private int frameheight = MyConstants.FRAME_HEIGHT;    
    private MySoundEffect  ownerBackgroundMusic;
    private VolumeManagement vmBackground;
    private MySoundEffect  clickedSound;    
    private JFrame currentFrame;
    private JFrame ownerFrame;
    
    public Credits(JFrame ownerFrame, MySoundEffect ownerBackgroundMusic, VolumeManagement vm) {
        
        this.vmBackground = vm;
        this.ownerBackgroundMusic = ownerBackgroundMusic;
        this.clickedSound = new MySoundEffect();
        clickedSound.setSound(MyConstants.FILE_CLICKED);
        this.ownerFrame = ownerFrame;
        frameIcon = new MyImageIcon(MyConstants.FILE_Selected);
        
        setTitle("Member Lists");
        setSize(framewidth, frameheight); 
        setLocationRelativeTo(null);
	setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        setIconImage(frameIcon.getImage());
        
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
        main_backgroundImg  = new MyImageIcon(MyConstants.FILE_CreditsMenu);
	drawpane = new JLabel();
	drawpane.setIcon(main_backgroundImg);
        drawpane.setLayout(new BoxLayout(drawpane, BoxLayout.Y_AXIS));
        
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
        
        
        // --------------- drawpane.add
//        drawpane.add(Box.createRigidArea(new Dimension(0, 600)));
        drawpane.add(backPanel);
//        drawpane.add(Box.createRigidArea(new Dimension(50, 20)));
        
        // --------------- setAlignment
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentpane.add(drawpane);
    }
}
