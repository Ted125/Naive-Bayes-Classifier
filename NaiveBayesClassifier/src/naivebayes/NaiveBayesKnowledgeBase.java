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
public class NaiveBayesKnowledgeBase {
    public int numObservations = 0;
    public int numCategories = 0;
    public int numFeatures = 0;
    public Map<String, Double> logPriors = new HashMap<>();
    public Map<String, Map<String, Double>> logLikelihoods = new HashMap<>();
}
