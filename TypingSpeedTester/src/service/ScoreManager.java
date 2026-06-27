package service;

import java.io.FileWriter;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.FileReader;
// import java.util.ArrayList;

public class ScoreManager {

    public static void saveScore(
            String difficulty,
            int duration,
            int wpm,
            double accuracy,
            int errors) {

        try {

            FileWriter writer = new FileWriter(
                    "../data/history.csv",
                    true);

            writer.write(
                    LocalDate.now()
                            + ","
                            + difficulty
                            + ","
                            + duration
                            + ","
                            + wpm
                            + ","
                            + String.format("%.2f", accuracy)
                            + ","
                            + errors
                            + "\n");

            writer.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }
    }

    public static int getBestWPM(
            String difficulty,
            int duration) {

        int best = 0;

        try {

            BufferedReader br = new BufferedReader(
                    new FileReader(
                            "../data/history.csv"));

            br.readLine(); // skip header

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                String diff = data[1];

                int dur = Integer.parseInt(data[2]);

                int wpm = Integer.parseInt(data[3]);

                if (diff.equals(difficulty)
                        &&
                        dur == duration) {

                    if (wpm > best) {
                        best = wpm;
                    }
                }
            }

            br.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return best;
    }
}