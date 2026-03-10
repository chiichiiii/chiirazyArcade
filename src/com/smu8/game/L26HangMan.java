package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class L26HangMan {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordManager wordManager = new WordManager();
            UI ui = new UI();
            Game game = new Game(wordManager, ui, 6);
            ui.bindGame(game);
            game.startNewRound();
        });
    }
}

class Game {
    private final WordManager wordManager;
    private final UI ui;
    private final int maxWrongCount;

    private String answer;
    private char[] masked;
    private final Set<Character> guessed = new LinkedHashSet<>();
    private int wrongCount;
    private boolean finished;

    Game(WordManager wordManager, UI ui, int maxWrongCount) {
        this.wordManager = wordManager;
        this.ui = ui;
        this.maxWrongCount = maxWrongCount;
    }

    public void startNewRound() {
        answer = wordManager.getRandomWord();
        masked = new char[answer.length()];
        Arrays.fill(masked, '_');
        guessed.clear();
        wrongCount = 0;
        finished = false;

        ui.showMessage("New game started.");
        ui.render(masked, guessed, wrongCount, maxWrongCount, false, false, null);
    }

    public void submitGuess(char guess) {
        if (finished) {
            ui.showMessage("Game finished. Click New Game.");
            return;
        }

        if (guessed.contains(guess)) {
            ui.showMessage("Already used: " + guess);
            return;
        }

        guessed.add(guess);
        int revealed = reveal(guess);
        if (revealed == 0) {
            wrongCount++;
            ui.showMessage("Wrong: " + guess);
        } else {
            ui.showMessage("Correct: " + guess);
        }

        boolean win = isWin();
        boolean lose = isLose();
        finished = win || lose;

        String answerText = finished ? answer : null;
        ui.render(masked, guessed, wrongCount, maxWrongCount, win, lose, answerText);
    }

    private int reveal(char guess) {
        int revealed = 0;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == guess && masked[i] == '_') {
                masked[i] = guess;
                revealed++;
            }
        }
        return revealed;
    }

    private boolean isWin() {
        for (char c : masked) {
            if (c == '_') {
                return false;
            }
        }
        return true;
    }

    private boolean isLose() {
        return wrongCount >= maxWrongCount;
    }
}

class WordManager {
    private final List<String> words = List.of(
            "java",
            "object",
            "class",
            "method",
            "inheritance",
            "interface",
            "encapsulation",
            "polymorphism",
            "exception",
            "package"
    );

    private final Random random = new Random();

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }
}

class UI extends JFrame {
    private final JLabel wordLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel usedLabel = new JLabel("Used: []");
    private final JLabel lifeLabel = new JLabel("Life: 6 / 6");
    private final JLabel frameLabel = new JLabel("Frame: [------]");
    private final JLabel messageLabel = new JLabel(" ");

    private final JTextField inputField = new JTextField();
    private final JButton guessButton = new JButton("Guess");
    private final JButton newGameButton = new JButton("New Game");

    private Game game;

    UI() {
        super("L26 Hangman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        wordLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        usedLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lifeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        frameLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        inputField.setFont(new Font("SansSerif", Font.BOLD, 18));
        inputField.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(20));
        center.add(wordLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(usedLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(lifeLabel);
        center.add(Box.createVerticalStrut(8));
        center.add(frameLabel);
        center.add(Box.createVerticalStrut(16));
        center.add(messageLabel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        inputField.setPreferredSize(new Dimension(80, 34));
        bottom.add(new JLabel("Letter:"));
        bottom.add(inputField);
        bottom.add(guessButton);
        bottom.add(newGameButton);

        setLayout(new BorderLayout());
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        guessButton.addActionListener(e -> onGuess());
        inputField.addActionListener(e -> onGuess());
        newGameButton.addActionListener(e -> {
            if (game != null) {
                game.startNewRound();
            }
        });

        setSize(520, 360);
        setLocation(640, 220);
        setVisible(true);
    }

    void bindGame(Game game) {
        this.game = game;
    }

    void onGuess() {
        if (game == null) {
            return;
        }

        String input = inputField.getText().trim().toLowerCase();
        inputField.setText("");
        if (input.length() != 1 || input.charAt(0) < 'a' || input.charAt(0) > 'z') {
            showMessage("Enter one alphabet letter (a-z).");
            return;
        }

        game.submitGuess(input.charAt(0));
    }

    void render(char[] masked, Set<Character> guessed, int wrongCount, int maxWrongCount,
                boolean win, boolean lose, String answer) {
        wordLabel.setText(formatMasked(masked));
        usedLabel.setText("Used: " + guessed);
        lifeLabel.setText("Life: " + (maxWrongCount - wrongCount) + " / " + maxWrongCount);
        frameLabel.setText("Frame: " + hangmanFrame(wrongCount, maxWrongCount));

        if (win) {
            messageLabel.setText("You win! Answer: " + answer);
        } else if (lose) {
            messageLabel.setText("You lose. Answer: " + answer);
        }
    }

    void showMessage(String message) {
        messageLabel.setText(message);
    }

    private String formatMasked(char[] masked) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < masked.length; i++) {
            sb.append(masked[i]);
            if (i < masked.length - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    private String hangmanFrame(int wrongCount, int maxWrongCount) {
        int safeWrong = Math.min(wrongCount, maxWrongCount);
        return "[" + "#".repeat(safeWrong) + "-".repeat(Math.max(0, maxWrongCount - safeWrong)) + "]";
    }
}