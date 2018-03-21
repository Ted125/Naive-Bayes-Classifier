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
    private static final String DATASET_TEST = "src/files/reviews_trainsets.csv";
    private static final int COLUMN_REVIEWS = 4;
    private static final int COLUMN_DECEPTION = 0;
    
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
    
    private static void inputTestData(NaiveBayesKnowledgeBase knowledgeBase) throws IOException{
       NaiveBayes nb = new NaiveBayes(knowledgeBase);

       int predicted_Truthful_No = 0; 
       int predicted_Truthful_Yes = 0;
       int predicted_Deceptive_No = 0; 
       int predicted_Deceptive_Yes = 0; 
       int i; 
       
       List<String[]> records = CSVLoader.Load(DATASET_TEST);
       String[][] testLines = new String[records.size()][2];
        
       for(i = 0; i < testLines.length; i++){
            testLines[i][0] = records.get(i)[COLUMN_DECEPTION];
            testLines[i][1] = records.get(i)[COLUMN_REVIEWS];
       }
        
        ///testing
        for(i = 0; i < testLines.length; i++){
            String input = testLines[i][1];
            String prediction =  nb.predict(testLines[i][1]);
            
            if(testLines[i][0].equalsIgnoreCase(prediction) && testLines[i][0].equalsIgnoreCase("Truthful")){
               predicted_Truthful_Yes++; 
            }else if (!testLines[i][0].equalsIgnoreCase(prediction) && testLines[i][0].equalsIgnoreCase("Truthful")){
                predicted_Truthful_No++;
            }else if(testLines[i][0].equalsIgnoreCase(prediction) && testLines[i][0].equalsIgnoreCase("Deceptive")){
               predicted_Deceptive_Yes++; 
            }else if (!testLines[i][0].equalsIgnoreCase(prediction) && testLines[i][0].equalsIgnoreCase("Deceptive")){
                predicted_Deceptive_No++;
            }
       }
              
        System.out.println ("****************************************************************************************************************************");
        System.out.println ("\t\t\tCONFUSION MATRIX FOR " + testLines.length + " RECORDS");
        System.out.println ("****************************************************************************************************************************");
        System.out.println ("\t" + ANSI_GREEN + "TRUTHFUL" + ANSI_RESET + " || " + ANSI_RED + "Deceptive" + ANSI_RESET);
        System.out.println (ANSI_GREEN + "TRUTHFUL: " + ANSI_RESET + predicted_Truthful_Yes + " || " + predicted_Truthful_No);
        System.out.println (ANSI_RED + "Deceptive: " + ANSI_RESET + predicted_Deceptive_Yes + " || " + predicted_Deceptive_No);
        
        System.out.println ("****************************************************************************************************************************");
       
        //calculations 
        double accuracy = (predicted_Truthful_Yes + predicted_Deceptive_Yes); 
        double miscalculationRate =(predicted_Truthful_No + predicted_Deceptive_No) ;
        double truthful = (predicted_Truthful_Yes + predicted_Truthful_No); 
        double deceptive = (predicted_Deceptive_Yes + predicted_Deceptive_No);
        double trueTruthfulPositive = predicted_Truthful_Yes/truthful; 
        double falseTruthfulPositive = predicted_Truthful_No/truthful;
        double trueDeceptivePositive = predicted_Deceptive_Yes/ deceptive; 
        double falseDeceptivePositive = predicted_Deceptive_No/ deceptive;

        System.out.println ("Accuracy: " + accuracy / testLines.length);
        System.out.println ("Miscalculation Rate: " + miscalculationRate / testLines.length);
        System.out.println (ANSI_GREEN + "TRUTHFUL" + ANSI_RESET + " True Positive:" + trueTruthfulPositive);
        System.out.println (ANSI_GREEN + "TRUTHFUL" + ANSI_RESET + " False Positive: " + falseTruthfulPositive);
        System.out.println (ANSI_RED + "Deceptive" + ANSI_RESET + " True Positive: " + trueDeceptivePositive);
        System.out.println (ANSI_RED + "Deceptive" + ANSI_RESET + " False Positive: " + falseDeceptivePositive);
        
    }
}
