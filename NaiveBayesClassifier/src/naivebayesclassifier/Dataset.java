/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author Ted125
 */
public class Dataset {
    private String[][] data = null;
    private Feature classFeature = null;
    private HashMap<String, Double> priorProbs = new HashMap<String, Double>();
    
    public Dataset(String[][] data){
        this.data = data;
    }
    
    public static void DisplayTable(List<String[]> records){
        for(String[] record : records){
            for(int offset = 0; offset < record.length; offset++){
                System.out.printf("|    %s    |", record[offset]);
            }
            
            System.out.println();
        }
    }
    
    private Dataset CreateDataset(FeatureValue classFeatureValue){
        String[][] returnData = new String[classFeatureValue.getOccurrences() + 1][data[0].length];
        returnData[0] = data[0];
        
        int counter = 1;
        
        for(int row = 1; row < data.length; row++){
            if(data[row][data[0].length - 1].equals(classFeatureValue.getName())){
                returnData[counter++] = data[row];
            }
        }
        
        return new Dataset(returnData);
    }
    
    private Dataset CalcPriorProbs(){
        classFeature = new Feature(data, data[0].length - 1);
        classFeature.getFeatureValues().stream().forEach(featureValue -> 
                priorProbs.put(featureValue.getName(), (double)featureValue.getOccurrences() / (data.length - 1))
        );
        
        return this;
    }
    
    public HashMap<String, Double> CalcCondProbs(HashMap<String, String> instance){
        CalcPriorProbs();
        HashMap<String, Double> condProbs = new HashMap<String, Double>();
        
        classFeature.getFeatureValues().forEach(featureValue -> {
            HashMap<String, String> logMap = new HashMap<String, String>();
            logMap.put(featureValue.getName(), featureValue.getOccurrences() + "/" + (data.length - 1));
            Dataset newDataset = CreateDataset(classFeature.GetFeatureValue(featureValue.getName()));
            double condProb = CalcCondProb(newDataset, featureValue.getName(), instance, logMap);
            condProbs.put(featureValue.getName(), condProb);
            System.out.println(GetResultStr(newDataset, instance, logMap, condProb, featureValue.getName()));
        });
        
        return condProbs;
    }
    
    private double CalcCondProb(Dataset newDataset, String classFeatureValue, HashMap<String, String> instanceMap, HashMap<String, String> logMap){
        ArrayList<Feature> features = new ArrayList<Feature>();
        
        instanceMap.keySet().stream().forEach(featureName -> 
                features.add(new Feature(newDataset.data, GetColNum(featureName)).CalculateProbability(instanceMap.get(featureName), logMap))
        );
        
        double condProb = priorProbs.get(classFeatureValue);
        
        for(int i = 0; i < features.size(); i++){
            condProb *= features.get(i).getProbability();
        }
        
        return condProb;
    }
    
    private static String GetResultStr(Dataset dataset, HashMap<String, String> instanceMap, HashMap<String, String> logMap, double prob, String featureValue){
        StringBuffer resultSB = new StringBuffer(dataset + "\n");
        String instanceStr = GetInstanceStr(dataset, instanceMap);
        
        resultSB.append("P(" + featureValue + "|" + instanceStr + ") = P(" + featureValue + ")");
        IntStream.range(0, dataset.data[0].length - 1).forEach(i -> resultSB.append(" * P(" + instanceMap.get(dataset.data[0][i]) + "|" + featureValue + ")"));
        resultSB.append(") / P(" + instanceStr + ")\n");
        
        resultSB.append("P(" + featureValue + "|" + instanceStr + ") = ((" + logMap.get(featureValue) + ")");
        IntStream.range(0, dataset.data[0].length - 1).forEach(i -> resultSB.append(" * (" + logMap.get(dataset.data[0][i]) + ")"));
        resultSB.append(") / P(" + instanceStr + ")\n");
        
        resultSB.append("P(" + featureValue + "|" + instanceStr + ") = " + String.format("%.5f", prob) + " / P(" + instanceStr + ") \n");
        
        return resultSB.toString();
    }
    
    static String GetInstanceStr(Dataset dataset, HashMap<String, String> instanceMap){
        StringBuffer instanceSB = new StringBuffer("<");
        IntStream.range(0, dataset.data[0].length - 2).forEach(i -> instanceSB.append(instanceMap.get(dataset.data[0][i]) + ", "));
        
        return (instanceSB.append(instanceMap.get(dataset.data[0][dataset.data[0].length - 2]) + ">")).toString();
    }
    
    private int GetColNum(String colName){
        int returnValue = -1;
        
        for(int column = 0; column < data[0].length; column++){
            if(data[0][column] == colName){
                returnValue = column;
                break;
            }
        }
        
        return returnValue;
    }
    
    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public Feature getClassFeature() {
        return classFeature;
    }

    public void setClassFeature(Feature classFeature) {
        this.classFeature = classFeature;
    }

    public HashMap<String, Double> getPriorProbs() {
        return priorProbs;
    }

    public void setPriorProbs(HashMap<String, Double> priorProbs) {
        this.priorProbs = priorProbs;
    }
    
    public String toString(){
        StringBuffer stringBuffer = new StringBuffer();
        
        IntStream.range(0, data.length).forEach(row -> {
            IntStream.range(0, data[row].length).forEach(column ->{
                stringBuffer.append(data[row][column]);
                IntStream.range(0, 24 - data[row][column].length()).forEach(i -> stringBuffer.append(" "));
            });
            
            stringBuffer.append("\n");
            
            if(row == 0){
                IntStream.range(0, 108).forEach(i -> stringBuffer.append("-"));
                stringBuffer.append("\n");
            }
        });
        
        return stringBuffer.toString();
    }
}
