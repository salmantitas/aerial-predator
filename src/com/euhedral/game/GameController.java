package com.euhedral.game;

import com.euhedral.engine.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class GameController {
    private UIHandler uiHandler;
    private Random r = new Random();

    public Scanner scanner;
    public static String cmd;

    /********************************************
     * Window Settings - Manually Configurable *
     *******************************************/

    private int gameWidth = 1024;
    private double gameRatio = 4 / 3;
    private int gameHeight = Engine.HEIGHT;
    private String gameTitle = "Aerial Predator";
    private Color gameBackground = Color.BLUE;

    // High Score
    private LinkedList<Integer> highScore = new LinkedList<>();
    private int highScoreNumbers = 5;
    private boolean updateHighScore = false;

    // Objects
    private VariableManager variableManager;
    private EntityManager entityManager;

    // Camera
    public static Camera camera;
    int offsetHorizontal;
    int offsetVertical;

    // Level Generation
    private LevelGenerator levelGenerator;

    // Levels
    private int levelHeight;
    private boolean loadMission = false; // levels will only loaded when this is true

    /******************
     * User variables *
     ******************/

//    private static int STARTLEVEL = 1;
//    private static int level;
//    private final int MAXLEVEL = 2;

    private boolean levelSpawned = false;
//    private boolean ground = false; // todo: move to Variable Manager

    private boolean keyboardControl = true; // false means mouse Control
    private BufferedImage level1 = null, level2 = null;

    private float inscreenMarker;

    public static Texture texture;

    /************
     * Graphics *
     ************/

    private SpriteSheet playerSpriteSheet;
    private BufferedImage[] playerImage;

    public GameController() {

        /******************
         * Window Setting *
         ******************/
        Engine.setTITLE(gameTitle);
//        Engine.setRatio(gameRatio);
        Engine.setWIDTH(gameWidth);
        Engine.setBACKGROUND_COLOR(gameBackground);
        gameHeight = Engine.HEIGHT;
        uiHandler = new UIHandler();
        initializeGraphics();
        initializeAnimations();
        initializeGame();
        initializeLevel();
    }

    /*************************
     * Initializer Functions *
     *************************/

    private void initializeGame() {
        /*************
         * Game Code *
         *************/

        Engine.menuState();
        level1 = Engine.loader.loadImage("/level1.png");
        level2 = Engine.loader.loadImage("/level2.png");
        variableManager = new VariableManager();
        entityManager = new EntityManager(variableManager);
        scanner = new Scanner(System.in);
    }

    private void initializeGraphics() {
        /*************
         * Game Code *
         *************/
        texture = new Texture();
    }

    private void initializeAnimations() {
        /*************
         * Game Code *
         *************/
    }

    private void initializeLevel() {
        /*************
         * Game Code *
         *************/

        levelGenerator = new LevelGenerator(this);
    }

    public void update() {
//        System.out.println(Engine.currentState);
        Engine.timer++;

        if (Engine.stateIs(GameState.Quit))
            Engine.stop();

        if (!Engine.stateIs(GameState.Pause) && !Engine.stateIs(GameState.Game) && !Engine.stateIs(GameState.Transition))
            resetGame();

        if (Engine.stateIs(GameState.Transition)) {
            /*************
             * Game Code *
             *************/

            /*
            * Spawn if the level can loaded and has not already been spawned
            * */

            if (loadMission) {
                if (!levelSpawned)
                    spawn();
            }
        }

        /*
        * Disable the level load permission, as the level is already running
        * */
        if (Engine.stateIs(GameState.Game) && !VariableManager.isConsole()) {
            loadMission = false;
            boolean endGameCondition = variableManager.getHealth() <= 0;

            if (endGameCondition) {
                Engine.gameOverState();
                resetGame();
            }

            /*************
             * Game Code *
             *************/

            else {

                entityManager.update();

                checkCollision();

                checkLevelStatus();
            }
        }
    }

    public void render(Graphics g) {

        if (Engine.currentState == GameState.Highscore) {
//            drawHighScore(g);
        }

        if (Engine.currentState == GameState.Transition) {
            /*************
             * Game Code *
             *************/

            g.setFont(new Font("arial", 1, Utility.percWidth(5)));
            g.setColor(Color.WHITE);
            g.drawString("Level " + variableManager.getLevel(), Utility.percWidth(40), Utility.percHeight(45));
            drawHealth(g);
            drawScore(g);
            drawPower(g);
        }

        if (Engine.currentState == GameState.Game || Engine.currentState == GameState.Pause ||
                Engine.currentState == GameState.GameOver) {


            /*************
             * Game Code *
             *************/

            if (Engine.currentState == GameState.Game || Engine.currentState == GameState.Pause ) {

                renderInCamera(g);

                if (VariableManager.isHud()) {
                    drawHealth(g);

                    entityManager.renderBossHealth(g);

                    drawScore(g);
                    drawPower(g);
                }

            }
        }

        /***************
         * Engine Code *
         ***************/

        uiHandler.render(g);
    }

    private void renderInCamera(Graphics g) {
        /***************
         * Engine Code *
         ***************/

        Graphics2D g2d = (Graphics2D) g;

        // Camera start
        g2d.translate(camera.getX(), camera.getY());

        /*************
         * Game Code *
         *************/
        entityManager.render(g);

        g.setColor(Color.RED);
        g.drawLine(0, (int) inscreenMarker, Engine.WIDTH, (int) inscreenMarker);

        /*****************
         * Engine Code *
         *****************/

        // Camera end
        g2d.translate(-camera.getX(), -camera.getY());
    }

    /************************
     * User Input Functions *
     ************************/

    public void mouseMoved(int mx, int my) {
        /*************
         * Game Code *
         *************/

        uiHandler.checkHover(mx, my);
        giveDestination(mx, my);
    }

    public void mouseDragged(int mx, int my) {
        /*************
         * Game Code *
         *************/

        giveDestination(mx, my);
    }

    public void mousePressed(int mouse) {
        /*************
         * Game Code *
         *************/

        if (mouse == MouseEvent.BUTTON1) {
            shootPlayer();
        }
    }

    public void mouseReleased(int mouse) {
        /*************
         * Game Code *
         *************/

        if (mouse == MouseEvent.BUTTON1) {
            stopShootPlayer();
        }
    }

    public void keyPressed(int key) {
        /*************
         * Game Code *
         *************/

        if (VariableManager.isConsole()) {
//            scanner = new Scanner(System.in);
//            cmd = scanner.nextLine();
//            variableManager.console();
        }

        if (key == 192) {
            System.out.println("TILDE PRESSED");
            VariableManager.console();
        }

        if (Engine.currentState != GameState.Game) {
            // Keyboard to Navigate buttons

            // Enter/Spacebar to select selected
            if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
                uiHandler.chooseSelected();
            }

            if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                uiHandler.keyboardSelection('r');
            }

            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                uiHandler.keyboardSelection('l');
            }
        }

        if (Engine.currentState != GameState.Pause) {
            if (key == (KeyEvent.VK_LEFT) || key == (KeyEvent.VK_A))
                movePlayer('l');

            if (key == (KeyEvent.VK_RIGHT) || key == (KeyEvent.VK_D))
                movePlayer('r');

            if (key == (KeyEvent.VK_UP) || key == (KeyEvent.VK_W))
                movePlayer('u');

            if (key == (KeyEvent.VK_DOWN) || key == (KeyEvent.VK_S))
                movePlayer('d');

            if (key == (KeyEvent.VK_SPACE) || key == (KeyEvent.VK_NUMPAD0))
                shootPlayer();

            if (key == KeyEvent.VK_CONTROL)
                entityManager.switchPlayerBullet();

            if (Engine.currentState == GameState.Game) {
                if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
                    Engine.pauseState();
                }
            }

        } else {

            if (Engine.currentState == GameState.Pause) {
                if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
                    Engine.gameState();
                }
            }

            if (key == (KeyEvent.VK_ESCAPE)) {
                if (Engine.currentState == GameState.Menu) {
                    System.exit(1);
                }
            }
        }

    }

    public void keyReleased(int key) {
        /*************
         * Game Code *
         *************/

        if (key == (KeyEvent.VK_LEFT) || key == (KeyEvent.VK_A))
            stopMovePlayer('l');

        if (key == (KeyEvent.VK_RIGHT) || key == (KeyEvent.VK_D))
            stopMovePlayer('r');

        if (key == (KeyEvent.VK_UP) || key == (KeyEvent.VK_W))
            stopMovePlayer('u');

        if (key == (KeyEvent.VK_DOWN) || key == (KeyEvent.VK_S))
            stopMovePlayer('d');

        if (key == (KeyEvent.VK_SPACE) || key == (KeyEvent.VK_NUMPAD0))
            stopShootPlayer();

    }

    private void setupHighScore() {
        for (int i = 0; i < highScoreNumbers; i++) {
            highScore.add(0);
        }
    }

    /***************************
     * Game Managing Functions *
     ***************************/

    public void resetGame() {

        Engine.timer = 0;
        variableManager.resetScore();
        variableManager.resetPower();
        variableManager.resetHealth();

        /*************
         * Game Code *
         *************/

        variableManager.resetLevel();
        levelSpawned = false;
        entityManager.clearEnemies();
        entityManager.clearBullets();
        entityManager.clearPickups();
        levelSpawned = false;
        uiHandler.ground = false;

//        testingCheat();
    }

    public void checkButtonAction(int mx, int my) {
        uiHandler.checkButtonAction(mx, my);
        performAction();
    }

    public void performAction() {
        ActionTag action = uiHandler.getAction();
        if (action != null) {
            if (action == ActionTag.go) {
                loadMission = true;
            } else if (action == ActionTag.control) {
                keyboardControl = !keyboardControl;
            } else if (action == ActionTag.health) {
                buyHealth();
            } else if (action == ActionTag.power) {
                buyPower();
            } else if (action == ActionTag.ground) {
                buyGround();
            } else if (action == ActionTag.save) {
                save();
            } else if (action == ActionTag.load) {
                load();
            }
            uiHandler.endAction();
        }
    }

    public void save() {
        new SaveData(variableManager);
        System.out.println("Saving");
    }

    public void load() {
        try {
            new LoadData(variableManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Loading");
    }

    public void notifyUIHandler(GameState state) {
            uiHandler.updateState(state);
        }

    /***************************
     * Render Helper Functions *
     ***************************/

    private void drawScore(Graphics g) {
        variableManager.renderScore(g);
    }

    private void drawHealth(Graphics g) {
        variableManager.renderHealth(g);
    }

    private void drawPower(Graphics g) {
        variableManager.renderPower(g);
    }

    /******************
     * User functions *
     ******************/

    // Shop Functions

    private void buyHealth() {
        int cost = 500;

//        int health = variableManager.getHealth();
        int healthDefault = variableManager.getHealthDefault();

        if (variableManager.getScore() >= cost) {
            if (variableManager.getHealth() < healthDefault) {
                variableManager.increaseHealth(25);
                variableManager.decreaseScore(cost);
                if (variableManager.getHealth() > healthDefault)
                    variableManager.resetHealth();
            } else {
                System.out.println("Health is full");
            }
        } else {
            System.out.println("Not enough score");
        }
    }

    private void buyPower() {
        int cost = 1000;

        if (variableManager.getScore() >= cost) {
            if (entityManager.getPlayerPower() < variableManager.getMaxPower()) {
                variableManager.increasePower(1);
                variableManager.decreaseScore(cost);
                if (variableManager.getPower() > variableManager.getMaxPower())
                    variableManager.decreasePower(1);
            } else {
                System.out.println("Max power is reached");
            }
        } else {
            System.out.println("Not enough score");
        }
    }

    private void buyGround() {
        int cost = 2000;
        if (variableManager.getScore() >= cost) {
            if (!variableManager.isGround()) {
                variableManager.decreaseScore(cost);
                variableManager.setGround(true);
                uiHandler.ground = true;
            }
        } else {
            System.out.println("Not enough score");
        }
    }

    public void movePlayer(char c) {
        if (c == 'l' || c == 'r' || c == 'u' | c == 'd') {
            entityManager.movePlayer(c);
        }
    }

    public void stopMovePlayer(char c) {
        if (c == 'l' || c == 'r' || c == 'u' | c == 'd') {
            entityManager.stopMovePlayer(c);
        }
    }

    private void giveDestination(int mx, int my) {
        if (!keyboardControl)
            entityManager.giveDestination(mx, my);
    }

    public void shootPlayer() {
        entityManager.playerCanShoot();
    }

    public void stopShootPlayer() {
        entityManager.playerCannotShoot();
    }

    public void checkCollision() {
        entityManager.playerVsPickupCollision();
        entityManager.playerVsEnemyCollision();
        entityManager.playerVsEnemyBulletCollision();
        entityManager.enemyVsPlayerBulletCollision();
    }

    private void spawn() {
        levelSpawned = !levelSpawned;
        Engine.gameState();

        int level = variableManager.getLevel();

        if (level == 1)
            levelGenerator.loadImageLevel(level1);

        if (level == 2)
            levelGenerator.loadImageLevel(level2);

        entityManager.spawnFlag();
    }

    public static Camera getCamera() {
        return camera;
    }

    // Using the id, spawns the appropriate entity.
    // todo: condense everything into one call to the EntityManager
    public void spawnEntity(int x, int y, EntityID id, Color color) {
        if (id == EntityID.Player) {
            spawnPlayer(x,y, levelHeight);
        }
        if (id == EntityID.Boss) {
            entityManager.spawnBoss(variableManager.getLevel(), x, y);
        }
        else
            entityManager.spawnEntity(x, y, id, color);
    }

    public void spawnPlayer(int width, int height, int levelHeight) {
        // todo: move to EntityManager
        offsetHorizontal = -gameWidth / 2 + 32;
        offsetVertical = gameHeight - 160;
        entityManager.spawnPlayer(width, height, levelHeight, variableManager.getPower(), variableManager.isGround());

        // sets the camera's width to center the player horizontally, essentially to 0, and
        // adjust the height so that player is at the bottom of the screen
//        camera = new Camera(player.getX() + offsetHorizontal, -player.getY() + offsetVertical);
        camera = new Camera(0, -entityManager.getPlayerY() + offsetVertical);
        camera.setMarker(entityManager.getPlayerY());
        inscreenMarker = camera.getMarker() + 100;
    }

    public void spawnCamera(int width, int height) {
        camera = new Camera(width, -750); // -700 = 2 fps;
    }

    // if the flag crosses the screen, advance level and if no levels remain, end game
    public void checkLevelStatus() {
        // If the boss is killed, updates the boolean variable
        entityManager.checkBoss();

        if (entityManager.getFlagY() > levelHeight && !variableManager.isBossLives()) {
            variableManager.nextLevel();
            levelSpawned = false;
            variableManager.setBossLives(false);

            if (variableManager.finishedFinalLevel()) {
                Engine.menuState(); // stub
                resetGame();
            } else {
                Engine.transitionState();
            }
        }
    }

    public void setLevelHeight(int h) {
        levelHeight = h;
    }

    private void testingCheat() {
        variableManager.setLevel(2);
        variableManager.setPower(3);
        variableManager.setGround(true);
    }

    public static Texture getTexture() {
        return texture;
    }

}
