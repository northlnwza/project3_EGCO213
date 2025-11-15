/*
 * Group Members:
 * 1. [Your Name] ([Your ID])
 * 2. [Name] ([ID])
 * 3. [Name] ([ID])
 * 4. [Name] ([ID])
 * 5. [Name] ([ID])
 */
package Project3_6713118_V2; // Make sure to rename XXX to your ID

import javax.swing.JLabel;

// This file contains all our "game object" classes.
// All references to "MainApplication" have been changed to "GameFrame".

// //////////////////////////////////////////////////////////////////////////////
// Class 1: The Player's Rocket
// //////////////////////////////////////////////////////////////////////////////
class PlayerRocket extends JLabel {
    private GameFrame parentFrame; // Changed from MainApplication
    private MyImageIcon rocketImg;
    private int curX, curY;

    public PlayerRocket(GameFrame pf) { // Changed from MainApplication
        parentFrame = pf;
        rocketImg = new MyImageIcon(MyConstants.FILE_ROCKET).resize(MyConstants.ROCKET_WIDTH, MyConstants.ROCKET_HEIGHT);
        setIcon(rocketImg);

        curX = (MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH) / 2;
        curY = MyConstants.GAME_PANEL_HEIGHT - MyConstants.ROCKET_HEIGHT - 20;
        setBounds(curX, curY, MyConstants.ROCKET_WIDTH, MyConstants.ROCKET_HEIGHT);
    }

    public void moveLeft() {
        curX -= parentFrame.getCurrentPlayerSpeed();
        if (curX < 0) {
            curX = 0;
        }
        setLocation(curX, curY);
    }

    public void moveRight() {
        curX += parentFrame.getCurrentPlayerSpeed();
        if (curX > MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH) {
            curX = MyConstants.GAME_PANEL_WIDTH - MyConstants.ROCKET_WIDTH;
        }
        setLocation(curX, curY);
    }
}

// //////////////////////////////////////////////////////////////////////////////
// Class 2: The Falling Asteroid (Object Thread)
// //////////////////////////////////////////////////////////////////////////////
class Asteroid extends JLabel implements Runnable {
    private GameFrame parentFrame; // Changed from MainApplication
    private MyImageIcon asteroidImg;
    private int curX, curY;
    private volatile boolean isRunning = true;

    public Asteroid(GameFrame pf, int startX) { // Changed from MainApplication
        parentFrame = pf;
        asteroidImg = new MyImageIcon(MyConstants.FILE_ASTEROID).resize(MyConstants.ASTEROID_WIDTH, MyConstants.ASTEROID_HEIGHT);
        setIcon(asteroidImg);

        curX = startX;
        curY = -MyConstants.ASTEROID_HEIGHT;
        setBounds(curX, curY, MyConstants.ASTEROID_WIDTH, MyConstants.ASTEROID_HEIGHT);
    }

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
            curY += MyConstants.ASTEROID_SPEED;
            setLocation(curX, curY);

            if (isRunning && this.getBounds().intersects(parentFrame.getPlayerRocket().getBounds())) {
                parentFrame.loseHealth(1);
                stopThread();
                break;
            }

            if (curY > MyConstants.GAME_PANEL_HEIGHT) {
                parentFrame.addGameLog("Asteroid missed.");
                parentFrame.loseHealth(1);
                stopThread();
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
    private int curX, curY;
    private volatile boolean isRunning = true;

    public Bullet(GameFrame pf, int startX, int startY) { // Changed from MainApplication
        parentFrame = pf;
        bulletImg = new MyImageIcon(MyConstants.FILE_BULLET).resize(MyConstants.BULLET_WIDTH, MyConstants.BULLET_HEIGHT);
        setIcon(bulletImg);

        curX = startX;
        curY = startY;
        setBounds(curX, curY, MyConstants.BULLET_WIDTH, MyConstants.BULLET_HEIGHT);
    }

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