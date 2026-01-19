/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dragonboatlegend;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
/**
 *
 * @author Jia Xin Li
 */
public class Sketch extends PApplet {
    private PFont normalSize, titleSize;
    private PImage background1, background2, background3;
    private static int scene = 0;

    public void settings() {
        size(600, 500);
    }
    public void setup(){
        background(255);
        scene = 0;
        normalSize = createFont("Font/Santiago-Sunrise.otf", 17);
        titleSize = createFont("Font/Santiago-Sunrise.otf", 50);
        background1 = loadImage("Background/background1.png");
        background2 = loadImage("Background/background2.png");
        background3 = loadImage("Background/background3.png");
    }
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
}