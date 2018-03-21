/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ted125
 */
public class NaiveBayes {
    private double chisquareCriticalValue = 10.83;
    
    private NaiveBayesKnowledgeBase knowledgeBase;
    
    public NaiveBayes(NaiveBayesKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
    
    public NaiveBayes() {
        this(null);
    }
    
    public NaiveBayesKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public double getChisquareCriticalValue() {
        return chisquareCriticalValue;
    }

    public void setChisquareCriticalValue(double chisquareCriticalValue) {
        this.chisquareCriticalValue = chisquareCriticalValue;
    }

    private List<Document> preprocessDataset(Map<String, String[]> trainingDataset) {
        List<Document> dataset = new ArrayList<>();
                
        String category;
        String[] examples;
        
        Document doc;
        
        Iterator<Map.Entry<String, String[]>> it = trainingDataset.entrySet().iterator();
        
        while(it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            category = entry.getKey();
            examples = entry.getValue();
            
            for(int i=0; i<examples.length; ++i) {
                doc = TextTokenizer.tokenize(examples[i]);
                doc.category = category;
                dataset.add(doc);
            }
        }
        
        return dataset;
    }

    private FeatureStats selectFeatures(List<Document> dataset) {        
        FeatureExtraction featureExtractor = new FeatureExtraction();
        
        FeatureStats stats = featureExtractor.extractFeatureStats(dataset);
        
        Map<String, Double> selectedFeatures = featureExtractor.chisquare(stats, chisquareCriticalValue);
        
        Iterator<Map.Entry<String, Map<String, Integer>>> it = stats.featureCategoryJointCount.entrySet().iterator();
        
        while(it.hasNext()) {
            String feature = it.next().getKey();
        
            if(selectedFeatures.containsKey(feature)==false) {
                it.remove();
            }
        }
        
        return stats;
    }

    public void train(Map<String, String[]> trainingDataset, Map<String, Double> categoryPriors) throws IllegalArgumentException {
        List<Document> dataset = preprocessDataset(trainingDataset);
        
        FeatureStats featureStats =  selectFeatures(dataset);
        
        knowledgeBase = new NaiveBayesKnowledgeBase();
        knowledgeBase.numObservations = featureStats.numObservations;
        knowledgeBase.numFeatures = featureStats.featureCategoryJointCount.size();
        
        
        if(categoryPriors==null) { 
            //if not estimate the priors from the sample
            knowledgeBase.numCategories = featureStats.categoryCounts.size();
            knowledgeBase.logPriors = new HashMap<>();
            
            String category;
            int count;
            double logvalue;
            
            for(Map.Entry<String, Integer> entry : featureStats.categoryCounts.entrySet()) {
                category = entry.getKey();
                count = entry.getValue() ;
                logvalue = Math.log((double)count / knowledgeBase.numObservations);
                
                knowledgeBase.logPriors.put(category, logvalue); 
            }
        }else {
            knowledgeBase.numCategories = categoryPriors.size();
            
            //make sure that the given priors are valid
            if(knowledgeBase.numCategories != featureStats.categoryCounts.size()) {
                throw new IllegalArgumentException("Invalid priors Array: Make sure you pass a prior probability for every supported category.");
            }
            
            String category;
            Double priorProbability;
            
            for(Map.Entry<String, Double> entry : categoryPriors.entrySet()) {
                category = entry.getKey();
                priorProbability = entry.getValue();
                
                if(priorProbability == null) {
                    throw new IllegalArgumentException("Invalid priors Array: Make sure you pass a prior probability for every supported category.");
                }
                else if(priorProbability < 0 || priorProbability>1) {
                    throw new IllegalArgumentException("Invalid priors Array: Prior probabilities should be between 0 and 1.");
                }
                
                knowledgeBase.logPriors.put(category, Math.log(priorProbability));
            }
        }
        
        Map<String, Double> featureOccurrencesInCategory = new HashMap<>();
        
        Integer occurrences;
        Double featureOccSum;
        
        for(String category : knowledgeBase.logPriors.keySet()) {
            featureOccSum = 0.0;
            
            for(Map<String, Integer> categoryListOccurrences : featureStats.featureCategoryJointCount.values()) {
                occurrences=categoryListOccurrences.get(category);
                
                if(occurrences!=null) {
                    featureOccSum+=occurrences;
                }
            }
            
            featureOccurrencesInCategory.put(category, featureOccSum);
        }
        
        String feature;
        Integer count;
        Map<String, Integer> featureCategoryCounts;
        double logLikelihood;
        
        for(String category : knowledgeBase.logPriors.keySet()) {
            for(Map.Entry<String, Map<String, Integer>> entry : featureStats.featureCategoryJointCount.entrySet()) {
                feature = entry.getKey();
                featureCategoryCounts = entry.getValue();
                
                count = featureCategoryCounts.get(category);
                
                if(count==null) {
                    count = 0;
                }
                
                //logLikelihood = Math.log((count+1.0) / (featureOccurrencesInCategory.get(category) + knowledgeBase.numFeatures));
                logLikelihood = (count+1.0) / (featureOccurrencesInCategory.get(category) + knowledgeBase.numFeatures);
                
                if(knowledgeBase.logLikelihoods.containsKey(feature) == false) {
                    knowledgeBase.logLikelihoods.put(feature, new HashMap<String, Double>());
                }
                
                knowledgeBase.logLikelihoods.get(feature).put(category, logLikelihood);
            }
        }
        
        featureOccurrencesInCategory=null;
        
        featureStats.displayFrequencyMatrix();
    }

    public void train(Map<String, String[]> trainingDataset) {
        train(trainingDataset, null);
    }

    public String predict(String text) throws IllegalArgumentException {
        if(knowledgeBase == null) {
            throw new IllegalArgumentException("Knowledge Bases missing: Make sure you train a classifier first before you use it.");
        }
        
        Document doc = TextTokenizer.tokenize(text);
        
        String category;
        String feature = null;
        Integer occurrences;
        Double logprob;
        
        String maxScoreCategory = null;
        Double maxScore = Double.NEGATIVE_INFINITY;
        
        for(Map.Entry<String, Double> entry1 : knowledgeBase.logPriors.entrySet()) {
            category = entry1.getKey();
            logprob = entry1.getValue();
                        
            for(Map.Entry<String, Integer> entry2 : doc.tokens.entrySet()) {
                feature = entry2.getKey();
                
                if(!knowledgeBase.logLikelihoods.containsKey(feature)) {
                    continue;
                }
                
                occurrences = entry2.getValue();
                
                logprob += occurrences * (knowledgeBase.logLikelihoods.get(feature).get(category));                
            }
            
            if(logprob > maxScore) {
                maxScore = logprob;
                maxScoreCategory = category; 
            }
        }
        
        System.out.println("Prob for" + text + " : " + maxScore);
        return maxScoreCategory;
    }
}
