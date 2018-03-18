 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentclassifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author Ted125
 */
public class Driver {
    public static final String DATASET_PATH = "src/files/deceptive-opinion.csv";
    public static final int ROW_LIMIT = 800;
    public static final int COLUMN_REVIEWS = 4;
    public static final int COLUMN_DECEPTIVENESS = 0;
    
    public static void main(String[] args) throws IOException {
        String[][] trainingData = LoadTrainingData(ROW_LIMIT);
        String[][] reviews = LoadReviews(trainingData);
        String[] labels = LoadLabels(trainingData);
        HashSet<String> vocabulary = LoadVocabulary(trainingData);
        
        InputTestData(new NaiveBayes(reviews, labels, new ArrayList<String>(vocabulary)));
    }
    
    public static String[][] LoadTrainingData(int limit){
        try {
            List<String[]> dataset = CSVLoader.Load(DATASET_PATH);
            int rows = (limit <= dataset.size())? limit : dataset.size();
            String[][] trainingSet = new String[rows][];
            
            System.out.println("Loading training data (" + rows + " rows)...");
            
            for(int row = 0; row < rows; row++){
                trainingSet[row] = dataset.get(row);
            }
            
            return trainingSet;
        } catch (IOException ex) {
            System.out.println("Failed to load training data.");
        }
        
        return null;
    }
    
    private static String[][] LoadReviews(String[][] trainingData){
        System.out.println("Loading reviews...");
        
        String[][] reviews = new String[trainingData.length -1][];
        IntStream.range(0, trainingData.length -1).forEach(i -> 
                reviews[i] = trainingData[i + 1][COLUMN_REVIEWS].toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+")
        );
        
        return reviews;
    }
    
    private static String[] LoadLabels(String[][] trainingData){
        System.out.println("Loading labels...");
        
        String[] label = new String[trainingData.length - 1];
        IntStream.range(0, trainingData.length - 1).forEach(row -> 
                label[row] = trainingData[row + 1][COLUMN_DECEPTIVENESS]
        );
        
        return label;
    }
    
    private static HashSet<String> LoadVocabulary(String[][] trainingData){
        System.out.println("Loading vocabulary...");
        
        HashSet<String> vocabulary = new HashSet<String>();
        IntStream.range(0, trainingData.length).forEach(row -> 
                IntStream.range(0, trainingData[row].length).forEach(column -> 
                        vocabulary.add(trainingData[row][column])
                )
        );
        
        return vocabulary;
    }
    
    static void InputTestData(NaiveBayes nb) throws IOException{
        System.out.println();
        
        while(true){
            System.out.println("Enter test review (or exit):");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String[] values = bufferedReader.readLine().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            
            if(values[0].equals("exit")){
                System.exit(0);
            }else{
                System.out.println("\n" + nb.Classify(values) + "\n\n");
            }
        }
    }
}
