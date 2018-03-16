/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ted125
 */
public class NaiveBayesClassifier {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            List<String[]> records = CSVLoader.Load();
            Dataset.DisplayTable(records);
        } catch (IOException ex) {
            System.out.println("Failed to load records.");
        }
    }
    
}
