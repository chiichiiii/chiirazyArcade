package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class L23Game extends JFrame {
    static class Cell {
        int row;
        int col;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    static class Block {
        int row;
        int value;

        Block(int row, int value) {
            this.row = row;
            this.value = value;
        }
    }

    static class FallingTile {
        int value;
        int col;
        double startRow;
        double endRow;

        FallingTile(int value, int col, double startRow, double endRow) {
            this.value = value;
            this.col = col;
            this.startRow = startRow;
            this.endRow = endRow;
        }
    }

    class GamePanel extends JPanel {
        final int rows = 10;
        final int cols = 8;
        final int cellSize = 56;
        final int infoHeight = 70;
        final int[][] board = new int[rows][cols];
        final int fruitKinds = 5;
        final int mixItem = 100;
        final Color[] palette = {
                new Color(236, 95, 95),
                new Color(79, 170, 255),
                new Color(255, 196, 79),
                new Color(110, 214, 138),
                new Color(193, 134, 255)
        };
        final Image[] fruitImages = new Image[fruitKinds];
        Image mixItemImage;
        final Random random = new Random();

        int score = 0;
        int selectedRow = -1;
        int selectedCol = -1;

        boolean animating = false;
        double animationProgress = 0.0;
        double animationStep = 0.1;
        Timer animationTimer;
        List<FallingTile> fallingTiles = new ArrayList<>();

        GamePanel() {
            setPreferredSize(new Dimension(cols * cellSize, rows * cellSize + infoHeight));
            setBackground(new Color(247, 247, 247));
            loadImages();
            refillPlayableBoard();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    onMousePressed(e.getX(), e.getY());
                }
            });
        }

        void loadImages() {
            fruitImages[0] = loadImage("/com/smu8/game/fruit_a.png");
            fruitImages[1] = loadImage("/com/smu8/game/fruit_b.png");
            fruitImages[2] = loadImage("/com/smu8/game/fruit_c.png");
            fruitImages[3] = loadImage("/com/smu8/game/fruit_d.png");
            fruitImages[4] = loadImage("/com/smu8/game/fruit_e.png");
            mixItemImage = loadImage("/com/smu8/game/item_mix.png");
        }

        Image loadImage(String path) {
            URL url = L23Game.class.getResource(path);
            if (url == null) {
                return null;
            }
            return new ImageIcon(url).getImage();
        }

        boolean isFruit(int value) {
            return value >= 1 && value <= fruitKinds;
        }

        boolean isItem(int value) {
            return value == mixItem;
        }

        void refillPlayableBoard() {
            do {
                fillRandomBoardNoImmediateMatches();
            } while (!hasPossibleMove());
        }

        void fillRandomBoardNoImmediateMatches() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    board[r][c] = randomValueAvoidingStreak(r, c);
                }
            }
        }

        int randomValueAvoidingStreak(int row, int col) {
            while (true) {
                int value = randomFruit();
                boolean makesHorizontal3 = col >= 2 && board[row][col - 1] == value && board[row][col - 2] == value;
                boolean makesVertical3 = row >= 2 && board[row - 1][col] == value && board[row - 2][col] == value;
                if (!makesHorizontal3 && !makesVertical3) {
                    return value;
                }
            }
        }

        int randomFruit() {
            return 1 + random.nextInt(fruitKinds);
        }

        void onMousePressed(int x, int y) {
            if (animating || y < infoHeight) {
                return;
            }

            int col = x / cellSize;
            int row = (y - infoHeight) / cellSize;

            if (row < 0 || row >= rows || col < 0 || col >= cols) {
                return;
            }

            if (selectedRow == -1) {
                selectedRow = row;
                selectedCol = col;
                repaint();
                return;
            }

            if (selectedRow == row && selectedCol == col) {
                clearSelection();
                repaint();
                return;
            }

            if (!isAdjacent(selectedRow, selectedCol, row, col)) {
                selectedRow = row;
                selectedCol = col;
                repaint();
                return;
            }

            int firstValue = board[selectedRow][selectedCol];
            int secondValue = board[row][col];
            boolean firstIsItem = isItem(firstValue);
            boolean secondIsItem = isItem(secondValue);

            swap(selectedRow, selectedCol, row, col);

            if (firstIsItem || secondIsItem) {
                int removed;
                int scoreMultiplier;
                if (firstIsItem && secondIsItem) {
                    removed = clearAllBlocks();
                    scoreMultiplier = 4;
                } else {
                    int itemRow = firstIsItem ? row : selectedRow;
                    int itemCol = firstIsItem ? col : selectedCol;
                    removed = activateMixItem(itemRow, itemCol);
                    scoreMultiplier = 2;
                }
                score += removed * scoreMultiplier;
                clearSelection();
                animating = true;
                resolveMatchesCascade();
                return;
            }

            if (hasAnyMatch()) {
                clearSelection();
                animating = true;
                resolveMatchesCascade();
            } else {
                swap(selectedRow, selectedCol, row, col);
                clearSelection();
                repaint();
            }
        }

        int activateMixItem(int row, int col) {
            boolean[][] remove = new boolean[rows][cols];

            if (row >= 0 && row < rows && col >= 0 && col < cols) {
                remove[row][col] = true;
                for (int c = 0; c < cols; c++) {
                    if (board[row][c] != 0) {
                        remove[row][c] = true;
                    }
                }
                for (int r = 0; r < rows; r++) {
                    if (board[r][col] != 0) {
                        remove[r][col] = true;
                    }
                }
            }

            int removed = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (remove[r][c] && board[r][c] != 0) {
                        board[r][c] = 0;
                        removed++;
                    }
                }
            }
            return removed;
        }

        int clearAllBlocks() {
            int removed = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (board[r][c] != 0) {
                        board[r][c] = 0;
                        removed++;
                    }
                }
            }
            return removed;
        }

        boolean isAdjacent(int r1, int c1, int r2, int c2) {
            int dist = Math.abs(r1 - r2) + Math.abs(c1 - c2);
            return dist == 1;
        }

        void clearSelection() {
            selectedRow = -1;
            selectedCol = -1;
        }

        void swap(int r1, int c1, int r2, int c2) {
            int temp = board[r1][c1];
            board[r1][c1] = board[r2][c2];
            board[r2][c2] = temp;
        }

        boolean hasAnyMatch() {
            boolean[][] matched = findMatchedCells();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (matched[r][c]) {
                        return true;
                    }
                }
            }
            return false;
        }

        boolean[][] findMatchedCells() {
            boolean[][] matched = new boolean[rows][cols];

            for (int r = 0; r < rows; r++) {
                int c = 0;
                while (c < cols) {
                    int value = board[r][c];
                    if (!isFruit(value)) {
                        c++;
                        continue;
                    }

                    int start = c;
                    while (c + 1 < cols && board[r][c + 1] == value) {
                        c++;
                    }

                    int len = c - start + 1;
                    if (len >= 3) {
                        for (int k = start; k <= c; k++) {
                            matched[r][k] = true;
                        }
                    }
                    c++;
                }
            }

            for (int c = 0; c < cols; c++) {
                int r = 0;
                while (r < rows) {
                    int value = board[r][c];
                    if (!isFruit(value)) {
                        r++;
                        continue;
                    }

                    int start = r;
                    while (r + 1 < rows && board[r + 1][c] == value) {
                        r++;
                    }

                    int len = r - start + 1;
                    if (len >= 3) {
                        for (int k = start; k <= r; k++) {
                            matched[k][c] = true;
                        }
                    }
                    r++;
                }
            }

            return matched;
        }

        int removeMatchedCellsAndCreateItems() {
            boolean[][] matched = findMatchedCells();
            boolean hasMatched = false;
            for (int r = 0; r < rows && !hasMatched; r++) {
                for (int c = 0; c < cols; c++) {
                    if (matched[r][c]) {
                        hasMatched = true;
                        break;
                    }
                }
            }
            if (!hasMatched) {
                return 0;
            }

            boolean[][] visited = new boolean[rows][cols];
            List<Cell> spawnItemPositions = new ArrayList<>();
            int[] dr = {-1, 1, 0, 0};
            int[] dc = {0, 0, -1, 1};

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (!matched[r][c] || visited[r][c] || !isFruit(board[r][c])) {
                        continue;
                    }

                    int fruit = board[r][c];
                    ArrayDeque<Cell> queue = new ArrayDeque<>();
                    List<Cell> component = new ArrayList<>();
                    queue.add(new Cell(r, c));
                    visited[r][c] = true;

                    while (!queue.isEmpty()) {
                        Cell cur = queue.poll();
                        component.add(cur);

                        for (int i = 0; i < 4; i++) {
                            int nr = cur.row + dr[i];
                            int nc = cur.col + dc[i];
                            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                                continue;
                            }
                            if (visited[nr][nc] || !matched[nr][nc]) {
                                continue;
                            }
                            if (board[nr][nc] != fruit) {
                                continue;
                            }
                            visited[nr][nc] = true;
                            queue.add(new Cell(nr, nc));
                        }
                    }

                    if (component.size() >= 5) {
                        Cell spawn = component.get(0);
                        for (Cell cell : component) {
                            if (cell.row > spawn.row || (cell.row == spawn.row && cell.col < spawn.col)) {
                                spawn = cell;
                            }
                        }
                        spawnItemPositions.add(spawn);
                    }
                }
            }

            int removed = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (matched[r][c]) {
                        board[r][c] = 0;
                        removed++;
                    }
                }
            }

            for (Cell pos : spawnItemPositions) {
                if (board[pos.row][pos.col] == 0) {
                    board[pos.row][pos.col] = mixItem;
                }
            }

            return removed;
        }

        void resolveMatchesCascade() {
            int removed = removeMatchedCellsAndCreateItems();
            if (removed > 0) {
                score += removed;
                startFallAnimation();
                return;
            }

            if (hasEmptyCell()) {
                startFallAnimation();
                return;
            }

            animating = false;
            if (!hasPossibleMove()) {
                refillPlayableBoard();
            }
            repaint();
        }

        boolean hasEmptyCell() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (board[r][c] == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        void startFallAnimation() {
            int[][] targetBoard = new int[rows][cols];
            List<FallingTile> moving = new ArrayList<>();

            for (int c = 0; c < cols; c++) {
                List<Block> survivors = new ArrayList<>();
                for (int r = rows - 1; r >= 0; r--) {
                    if (board[r][c] != 0) {
                        survivors.add(new Block(r, board[r][c]));
                    }
                }

                int writeRow = rows - 1;
                for (Block s : survivors) {
                    targetBoard[writeRow][c] = s.value;
                    moving.add(new FallingTile(s.value, c, s.row, writeRow));
                    writeRow--;
                }

                int emptyCount = writeRow + 1;
                for (int r = writeRow; r >= 0; r--) {
                    int value = randomFruit();
                    targetBoard[r][c] = value;
                    double startRow = r - emptyCount;
                    moving.add(new FallingTile(value, c, startRow, r));
                }
            }

            for (int r = 0; r < rows; r++) {
                System.arraycopy(targetBoard[r], 0, board[r], 0, cols);
            }

            fallingTiles = moving;
            animationProgress = 0.0;

            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            animationTimer = new Timer(16, e -> {
                animationProgress += animationStep;
                if (animationProgress >= 1.0) {
                    animationProgress = 1.0;
                    animationTimer.stop();
                    resolveMatchesCascade();
                    return;
                }
                repaint();
            });
            animationTimer.start();
        }

        boolean hasPossibleMove() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (isItem(board[r][c])) {
                        if ((c + 1 < cols && board[r][c + 1] != 0)
                                || (c - 1 >= 0 && board[r][c - 1] != 0)
                                || (r + 1 < rows && board[r + 1][c] != 0)
                                || (r - 1 >= 0 && board[r - 1][c] != 0)) {
                            return true;
                        }
                    }

                    if (c + 1 < cols) {
                        if (isItem(board[r][c]) || isItem(board[r][c + 1])) {
                            return true;
                        }
                        swap(r, c, r, c + 1);
                        boolean ok = hasAnyMatch();
                        swap(r, c, r, c + 1);
                        if (ok) {
                            return true;
                        }
                    }
                    if (r + 1 < rows) {
                        if (isItem(board[r][c]) || isItem(board[r + 1][c])) {
                            return true;
                        }
                        swap(r, c, r + 1, c);
                        boolean ok = hasAnyMatch();
                        swap(r, c, r + 1, c);
                        if (ok) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(35, 35, 35));
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2.drawString("L23 PicoPop", 12, 30);

            g2.setColor(new Color(70, 70, 70));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g2.drawString("Score: " + score, 12, 58);

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = c * cellSize;
                    int y = infoHeight + r * cellSize;
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRoundRect(x + 3, y + 3, cellSize - 6, cellSize - 6, 14, 14);
                }
            }

            if (animating) {
                for (FallingTile tile : fallingTiles) {
                    double drawRow = tile.startRow + (tile.endRow - tile.startRow) * animationProgress;
                    int x = tile.col * cellSize;
                    int y = infoHeight + (int) Math.round(drawRow * cellSize);
                    drawTile(g2, tile.value, x, y);
                }
            } else {
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        int value = board[r][c];
                        if (value == 0) {
                            continue;
                        }
                        int x = c * cellSize;
                        int y = infoHeight + r * cellSize;
                        drawTile(g2, value, x, y);
                    }
                }
            }

            if (!animating && selectedRow >= 0 && selectedCol >= 0) {
                int sx = selectedCol * cellSize;
                int sy = infoHeight + selectedRow * cellSize;
                g2.setColor(new Color(255, 255, 255));
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(sx + 4, sy + 4, cellSize - 8, cellSize - 8, 12, 12);
            }
        }

        void drawTile(Graphics2D g2, int value, int x, int y) {
            if (isItem(value)) {
                if (mixItemImage != null) {
                    g2.drawImage(mixItemImage, x + 6, y + 6, cellSize - 12, cellSize - 12, null);
                    return;
                }
                g2.setColor(new Color(255, 255, 255));
                g2.fillRoundRect(x + 8, y + 8, cellSize - 16, cellSize - 16, 12, 12);
                g2.setColor(new Color(50, 50, 50));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("MIX", x + 14, y + 33);
                return;
            }

            Image fruit = fruitImages[value - 1];
            if (fruit != null) {
                g2.drawImage(fruit, x + 6, y + 6, cellSize - 12, cellSize - 12, null);
                return;
            }

            g2.setColor(palette[value - 1]);
            g2.fillOval(x + 8, y + 8, cellSize - 16, cellSize - 16);
        }
    }

    public L23Game() {
        super("L23 PicoPop");
        setContentPane(new GamePanel());
        pack();
        setLocation(600, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(L23Game::new);
    }
}