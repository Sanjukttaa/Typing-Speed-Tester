package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

public class PassageManager {

    public static String getRandomPassage(String difficulty) {
        System.out.println("METHOD CALLED");

        String filePath = "";

        switch (difficulty) {

            case "Easy":
                filePath = "../data/easy.txt";
                break;

            case "Medium":
                filePath = "../data/medium.txt";
                break;

            case "Hard":
                filePath = "../data/hard.txt";
                break;

            case "Coding":
                filePath = "../data/coding.txt";
                break;

            default:
                return "Invalid difficulty selected.";
        }
        System.out.println("FILE PATH = " + filePath);

        ArrayList<String> passages = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(
                    new FileReader(filePath));

            String line;

            StringBuilder paragraph = new StringBuilder();

            while ((line = br.readLine()) != null) {

                line = line.trim();

                // End of one paragraph
                if (line.equals("---")) {

                    if (paragraph.length() > 0) {

                        passages.add(
                                paragraph.toString().trim());

                        paragraph.setLength(0);
                    }

                }

                else {

                    paragraph.append(line)
                            .append("\n");
                }

            }

            // Add the last paragraph
            if (paragraph.length() > 0) {

                passages.add(
                        paragraph.toString().trim());

            }

            br.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.toString());

            e.printStackTrace();

            return "Error loading passage.";
        }

        if (passages.isEmpty()) {
            return "No passages found.";
        }

        Random random = new Random();

        return passages.get(
                random.nextInt(passages.size()));
    }
}