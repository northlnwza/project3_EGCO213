/*
 * Group Members:
 * 1. [Your Name] ([Your ID])
 * 2. [Name] ([ID])
 * 3. [Name] ([ID])
 * 4. [Name] ([ID])
 * 5. [Name] ([ID])
 */
package Project3_6713118; // Make sure to rename XXX to your ID

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.sound.sampled.*;
import java.io.File;

// //////////////////////////////////////////////////////////////////////////////
// Interface for keeping all constant values
// //////////////////////////////////////////////////////////////////////////////
interface MyConstants {
    // --- Resource files ---
    // Make sure you have a 'resources' folder inside your Project3_XXX folder
    // E.g., Project3_XXX/resources/rocket.png
    static final String PATH = "src/main/java/Project3_6713118_V2/resources/"; // Relative path
    static final String FILE_BACKGROUND = PATH + "background.jpg";
    static final String FILE_ROCKET = PATH + "rocket.png";
    static final String FILE_ASTEROID = PATH + "asteroid.png";
    static final String FILE_BULLET = PATH + "bullet.png";

    static final String FILE_THEME_MUSIC = PATH + "theme.wav";
    static final String FILE_LASER_SOUND = PATH + "laser.wav";
    static final String FILE_EXPLOSION_SOUND = PATH + "explosion.wav";
    static final String FILE_PLAYER_HIT_SOUND = PATH + "player_hit.wav";

    // --- Sizes and locations ---
    static final int FRAME_WIDTH = 1200;
    static final int FRAME_HEIGHT = 800;

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

    // --- Game Speed ---
    static final int PLAYER_SPEED = 20; // Pixels per key press
    static final int ASTEROID_SPEED = 5; // Pixels per game tick
    static final int BULLET_SPEED = 15; // Pixels per game tick
    static final int ASTEROID_SPAWN_DELAY = 3000; // Milliseconds (3 seconds)
    static final int PLAYER_FIRE_DELAY = 100; // n millisec / 500 Millis (2 shots per second)
    static final int UPGRADE_SPEED_AMOUNT = 5; // Speed increase per upgrade

    // --- Upgrade Costs ---
    static final int COST_HP = 50;
    static final int COST_FASTER_SHIP = 30;
    static final int COST_FASTER_BULLETS = 30;
    static final int COST_DOUBLE_SHOT = 100;
    static final int COST_SHIELD = 70;
}

// //////////////////////////////////////////////////////////////////////////////
// Auxiliary class to resize image (from your example)
// //////////////////////////////////////////////////////////////////////////////
class MyImageIcon extends ImageIcon {
    public MyImageIcon(String fname) {
        super(fname);
    }

    public MyImageIcon(Image image) {
        super(image);
    }

    public MyImageIcon resize(int width, int height) {
        Image oldimg = this.getImage();
        Image newimg = oldimg.getScaledInstance(width, height, java.awt.Image.SCALE_DEFAULT);
        return new MyImageIcon(newimg);
    }
}

// //////////////////////////////////////////////////////////////////////////////
// Auxiliary class to play sound effect (from your example)
// //////////////////////////////////////////////////////////////////////////////
class MySoundEffect {
    private Clip clip;
    private FloatControl gainControl;

    public MySoundEffect(String filename) {
        try {
            File file = new File(filename);
            if (file.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } else {
                System.err.println("Sound file not found: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound: " + filename);
            e.printStackTrace();
        }
    }

    public void playOnce() {
        if (clip == null) return;
        clip.setMicrosecondPosition(0);
        clip.start();
    }

    public void playLoop() {
        if (clip == null) return;
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null) return;
        clip.stop();
    }

    public void setVolume(float gain) {
        if (gainControl == null) return;
        if (gain < 0.0f) gain = 0.0f;
        if (gain > 1.0f) gain = 1.0f;
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        gainControl.setValue(dB);
    }
}