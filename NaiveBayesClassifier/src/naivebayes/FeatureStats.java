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
}
