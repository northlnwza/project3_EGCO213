/*
Group Members:
Thanakrit Jomhong 6713118
Phurinut Wongwatcharapaiboon 6713245
Jitchaya Hirunsri 6713222
Tanop Udomkanaruck 6713233
 */

package Project3_6713118;


import java.awt.Image;
import javax.swing.ImageIcon;
import javax.sound.sampled.*;     // for sounds
import javax.swing.JButton;


// Interface for keeping constant values
interface MyConstants
{
    //----- Resource files
    static final String PATH                = "src/main/java/Project3_6713118/resources/";
    static final String FILE_BG             = PATH + "main-background.png";
    static final String FILE_Play           = PATH + "playgame.png";
    static final String FILE_Play_Glow      = PATH + "playgame-glow.png";
    static final String FILE_Setting        = PATH + "setting.png";
    static final String FILE_Setting_Glow   = PATH + "setting-glow.png";
    static final String FILE_GameLogo       = PATH + "gamelogo.png";
    static final String FILE_GameLogo_Glow  = PATH + "gamelogo-glow.png";
    static final String FILE_Credits        = PATH + "credits.png";
    static final String FILE_Credits_Glow   = PATH + "credits-glow.png";
    static final String FILE_Howto          = PATH + "howtoplay.png";
    static final String FILE_Howto_Glow     = PATH + "howtoplay-glow.png";
    static final String FILE_Save           = PATH + "save.png";
    static final String FILE_Save_Glow      = PATH + "save-glow.png";
    static final String FILE_BoxSettingMenu = PATH + "box-settingmenu.png";
    static final String FILE_SettingMenu    = PATH + "settingmenu.png";
    static final String FILE_Back           = PATH + "back.png";
    static final String FILE_Back_Glow      = PATH + "back-glow.png";
    static final String FILE_Speaker        = PATH + "speaker.png";
    static final String FILE_MusicTheme     = PATH + "music_theme.png";
    static final String FILE_Volume         = PATH + "volume.png";
    static final String FILE_Selected       = PATH + "selected.png";
    static final String FILE_Unselected     = PATH + "unselected.png";
    static final String FILE_Start          = PATH + "startgame.png";
    static final String FILE_Start_Glow     = PATH + "startgame-glow.png";
    static final String FILE_BackMenu       = PATH + "backmenu.png";
    static final String FILE_BackMenu_Glow  = PATH + "backmenu-glow.png";
    static final String FILE_PlayIcon  = PATH + "spaceship1 Background Removed.png";
    

    
    // gameplay background
    static final String FILE_BACKGROUND     = PATH + "background.jpg";
    static final String FILE_ROCKET         = PATH + "rocket.png";
    static final String FILE_ASTEROID       = PATH + "asteroid.png";
    static final String FILE_BULLET         = PATH + "bullet.png";

    // game play sound
//    static final String FILE_THEME_MUSIC            = PATH + "theme.wav";
    static final String FILE_LASER_SOUND            = PATH + "laser.wav";
    static final String FILE_EXPLOSION_SOUND        = PATH + "explosion.wav";
    static final String FILE_PLAYER_HIT_SOUND       = PATH + "player_hit.wav";
    
    // main menu and setting sound
    static final String FILE_SpaceshipShooterSound  = PATH + "spaceship_sound.wav";
    static final String FILE_RetroSound             = PATH + "retrogame_sound.wav";
    static final String FILE_8bitSound              = PATH + "8bit_sound.wav";
    static final String FILE_HappySound             = PATH + "happy_sound.wav";
    static final String FILE_CyberSound             = PATH + "cyber_sound.wav";
    static final String FILE_CLICKED                = PATH + "clicksound.wav";
    
    //----- Sizes and locations
    static final int FRAME_WIDTH  = 1280;
    static final int FRAME_HEIGHT = 720;
//    static final int GROUND_Y     = 200;
//    static final int BTN_WIDTH    = 400;
//    static final int BTN_HEIGHT   = 200;
    static final float DEFAULT_VL = 0.5f;
    
        // --- NEW: Define the panel sizes ---
    // We reserve space for the top, bottom, and side panels
    static final int SOUTH_PANEL_HEIGHT = 100; // Approx. height of your 2-row south panel
    static final int EAST_PANEL_WIDTH = 250;   // Approx. width of your JTextArea log
    
    // --- NEW: Define the *actual* game area size ---
    static final int GAME_PANEL_WIDTH = FRAME_WIDTH - EAST_PANEL_WIDTH; // 1200 - 250 = 950
    static final int GAME_PANEL_HEIGHT = FRAME_HEIGHT - SOUTH_PANEL_HEIGHT; // 800 - 120 - 100 = 580

    static final int ROCKET_WIDTH = 80;
    static final int ROCKET_HEIGHT = 100;
    static final int ASTEROID_WIDTH = 60;
    static final int ASTEROID_HEIGHT = 60;
    static final int BULLET_WIDTH = 10;
    static final int BULLET_HEIGHT = 25;
    
    static final int ASTEROID_TYPE_SMALL = 0;
    static final int ASTEROID_TYPE_MEDIUM = 1;
    static final int ASTEROID_TYPE_LARGE = 2;
    static final int SIZE_SMALL = 40;
    static final int SIZE_MEDIUM = 60;
    static final int SIZE_LARGE = 100;
    static final int SPEED_SMALL = 7;  // Fast!
    static final int SPEED_MEDIUM = 4; // Normal
    static final int SPEED_LARGE = 2;  // Slow


    // --- Game Speed ---
    static final int PLAYER_SPEED = 20; // Pixels per key press
    static final int ASTEROID_SPEED = 5; // Pixels per game tick
    static final int BULLET_SPEED = 15; // Pixels per game tick
    static final int ASTEROID_SPAWN_DELAY = 2000; // Milliseconds (3 seconds)
    static final int PLAYER_FIRE_DELAY = 500; // n millisec / 500 Millis (2 shots per second) 500 default
    static final int UPGRADE_SPEED_AMOUNT = 10; // Speed increase per upgrade
    static final int UPGRADE_BULLET_FREQUENCY_AMOUNT = 10; // Speed decrease per upgrade

    static final int MAX_PLAYER_SPEED = 40; // Cap speed (Base is 20)
    static final int MAX_BULLET_SPEED = 50; // Cap speed (Base is 10)
    static final int MAX_BULLET_FQ = 50;
    // --- Upgrade Costs ---
    static final int COST_HP = 50;
    static final int COST_FASTER_SHIP = 30;
    static final int COST_FASTER_BULLETS = 30;
    static final int COST_MORE_FREQUENCY_BULLETS = 10;
    static final int COST_DOUBLE_SHOT = 100;
    static final int COST_SHIELD = 70;
    
}


// Auxiliary class to resize image
class MyImageIcon extends ImageIcon
{
    public MyImageIcon(String fname)  { super(fname); }
    public MyImageIcon(Image image)   { super(image); }

    public MyImageIcon resize(int width, int height)
    {
	Image oldimg = this.getImage();
	Image newimg = oldimg.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
        return new MyImageIcon(newimg);
    }
}


class RemoveBG {
    public static void removeBgBtn(JButton btn) {
        btn.setBorderPainted(false); 
        btn.setContentAreaFilled(false); 
        btn.setFocusPainted(false); 
        btn.setOpaque(false); 
    }
}

class MySoundEffect {
    private Clip         clip;
    private FloatControl gainControl;        
    private String       currentMusicTheme;
    
    public void setSound(String filename) {
        try
	{
//            if(filename.equals(MyConstants.FILE_RetroSound)) currentMusicTheme = "Retro Theme";
//            else if(filename.equals(MyConstants.FILE_RetroSound)) currentMusicTheme = "8-bit Theme";
//            else if(filename.equals(MyConstants.FILE_RetroSound)) currentMusicTheme = "Spaceship Shooter Theme";
            
            currentMusicTheme = filename;
            java.io.File file = new java.io.File(filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);       
            gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
	}
	catch (Exception e) { e.printStackTrace(); }
    }
    
    public void playOnce()             { clip.setMicrosecondPosition(0); clip.start(); }
    public void playLoop()             { clip.loop(Clip.LOOP_CONTINUOUSLY); }
    public void stop()                 { clip.stop(); }
    public String getCurrentMusic()    { return currentMusicTheme; }
        
    public void setVolume(float gain) { 
        if (gain < 0.0f)  gain = 0.0f;
        if (gain > 1.0f)  gain = 1.0f;
        float dB = (float)(Math.log(gain) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}

class VolumeManagement {
    private float gain;
    private boolean mute;
    
    public VolumeManagement() {
        this.gain = MyConstants.DEFAULT_VL;
        mute = false;
    }
    public void updateGain(float newGain) {
        gain = newGain;
    }
    public void setMute(boolean m) { mute = m; }
    public boolean getMuteValue() { return mute; }
    public float getGain() { return gain; }
    public float getVolumeSliderBar() { return gain * 100; }
}