/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Ted125
 */
public class Driver {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    private static final String DATASET_TRUTHFUL = "src/files/reviews_truthful.csv";
    private static final String DATASET_DECEPTIVE = "src/files/reviews_deceptive.csv";
    private static final int COLUMN_REVIEWS = 4;
    
    public static void main(String[] args) throws IOException {
        Map<String, String> trainingFiles = new HashMap<>();
        trainingFiles.put("Truthful", DATASET_TRUTHFUL);
        trainingFiles.put("Deceptive", DATASET_DECEPTIVE);
        
        Map<String, String[]> trainingExamples = new HashMap<>();
        
        for(Map.Entry<String, String> entry : trainingFiles.entrySet()) {
            trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
        }
        
        NaiveBayes nb = new NaiveBayes();
        nb.setChisquareCriticalValue(6.63);
        nb.train(trainingExamples);
        
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
        
        inputTestData(knowledgeBase);
    }
    
    public static String[] readLines(String path) throws IOException {
        List<String[]> records = CSVLoader.Load(path);
        String[] lines = new String[records.size()];
        
        for(int i = 0; i < lines.length; i++){
            lines[i] = records.get(i)[COLUMN_REVIEWS];
        }
        
        return lines;
    }
    
    private static void inputTestData(NaiveBayesKnowledgeBase knowledgeBase){
        NaiveBayes nb = new NaiveBayes(knowledgeBase);
        
        while(true){
            System.out.println("Enter test review (or exit):");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            
            if(input.equals("exit")){
                System.exit(0);
            }else{
                if(nb.predict(input).equalsIgnoreCase("Truthful")){
                    System.out.printf("The text is classified as " + ANSI_GREEN + "TRUTHFUL" + ANSI_RESET + ".\n\n");
                }else if(nb.predict(input).equalsIgnoreCase("Deceptive")){
                    System.out.printf("The text is classified as " + ANSI_RED + "DECEPTIVE" + ANSI_RESET + ".\n\n");
                }
            }
        }
    }
}
