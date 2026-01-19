/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import processing.core.PApplet;
import processing.core.PImage;
/**
 *
 * @author Jia Xin Li
 */
public abstract class GameEntity {
    protected float x;
    protected float y; 
    protected PImage sprite;
    public GameEntity(float x, float y, PImage sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
    }
    public abstract void draw(PApplet app);
}
