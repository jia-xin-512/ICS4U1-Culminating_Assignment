/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package DragonBoatLegend;
import processing.core.PApplet;
import java.io.*;
import java.util.*;

/**
 * Represents a player's score in the game, implementing Comparable for sorting.
 * This class stores a player's name and their score, allowing easy comparison and ranking.
 * @author Jia Xin Li
 */

public class Score implements Comparable<Score> {
    // The name of the player who achieved the score
    String name;
    
    // The numeric value of the player's score
    int score;
    
    /**
     * Constructs a new Score object with a player's name and score.
     * @param name The name of the player
     * @param score The numeric score achieved by the player
     */
    public Score(String name, int score) {
        // Initialize the player's name
        this.name = name; 
        // Initialize the player's score
        this.score = score;  
    }
    
    /**
     * Compares this score with another score for sorting purposes.
     * Sorts in descending order, so higher scores come first.
     * 
     * @param other The other Score object to compare with
     * @return A negative integer, zero, or a positive integer if this score is greater than, equal to, or less than the other score
     */
    @Override
    public int compareTo(Score other) {
        // Sort scores in descending order
        return other.score - this.score;  
    }
}