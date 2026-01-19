/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import processing.core.PApplet;
import processing.core.PImage;
/**
 * An abstract base class representing a game entity with position and sprite.
 * This class provides a structure for location and visual representation.
 * Usage of AI includes protected variables and abstract method
 * @author Jia Xin
 */
public abstract class GameEntity {
    // The x-coordinate of the game entity on the game screen
    protected float x;
    // The y-coordinate of the game entity on the game screen
    protected float y;
    // The visual representation (sprite) of the game entity
    protected PImage sprite;
    /**
     * Constructs a new GameEntity with specified position and sprite.
     * @param x The initial x-coordinate of the entity
     * @param y The initial y-coordinate of the entity
     * @param sprite The image representing the entity's visual appearance
     */
    public GameEntity(float x, float y, PImage sprite) {
        // Set the initial x-coordinate
        this.x = x; 
        // Set the initial y-coordinate
        this.y = y;
        // Set the entity's sprite
        this.sprite = sprite; 
    }
    /**
     * Abstract method to draw the game entity.
     * @param app The PApplet context used for drawing
     */
    public abstract void draw(PApplet app);
}