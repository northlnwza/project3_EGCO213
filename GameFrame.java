/*
 * Group Members:
 * 1. [Your Name] ([Your ID])
 * 2. [Name] ([ID])
 * 3. [Name] ([ID])
 * 4. [Name] ([ID])
 * 5. [Name] ([ID])
 */
package Project3_6713118; // Make sure to rename XXX to your ID

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFrame extends JFrame {

    // --- Core Components ---
    private JPanel contentpane;
    private JLabel drawpane; // This is the "game screen"
    private MyImageIcon backgroundImg;
    private GameFrame currentFrame;
    private JFrame mainFrame;       // Reference to the Main Menu
    private PlayerRocket playerRocket;
    private Random rand = new Random();
    private MySoundEffect themeSound; 
    private VolumeManagement vm; 

    // --- Game State ---
    private int score = 0;
    private int targetsDestroyed = 0;
    private int targetToWin;
    private int playerHP = 3;
    private boolean gameRunning = true;
    private String playerName; // Store player name from menu
    private String difficulty;
    

        // --- Dynamic Difficulty Stats ---
    private long baseSpawnDelay;         // Starting slowness
    private long minSpawnDelay;          // Max speed limit (Terminal Velocity)
    private double difficultyRampFactor; // How fast it gets harder (ms removed per second)
    private long currentSpawnDelay;      // The actual current delay
    
    // --- Upgradeable Stats ---
    private int currentBulletSpeed = MyConstants.BULLET_SPEED;
    private int currentBulletFrequency = MyConstants.PLAYER_FIRE_DELAY;
    private int currentPlayerSpeed = MyConstants.PLAYER_SPEED;
    private boolean hasDoubleShot = false;
    private boolean hasShield = false;
    
    // --- FIX 1: Pre-load all sounds to crush Sound I/O lag ---
    private MySoundEffect laserSound;
    private MySoundEffect explosionSound;
    private MySoundEffect playerHitSound;

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
    private JButton buyRapidBulletButton;
    private JButton buyShieldButton;
    private JButton mainMenuButton; // <-- NEW: Button to go back
    private JTextField scoreText;
    private JTextField hpText;
    private JLabel playerLabel; // To show player's name


    // Constructor now accepts settings from the menu
    public GameFrame(JFrame mainFrame, String playerName, String difficulty,MySoundEffect backgroundMusic, VolumeManagement v) 
    {
        this.mainFrame = mainFrame; // Store reference to main menu
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.currentFrame = this;
        themeSound = backgroundMusic;
        vm = v;
        
        applyDifficultySettings(difficulty);
        setTitle("Space Fighter - " + playerName + " " + difficulty);
        setSize(MyConstants.FRAME_WIDTH, MyConstants.FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Should probably be DISPOSE_ON_CLOSE if main stays open

        // (Req #3) We need 4 event handlers.
        // We add a WindowListener for game shutdown logic.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                stopGame(); // Helper method to stop everything
                mainFrame.setVisible(true); // Show main menu
            }
            @Override
            public void windowClosing(WindowEvent e) {
                stopGame();
                mainFrame.setVisible(true);
                // The default close operation is EXIT_ON_CLOSE, so we might need to change that
                // or just let dispose() handle it if we change default close op.
            }
        });
        
        // IMPORTANT: change to DISPOSE so we don't kill the whole app, just this window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentpane = (JPanel) getContentPane();
        contentpane.setLayout(new BorderLayout());

        AddComponents(); // Add all game components

        // Start the game logic
        laserSound = new MySoundEffect();
        explosionSound = new MySoundEffect();
        playerHitSound = new MySoundEffect();
        
        // Pre-load SFX
        laserSound.setSound(MyConstants.FILE_LASER_SOUND);
        explosionSound.setSound(MyConstants.FILE_EXPLOSION_SOUND);
        playerHitSound.setSound(MyConstants.FILE_PLAYER_HIT_SOUND);

        // Start game threads
        startAsteroidSpawner();
        startAutoShooter();
    }
    
     private void applyDifficultySettings(String diff) 
     {
        // Adjust spawn rate based on difficulty
        switch (diff) {
            case "Recruit":
                playerHP = 5;
                minSpawnDelay = 1000;  // Ends at moderate speed
                targetToWin = 20; 
                difficultyRampFactor = 10; // Gets faster by 20ms every second
                break;
            case "Soldier":
                playerHP = 3;
                minSpawnDelay = 800;
                targetToWin = 60;
                difficultyRampFactor = 20; 
                break;
            case "Veteran":
                playerHP = 3;
                minSpawnDelay = 600;   // Ends fast
                targetToWin = 60;
                difficultyRampFactor = 30; 
                break;
            case "Ace":
                playerHP = 2;
                minSpawnDelay = 400;   // Ends very fast
                targetToWin = 80;
                difficultyRampFactor = 40; 
                break;
            case "Impossible":
                playerHP = 1;
                minSpawnDelay = 200;   // Ends at "Chaos"
                targetToWin = 100;
                difficultyRampFactor = 50; 
                break;
            default: // Default safe values
                minSpawnDelay = 800;
                difficultyRampFactor = 30;
                targetToWin = 30;
        }
        
        baseSpawnDelay = MyConstants.ASTEROID_SPAWN_DELAY;
        currentSpawnDelay = MyConstants.ASTEROID_SPAWN_DELAY; //initialized
     }
    // Helper to cleanly stop the game
    private void stopGame() {
        playerRocket.setIsRunning(false);
        setGameRunning(false);
        for (Thread t : entityThreads) {
            if (t != null && t.isAlive()) t.interrupt();
        }
    }
    public void openSettingsMenu() {
        // 1. Pause the game logic
        
        // 2. Hide the game window (SettingApplication does this to owner, but let's be explicit)
        this.setVisible(false);
        
        // 3. Create the Settings Frame
        // We pass 'this' (GameFrame) as the owner, so when Settings closes, GameFrame re-appears.
        SettingApplication settings = new SettingApplication(this, themeSound, vm);
        
        // 4. Add a listener to resume game when settings close
        settings.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Resume the game!
                // Ensure focus returns to drawpane for keyboard controls
                drawpane.requestFocusInWindow(); 
            }
        });
    }


    public void AddComponents() {

        // --- CENTER: The Game Draw Pane ---
        // This line now uses the CORRECTED GAME_PANEL_HEIGHT (700px)
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
                }else if (keyCode == KeyEvent.VK_ESCAPE) 
                {
                    openSettingsMenu();
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
        
        // --- NEW: Main Menu Button in Status Panel ---
        mainMenuButton = new JButton("Resign");
        mainMenuButton.addActionListener(e -> {
            triggerGameOver();
        });
        statusPanel.add(mainMenuButton);
        
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
                if (currentPlayerSpeed < MyConstants.MAX_PLAYER_SPEED) {
                    addScore(-MyConstants.COST_FASTER_SHIP); 
                    currentPlayerSpeed += MyConstants.UPGRADE_SPEED_AMOUNT; 
                    addGameLog("Ship Speed Up!");
                    
                    // Check if we just hit the max
                    if (currentPlayerSpeed >= MyConstants.MAX_PLAYER_SPEED) {
                        buyFasterShipButton.setEnabled(false);
                        buyFasterShipButton.setText("Ship Speed MAXED");
                    }
                } else {
                    buyFasterShipButton.setEnabled(false);
                    buyFasterShipButton.setText("Ship Speed MAXED");
                }
            } else {
                addGameLog("Not enough score for faster ship!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyFasterShipButton);

//        // 3. Buy Faster Bullets Button
        buyFasterBulletButton = new JButton("Faster Bullets (Cost: " + MyConstants.COST_FASTER_BULLETS + ")");
        buyFasterBulletButton.addActionListener(e -> {
            if (score >= MyConstants.COST_FASTER_BULLETS) {
                if (currentBulletSpeed < MyConstants.MAX_BULLET_SPEED) {
                    addScore(-MyConstants.COST_FASTER_BULLETS); 
                    currentBulletSpeed += MyConstants.UPGRADE_SPEED_AMOUNT; 
                    addGameLog("Bullet Speed Up!");
                    
                    // Check if we just hit the max
                    if (currentBulletSpeed >= MyConstants.MAX_BULLET_SPEED) {
                        buyFasterBulletButton.setEnabled(false);
                        buyFasterBulletButton.setText("Bullet SP MAXED");
                    }
                } else {
                    buyFasterBulletButton.setEnabled(false);
                    buyFasterBulletButton.setText("Bullet SP MAXED");
                }
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
                buyDoubleShotButton.setText("Bought Double Shot");
            } else {
                addGameLog("Not enough score for Double Shot!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyDoubleShotButton);
        
        // 5. Buy Rapid Bullet Button
        buyRapidBulletButton = new JButton("more rapid bullet (Cost: " + MyConstants.COST_MORE_FREQUENCY_BULLETS + ")");
        buyRapidBulletButton.addActionListener(e -> {
            if (score >= MyConstants.COST_MORE_FREQUENCY_BULLETS) {
                if (currentBulletFrequency > MyConstants.MAX_BULLET_FQ) {
                    addScore(-MyConstants.COST_MORE_FREQUENCY_BULLETS); 
                    currentBulletFrequency -= MyConstants.UPGRADE_BULLET_FREQUENCY_AMOUNT; 
                    addGameLog("Bullet More Rapid!");
                    
                    // Check if we just hit the max
                    if (currentBulletFrequency <= MyConstants.MAX_BULLET_SPEED) {
                        buyRapidBulletButton.setEnabled(false);
                        buyRapidBulletButton.setText("Bullet FQ MAXED");
                    }
                } else {
                    buyRapidBulletButton.setEnabled(false);
                    buyRapidBulletButton.setText("Bullet FQ MAXED");
                }

            } else {
                addGameLog("Not enough score for more rapid bullet!");
            }
            drawpane.requestFocusInWindow();
        });
        upgradePanel.add(buyRapidBulletButton);

        // 6. Buy Shield Button
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
                while (isGameRunning()) {
                    SwingUtilities.invokeLater(() -> {
                        if (isGameRunning()) spawnAsteroid();
                    });
                    Thread.sleep(currentSpawnDelay);
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
                while (isGameRunning()) {
                    SwingUtilities.invokeLater(() -> {
                        if (isGameRunning()) fireBullet();
                    });
                    Thread.sleep(currentBulletFrequency);
                }
            } catch (InterruptedException e) {
                System.out.println("Auto-Shooter thread interrupted.");
            }
        });
        shooterThread.start();
    }

    // --- Game Logic Methods ---

    public synchronized void spawnAsteroid() {
        if (!isGameRunning()) return;
        
        int startX = rand.nextInt(MyConstants.GAME_PANEL_WIDTH - MyConstants.EAST_PANEL_WIDTH);
        
        // Decide Type based on Difficulty
        int type = MyConstants.ASTEROID_TYPE_MEDIUM; // Default
        int roll = rand.nextInt(100); // 0-99
        
        if (difficulty.equals("Recruit")) {
            // Mostly Large (Slow/Easy), some Medium
            if (roll < 60) type = MyConstants.ASTEROID_TYPE_LARGE;
            else type = MyConstants.ASTEROID_TYPE_MEDIUM;
            
        } else if (difficulty.equals("Soldier")) {
            // Balanced
            if (roll < 50) type = MyConstants.ASTEROID_TYPE_MEDIUM;
            else if (roll < 80) type = MyConstants.ASTEROID_TYPE_LARGE;
            else type = MyConstants.ASTEROID_TYPE_SMALL; // 20% Small
            
        } else if (difficulty.equals("Veteran")) {
            // Harder: More Small/Medium
            if (roll < 40) type = MyConstants.ASTEROID_TYPE_MEDIUM;
            else if (roll < 80) type = MyConstants.ASTEROID_TYPE_SMALL;
            else type = MyConstants.ASTEROID_TYPE_LARGE;
            
        } else if (difficulty.equals("Ace")) {
            // Very Hard: Mostly Small/Medium
            if (roll < 60) type = MyConstants.ASTEROID_TYPE_SMALL;
            else type = MyConstants.ASTEROID_TYPE_MEDIUM;
            
        } else if (difficulty.equals("Impossible")) {
            // Chaos: Almost all Small (Fast)
            if (roll < 80) type = MyConstants.ASTEROID_TYPE_SMALL;
            else type = MyConstants.ASTEROID_TYPE_MEDIUM;
        }
        Asteroid asteroid = new Asteroid(currentFrame, startX, type);
        drawpane.add(asteroid);
        
        Thread asteroidThread = new Thread(asteroid);
        asteroidThread.start();
        
        entityThreads.add(asteroidThread);
        asteroids.add(asteroid);
    }

    public void fireBullet() {
        if (!isGameRunning()) return;

       // MySoundEffect fireSound = new MySoundEffect(MyConstants.FILE_LASER_SOUND);
        laserSound.playOnce();

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
        
        // Loop BACKWARDS through the list by Index
        for (int i = asteroids.size() - 1; i >= 0; i--) {
            Asteroid asteroid = asteroids.get(i);
            
            if (asteroid.isRunning() && bullet.getBounds().intersects(asteroid.getBounds())) {
                // Collision!
                explosionSound.playOnce();
                
                bullet.stopThread();    // Stop bullet thread
                
                // 1. Stop the Asteroid Thread
                asteroid.stopThreadOnly();
                
                // 2. Remove Visuals
                removeEntityGUI(asteroid);
                
                // 3. Remove from List using Index (Safe because we are looping backwards)
                asteroids.remove(i);
                
                addScore(10);
                targetsDestroyed++;
                long reduction = (long)(targetsDestroyed * difficultyRampFactor);
                currentSpawnDelay = Math.max(minSpawnDelay, baseSpawnDelay - reduction);
                addGameLog("Asteroid destroyed! +10");
                String showspawnrate = String.format("spawn[object/mms]: %d", currentSpawnDelay);
                addGameLog(showspawnrate);
                if (targetsDestroyed >= targetToWin) 
                {
                        triggerVictory();
                } else {
                        addGameLog("Hit! " + targetsDestroyed + "/" + targetToWin);
                    }
                return; // Bullet is destroyed, stop checking other asteroids
            }
        }
    }
    public synchronized void triggerVictory() 
    {
        setGameRunning(false);
        themeSound.stop();
        stopGame();
        
        // Visual Victory Screen
        JLabel winLabel = new JLabel("MISSION ACCOMPLISHED");
        winLabel.setFont(new Font("Arial", Font.BOLD, 50)); // Slightly smaller to fit
        winLabel.setForeground(Color.GREEN);
        winLabel.setBounds(0, MyConstants.GAME_PANEL_HEIGHT / 2 - 100, MyConstants.GAME_PANEL_WIDTH, 100);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton returnButton = new JButton("Return to Base");
        returnButton.setFont(new Font("Arial", Font.BOLD, 30));
        returnButton.setBounds(MyConstants.GAME_PANEL_WIDTH / 2 - 150, MyConstants.GAME_PANEL_HEIGHT / 2 + 20, 300, 60);
        returnButton.addActionListener(e -> {
            currentFrame.dispose();
            mainFrame.setVisible(true);
        });
        
        drawpane.add(winLabel, 0);
        drawpane.add(returnButton, 0);
        drawpane.repaint();
    }
    
    public synchronized void triggerGameOver()
    {
                    // Create Game Over UI
            stopGame();
            JLabel gameOverLabel = new JLabel("MISSION FAILED");
            gameOverLabel.setFont(new Font("Arial", Font.BOLD, 80));
            gameOverLabel.setForeground(Color.RED);
            gameOverLabel.setBounds(0, MyConstants.GAME_PANEL_HEIGHT / 2 - 100, MyConstants.GAME_PANEL_WIDTH, 100);
            gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JButton returnButton = new JButton("Return to Base");
            returnButton.setFont(new Font("Arial", Font.BOLD, 30));
            returnButton.setBounds(MyConstants.GAME_PANEL_WIDTH / 2 - 150, MyConstants.GAME_PANEL_HEIGHT / 2 + 20, 300, 60);
            returnButton.addActionListener(e -> {
                currentFrame.dispose(); // Close game window
                mainFrame.setVisible(true); // Show main menu
            });
            
            drawpane.add(gameOverLabel, 0); // Add on top
            drawpane.add(returnButton, 0);
            drawpane.repaint();
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
        if (!isGameRunning()) return;
        
         MySoundEffect hitSound = new MySoundEffect();
         hitSound.setSound(MyConstants.FILE_PLAYER_HIT_SOUND);
         
        if (hasShield) {
            hasShield = false;
            addGameLog("Shield blocked the hit!");
            hitSound.playOnce();
            return;
        }

        playerHP -= amount;
        hpText.setText(String.valueOf(playerHP));
        hitSound.playOnce();
        
        if (playerHP <= 0) {
            // Game Over
            setGameRunning(false);
            addGameLog("GAME OVER. Final Score: " + score);
            triggerGameOver();
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
    


    public int getCurrentBulletSpeed() {
        return currentBulletSpeed;
    }
    
    public int getCurrentBulletFrequency() {
        return currentBulletFrequency;
    }

    public int getCurrentPlayerSpeed() {
        return currentPlayerSpeed;
    }

    // --- NEW SPECIALIZED METHOD ---
    /**
     * This method ONLY removes the JLabel from the drawpane.
     * It is "GUI-safe" and does NOT touch any of the underlying data lists.
     * This is safe to call from anywhere, even inside an iterator,
     * because it doesn't modify the list being iterated.
     */
    public synchronized void removeEntityGUI(JLabel entity) {
        SwingUtilities.invokeLater(() -> {
            drawpane.remove(entity);
            drawpane.revalidate();
            drawpane.repaint();
        });
    }

    /**
     * This is the "full" remove method.
     * It removes an entity from the GUI *and* its tracking list.
     * This is called when an entity dies "on its own" (hits ground, player, etc.)
     * IT MUST NOT be called from inside an iterator loop.
     */
        public synchronized void removeEntity(JLabel entity) {
            // 1. Remove the visuals
            removeEntityGUI(entity); 

            // 2. Remove from the data list
            if (entity instanceof Asteroid) {
                asteroids.remove((Asteroid) entity);
            } else if (entity instanceof Bullet) {
                bullets.remove((Bullet) entity);
            }
        }
        public synchronized void setGameRunning(boolean running) 
        {
            this.gameRunning = running;
        }
        public synchronized boolean isGameRunning() 
        {
        return gameRunning;
        }
}