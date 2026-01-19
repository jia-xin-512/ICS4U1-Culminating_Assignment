/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import processing.core.PApplet;
import java.io.*;
import java.util.*;
/**
 * Manages high scores for the game, handling loading, saving, and retrieving scores.
 * Utilizes Processing file handling capabilities and provides score persistence.
 * Usage of AI includes ArrayList<Score>, a bit of loadScores, and a bit of saveScore, specifically, the Buffer Reader portion
 * @author Jia Xin Li
 */
public class ScoreManager {
    // The filename where high scores are stored
    private String filename;
    // List to store and manage high scores
    private ArrayList<Score> highScores;
    // Reference to the parent PApplet for file path and error handling
    private PApplet parent;
    /**
     * Constructs a ScoreManager and initializes score loading.
     * 
     * @param parent The parent PApplet for accessing sketch-related utilities
     * @param filename The name of the file to store high scores
     */
    public ScoreManager(PApplet parent, String filename) {
        // Store reference to parent PApplet
        this.parent = parent;  
        // Store filename for score persistence
        this.filename = filename;  
        // Initialize high scores list
        this.highScores = new ArrayList<>();  
        // Load existing scores on initialization
        loadScores();  
    }
    /**
     * Loads high scores from the file, creating the file if it doesn't exist.
     * Reads scores line by line, parsing name and score values.
     */
    public void loadScores() {
        try {
            // Create file if it doesn't exist
            File file = new File(parent.sketchPath(filename));
            if (!file.exists()) {
                // Create new file if not present
                file.createNewFile();  
                return;
            }
            // Read scores from file
            BufferedReader reader = parent.createReader(filename);
            String line;
            while ((line = reader.readLine()) != null) {
                // Split line into name and score
                String[] parts = line.split(",");  
                if (parts.length == 2) {
                    // Add score to high scores list
                    highScores.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }
            }
            // Close file reader
            reader.close();  
        } catch (IOException e) {
            // Print error message if loading fails
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }
    /**
     * Saves a new score to the high scores list and persists to file.
     * Sorts scores in descending order before saving.
     * 
     * @param name The name of the player
     * @param score The score achieved by the player
     */
    public void saveScore(String name, int score) {
        // Add new score
        highScores.add(new Score(name, score));  
        // Sort scores in descending order
        Collections.sort(highScores);  
        try {
            // Write sorted scores to file
            PrintWriter writer = parent.createWriter(filename);
            for (Score s : highScores) {
                // Write each score
                writer.println(s.name + "," + s.score);  
            }
            // Ensure all data is written
            writer.flush();  
            // Close file writer
            writer.close();  
        } catch (Exception e) {
            // Print error message if saving fails
            System.out.println("Error saving score: " + e.getMessage());
        }
    }
    /**
     * Retrieves the top scores from the high scores list.
     * 
     * @param count The number of top scores to retrieve
     * @return An ArrayList of the top scores, limited by the specified count
     */
    public ArrayList<Score> getTopScores(int count) {
        ArrayList<Score> top = new ArrayList<>();
        // Add top scores, limited by count or total available scores
        for (int i = 0; i < Math.min(count, highScores.size()); i++) {
            top.add(highScores.get(i));
        }
        return top;
    }
}