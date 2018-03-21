/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ted125
 */
public class FeatureExtraction {
    public FeatureStats extractFeatureStats(List<Document> dataset) {
        FeatureStats stats = new FeatureStats();
        
        Integer categoryCount;
        String category;
        Integer featureCategoryCount;
        String feature;
        Map<String, Integer> featureCategoryCounts;
        
        for(Document doc : dataset) {
            ++stats.numObservations;
            category = doc.category;
            
            categoryCount = stats.categoryCounts.get(category);
            
            if(categoryCount == null) {
                stats.categoryCounts.put(category, 1);
            }
            else {
                stats.categoryCounts.put(category, categoryCount + 1 );
            }
            
            for(Map.Entry<String, Integer> entry : doc.tokens.entrySet()) {
                feature = entry.getKey();
                
                featureCategoryCounts = stats.featureCategoryJointCount.get(feature);
                
                if(featureCategoryCounts == null) { 
                    stats.featureCategoryJointCount.put(feature, new HashMap<String, Integer>());
                }
                
                featureCategoryCount = stats.featureCategoryJointCount.get(feature).get(category);
                
                if(featureCategoryCount == null) {
                    featureCategoryCount = 0;
                }
                
                stats.featureCategoryJointCount.get(feature).put(category, ++featureCategoryCount);
            }
        }
        
        return stats;
    }
    
    public Map<String, Double> chisquare(FeatureStats stats, double criticalLevel) {
        Map<String, Double> selectedFeatures = new HashMap<>();
        
        String feature;
        String category;
        Map<String, Integer> categoryList;
        
        int N1dot, N0dot, N00, N01, N10, N11;
        double chisquareScore;
        Double previousScore;
        
        for(Map.Entry<String, Map<String, Integer>> entry1 : stats.featureCategoryJointCount.entrySet()) {
            feature = entry1.getKey();
            categoryList = entry1.getValue();
            
            N1dot = 0;
            for(Integer count : categoryList.values()) {
                N1dot += count;
            }
            
            N0dot = stats.numObservations - N1dot;
            
            for(Map.Entry<String, Integer> entry2 : categoryList.entrySet()) {
                category = entry2.getKey();
                N11 = entry2.getValue();
                N01 = stats.categoryCounts.get(category) - N11;
                
                N00 = N0dot - N01;
                N10 = N1dot - N11;
                
                chisquareScore = stats.numObservations * Math.pow(N11 * N00 - N10 * N01, 2) / ((N11 + N01) * (N11 + N10) * (N10 + N00) * (N01 + N00));
                
                if(chisquareScore >= criticalLevel) {
                    previousScore = selectedFeatures.get(feature);
                    
                    if(previousScore == null || chisquareScore > previousScore) {
                        selectedFeatures.put(feature, chisquareScore);
                    }
                }
            }
        }
        
        return selectedFeatures;
    }
}
