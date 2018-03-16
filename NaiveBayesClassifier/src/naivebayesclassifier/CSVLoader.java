/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naivebayesclassifier;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Ted125
 */
public class CSVLoader {
    private static final String FULL_DATASET_FILE_PATH = "src/files/deceptive-opinion.csv"; 
    
    public static List<String[]> Load() throws IOException{
        try (
            Reader reader = Files.newBufferedReader(Paths.get(FULL_DATASET_FILE_PATH));
            CSVReader csvReader = new CSVReader(reader);
        ) {
            return csvReader.readAll();
        }
    }
}
