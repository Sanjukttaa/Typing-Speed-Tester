package gui;

import service.PassageManager;
import service.ScoreManager;
import service.HistoryManager;
import javax.swing.border.TitledBorder;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

        private JComboBox<String> difficultyBox;
        private JComboBox<String> durationBox;

        private JTextArea passageArea;
        private JTextArea typingArea;

        private JLabel timerLabel;
        private JLabel wpmLabel;
        private JLabel accuracyLabel;
        private JLabel errorLabel;
        private JLabel bestLabel;

        private JButton startButton;
        private JButton resetButton;
        private JButton historyButton;

        private Timer timer;
        private int timeLeft;

        public MainFrame() {

                setTitle("Typing Speed Tester - Java Swing");

                setSize(1000, 750);

                // Minimum size so the layout doesn't break
                setMinimumSize(new Dimension(900, 650));

                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                setLocationRelativeTo(null);

                // Allow resizing
                setResizable(true);

                createUI();

                setVisible(true);
        }

        private void startTimer(int seconds) {

                timeLeft = seconds;

                timerLabel.setText("Time Left: " + timeLeft);

                timer = new Timer(1000, e -> {

                        timeLeft--;

                        timerLabel.setText("Time Left: " + timeLeft);

                        if (timeLeft <= 0) {

                                finishTest(false);

                        }

                });

                timer.start();
        }

        private void checkCompletion() {

                String original = passageArea.getText().trim();
                String typed = typingArea.getText().trim();

                String duration = (String) durationBox.getSelectedItem();

                int durationSeconds = Integer.parseInt(duration);

                int elapsedSeconds = durationSeconds - timeLeft;

                if (elapsedSeconds <= 0) {
                        elapsedSeconds = 1;
                }

                int liveWPM = calculateWPM(
                                typed,
                                elapsedSeconds);

                double liveAccuracy = calculateAccuracy(
                                original,
                                typed);

                int liveErrors = calculateErrors(
                                original,
                                typed);

                wpmLabel.setText("WPM: " + liveWPM);

                accuracyLabel.setText(
                                String.format("Accuracy: %.2f%%",
                                                liveAccuracy));

                errorLabel.setText(
                                "Errors: " + liveErrors);

                // Check if the typed text matches the beginning of the original
                if (!original.startsWith(typed)) {
                        typingArea.setBackground(new Color(255, 220, 220)); // light red
                } else {
                        typingArea.setBackground(Color.WHITE);
                }

                // Finished typing

                System.out.println("Original = [" + original + "]");
                System.out.println("Typed    = [" + typed + "]");

                if (typed.equals(original)
                                && timer != null
                                && timer.isRunning()) {

                        System.out.println("MATCHED!");

                        finishTest(true);
                }
        }

        private void finishTest(boolean completedEarly) {

                if (timer != null) {
                        timer.stop();
                }

                typingArea.setEditable(false);

                String typedText = typingArea.getText();

                String duration = (String) durationBox.getSelectedItem();

                int durationSeconds = Integer.parseInt(duration);

                int wpm = calculateWPM(
                                typedText,
                                durationSeconds);

                double accuracy = calculateAccuracy(
                                passageArea.getText(),
                                typedText);

                int errors = calculateErrors(
                                passageArea.getText(),
                                typedText);

                wpmLabel.setText("WPM: " + wpm);

                accuracyLabel.setText(
                                String.format(
                                                "Accuracy: %.2f%%",
                                                accuracy));

                errorLabel.setText(
                                "Errors: " + errors);

                String difficulty = (String) difficultyBox.getSelectedItem();

                int previousBest = ScoreManager.getBestWPM(
                                difficulty,
                                durationSeconds);

                ScoreManager.saveScore(
                                difficulty,
                                durationSeconds,
                                wpm,
                                accuracy,
                                errors);

                int bestWPM = ScoreManager.getBestWPM(
                                difficulty,
                                durationSeconds);

                bestLabel.setText(
                                "Best WPM: " + bestWPM);

                if (wpm > previousBest) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "🏆 Congratulations!\n\n"
                                                        + "New Personal Best!\n\n"
                                                        + "Previous Best: "
                                                        + previousBest
                                                        + " WPM\n"
                                                        + "Current Best: "
                                                        + bestWPM
                                                        + " WPM",
                                        "New Record!",
                                        JOptionPane.INFORMATION_MESSAGE);
                }

                if (completedEarly) {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "🎉 Test Completed!");

                } else {

                        JOptionPane.showMessageDialog(
                                        this,
                                        "⏰ Time Up!");

                }

        }

        private int calculateWPM(String typedText, int testDuration) {

                int charactersTyped = typedText.length();

                double wordsTyped = charactersTyped / 5.0;

                double minutes = testDuration / 60.0;

                return (int) (wordsTyped / minutes);
        }

        private double calculateAccuracy(
                        String originalText,
                        String typedText) {

                int correct = 0;

                int minLength = Math.min(
                                originalText.length(),
                                typedText.length());

                for (int i = 0; i < minLength; i++) {

                        if (originalText.charAt(i) == typedText.charAt(i)) {

                                correct++;
                        }
                }

                if (typedText.length() == 0) {
                        return 0;
                }

                return ((double) correct
                                / typedText.length()) * 100;
        }

        private int calculateErrors(
                        String originalText,
                        String typedText) {

                int errors = 0;

                int minLength = Math.min(
                                originalText.length(),
                                typedText.length());

                for (int i = 0; i < minLength; i++) {

                        if (originalText.charAt(i) != typedText.charAt(i)) {

                                errors++;
                        }
                }

                return errors;
        }

        private void createUI() {

                setLayout(new BorderLayout());

                JPanel topPanel = new JPanel();
                topPanel.setBackground(new Color(250, 248, 228));

                JLabel titleLabel = new JLabel(
                                "Typing Speed Tester",
                                SwingConstants.CENTER);

                titleLabel.setFont(
                                new Font("Segoe UI", Font.BOLD, 32));
                titleLabel.setForeground(new Color(10, 51, 35));

                difficultyBox = new JComboBox<>(
                                new String[] { "Easy", "Medium", "Hard", "Coding" });

                durationBox = new JComboBox<>(
                                new String[] { "15", "30", "60" });

                topPanel.add(new JLabel("Difficulty:"));
                topPanel.add(difficultyBox);

                topPanel.add(new JLabel("Duration (sec):"));
                topPanel.add(durationBox);

                add(topPanel, BorderLayout.NORTH);

                JPanel northPanel = new JPanel(new BorderLayout());
                northPanel.setBackground(new Color(247, 244, 213));

                northPanel.add(titleLabel, BorderLayout.NORTH);
                northPanel.add(topPanel, BorderLayout.CENTER);

                add(northPanel, BorderLayout.NORTH);

                JPanel centerPanel = new JPanel(
                                new GridLayout(2, 1, 0, 5));
                centerPanel.setBackground(new Color(247, 244, 213));
                centerPanel.setBorder(
                                BorderFactory.createEmptyBorder(10, 10, 10, 10));

                passageArea = new JTextArea();
                passageArea.setMargin(new Insets(10, 10, 10, 10));
                passageArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                passageArea.setEditable(false);
                passageArea.setFocusable(true);

                passageArea.setBackground(new Color(252, 250, 242));
                passageArea.setLineWrap(true);
                passageArea.setWrapStyleWord(true);

                passageArea.getInputMap().put(
                                KeyStroke.getKeyStroke("ctrl C"),
                                "copyWarning");

                passageArea.getActionMap().put(
                                "copyWarning",
                                new AbstractAction() {

                                        @Override
                                        public void actionPerformed(java.awt.event.ActionEvent e) {

                                                JOptionPane.showMessageDialog(
                                                                MainFrame.this,
                                                                "Copying the passage is not allowed during the test.",
                                                                "Copy Blocked",
                                                                JOptionPane.WARNING_MESSAGE);
                                        }
                                });
                

                passageArea.getInputMap().put(
                                KeyStroke.getKeyStroke("ctrl A"),
                                "selectWarning");

                passageArea.getActionMap().put(
                                "selectWarning",
                                new AbstractAction() {

                                        @Override
                                        public void actionPerformed(java.awt.event.ActionEvent e) {

                                                JOptionPane.showMessageDialog(
                                                                MainFrame.this,
                                                                "Selecting the passage is disabled during the test.",
                                                                "Selection Blocked",
                                                                JOptionPane.WARNING_MESSAGE);
                                        }
                                });

                typingArea = new JTextArea();
                typingArea.setMargin(new Insets(10, 10, 10, 10));
                typingArea.setFont(new Font("Consolas", Font.PLAIN, 18));
                typingArea.setTransferHandler(null);
                typingArea.setLineWrap(true);
                typingArea.setWrapStyleWord(true);
                typingArea.setBackground(new Color(255, 252, 246));

                typingArea.getInputMap().put(
                                KeyStroke.getKeyStroke("ctrl V"),
                                "none");

                typingArea.setComponentPopupMenu(null);

                typingArea.addKeyListener(new java.awt.event.KeyAdapter() {

                        @Override
                        public void keyPressed(java.awt.event.KeyEvent e) {

                                if (e.isControlDown()
                                                && e.getKeyCode() == java.awt.event.KeyEvent.VK_V) {

                                        JOptionPane.showMessageDialog(
                                                        MainFrame.this,
                                                        "⚠ Pasting is not allowed.\nPlease type manually.");

                                        e.consume();
                                }

                        }

                });

                typingArea.getInputMap().put(
                                KeyStroke.getKeyStroke("shift INSERT"),
                                "none");

                typingArea.getDocument().addDocumentListener(
                                new javax.swing.event.DocumentListener() {

                                        public void insertUpdate(
                                                        javax.swing.event.DocumentEvent e) {

                                                checkCompletion();

                                        }

                                        public void removeUpdate(
                                                        javax.swing.event.DocumentEvent e) {

                                                checkCompletion();

                                        }

                                        public void changedUpdate(
                                                        javax.swing.event.DocumentEvent e) {

                                                checkCompletion();

                                        }

                                });

                JScrollPane passageScroll = new JScrollPane(passageArea);

                TitledBorder passageBorder = BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                                new Color(120, 145, 82), 2),
                                "Passage");

                passageBorder.setTitleColor(new Color(10, 51, 35));
                passageBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));

                passageScroll.setBorder(passageBorder);

                JScrollPane typingScroll = new JScrollPane(typingArea);

                TitledBorder typingBorder = BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(
                                                new Color(120, 145, 82), 2),
                                "Type Here");

                typingBorder.setTitleColor(new Color(10, 51, 35));
                typingBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));

                typingScroll.setBorder(typingBorder);
                centerPanel.add(passageScroll);

                centerPanel.add(typingScroll);

                add(centerPanel, BorderLayout.CENTER);

                JPanel bottomPanel = new JPanel();
                bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

                bottomPanel.setBackground(new Color(250, 248, 228));
                JPanel statsPanel = new JPanel();
                statsPanel.setBackground(new Color(250, 248, 228));

                Font statsFont = new Font("Segoe UI", Font.BOLD, 16);

                timerLabel = new JLabel("Time Left: 0");
                timerLabel.setFont(statsFont);
                timerLabel.setForeground(new Color(10, 51, 35));

                wpmLabel = new JLabel("WPM: 0");
                wpmLabel.setFont(statsFont);
                wpmLabel.setForeground(new Color(10, 51, 35));

                accuracyLabel = new JLabel("Accuracy: 0%");
                accuracyLabel.setFont(statsFont);
                accuracyLabel.setForeground(new Color(10, 51, 35));

                errorLabel = new JLabel("Errors: 0");
                errorLabel.setFont(statsFont);
                errorLabel.setForeground(new Color(10, 51, 35));

                bestLabel = new JLabel("Best WPM: 0");
                bestLabel.setFont(statsFont);
                bestLabel.setForeground(new Color(10, 51, 35));

                statsPanel.add(timerLabel);
                statsPanel.add(wpmLabel);
                statsPanel.add(accuracyLabel);
                statsPanel.add(errorLabel);
                statsPanel.add(bestLabel);

                JPanel buttonPanel = new JPanel(
                                new FlowLayout(FlowLayout.CENTER, 25, 20));
                buttonPanel.setBackground(new Color(250, 248, 228));

                Font buttonFont = new Font("Segoe UI", Font.BOLD, 18);

                startButton = new JButton("Start Test");
                startButton.setBackground(new Color(43, 79, 52)); // Dark green
                startButton.setForeground(Color.WHITE);

                startButton.setOpaque(true);
                startButton.setBorderPainted(false);
                startButton.setFocusPainted(false);
                startButton.setContentAreaFilled(true);
                startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                startButton.setFont(buttonFont);
                startButton.setFocusPainted(false);
                startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                resetButton = new JButton("Reset");
                resetButton.setBackground(new Color(148, 156, 98)); // Moss green
                resetButton.setForeground(Color.WHITE);

                resetButton.setOpaque(true);
                resetButton.setBorderPainted(false);
                resetButton.setFocusPainted(false);
                resetButton.setContentAreaFilled(true);
                resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                resetButton.setFont(buttonFont);
                resetButton.setFocusPainted(false);
                resetButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                historyButton = new JButton("View History");
                historyButton.setBackground(new Color(211, 150, 140)); // Rosy brown
                historyButton.setForeground(Color.WHITE);

                historyButton.setOpaque(true);
                historyButton.setBorderPainted(false);
                historyButton.setFocusPainted(false);
                historyButton.setContentAreaFilled(true);
                historyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                historyButton.setFont(buttonFont);
                historyButton.setFocusPainted(false);
                historyButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                Dimension buttonSize = new Dimension(180, 55);

                startButton.setPreferredSize(buttonSize);
                resetButton.setPreferredSize(buttonSize);
                historyButton.setPreferredSize(buttonSize);

                buttonPanel.add(startButton);
                buttonPanel.add(resetButton);
                buttonPanel.add(historyButton);

                bottomPanel.add(statsPanel);
                bottomPanel.add(Box.createVerticalStrut(5));
                bottomPanel.add(buttonPanel);
                ;

                add(bottomPanel, BorderLayout.SOUTH);

                // --------------------------------
                // START BUTTON
                // --------------------------------

                startButton.addActionListener(e -> {
                        if (timer != null && timer.isRunning()) {

                                int option = JOptionPane.showConfirmDialog(
                                                this,
                                                "A typing test is already running.\n\nDo you want to start a new test?",
                                                "Restart Test",
                                                JOptionPane.YES_NO_OPTION);

                                if (option == JOptionPane.NO_OPTION) {
                                        return;
                                }

                                timer.stop();
                        }

                        String difficulty = (String) difficultyBox.getSelectedItem();

                        String passage = PassageManager.getRandomPassage(difficulty);

                        passageArea.setText(passage);

                        typingArea.setText("");

                        typingArea.setEditable(true);

                        String duration = (String) durationBox.getSelectedItem();

                        int seconds = Integer.parseInt(duration);
                        if (timer != null) {
                                timer.stop();
                        }
                        startTimer(seconds);

                });

                // --------------------------------
                // RESET BUTTON
                // --------------------------------

                resetButton.addActionListener(e -> {

                        // Stop timer
                        if (timer != null) {
                                timer.stop();
                        }

                        // Clear text areas
                        passageArea.setText("");
                        typingArea.setText("");

                        // Enable typing again
                        typingArea.setEditable(true);
                        typingArea.requestFocusInWindow();

                        // Reset background color
                        typingArea.setBackground(Color.WHITE);

                        // Reset labels
                        timerLabel.setText("Time Left: 0");
                        wpmLabel.setText("WPM: 0");
                        accuracyLabel.setText("Accuracy: 0%");
                        errorLabel.setText("Errors: 0");

                        // Show current best score
                        String difficulty = (String) difficultyBox.getSelectedItem();
                        int duration = Integer.parseInt((String) durationBox.getSelectedItem());

                        int best = ScoreManager.getBestWPM(difficulty, duration);

                        bestLabel.setText("Best WPM: " + best);

                        // Move cursor to typing area
                        typingArea.requestFocus();

                });

                historyButton.addActionListener(e -> {

                        String history = HistoryManager.getHistory();

                        String[] rows = history.split("\n");

                        if (rows.length <= 1) {
                                JOptionPane.showMessageDialog(
                                                this,
                                                "No history available.");
                                return;
                        }

                        // Column names (first row of CSV)
                        String[] columns = rows[0].split(",");

                        // Data rows
                        String[][] data = new String[rows.length - 1][columns.length];

                        for (int i = 1; i < rows.length; i++) {

                                data[i - 1] = rows[i].split(",");

                        }

                        JTable table = new JTable(data, columns);

                        table.setEnabled(false);
                        table.setRowHeight(25);

                        JScrollPane scrollPane = new JScrollPane(table);

                        scrollPane.setPreferredSize(new Dimension(700, 300));

                        JOptionPane.showMessageDialog(
                                        this,
                                        scrollPane,
                                        "Typing Test History",
                                        JOptionPane.INFORMATION_MESSAGE);

                });
        }
}