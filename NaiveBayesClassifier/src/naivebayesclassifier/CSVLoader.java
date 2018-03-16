/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Ted125
 */
public class CSVLoader {
    public static String[][] Load(){
        String filePath = "C:\\Users\\Ted125\\Documents\\My Codes\\Java\\NaiveBayesClassifier\\src\\files\\deceptive-opinion.csv"; 
        String cvsSplitBy = ",";
        String[][] table = new String[10][5];
        
        return ReadFile(filePath, cvsSplitBy, table);
    }
    
    public static String[][] ReadFile( String file_path, String splitBy, String[][] table){
        String line = "";
        int ndx = 0; 
        
        try(BufferedReader br = new BufferedReader(new FileReader(file_path))){
            while((line = br.readLine()) != null){
                // use comma as separator
                String[] xy = line.split(splitBy);
                table[ndx][0] = xy[0]; 
                table[ndx][1] = xy[1]; 
                ndx++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            return table;
        }
    }
}
