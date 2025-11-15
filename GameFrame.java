
package Project3_6713118_V2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// This is NOW Frame 2, the Game.
// It is opened by MainApplication.java.
public class GameFrame extends JFrame {

    // --- Core Components ---
    private JPanel contentpane;
    private JLabel drawpane; // This is the "game screen"
    private MyImageIcon backgroundImg;
    private GameFrame currentFrame; // Renamed from MainApplication
    private PlayerRocket playerRocket;
    private MySoundEffect themeSound;
    private Random rand = new Random();

    // --- Game State ---
    private int score = 0;
    private int playerHP = 3;
    private volatile boolean gameRunning = true;
    private String playerName; // Store player name from menu
    private String difficulty; // Store difficulty from menu

    // --- Upgradeable Stats ---
    private int currentBulletSpeed = MyConstants.BULLET_SPEED;
    private int currentPlayerSpeed = MyConstants.PLAYER_SPEED;
    private boolean hasDoubleShot = false;
    private boolean hasShield = false;

    // --- Thread Management ---
    private List<Thread> entityThreads = new ArrayList<>();
    private List<Asteroid> asteroids = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();

    // --- GUI Components ---
    private JTextArea gameLogArea;
    private JButton buyHpButton;
    private JButton buyFasterShipButton;
    private JButton buyFasterBulletButton;
    private JButton buyDoubleShotButton;
    private JButton buyShieldButton;
    private JTextField scoreText;
    private JTextField hpText;
    private JLabel playerLabel; // To show player's name



    // Constructor now accepts settings from the menu
    public GameFrame(String playerName, String difficulty) {
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.currentFrame = this;

        setTitle("Space Defender - " + playerName);
        setSize(MyConstants.FRAME_WIDTH, MyConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // (Req #3) We need 4 event handlers.
        // We add a WindowListener for game shutdown logic.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameRunning = false; // Stop all threads
                themeSound.stop();
                // Interrupt all running threads
                for (Thread t : entityThreads) {
                    t.interrupt();
                }
            }
        });

        contentpane = (JPanel) getContentPane();
        contentpane.setLayout(new BorderLayout());

        AddComponents(); // Add all game components

        // Start the game logic
        themeSound = new MySoundEffect(MyConstants.FILE_THEME_MUSIC);
        themeSound.playLoop();

        // Start game threads
        startAsteroidSpawner();
        startAutoShooter();
    }

    public void AddComponents() {

        // --- CENTER: The Game Draw Pane ---
        backgroundImg = new MyImageIcon(MyConstants.FILE_BACKGROUND).resize(MyConstants.GAME_PANEL_WIDTH, MyConstants.GAME_PANEL_HEIGHT);
        drawpane = new JLabel();
        drawpane.setIcon(backgroundImg);
        drawpane.setLayout(null); // We manually position entities
        
        // Add the player rocket to the drawpane
        playerRocket = new PlayerRocket(currentFrame);
        drawpane.add(playerRocket);

        // (Req #3) This is our required KeyEvent handler
        drawpane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
                    playerRocket.moveLeft();
                } else if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
                    playerRocket.moveRight();
                }
            }
        });

        // Critical for KeyListener to work on a JLabel
        drawpane.setFocusable(true);
        drawpane.requestFocusInWindow();

        // --- SOUTH: Game Status & Upgrades ---
        JPanel southPanel = new JPanel(new GridLayout(2, 1)); 
        
        // Panel for HP and Score
        JPanel statusPanel = new JPanel();
        
        playerLabel = new JLabel("Player: " + this.playerName);
        statusPanel.add(playerLabel);
        
        statusPanel.add(new JLabel("HP:"));
        hpText = new JTextField(String.valueOf(playerHP), 3);
        hpText.setEditable(false);
        statusPanel.add(hpText);

        statusPanel.add(new JLabel("Score:"));
        scoreText = new JTextField(String.valueOf(score), 6);
        scoreText.setEditable(false);
        statusPanel.add(scoreText);
        
        // Panel for new upgrade buttons
        JPanel upgradePanel = new JPanel();
        upgradePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // --- (Req #3) All these buttons have ActionListeners ---

        // 1. Buy HP Button
        buyHpButton = new JButton("HP+1 (Cost: " + MyConstants.COST_HP + ")");
        buyHpButton.addActionListener(e -> {
            if (score >= MyConstants.COST_HP) {
                addScore(-MyConstants.COST_HP);
                addHealth(1);
                addGameLog("Bought +1 HP!");
            } else {
                addGameLog("Not enough score for HP!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyHpButton);

        // 2. Buy Faster Ship Button
        buyFasterShipButton = new JButton("Faster Ship (Cost: " + MyConstants.COST_FASTER_SHIP + ")");
        buyFasterShipButton.addActionListener(e -> {
            if (score >= MyConstants.COST_FASTER_SHIP) {
                addScore(-MyConstants.COST_FASTER_SHIP);
                currentPlayerSpeed += MyConstants.UPGRADE_SPEED_AMOUNT;
                addGameLog("Ship is faster!");
            } else {
                addGameLog("Not enough score for faster ship!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyFasterShipButton);

        // 3. Buy Faster Bullets Button
        buyFasterBulletButton = new JButton("Faster Bullets (Cost: " + MyConstants.COST_FASTER_BULLETS + ")");
        buyFasterBulletButton.addActionListener(e -> {
            if (score >= MyConstants.COST_FASTER_BULLETS) {
                addScore(-MyConstants.COST_FASTER_BULLETS);
                currentBulletSpeed += MyConstants.UPGRADE_SPEED_AMOUNT;
                addGameLog("Bullets are faster!");
            } else {
                addGameLog("Not enough score for faster bullets!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyFasterBulletButton);

        // 4. Buy Double Shot Button
        buyDoubleShotButton = new JButton("Double Shot (Cost: " + MyConstants.COST_DOUBLE_SHOT + ")");
        buyDoubleShotButton.addActionListener(e -> {
            if (score >= MyConstants.COST_DOUBLE_SHOT) {
                addScore(-MyConstants.COST_DOUBLE_SHOT);
                hasDoubleShot = true;
                addGameLog("Double Shot active!");
                ((JButton)e.getSource()).setEnabled(false); // One-time purchase
            } else {
                addGameLog("Not enough score for Double Shot!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyDoubleShotButton);

        // 5. Buy Shield Button
        buyShieldButton = new JButton("Shield (Cost: " + MyConstants.COST_SHIELD + ")");
        buyShieldButton.addActionListener(e -> {
            if (score >= MyConstants.COST_SHIELD) {
                if (hasShield) {
                    addGameLog("Shield is already active!");
                } else {
                    addScore(-MyConstants.COST_SHIELD);
                    hasShield = true;
                    addGameLog("Shield activated!");
                }
            } else {
                addGameLog("Not enough score for Shield!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyShieldButton);

        // (Req #2) JButton that opens another dialog
        // We add a "Help" button here too.
        JButton helpButton = new JButton("Help/Info");
        helpButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(currentFrame, 
                "Space Defender\n\n- Use A/D or Left/Right arrows to move.\n- Shooting is automatic.\n- Asteroids hitting you or the ground cost 1 HP.\n- Buy upgrades to survive longer!",
                "Help & Info",
                JOptionPane.INFORMATION_MESSAGE);
            drawpane.requestFocusInWindow();
        });
        statusPanel.add(helpButton);


        southPanel.add(statusPanel);
        southPanel.add(upgradePanel);

        // --- EAST: Game Log ---
        // (Req #2) JTextArea
        gameLogArea = new JTextArea(10, 20);
        gameLogArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(gameLogArea);
        logScrollPane.setPreferredSize(new Dimension(MyConstants.EAST_PANEL_WIDTH, 0)); 
        gameLogArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gameLogArea.append("Game Started, " + playerName + "!\n");
        gameLogArea.append("Difficulty: " + difficulty + "\n");

        // --- Add all panels to the content pane ---
        contentpane.add(drawpane, BorderLayout.CENTER);
        contentpane.add(southPanel, BorderLayout.SOUTH);
        contentpane.add(logScrollPane, BorderLayout.EAST);
    }
    
    // --- Thread Spawners ---

    public void startAsteroidSpawner() {
        Thread spawnerThread = new Thread(() -> {
            try {
                while (gameRunning) {
                    SwingUtilities.invokeLater(() -> {
                        if (gameRunning) spawnAsteroid();
                    });
                    Thread.sleep(MyConstants.ASTEROID_SPAWN_DELAY);
                }
            } catch (InterruptedException e) {
                System.out.println("Asteroid Spawner thread interrupted.");
            }
        });
        spawnerThread.start();
    }

    public void startAutoShooter() {
        Thread shooterThread = new Thread(() -> {
            try {
                while (gameRunning) {
                    SwingUtilities.invokeLater(() -> {
                        if (gameRunning) fireBullet();
                    });
                    Thread.sleep(MyConstants.PLAYER_FIRE_DELAY);
                }
            } catch (InterruptedException e) {
                System.out.println("Auto-Shooter thread interrupted.");
            }
        });
        shooterThread.start();
    }

    // --- Game Logic Methods ---

    public void spawnAsteroid() {
        if (!gameRunning) return;
        
        Asteroid asteroid = new Asteroid(currentFrame, rand.nextInt(MyConstants.GAME_PANEL_WIDTH - MyConstants.ASTEROID_WIDTH));
        drawpane.add(asteroid);
        
        Thread asteroidThread = new Thread(asteroid);
        asteroidThread.start();
        
        entityThreads.add(asteroidThread);
        asteroids.add(asteroid);
    }

    public void fireBullet() {
        if (!gameRunning) return;

        MySoundEffect fireSound = new MySoundEffect(MyConstants.FILE_LASER_SOUND);
        fireSound.playOnce();

        if (hasDoubleShot) {
            int bulletX1 = playerRocket.getX() + (MyConstants.ROCKET_WIDTH / 4) - (MyConstants.BULLET_WIDTH / 2);
            int bulletX2 = playerRocket.getX() + (MyConstants.ROCKET_WIDTH * 3 / 4) - (MyConstants.BULLET_WIDTH / 2);
            int bulletY = playerRocket.getY();
            
            Bullet bullet1 = new Bullet(currentFrame, bulletX1, bulletY);
            Bullet bullet2 = new Bullet(currentFrame, bulletX2, bulletY);
            
            drawpane.add(bullet1);
            drawpane.add(bullet2);

            Thread bulletThread1 = new Thread(bullet1);
            Thread bulletThread2 = new Thread(bullet2);
            bulletThread1.start();
            bulletThread2.start();

            entityThreads.add(bulletThread1);
            entityThreads.add(bulletThread2);
            bullets.add(bullet1);
            bullets.add(bullet2);

        } else {
            int bulletX = playerRocket.getX() + (MyConstants.ROCKET_WIDTH / 2) - (MyConstants.BULLET_WIDTH / 2);
            int bulletY = playerRocket.getY();
            Bullet bullet = new Bullet(currentFrame, bulletX, bulletY);
            drawpane.add(bullet);

            Thread bulletThread = new Thread(bullet);
            bulletThread.start();

            entityThreads.add(bulletThread);
            bullets.add(bullet);
        }
    }

    public synchronized void checkBulletCollisions(Bullet bullet) {
        if (!bullet.isRunning()) return;

        // Use iterator to safely remove asteroids
        Iterator<Asteroid> iter = asteroids.iterator();
        while (iter.hasNext()) {
            Asteroid asteroid = iter.next();
            if (asteroid.isRunning() && bullet.getBounds().intersects(asteroid.getBounds())) {
                // Collision!
                MySoundEffect hitSound = new MySoundEffect(MyConstants.FILE_EXPLOSION_SOUND);
                hitSound.playOnce();
                
                bullet.stopThread();    // Stop and remove bullet
                asteroid.stopThread();  // Stop and remove asteroid
                
                addScore(10);           // Add score
                addGameLog("Asteroid destroyed! +10");
                
                iter.remove(); // Remove asteroid from list
                return; // Bullet is destroyed, stop checking
            }
        }
    }

    public synchronized void addScore(int points) {
        score += points;
        scoreText.setText(String.valueOf(score));
    }

    public synchronized void addHealth(int amount) {
        playerHP += amount;
        hpText.setText(String.valueOf(playerHP));
    }

    public synchronized void loseHealth(int amount) {
        if (!gameRunning) return;
        
        if (hasShield) {
            hasShield = false;
            addGameLog("Shield blocked the hit!");
            MySoundEffect hitSound = new MySoundEffect(MyConstants.FILE_PLAYER_HIT_SOUND);
            hitSound.playOnce();
            return;
        }

        playerHP -= amount;
        hpText.setText(String.valueOf(playerHP));
        MySoundEffect hitSound = new MySoundEffect(MyConstants.FILE_PLAYER_HIT_SOUND);
        hitSound.playOnce();
        
        if (playerHP <= 0) {
            // Game Over
            gameRunning = false;
            themeSound.stop();
            addGameLog("GAME OVER. Final Score: " + score);
            
            // Stop all threads
            for (Thread t : entityThreads) {
                t.interrupt();
            }
            
            JLabel gameOverLabel = new JLabel("GAME OVER");
            gameOverLabel.setFont(new Font("Arial", Font.BOLD, 80));
            gameOverLabel.setForeground(Color.RED);
            gameOverLabel.setBounds(0, 0, MyConstants.GAME_PANEL_WIDTH, MyConstants.GAME_PANEL_HEIGHT);
            gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
            drawpane.add(gameOverLabel, 0); // Add on top
            drawpane.repaint();
        }
    }

    public void addGameLog(String message) {
        gameLogArea.append(message + "\n");
        gameLogArea.setCaretPosition(gameLogArea.getDocument().getLength()); // Auto-scroll
    }
    
    // --- Getters for other classes ---
    public PlayerRocket getPlayerRocket() {
        return playerRocket;
    }
    
    public boolean isGameRunning() {
        return gameRunning;
    }

    public int getCurrentBulletSpeed() {
        return currentBulletSpeed;
    }

    public int getCurrentPlayerSpeed() {
        return currentPlayerSpeed;
    }

    // Methods for entities to remove themselves from the game
    public synchronized void removeEntity(JLabel entity) {
        SwingUtilities.invokeLater(() -> {
            drawpane.remove(entity);
            drawpane.revalidate();
            drawpane.repaint();
        });

        if (entity instanceof Asteroid) {
            asteroids.remove((Asteroid) entity);
        } else if (entity instanceof Bullet) {
            bullets.remove((Bullet) entity);
        }
    }
}