package service;

import java.io.BufferedReader;
import java.io.FileReader;

public class HistoryManager {

    public static String getHistory() {

        StringBuilder history =
                new StringBuilder();

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(
                                    "../data/history.csv"));

            String line;

            while((line = br.readLine()) != null) {

                history.append(line)
                       .append("\n");
            }

            br.close();

        }

        catch(Exception e) {

            e.printStackTrace();

        }

        return history.toString();
    }
}