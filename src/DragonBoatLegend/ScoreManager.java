/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DragonBoatLegend;
import processing.core.PApplet;
import java.io.*;
import java.util.*;

/**
 *
 * @author Jia Xin Li
 */

public class ScoreManager {
    private String filename;
    private ArrayList<Score> highScores;
    private PApplet parent;
    public ScoreManager(PApplet parent, String filename) {
        this.parent = parent;
        this.filename = filename;
        this.highScores = new ArrayList<>();
        loadScores();
    }
    public void loadScores() {
        try {
            File file = new File(parent.sketchPath(filename));
            if (!file.exists()) {
                file.createNewFile();
                return;
            }
            BufferedReader reader = parent.createReader(filename);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    highScores.add(new Score(parts[0], Integer.parseInt(parts[1])));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading scores: " + e.getMessage());
        }
    }
    public void saveScore(String name, int score) {
        highScores.add(new Score(name, score));
        Collections.sort(highScores); 
        
        try {
            PrintWriter writer = parent.createWriter(filename);
            for (Score s : highScores) {
                writer.println(s.name + "," + s.score);
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }
    public ArrayList<Score> getTopScores(int count) {
        ArrayList<Score> top = new ArrayList<>();
        for (int i = 0; i < Math.min(count, highScores.size()); i++) {
            top.add(highScores.get(i));
        }
        return top;
    }
}