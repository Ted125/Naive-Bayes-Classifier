/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Ted125
 */
public class FeatureStats {
    public int numObservations;
    public Map<String, Map<String, Integer>> featureCategoryJointCount;
    public Map<String, Integer> categoryCounts;
    
    public FeatureStats(){
        numObservations = 0;
        featureCategoryJointCount = new HashMap<>();
        categoryCounts = new HashMap<>();
    }
    
    public void displayFrequencyMatrix(){
        System.out.println("Word : " + Driver.ANSI_GREEN + "TRUTHFUL" + Driver.ANSI_RESET + " | " + Driver.ANSI_RED + "DECEPTIVE" + Driver.ANSI_RESET);
        
        for(Map.Entry<String, Map<String, Integer>> entry : featureCategoryJointCount.entrySet()){
            String word = entry.getKey();
            System.out.print(word + " : ");
            
            for(Map.Entry<String, Integer> categoryEntry : entry.getValue().entrySet()){
                String category = categoryEntry.getKey();
                int frequency = categoryEntry.getValue();
                
                if(category.equalsIgnoreCase("truthful")){
                    System.out.print(Driver.ANSI_GREEN + frequency + Driver.ANSI_RESET + " | ");
                }else if(category.equalsIgnoreCase("deceptive")){
                    System.out.print(Driver.ANSI_RED + frequency + Driver.ANSI_RESET + "\n");
                }
            }
        }
    }
}
