/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

/**
 *
 * @author Ted125
 */
public class FeatureValue {
    private String name;
    private int occurrences;
    
    public FeatureValue(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }
    
    @Override
    public int hashCode(){
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object object){
        boolean returnValue = true;
        
        if(object == null || getClass() != object.getClass()){
            returnValue = false;
        }
        
        if(name == null){
            if(((FeatureValue)object).name != null){
                returnValue = false;
            }
        }else if(!name.equals(((FeatureValue)object).name)){
            returnValue = false;
        }
        
        return returnValue;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
