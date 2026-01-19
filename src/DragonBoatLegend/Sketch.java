/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import java.util.ArrayList;
import processing.core.PApplet;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNER;
import processing.core.PImage;
import processing.core.PFont;
/**
 *
 * @author Jia Xin Li
 */
public class Sketch extends PApplet {
    private PFont normalSize, titleSize;
    private PImage background1;
    private PImage background2;
    private PImage background3;
    private PImage background4;
    
    private static int scene = 0;
    private float villagerX = 30;
    private float villagerY = 30;
    
    private static final int VILLAGER_SIZE = 40;
    private static final int WALL_SIZE = 40;
    private static final int COMPANION_SIZE = 50;
    private static final int ATTACK_SIZE = 50;
    
    private static final int RIGHT = 1;
    private static final int LEFT = -1;
    
    private static final float ATTACK_Y_OFFSET = -15;

    private PImage groundTile;
    private PImage wallTile;
    private PImage breakableWallTile;
    private PImage waterTile;

    private boolean showTitleScreen = true;
    
    private PImage[] walkingFrames;
    private PImage[] attackFrames;
    private PImage playerImage;
    
    private int currentFrame = 0;
    private int attackFrame = 0;
    private int frameCounter = 0;
    private static final int FRAME_DELAY = 5;
    private boolean isMoving = false;
    private boolean isAttacking = false;
    private int lastDirection = RIGHT;
    
    private int gameTimer;
    private int startTime;
    private int endTime = -1;
    private boolean gameEnded;
    private ScoreManager scoreManager;
    private String playerName = " ";
    private boolean enteringName = false;
    
    private CompanionState Cow; 
    private CompanionState Chicken; 
    private CompanionState Pig;
    
    private class CompanionState extends GameEntity {
        boolean collected;
        float targetX, targetY;
        
        PImage sprite;
        
        CompanionState(PApplet app, String imagePath) {
            super(0, 0, null);
            this.collected = false;
            this.sprite = app.loadImage(imagePath);
            if (this.sprite != null) {
                this.sprite.resize(COMPANION_SIZE, COMPANION_SIZE);
            }
        }
        CompanionState(PApplet app, String imagePath, int size) { 
            super(0, 0, null); 
            this.collected = false; 
            this.sprite = app.loadImage(imagePath); 
            if (this.sprite != null) { 
                this.sprite.resize(size, size); 
            } 
        }
        
        void updatePosition(float villagerX, float villagerY, float offset, int companionIndex) {
            if (collected) {
                boolean isUpPressed = keyPressed && keyCode == UP;
                boolean isDownPressed = keyPressed && keyCode == DOWN;
                boolean isLeftPressed = keyPressed && keyCode == LEFT;
                boolean isRightPressed = keyPressed && keyCode == RIGHT;
                
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
                
                float easing = 0.2f;
                x += (targetX - x) * easing;
                y += (targetY - y) * easing;
            }
        }
        
        public void draw(PApplet app) {
            if (collected && sprite != null) {
                app.imageMode(CENTER);
                app.image(sprite, x, y);
            }
        }
    }
    
    private float baseSpeed = 5;
    private float currentSpeed = baseSpeed;
    
    private int[][] maze = {
        {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 4, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 0, 0, 0, 5, 0, 0, 0, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
        {1, 0, 3, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
        {1, 0, 0, 0, 2, 0, 6, 0, 0, 0, 0, 0, 0, 2, 1},
        {1, 0, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1},
        {1, 0, 0, 0, 2, 0, 0, 0, 0, 2, 1, 1, 1, 2, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 6, 0, 2, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1}
    };

    public void settings() {
        size(600, 500);
    }
    public void setup(){
        size(600, 500);
        
        background(255);
        scene = 0;
        normalSize = createFont("Font/Santiago-Sunrise.otf", 17);
        titleSize = createFont("Font/Santiago-Sunrise.otf", 50);
        background1 = loadImage("Background/background1.png");
        background2 = loadImage("Background/background2.png");
        background3 = loadImage("Background/background3.png");
        background4 = loadImage("Background/background4.png");
        
        groundTile = loadImage("level/ground.png");
        wallTile = loadImage("level/wall.png");
        breakableWallTile = loadImage("level/wallBreakable.png");
        waterTile = loadImage("level/water.png");
        Cow = new CompanionState(this, "assets/Cow.png", 55); 
        Chicken = new CompanionState(this, "assets/Chicken.png"); 
        Pig = new CompanionState(this, "assets/Pig.png");
        
        if (groundTile != null) groundTile.resize(WALL_SIZE, WALL_SIZE);
        if (wallTile != null) wallTile.resize(WALL_SIZE, WALL_SIZE);
        if (breakableWallTile != null) breakableWallTile.resize(WALL_SIZE, WALL_SIZE);
        if (waterTile != null) waterTile.resize(WALL_SIZE, WALL_SIZE);
        
        walkingFrames = new PImage[6];
        for (int i = 0; i < walkingFrames.length; i++) {
            walkingFrames[i] = loadImage("walking/" + i + ".png");
            if (walkingFrames[i] != null) {
                walkingFrames[i].resize(VILLAGER_SIZE, VILLAGER_SIZE);
            }
        }
        
        attackFrames = new PImage[6];
        for (int i = 0; i < attackFrames.length; i++) {
            attackFrames[i] = loadImage("attack/" + i + ".png");
            if (attackFrames[i] != null) {
                attackFrames[i].resize(ATTACK_SIZE, ATTACK_SIZE);
            }
        }
        
        playerImage = loadImage("assets/Player.png");
        if (playerImage != null) {
            playerImage.resize(VILLAGER_SIZE, VILLAGER_SIZE);
        }
        
        scoreManager = new ScoreManager(this, "highscores.txt");
        startTime = millis();
        gameTimer = 180000;
    }
    

    private int calculateScore(int timeLeft, int companionsCollected) {
        return (timeLeft / 1000) * 100 + (companionsCollected * 500);
    }
    
    private void drawTitleScreen() {
        imageMode(CENTER); image(background4, 300, 250, 600, 500);
        
        textAlign(CENTER, CENTER);
        textFont(titleSize);
        text("Save Player!", width/2, 150);
              
        textFont(normalSize);
        fill(255, 255, 255);
        text("How to Play:", width/2, 220);
        
        String[] instructions = {
            "Use WASD Keys for Movement",
            "You're the Villager!",
            "Cow Helps Cross Water",
            "Chicken Increases Speed",
            "Pig Break Obstacles (Space)",
            "Find Player Before Time Runs Out!",
        };
        
        for (int i = 0; i < instructions.length; i++) {
            text(instructions[i], width/2, 270 + i * 20);
        }
        
        text("Press ENTER to start", width/2, height - 40);
    }
    

    private void drawNameEntry() {
        background(0, 150);
        
        textAlign(CENTER, CENTER);
        textFont(titleSize);
        fill(255);
        text("Congrats! Saved!", width/2, height/3);
        
        int companionsCollected = (Cow.collected ? 1 : 0) +
                                (Chicken.collected ? 1 : 0) +
                                (Pig.collected ? 1 : 0);
        
        int timeLeft = endTime >= 0 ? gameTimer - (endTime - startTime) : 0;
        int finalScore = calculateScore(timeLeft, companionsCollected);
        
        textFont(normalSize);
        text("Score: " + finalScore, width/2, height/2 - 40);
        text("Enter Your Name: " + playerName + "_", width/2, height/2);
        
        ArrayList<Score> topScores = scoreManager.getTopScores(5);
        text("High Scores:", width/2, height/2 + 40);
        for (int i = 0; i < topScores.size(); i++) {
            Score score = topScores.get(i);
            text(score.name + ": " + score.score, width/2, height/2 + 70 + i * 20);
        }
    }

    private void updateCompanions() {        
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
 
    private void drawMaze() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                float x = j * WALL_SIZE;
                float y = i * WALL_SIZE;
                
                if (groundTile != null) {
                    image(groundTile, x, y);
                }
                
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
    
    @Override
    public void draw(){
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
            case 5:
                if (showTitleScreen) {
                    drawTitleScreen();
                    return;
                }
        
                if (enteringName) {
                    drawNameEntry();
                    return;
                }
        
                background(255);
                imageMode(CORNER);
                drawMaze();
                imageMode(CENTER);
        
                updateCompanions();
        
                pushMatrix();
                translate(villagerX, villagerY);
                if (lastDirection == LEFT) {
                    scale(-1, 1);
                }
        
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
        
                Cow.draw(this);
                Chicken.draw(this);
                Pig.draw(this);
        
                if (playerImage != null) {
                    image(playerImage, 380, 380);
                }
        
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
        
                textAlign(LEFT, TOP);
                textFont(normalSize);
                fill(0);
                text("Time: " + nf(timeLeft/1000, 2) + "s", 10, 10);
        
                if (dist(villagerX, villagerY, 380, 380) < VILLAGER_SIZE && !gameEnded) {
                    gameEnded = true;
                    endTime = millis();
                    enteringName = true;
                }
                isMoving = false;
            break;
        }
    }

    public void mousePressed(){
        switch (scene){
            case 0:
                if (mouseX > 600/4 - 100 && mouseX < 600/4 - 100 + 200 && mouseY > 500/2 + 75/2 && mouseY < 500/2 + 75/2 + 75){
                    background(255);
                    scene = 1;
                }
                else if (mouseX > 600/4 + 200 && mouseX < 600/4 + 200 + 200 && mouseY > 500/2 + 75/2 && mouseY < 500/2 + 75/2 + 75){
                    exit();
                }
            break;
            case 1:
                if (mouseX > 490 && mouseX < 490 + 60 && mouseY > 450 && mouseY < 450 + 20){
                    scene = 2;
                }
            break;
            case 2:
                if (mouseX > 150 && mouseX < 150 + 60 && mouseY > 450 && mouseY < 450 + 20) { 
                    scene = 4; 
                } 
                else if (mouseX > 400 && mouseX < 400 + 60 && mouseY > 450 && mouseY < 450 + 20) { 
                    scene = 3; 
                }
            break;
            case 3:
                if (mouseX > 600/2 - 100 && mouseX < 600/2 - 100 + 200 && mouseY > 500/2 + 74/2 && mouseY < 500/2 + 74/2 + 74) {
                    scene = 0;
                }
            case 4:
                if (mouseX > 490 && mouseX < 490 + 60 && mouseY > 450 && mouseY < 450 + 20){
                    scene = 5;
                }
            break;
        }
    }
    

    public void keyPressed() {
        if (showTitleScreen) {
            if (keyCode == ENTER) {
                showTitleScreen = false;
                return;
            }
        } 
        else if (enteringName) {
            if (key == ENTER && playerName.length() > 0) {
                int timeLeft = endTime >= 0 ? gameTimer - (endTime - startTime) : 0;
                int companionsCollected = (Cow.collected ? 1 : 0) +
                                       (Chicken.collected ? 1 : 0) +
                                       (Pig.collected ? 1 : 0);
                int finalScore = calculateScore(timeLeft, companionsCollected);
                scoreManager.saveScore(playerName, finalScore);
                exit();
            } else if (key == BACKSPACE && playerName.length() > 0) {
                playerName = playerName.substring(0, playerName.length() - 1);
            } else if ((key >= 'A' && key <= 'Z') || (key >= 'a' && key <= 'z')) {
                if (playerName.length() < 10) {
                    playerName += key;
                }
            }
        } 
        else {
            float nextX = villagerX;
            float nextY = villagerY;
            
            if (key == ' ' && Pig.collected && !isAttacking) {
                isAttacking = true;
                attackFrame = 0;
                
                int villagerCellX = (int)(villagerX / WALL_SIZE);
                int villagerCellY = (int)(villagerY / WALL_SIZE);
                
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int checkX = villagerCellX + dx;
                        int checkY = villagerCellY + dy;
                        
                        if (checkX >= 0 && checkX < maze[0].length && 
                            checkY >= 0 && checkY < maze.length) {
                            if (maze[checkY][checkX] == 6) {
                                maze[checkY][checkX] = 0;
                            }
                        }
                    }
                }
                return;
            }
            
            boolean moved = false;
            
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
            
            if (moved) {
                int cellX = (int)(nextX / WALL_SIZE);
                int cellY = (int)(nextY / WALL_SIZE);
                
                if (cellX >= 0 && cellX < maze[0].length && 
                    cellY >= 0 && cellY < maze.length) {
                    
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