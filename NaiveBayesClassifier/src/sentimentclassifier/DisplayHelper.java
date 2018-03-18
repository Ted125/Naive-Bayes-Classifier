/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentclassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Ted125
 */
public class DisplayHelper {
    RealMatrix negativeProbabilityNumber = null;
    RealMatrix positiveProbabilityNumber = null;
    double negativeProbabilityDenominator;
    double positiveProbabilityDenominator;
    double priorNumber;
    double priorDenominator;
    ArrayList<String> vocabulary = null;
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    public DisplayHelper(ArrayList<String> vocabulary){
        this.vocabulary = vocabulary;
    }
    
    DisplayHelper SetPriors(double priorNumber, double priorDenominator){
        this.priorNumber = priorNumber;
        this.priorDenominator = priorDenominator;
        
        return this;
    }
    
    DisplayHelper DisplayTrainingData(String[][] trainingData){
        int size = trainingData[0][0].toString().length();
        
        for(int i = 1; i < trainingData.length; i++){
            if(size < trainingData[i][0].toString().length()){
                size = trainingData[i][0].toString().length();
            }
        }
        
        System.out.println("Sentiment Classification of Reviews\n");
        
        System.out.print(trainingData[0][0]);
        IntStream.range(0, size - trainingData[0][0].length()).forEach(x -> 
                System.out.print(" ")
        );
        
        System.out.println(" | " + trainingData[0][1]);
        IntStream.range(0, size + 20).forEach(x -> 
                System.out.print("-")
        );
        
        System.out.println();
        StringBuffer temp = null;
        
        for(int i = 1; i < trainingData.length; i++){
            temp = new StringBuffer(trainingData[i][Driver.COLUMN_DECEPTIVENESS]);
            temp.setLength(size);
            System.out.println(temp + " | " + trainingData[i][Driver.COLUMN_REVIEWS]);
        }
        
        return this;
    }
    
    DisplayHelper DisplayCalculationOfConditionalProbabilities(RealMatrix negativeProbabilityNumber, RealMatrix positiveProbabilityNumber, double negativeProbabilityDenominator, double positiveProbabilityDenominator){
        int seperatorSize = Arrays.toString(vocabulary.toArray()).length();
        StringBuffer temp = null;
        
        System.out.println("\n        | ");
        
        for(int i = 0; i < vocabulary.size(); i++){
            temp = new StringBuffer(vocabulary.get(i));
            
            if(vocabulary.get(i).length() < 4){
                temp.setLength(4);
            }
            
            System.out.print(temp);
            
            if(i < vocabulary.size() - 1){
                System.out.print("|");
            }
        }
        
        System.out.println();
        
        IntStream.range(0, seperatorSize).forEach(x -> 
                System.out.print("-")
        );
        
        System.out.print("\n P(review | truthful) | ");
        
        for(int i = 0; i < vocabulary.size(); i++){
            temp = new StringBuffer((int)positiveProbabilityNumber.getData()[0][i] + "/" + (int)positiveProbabilityDenominator);
            
            if(vocabulary.get(i).length() >= 4){
                temp.setLength(vocabulary.get(i).length());
            }else{
                temp.setLength(4);
            }
            
            System.out.print(temp);
            
            if(i < vocabulary.size() - 1){
                System.out.print("|");
            }
        }
        
        System.out.println();
        
        IntStream.range(0, seperatorSize).forEach(x -> 
                System.out.print("-")
        );
        
        System.out.print("\n P(review | deceptive) | ");
        
        for(int i = 0; i < vocabulary.size(); i++){
            temp = new StringBuffer((int)negativeProbabilityNumber.getData()[0][i] + "/" + (int)negativeProbabilityDenominator);
            
            if(vocabulary.get(i).length() >= 4){
                temp.setLength(vocabulary.get(i).length());
            }else{
                temp.setLength(4);
            }
            
            System.out.print(temp);
            
            if(i < vocabulary.size() - 1){
                System.out.print("|");
            }
        }
        
        System.out.println();
        
        this.negativeProbabilityNumber = negativeProbabilityNumber;
        this.positiveProbabilityNumber = positiveProbabilityNumber;
        this.negativeProbabilityDenominator = negativeProbabilityDenominator;
        this.positiveProbabilityDenominator = positiveProbabilityDenominator;
        
        return this;
    }
    
    DisplayHelper DisplayClassification(RealMatrix sentiment, double positiveProbability, double negativeProbability){
        System.out.print("logprior + loglikelihood of truthful sentiment = ln(" + (int)priorNumber + "/" + (int)priorDenominator + ")");
        
        for(int i = 0; i < sentiment.getData()[0].length; i++){
            if(sentiment.getData()[0][i] == 1){
                System.out.print(" + ln(" + (int)positiveProbabilityNumber.getData()[0][i] + "/" + (int)positiveProbabilityDenominator + ")");
            }
        }
        
        System.out.println(" = " + positiveProbability);
        
        System.out.print("logprior + loglikelihood of deceptive sentiment = ln(" + (int)(priorDenominator - priorNumber) + "/" + (int)priorDenominator + ")");
        
        for(int i = 0; i < sentiment.getData()[0].length; i++){
            if(sentiment.getData()[0][i] == 1){
                System.out.print(" + ln(" + (int)negativeProbabilityNumber.getData()[0][i] + "/" + (int)negativeProbabilityDenominator + ")");
            }
        }
        
        System.out.println(" = " + negativeProbability);
        
        System.out.println("prior + likelihood of truthful sentiment = " + Math.exp(positiveProbability));
        System.out.println("prior + likelihood of deceptive sentiment = " + Math.exp(negativeProbability));
        
        System.out.println("probability of truthful sentiment = " + (Math.exp(positiveProbability)) / (Math.exp(positiveProbability) + Math.exp(negativeProbability)));
        System.out.println("probability of deceptive sentiment = " + (Math.exp(negativeProbability)) / (Math.exp(positiveProbability) + Math.exp(negativeProbability)));
        
        return this;
    }
}
