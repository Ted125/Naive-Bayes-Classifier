/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentclassifier;

import java.util.ArrayList;
import java.util.stream.IntStream;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Ted125
 */
public class NaiveBayes {
    private ArrayList<String> vocabulary = null;
    private double priorProbability;
    
    private RealMatrix positiveProbabilities;
    private RealMatrix negativeProbabilities;
    
    DisplayHelper displayHelper = null;
    
    private static String POSITIVE_LABEL = "truthful";
    private static String NEGATIVE_LABEL = "deceptive";
    
    public NaiveBayes(String[][] data, String[] label, ArrayList<String> vocabulary) {
        this.vocabulary = vocabulary;
        double[][] probabilityArray = new double[data.length][];
        
        System.out.println("Mapping data to vocabulary...");
        
        IntStream.range(0, data.length).forEach(i -> 
                probabilityArray[i] = MapDocumentToVocabulary(data[i])
        );
        
        displayHelper = new DisplayHelper(vocabulary);
        displayHelper.DisplayTrainingData(Driver.LoadTrainingData(Driver.ROW_LIMIT));
        
        CalculatePriorProbability(label).CalculateConditionalProbabilities(MatrixUtils.createRealMatrix(probabilityArray), label);
    }
    
    private double[] MapDocumentToVocabulary(String[] doc){        
        double[] mappedDoc = new double[vocabulary.size()];
        
        IntStream.range(0, vocabulary.size()).forEach(i -> 
                mappedDoc[i] = 0
        );
        
        for(int i = 0; i < doc.length; i++){
            for(int j = 0; j < vocabulary.size(); j++){
                if(doc[i].equalsIgnoreCase(vocabulary.get(j))){
                    mappedDoc[j] += 1;
                }
            }
        }
        
        return mappedDoc;
    }
    
    private NaiveBayes CalculatePriorProbability(String[] label){
        System.out.println("Calculating prior probabilities...");
        
        int sum = 0;
        
        for(int i = 0; i < label.length; i++){
            if(label[i].equals(POSITIVE_LABEL)){
                sum += 1;
            }
        }
        
        priorProbability = sum / (double) label.length;
        
        displayHelper.SetPriors(sum, label.length);
        
        return this;
    }
    
    private RealMatrix log(RealMatrix matrix){
        double[] returnData = new double[matrix.getData()[0].length];
        
        IntStream.range(0, returnData.length).forEach(j -> 
                returnData[j] = Math.log(matrix.getData()[0][j])
        );
        
        return MatrixUtils.createRowRealMatrix(returnData);
    }
    
    private NaiveBayes CalculateConditionalProbabilities(RealMatrix probabilities, String[] label){
        System.out.println("Calculating conditional probabilities (This may take a few minutes)...");
        
        RealMatrix negativeProbabilityNumber = MatrixUtils.createRowRealMatrix(new double[vocabulary.size()]);
        
        for(int i = 0; i < vocabulary.size(); i++){
            negativeProbabilityNumber.setEntry(0, i, 1.0);
        }
        
        RealMatrix positiveProbabilityNumber = MatrixUtils.createRowRealMatrix(new double[vocabulary.size()]);
        
        for(int i = 0; i < vocabulary.size(); i++){
            positiveProbabilityNumber.setEntry(0, i, 1.0);
        }
        
        double negativeProbabilityDenominator = vocabulary.size();
        double positiveProbabilityDenominator = vocabulary.size();
        
        for(int i = 0; i < label.length; i++){
            if(label[i].equals(POSITIVE_LABEL)){
                positiveProbabilityNumber = positiveProbabilityNumber.add(probabilities.getRowMatrix(i));
                
                for(int j = 0; j < probabilities.getData()[0].length; j++){
                    positiveProbabilityDenominator += probabilities.getEntry(i, j);
                }
            }else{
                negativeProbabilityNumber = negativeProbabilityNumber.add(probabilities.getRowMatrix(i));
                
                for(int j = 0; j < probabilities.getData()[0].length; j++){
                    negativeProbabilityDenominator += probabilities.getEntry(i, j);
                }
            }
        }
        
        positiveProbabilities = log(positiveProbabilityNumber.scalarMultiply(1 / positiveProbabilityDenominator));
        negativeProbabilities = log(negativeProbabilityNumber.scalarMultiply(1 / negativeProbabilityDenominator));
        
        displayHelper.DisplayCalculationOfConditionalProbabilities(negativeProbabilityNumber, positiveProbabilityNumber, negativeProbabilityDenominator, positiveProbabilityDenominator);
        
        return this;
    }
    
    public String Classify(String[] docArray){
        String sentiment = DisplayHelper.ANSI_RED + NEGATIVE_LABEL + DisplayHelper.ANSI_RESET;
        RealMatrix doc = MatrixUtils.createRowRealMatrix(MapDocumentToVocabulary(docArray));
        
        double positiveLogSums = Math.log(priorProbability) + doc.multiply(positiveProbabilities.transpose()).getData()[0][0];
        double negativeLogSums = Math.log(1 - priorProbability) + doc.multiply(negativeProbabilities.transpose()).getData()[0][0];
        
        if(positiveLogSums > negativeLogSums){
            sentiment = DisplayHelper.ANSI_GREEN + POSITIVE_LABEL + DisplayHelper.ANSI_RESET;
        }
        
        displayHelper.DisplayClassification(doc, positiveLogSums, negativeLogSums);
        
        return "The text is classified as expressing a " + sentiment + " sentiment.";
    }
}
