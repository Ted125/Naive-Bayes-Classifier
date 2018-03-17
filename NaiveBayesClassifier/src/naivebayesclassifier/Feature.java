/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.IntStream;

/**
 *
 * @author Ted125
 */
public class Feature {
    private String name = null;
    private String[][] data = null;
    private HashSet<FeatureValue> featureValues = new HashSet<FeatureValue>();
    private double probability;
    
    public Feature(String[][] data, int column){
        this.name = data[0][column];
        this.data = data;
        
        IntStream.range(1, data.length).forEach(row -> featureValues.add(new FeatureValue(data[row][column])));
        
        featureValues.stream().forEach(featureValue -> {
            int counter = 0;
            
            for(int row = 1; row < data.length; row++){
                if(featureValue.getName() == data[row][column]){
                    featureValue.setOccurrences(++counter);
                }
            }
        });
    }
    
    public FeatureValue GetFeatureValue(String featureValueName){
        FeatureValue returnValue = null;
        Iterator<FeatureValue> iterator = featureValues.iterator();
        
        while(iterator.hasNext()){
            FeatureValue featureValue = iterator.next();
            
            if(featureValue.getName().equals(featureValueName)){
                returnValue = featureValue;
                break;
            }
        }
        
        return returnValue;
    }
    
    public Feature CalculateProbability(String featureValueName, HashMap<String, String> logMap){
        if(GetFeatureValue(featureValueName) != null){
            probability = (((double)GetFeatureValue(featureValueName).getOccurrences()) / (data.length - 1));
            logMap.put(this.name, GetFeatureValue(featureValueName).getOccurrences() + "/" + (data.length - 1));
        }else{
            probability = 0;
            logMap.put(this.name, "0/" + (data.length - 1));
        }
        
        return this;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public HashSet<FeatureValue> getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(HashSet<FeatureValue> featureValues) {
        this.featureValues = featureValues;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
