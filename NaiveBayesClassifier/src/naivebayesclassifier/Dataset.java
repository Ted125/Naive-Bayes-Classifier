/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import java.util.List;

/**
 *
 * @author Ted125
 */
public class Dataset {
    public static void DisplayTable(List<String[]> records){
        for(String[] record : records){
            for(int offset = 0; offset < record.length; offset++){
                System.out.printf("|    %s    |", record[offset]);
            }
        }
    }
}
