/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.ArrayList;
/**
 * Sketch class for the "Dragon Boat Legend" game.
 * This is a Role-playing and Mini Game based on the Legend that created the Dragon Boat Festival.
 * Usage of AI includes finding out calculations (including boxes, sizes, and such), easing, offsets, and pop/push Matrix
 * @author Jia Xin Li
 * @version 1.0
 */
public class Sketch extends PApplet {
    // Font Sizes
    private PFont normalSize, titleSize;
    // Background
    private PImage background1;
    private PImage background2;
    private PImage background3;
    private PImage background4;
    // Role-play Scenes
    private static int scene = 0;
    // Player position and movement constants
    private float villagerX = 30;
    private float villagerY = 30;
    //Size constants for game elements
    private static final int VILLAGER_SIZE = 40;
    private static final int WALL_SIZE = 40;
    private static final int COMPANION_SIZE = 50;
    private static final int ATTACK_SIZE = 50;
    // Direction constants
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    // Attack animation offset
    private static final float ATTACK_Y_OFFSET = -15;
    // Tile images for maze rendering
    private PImage groundTile;
    private PImage wallTile;
    private PImage breakableWallTile;
    private PImage waterTile;
    // Title screen
    private boolean showTitleScreen = true;
    // Animation frames
    private PImage[] walkingFrames;
    private PImage[] attackFrames;
    private PImage playerImage;
    // Animation control variables
    private int currentFrame = 0;
    private int attackFrame = 0;
    private int frameCounter = 0;
    private static final int FRAME_DELAY = 5;
    private boolean isMoving = false;
    private boolean isAttacking = false;
    private int lastDirection = RIGHT;
    // Game state variables
    private int gameTimer;
    private int startTime;
    private int endTime = -1;
    private boolean gameEnded;
    private ScoreManager scoreManager;
    private String playerName = "";
    private boolean enteringName = false;
    // Companion variables
    private CompanionState Cow; 
    private CompanionState Chicken; 
    private CompanionState Pig;
    
    @Override
    public void settings() {
        size(600, 500);
    }
    public void setup(){
        background(255);
        // Setting first role-play scene
        scene = 0;
        // Initalize fonts
        normalSize = createFont("Font/Santiago-Sunrise.otf", 17);
        titleSize = createFont("Font/Santiago-Sunrise.otf", 50);
        // Initalize background
        background1 = loadImage("Background/background1.png");
        background2 = loadImage("Background/background2.png");
        background3 = loadImage("Background/background3.png");
        background4 = loadImage("Background/background4.png");
        // Initalize tiles 
        groundTile = loadImage("level/ground.png");
        wallTile = loadImage("level/wall.png");
        breakableWallTile = loadImage("level/wallBreakable.png");
        waterTile = loadImage("level/water.png");
        // Initalize companions
        Cow = new CompanionState(this, "assets/Cow.png", 55); 
        Chicken = new CompanionState(this, "assets/Chicken.png"); 
        Pig = new CompanionState(this, "assets/Pig.png");
        //Resize tile images to standard wall size
        if (groundTile != null) groundTile.resize(WALL_SIZE, WALL_SIZE);
        if (wallTile != null) wallTile.resize(WALL_SIZE, WALL_SIZE);
        if (breakableWallTile != null) breakableWallTile.resize(WALL_SIZE, WALL_SIZE);
        if (waterTile != null) waterTile.resize(WALL_SIZE, WALL_SIZE);
        // Initalize walking frames
        walkingFrames = new PImage[6];
        // Load each walking animation frame into the walkingFrames array
        for (int i = 0; i < walkingFrames.length; i++) {
            // Load the image file named "walking/0.png", "walking/1.png", etc
            walkingFrames[i] = loadImage("walking/" + i + ".png");
            // If the image successfully loaded, resize it to the villager's standard size
            if (walkingFrames[i] != null) {
                walkingFrames[i].resize(VILLAGER_SIZE, VILLAGER_SIZE);
            }
        }
        // Load attack animation frames (0.png to 5.png) into the attackFrames array
        attackFrames = new PImage[6];
        for (int i = 0; i < attackFrames.length; i++) {
            // Load each attack frame from the "attack" folder
            attackFrames[i] = loadImage("attack/" + i + ".png");
            // If the image loads successfully, resize it to the defined attack sprite size
            if (attackFrames[i] != null) {
                attackFrames[i].resize(ATTACK_SIZE, ATTACK_SIZE);
            }
        }
        // Load the main player sprite
        playerImage = loadImage("assets/player.png");
        // Resize the player sprite if it loaded correctly
        if (playerImage != null) {
            playerImage.resize(VILLAGER_SIZE, VILLAGER_SIZE);
        }
        // Initialize the score manager and load/create the highscores file
        scoreManager = new ScoreManager(this, "highscores.txt");
        // Record the starting time of the game session
        startTime = millis();
        // Set the total game duration (in milliseconds) — here, 180 seconds (3 minutes)
        gameTimer = 180000; 
    }
    
    /**
     * Inner class representing companion state and behavior.
     * Extends GameEntity to provide additional game-specific functionality.
     */
    private class CompanionState extends GameEntity {
        // Flag indicating if companion is collected
        boolean collected;
        // Target x-coordinate for companion movement
        float targetX, targetY;
        private PImage sprite;
        
        /**
         * Constructor for CompanionState.
         * @param imagePath Path to companion sprite image
         */
        CompanionState(PApplet app, String imagePath) {
            super(0, 0, null);
            // Initially not collected
            this.collected = false; 
            // Load and resize companion sprite
            this.sprite = app.loadImage(imagePath);
            if (this.sprite != null) {
                this.sprite.resize(COMPANION_SIZE, COMPANION_SIZE);
            }
        }
        
        /**
         * Overloaded Constructor for CompanionState.
         * @param imagePath Path to companion sprite image
         * @param size for companion sprite image resize
         */
        CompanionState(PApplet app, String imagePath, int size) {
            super(0, 0, null);
            // Initially not collected
            this.collected = false; 
            // Load and resize companion sprite
            this.sprite = app.loadImage(imagePath);
            if (this.sprite != null) {
                this.sprite.resize(COMPANION_SIZE, COMPANION_SIZE);
            }
        }
        
        /**
         * Update companion position relative to player.
         * Implements a smooth following mechanism with easing.
         * 
         * @param playerX Current player x-coordinate
         * @param playerY Current player y-coordinate
         * @param offset Distance offset from player
         * @param companionIndex Index of companion for positioning
         */
        private void updatePosition(float villagerX, float villagerY, float offset, int companionIndex) {
            if (collected) {
                // Determine target position based on player movement and direction
                boolean isUpPressed = keyPressed && keyCode == UP;
                boolean isDownPressed = keyPressed && keyCode == DOWN;
                
                if (isUpPressed) {
                    targetX = villagerX;
                    targetY = villagerY + (offset * (companionIndex + 1));
                } else if (isDownPressed) {
                    targetX = villagerX;
                    targetY = villagerY - (offset * (companionIndex + 1));
                } else if (lastDirection == LEFT) {
                    targetX = villagerX + (offset * (companionIndex + 1));
                    targetY = villagerY;
                } else {
                    targetX = villagerX - (offset * (companionIndex + 1));
                    targetY = villagerY;
                }
                // Smooth movement using easing
                float easing = 0.2f;
                x += (targetX - x) * easing;
                y += (targetY - y) * easing;
            }
        }
        /**
        * Draw companion if collected.
        * @param app Processing application context
        */
        public void draw(PApplet app) {
            if (collected && sprite != null) {
                app.imageMode(CENTER);
                app.image(sprite, x, y);
            }
        }
    }
    
    // Movement variables
    // Base movement speed
    private float baseSpeed = 5;
    // Current movement speed (can be modified by companions)
    private float currentSpeed = baseSpeed;
    // Game maze representation
    /** 2D array representing the game maze 
     * Tile values:
     * 0: Empty space
     * 1: Wall
     * 2: Water
     * 3: Cow initial position
     * 4: Chicken initial position
     * 5: Pig initial position
     * 6: Breakable wall
     */
    private int[][] maze = {
        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 4, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 0, 0, 0, 5, 0, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
        {1, 0, 3, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 2, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 2, 1},
        {1, 0, 0, 0, 2, 0, 6, 0, 0, 1, 1, 1, 1, 2, 1},
        {1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
        {1, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 6, 0, 2, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1}
    };
    
    /**
     * Calculate player's final score based on time left and companions collected.
     * 
     * @param timeLeft Remaining game time
     * @param companionsCollected Number of companions collected
     * @return Calculated score
     */
    private int calculateScore(int timeLeft, int companionsCollected) {
        return (timeLeft / 1000) * 100 + (companionsCollected * 500);
    }
    
    /**
     * Renders the title screen with game instructions and animated characters.
     * Displays game title, companion characters, and how-to-play instructions.
     */
    private void drawTitleScreen() {
        imageMode(CENTER); image(background4, 300, 250, 600, 500);
        // Set text alignment and style for title
        textAlign(CENTER, CENTER);
        textFont(titleSize);
        fill(255, 255, 255);
        text("Save Player!", width/2, 150);
        // Render game instructions
        textFont(normalSize);
        text("How to Play:", width/2, 225);
        // Detailed game instructions
        textFont(normalSize);
        String[] instructions = {
            "Use WASD Keys for Movement",
            "You're the Villager",
            "Cow Helps Cross Water",
            "Chicken Increases Speed",
            "Pig Break Obstacles (Space)",
            "Save Player Before Time Runs Out!",
        };
        // Render instructions
        for (int i = 0; i < instructions.length; i++) {
            text(instructions[i], width/2, 275 + i * 20);
        }
        textFont(normalSize);
        text("Press ENTER to start", width/2, 425);
    }
    /**
     * Renders the name entry screen after game ends.
     * Displays final score, allows player to enter name, and shows high scores.
     */
    private void drawNameEntry() {
        // Semi-transparent dark background
        background(0, 150);
        // Setup text styling
        textAlign(CENTER, CENTER);
        textFont(titleSize);
        fill(255);
        text("Congrats! Saved!", width/2, height/3);
        // Calculate companions collected
        int companionsCollected = (Cow.collected ? 1 : 0) +
                                (Chicken.collected ? 1 : 0) +
                                (Pig.collected ? 1 : 0);
        // Calculate remaining time and final score
        int timeLeft = endTime >= 0 ? gameTimer - (endTime - startTime) : 0;
        int finalScore = calculateScore(timeLeft, companionsCollected);
        // Display score
        textFont(normalSize);
        text("Score: " + finalScore, width/2, height/2 - 40);
        text("Enter Your Name: " + playerName + "_", width/2, height/2);
        // Display high scores
        ArrayList<Score> topScores = scoreManager.getTopScores(5);
        text("High Scores:", width/2, height/2 + 40);
        // Display each score from the topScores list on the screen
        for (int i = 0; i < topScores.size(); i++) {
            // Retrieve the Score object at index i
            Score score = topScores.get(i);
            // Draw the player's name and score, spacing each entry 20px apart vertically
            text(score.name + ": " + score.score, width/2, height/2 + 70 + i * 20);
        }
    }
    /**
     * Updates positions of collected companions relative to player.
     * Implements a smooth following mechanism for companions.
     */
    private void updateCompanions() {        
        // Update each companion's position if collected
        // Offset is between player and companions
        if (Cow.collected) {
            Cow.updatePosition(villagerX, villagerY, 30, 0);
        }
        if (Chicken.collected) {
            Chicken.updatePosition(villagerX, villagerY, 30, 1);
        }
        if (Pig.collected) {
            Pig.updatePosition(villagerX, villagerY, 30, 2);
        }
    }
    
    /**
     * Renders the game maze with various tile types and companion positions.
     * Handles rendering of ground, walls, water, breakable walls, and companions.
     */
    private void drawMaze() {
        // Iterate through maze grid
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                // Calculate tile position
                float x = j * WALL_SIZE;
                float y = i * WALL_SIZE;
                // Always render ground tile
                if (groundTile != null) {
                    image(groundTile, x, y);
                }
                // Render different tile types based on maze value
                switch (maze[i][j]) {
                    case 1: 
                        if (wallTile != null) {
                            image(wallTile, x, y);
                        }
                        break;
                    case 2: 
                        if (waterTile != null) {
                            image(waterTile, x, y);
                        }
                        break;
                    case 6:
                        if (breakableWallTile != null) {
                            image(breakableWallTile, x, y);
                        }
                        break;
                    case 3:
                        if (!Cow.collected) {
                            imageMode(CENTER);
                            image(Cow.sprite, x + WALL_SIZE/2, y + WALL_SIZE/2);
                            imageMode(CORNER);
                        }
                        break;
                    case 4: 
                        if (!Chicken.collected) {
                            imageMode(CENTER);
                            image(Chicken.sprite, x + WALL_SIZE/2, y + WALL_SIZE/2);
                            imageMode(CORNER);
                        }
                        break;
                    case 5: 
                        if (!Pig.collected) {
                            imageMode(CENTER);
                            image(Pig.sprite, x + WALL_SIZE/2, y + WALL_SIZE/2);
                            imageMode(CORNER);
                        }
                        break;
                }
            }
        }
    }
    
    /**
     * Main game rendering method called every frame.
     * Handles game state, rendering, animations, and game logic.
     */
    @Override
    public void draw(){
        // Switch case for starting screen
        switch (scene){
            case 0:
                imageMode(CENTER); image(background1, 300, 250, 600, 500);
                textFont(titleSize);
                textAlign(CENTER);
                text("Dragon Boat Legend", 300, 200);
                fill(0, 255, 0);
                rect(50, 285, 200, 75);
                fill(0);
                text("Start", 150, 340); 
                fill(255, 0, 0);
                rect(350, 280, 200, 75);
                fill(0);
                text("Exit", 450 , 340); 
            break;
            // Switch case for scene 1
            case 1:
                imageMode(CENTER); image(background2, 300, 250, 600, 500);
                textFont(normalSize);
                fill(255); 
                rect(50, 325, 500, 100);
                fill(0);
                text("Once upon a time, there lived a noble scholar, poet, and advisor, you. Villagers respected you. In this game, you must try to save yourself and the villagers", 50 + 10, 325 + 20, 500 - 20, 100 - 30);
                fill(255); 
                rect(490, 450, 60, 20);
                fill(0);
                text("Next", 520, 465);
            break;
            // Switch case for scene 2
            case 2:
                imageMode(CENTER); image(background2, 300, 250, 600, 500);
                textFont(normalSize);
                fill(255); 
                rect(50, 325, 500, 100);
                fill(0);
                text("China, at this time, was in a state of war, and you realized that the neighbouring country was an impending threat. Would you warn the King?", 50 + 10, 325 + 20, 500 - 20, 100 - 30);
                fill(255, 0, 0); 
                rect(400, 450, 60, 20);
                fill(0);
                text("No", 430, 465);
                fill(0, 255, 0);
                rect(150, 450, 60, 20);
                fill(0);
                text("Yes", 180, 465);
            break;
            // Switch case for scene 3
            case 3:
                background(0);
                textAlign(CENTER); 
                fill(255); 
                textFont(titleSize);
                text("You Died", 600/2, 500/2 - 20); 
                textFont(normalSize);
                text("Neighbouring kingdom killed you and the villagers", 600/2, 500/2 + 20);
                fill(255, 255, 255);
                rect(600/2 - 100, 500/2 + 74/2, 200, 74);
                fill(0);
                textFont(titleSize);
                text("Restart", 600/2, 500/2 + 93); 
            break;
            // Switch case for scene 4
            case 4:
                imageMode(CENTER); image(background3, 300, 250, 600, 500);
                textFont(normalSize);
                fill(255); 
                rect(50, 325, 500, 100);
                fill(0);
                text("You got exiled as the King did not believe you. Sadness took over and you decide to suicide in river. Point of view is now changing to villagers...", 50 + 10, 325 + 20, 500 - 20, 100 - 30);
                fill(255); 
                rect(490, 450, 60, 20);
                fill(0);
                text("Next", 520, 465);
            break;
            // Switch case maze game
            case 5:
                // Handle title screen
                if (showTitleScreen) {
                    drawTitleScreen();
                    return;
                }
                // Handle name entry screen
                if (enteringName) {
                    drawNameEntry();
                    return;
                }
                // Main game rendering
                background(255);
                imageMode(CORNER);
                drawMaze();
                imageMode(CENTER);
                // Update companion positions
                updateCompanions();
                // Draw player with directional and animation handling
                pushMatrix();
                translate(villagerX, villagerY);
                if (lastDirection == LEFT) {
                    scale(-1, 1);
                }
                // Handle attack animation
                if (isAttacking && attackFrames[attackFrame] != null) {
                    image(attackFrames[attackFrame], 0, ATTACK_Y_OFFSET);
                    frameCounter++;
                    if (frameCounter >= FRAME_DELAY) {
                        attackFrame = (attackFrame + 1) % attackFrames.length;
                        frameCounter = 0;
                        if (attackFrame == 0) {
                            isAttacking = false;
                        }
                    }
                // Handle walking animation
                } else if (walkingFrames[currentFrame] != null) {
                    image(walkingFrames[currentFrame], 0, 0);
                    if (isMoving) {
                        frameCounter++;
                        if (frameCounter >= FRAME_DELAY) {
                            currentFrame = (currentFrame + 1) % walkingFrames.length;
                            frameCounter = 0;
                        }
                    }
                }
                popMatrix();
                // Draw villagers and companions
                Cow.draw(this);
                Chicken.draw(this);
                Pig.draw(this);
                // Draw player (goal)
                if (playerImage != null) {
                    image(playerImage, 380, 380);
                }
                // Update and display timer
                int timeLeft;
                if (gameEnded) {
                    timeLeft = endTime >= 0 ? gameTimer - (endTime - startTime) : 0;
                } else {
                    timeLeft = gameTimer - (millis() - startTime);
                    if (timeLeft <= 0) {
                        timeLeft = 0;
                        if (!gameEnded) {
                            gameEnded = true;
                            endTime = millis();
                            enteringName = true;
                            return;
                        }
                    }
                }
                // Display time and companions info
                textAlign(LEFT, TOP);
                textFont(normalSize);
                fill(0);
                text("Time: " + nf(timeLeft/1000, 2) + "s", 10, 10);
                text("Companions: " + 
                    (Cow.collected ? "Cow " : "") +
                    (Chicken.collected ? "Dragon " : "") +
                    (Pig.collected ? "Pig" : ""), 10, 30);
                // Check win condition
                if (dist(villagerX, villagerY, 380, 380) < VILLAGER_SIZE && !gameEnded) {
                    gameEnded = true;
                    endTime = millis();
                    enteringName = true;
                }
                // Reset movement flag
                isMoving = false;
            break;
        }
    }

    /**
     * Handles mouse input for game interactions.
     * Manages player next button, start button, yes button, no button, restart button, or exit button
     */
    public void mousePressed(){
        switch (scene){
            // Check to see if player clicked start or exit
            case 0:
                if (mouseX > 600/4 - 100 && mouseX < 600/4 - 100 + 200 && mouseY > 500/2 + 75/2 && mouseY < 500/2 + 75/2 + 75){
                    background(255);
                    scene = 1;
                }
                else if (mouseX > 600/4 + 200 && mouseX < 600/4 + 200 + 200 && mouseY > 500/2 + 75/2 && mouseY < 500/2 + 75/2 + 75){
                    exit();
                }
            break;
            // Check to see if player clicked next
            case 1:
                if (mouseX > 490 && mouseX < 490 + 60 && mouseY > 450 && mouseY < 450 + 20){
                    scene = 2;
                }
            break;
            // Check to see if player clicked yes or no
            case 2:
                if (mouseX > 150 && mouseX < 150 + 60 && mouseY > 450 && mouseY < 450 + 20) { 
                    scene = 4; 
                } 
                else if (mouseX > 400 && mouseX < 400 + 60 && mouseY > 450 && mouseY < 450 + 20) { 
                    scene = 3; 
                }
            break;
            // Check to see if player clicked restart
            case 3:
                if (mouseX > 600/2 - 100 && mouseX < 600/2 - 100 + 200 && mouseY > 500/2 + 74/2 && mouseY < 500/2 + 74/2 + 74) {
                    scene = 0;
                }
            // Check to see if player clicked next
            case 4:
                if (mouseX > 490 && mouseX < 490 + 60 && mouseY > 450 && mouseY < 450 + 20){
                    scene = 5;
                }
            break;
        }
    }
    
    /**
     * Handles keyboard input for game interactions.
     * Manages player movement, companion collection, and special actions.
     */
    public void keyPressed() {
        // Handle title screen start
        if (showTitleScreen) {
            if (keyCode == ENTER) {
                showTitleScreen = false;
            }
        } 
        // Handle name entry
        else if (enteringName) {
            if (key == ENTER && playerName.length() > 0) {
                // Calculate and save final score
                int timeLeft = endTime >= 0 ? gameTimer - (endTime - startTime) : 0;
                int companionsCollected = (Cow.collected ? 1 : 0) +
                                       (Chicken.collected ? 1 : 0) +
                                       (Pig.collected ? 1 : 0);
                int finalScore = calculateScore(timeLeft, companionsCollected);
                scoreManager.saveScore(playerName, finalScore);
                exit();
            } else if (key == BACKSPACE && playerName.length() > 0) {
                // Remove last character from name
                playerName = playerName.substring(0, playerName.length() - 1);
            } else if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {
                // Add alphabetic characters to name
                if (playerName.length() < 10) {
                    playerName += key;
                }
            }
        } 
        // Main game input handling
        else {
            float nextX = villagerX;
            float nextY = villagerY;
            // Pig special attack
            if (key == ' ' && Pig.collected && !isAttacking) {
                isAttacking = true;
                attackFrame = 0;
                // Break nearby breakable walls
                int villagerCellX = (int)(villagerX / WALL_SIZE);
                int villagerCellY = (int)(villagerY / WALL_SIZE);
                // Check all neighboring cells in a 3×3 area around the villager
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int checkX = villagerCellX + dx;
                        int checkY = villagerCellY + dy;
                        // Check bounds and break breakable walls
                        if (checkX >= 0 && checkX < maze[0].length && 
                            checkY >= 0 && checkY < maze.length) {
                            // Compute the coordinates of the cell being inspected
                            if (maze[checkY][checkX] == 6) {
                                maze[checkY][checkX] = 0;
                            }
                        }
                    }
                }
                return;
            }
            // Movement handling
            boolean moved = false;
            // Check movement keys
            if (keyCode == UP || key == 'w' || key == 'W') {
                nextY -= currentSpeed;
                moved = true;
            } 
            if (keyCode == DOWN || key == 's' || key == 'S') {
                nextY += currentSpeed;
                moved = true;
            } 
            if (keyCode == LEFT || key == 'a' || key == 'A') {
                nextX -= currentSpeed;
                moved = true;
                lastDirection = LEFT;
            } 
            if (keyCode == RIGHT || key == 'd' || key == 'D') {
                nextX += currentSpeed;
                moved = true;
                lastDirection = RIGHT;
            }
            // Process movement and interactions
            if (moved) {
                int cellX = (int)(nextX / WALL_SIZE);
                int cellY = (int)(nextY / WALL_SIZE);
                if (cellX >= 0 && cellX < maze[0].length && 
                    cellY >= 0 && cellY < maze.length) {
                    // Check for collectible companions
                    switch (maze[cellY][cellX]) {
                        case 3: 
                            Cow.collected = true;
                            Cow.x = cellX * WALL_SIZE + WALL_SIZE/2;
                            Cow.y = cellY * WALL_SIZE + WALL_SIZE/2;
                            maze[cellY][cellX] = 0;
                            break;
                        case 4: 
                            Chicken.collected = true;
                            Chicken.x = cellX * WALL_SIZE + WALL_SIZE/2;
                            Chicken.y = cellY * WALL_SIZE + WALL_SIZE/2;
                            currentSpeed = baseSpeed * 1.5f;
                            maze[cellY][cellX] = 0;
                            break;
                        case 5: 
                            Pig.collected = true;
                            Pig.x = cellX * WALL_SIZE + WALL_SIZE/2;
                            Pig.y = cellY * WALL_SIZE + WALL_SIZE/2;
                            maze[cellY][cellX] = 0;
                            break;
                    }
                    // Check if movement is allowed
                    if (maze[cellY][cellX] == 0 || 
                        (maze[cellY][cellX] == 2 && Cow.collected)) {
                        villagerX = nextX;
                        villagerY = nextY;
                        isMoving = true;
                    }
                }
            }
        }
    }
}