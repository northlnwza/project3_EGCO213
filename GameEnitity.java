/*
Group Members:
Thanakrit Jomhong 6713118
Phurinut Wongwatcharapaiboon 6713245
Jitchaya Hirunsri 6713222
Tanop Udomkanaruck 6713233
 */

package Project3_6713118; // Make sure to rename XXX to your ID

//import Project3_EGCO213.GameFrame;
import javax.swing.*;

// This file contains all our "game object" classes.
// All references to "MainApplication" have been changed to "GameFrame".

// //////////////////////////////////////////////////////////////////////////////
// Class 1: The Player's Rocket
// //////////////////////////////////////////////////////////////////////////////
class PlayerRocket extends JLabel {
    private GameFrame parentFrame; // Changed from MainApplication
    private MyImageIcon rocketImg;
    private boolean isRunning = true;
    private int curX, curY;

    public PlayerRocket(GameFrame pf) { // Changed from MainApplication
        parentFrame = pf;
        rocketImg = new MyImageIcon(MyConstants.FILE_ROCKET2).resize(MyConstants.ROCKET_WIDTH, MyConstants.ROCKET_HEIGHT);
        setIcon(rocketImg);

        // This calculation now uses the CORRECTED GAME_PANEL_HEIGHT (700px)
        curX = (MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH) / 2;
        curY = MyConstants.GAME_PANEL_HEIGHT - MyConstants.ROCKET_HEIGHT - 20;
        setBounds(curX, curY, MyConstants.ROCKET_WIDTH, MyConstants.ROCKET_HEIGHT);
    }

    public void moveLeft() {
        if (isRunning)
        {
        curX -= parentFrame.getCurrentPlayerSpeed();
        if (curX < 0) {
            curX = 0;
        }
        setLocation(curX, curY);
        }
    }

    public void moveRight() {
        if (isRunning)
        {
        curX += parentFrame.getCurrentPlayerSpeed();
        if (curX > MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH) {
            curX = MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH;
        }
        setLocation(curX, curY);
        }
    }
    public void setIsRunning(boolean b)
    {
        isRunning = b;
    }
}

// //////////////////////////////////////////////////////////////////////////////
// Class 2: The Falling Asteroid (Object Thread)
// //////////////////////////////////////////////////////////////////////////////
class Asteroid extends JLabel implements Runnable {
    private GameFrame parentFrame; // Changed from MainApplication
    private MyImageIcon asteroidImg;
    private MyImageIcon explosionImg;
    private int curX, curY;
    private int speed; // Instance speed
    private int width, height; // Instance size
    //private volatile boolean isRunning = true;
    private boolean isRunning = true;

    public Asteroid(GameFrame pf, int startX, int type) 
    {
        parentFrame = pf;
        switch (type) 
        {
            case MyConstants.ASTEROID_TYPE_SMALL:
                this.width = MyConstants.SIZE_SMALL;
                this.height = MyConstants.SIZE_SMALL;
                this.speed = MyConstants.SPEED_SMALL;
                break;
            case MyConstants.ASTEROID_TYPE_LARGE:
                this.width = MyConstants.SIZE_LARGE;
                this.height = MyConstants.SIZE_LARGE;
                this.speed = MyConstants.SPEED_LARGE;
                break;
            case MyConstants.ASTEROID_TYPE_MEDIUM:
            default:
                this.width = MyConstants.SIZE_MEDIUM;
                this.height = MyConstants.SIZE_MEDIUM;
                this.speed = MyConstants.SPEED_MEDIUM;
                break;
        }
        asteroidImg = new MyImageIcon(MyConstants.FILE_ASTEROID).resize(this.width, this.height);
        explosionImg = new MyImageIcon(MyConstants.FILE_EXPLOSION).resize(this.width, this.height);
        setIcon(asteroidImg);

        curX = startX;
        curY = -this.height;
        setBounds(curX, curY, this.width, this.height);
    }
    
    
    public void Explosion() { setIcon(explosionImg); }

    /**
     * COMMAND A: "Destroyed by Bullet"
     * This is called by checkBulletCollisions.
     * It *only* stops the thread's loop.
     * The GameFrame is responsible for removing the GUI and List item.
     */
    public void stopThreadOnly() 
    {
        isRunning = false;
    }
    public void stopThreadAndRemoveFromList() {
        isRunning = false;
        // This tells GameFrame to remove it from the list AND the GUI
        parentFrame.removeEntity(this);
    }
    
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        while (isRunning && parentFrame.isGameRunning()) {
            //curY += MyConstants.ASTEROID_SPEED;
            curY += speed;
            setLocation(curX, curY);

            if (isRunning && this.getBounds().intersects(parentFrame.getPlayerRocket().getBounds())) {
                parentFrame.loseHealth(1);
                Explosion();
                try { Thread.sleep(350); } catch(InterruptedException e) {}
                stopThreadAndRemoveFromList(); // Use the full remove method
                break;
            }

            // This check now uses the CORRECTED GAME_PANEL_HEIGHT (700px)
            if (curY > MyConstants.GAME_PANEL_HEIGHT) {
                parentFrame.addGameLog("Asteroid missed.");
                parentFrame.loseHealth(1);
                stopThreadAndRemoveFromList(); // Use the full remove method
                break;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
        if (isRunning) {
            parentFrame.removeEntity(this);
        }
    }
}

// //////////////////////////////////////////////////////////////////////////////
// Class 3: The Player's Bullet (Object Thread)
// //////////////////////////////////////////////////////////////////////////////
class Bullet extends JLabel implements Runnable {
    private GameFrame parentFrame; // Changed from MainApplication
    private MyImageIcon bulletImg;
    private MyImageIcon bullet2Img;
    private int curX, curY;
    //private volatile boolean isRunning = true;
    private boolean isRunning = true;

    public Bullet(GameFrame pf, int startX, int startY) { // Changed from MainApplication
        parentFrame = pf;
        bulletImg = new MyImageIcon(MyConstants.FILE_BULLET).resize(MyConstants.BULLET_WIDTH, MyConstants.BULLET_HEIGHT);
        bullet2Img = new MyImageIcon(MyConstants.FILE_BULLET2).resize(MyConstants.BULLET_WIDTH, MyConstants.BULLET_HEIGHT);
        setIcon(bullet2Img);

        curX = startX;
        curY = startY;
        setBounds(curX, curY, MyConstants.BULLET_WIDTH, MyConstants.BULLET_HEIGHT);
    }
    
    public void upgradeBullet() { setIcon(bulletImg); }

    public void stopThread() {
        isRunning = false;
        parentFrame.removeEntity(this);
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        while (isRunning && parentFrame.isGameRunning()) {
            curY -= parentFrame.getCurrentBulletSpeed();
            setLocation(curX, curY);

            parentFrame.checkBulletCollisions(this);

            if (curY < -MyConstants.BULLET_HEIGHT) {
                stopThread();
                break;
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                isRunning = false;
            }
        }
        if (isRunning) {
            parentFrame.removeEntity(this);
        }
    }
}