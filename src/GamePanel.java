import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private static final int FRAME_MS = 16;

    private static final Color GRID_COLOR = new Color(65, 65, 65);
    private static final Color BOARD_BG = new Color(38, 38, 38);
    private static final Color PATH_COLOR = new Color(137, 110, 79);
    private static final Color SPAWN_COLOR = new Color(70, 170, 70);
    private static final Color GOAL_COLOR = new Color(180, 70, 70);
    private static final Color TOWER_COLOR = new Color(100, 170, 240);
    private static final Color ENEMY_COLOR = new Color(232, 138, 48);
    private static final Color UI_BG = new Color(25, 25, 25);

    private final GameState state;
    private final Timer timer;

    private long lastTickNanos;

    private boolean towerSelected;
    private int hoverTileX = -1;
    private int hoverTileY = -1;

    private final Rectangle towerButton;
    private final Rectangle nextWaveButton;

    public GamePanel() {
        this.state = new GameState();
        this.towerSelected = true;

        int uiX = GameState.BOARD_COLS * GameState.TILE_SIZE;
        this.towerButton = new Rectangle(uiX + 20, 70, GameState.UI_WIDTH - 40, 44);
        this.nextWaveButton = new Rectangle(uiX + 20, 130, GameState.UI_WIDTH - 40, 44);

        setPreferredSize(new Dimension(GameState.SCREEN_WIDTH, GameState.SCREEN_HEIGHT));
        setFocusable(true);

        setupInput();

        this.lastTickNanos = System.nanoTime();
        this.timer = new Timer(FRAME_MS, e -> onTick());
        this.timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private void onTick() {
        long now = System.nanoTime();
        double dtMs = (now - lastTickNanos) / 1_000_000.0;
        lastTickNanos = now;

        // Large dt clamp when debugger break or first frame delay happens.
        if (dtMs < 0 || dtMs > 100) {
            dtMs = FRAME_MS;
        }

        state.update(dtMs);
        repaint();
    }

    private void setupInput() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("SPACE"), "startWave");
        am.put("startWave", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!state.isGameOver() && !state.isWaveInProgress()) {
                    state.startNextWave();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("R"), "restart");
        am.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (state.isGameOver()) {
                    state.reset();
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
        am.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(GamePanel.this);
                if (window != null) {
                    window.dispose();
                }
                System.exit(0);
            }
        });

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoverTile(e.getX(), e.getY());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverTileX = -1;
                hoverTileY = -1;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        };

        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
    }

    private void handleClick(int mx, int my) {
        if (state.isGameOver()) {
            return;
        }

        if (towerButton.contains(mx, my)) {
            towerSelected = true;
            return;
        }

        if (nextWaveButton.contains(mx, my)) {
            if (!state.isWaveInProgress()) {
                state.startNextWave();
            }
            return;
        }

        int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
        if (mx < 0 || mx >= boardWidth || my < 0 || my >= GameState.SCREEN_HEIGHT) {
            return;
        }

        int tx = mx / GameState.TILE_SIZE;
        int ty = my / GameState.TILE_SIZE;

        if (towerSelected) {
            state.placeTower(tx, ty);
        }
    }

    private void updateHoverTile(int mx, int my) {
        int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
        if (mx >= 0 && mx < boardWidth && my >= 0 && my < GameState.SCREEN_HEIGHT) {
            hoverTileX = mx / GameState.TILE_SIZE;
            hoverTileY = my / GameState.TILE_SIZE;
        } else {
            hoverTileX = -1;
            hoverTileY = -1;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2);
        drawPath(g2);
        drawTowers(g2);
        drawEnemies(g2);
        drawEffects(g2);
        drawPlacementPreview(g2);
        drawUI(g2);

        if (state.isGameOver()) {
            drawGameOver(g2);
        }

        g2.dispose();
    }

    private void drawBoard(Graphics2D g2) {
        int boardWidth = GameState.BOARD_COLS * GameState.TILE_SIZE;
        int boardHeight = GameState.BOARD_ROWS * GameState.TILE_SIZE;

        g2.setColor(BOARD_BG);
        g2.fillRect(0, 0, boardWidth, boardHeight);

        g2.setColor(GRID_COLOR);
        for (int x = 0; x <= GameState.BOARD_COLS; x++) {
            int px = x * GameState.TILE_SIZE;
            g2.drawLine(px, 0, px, boardHeight);
        }
        for (int y = 0; y <= GameState.BOARD_ROWS; y++) {
            int py = y * GameState.TILE_SIZE;
            g2.drawLine(0, py, boardWidth, py);
        }
    }

    private void drawPath(Graphics2D g2) {
        g2.setColor(PATH_COLOR);
        for (Point p : state.getPathTiles()) {
            g2.fillRect(p.x * GameState.TILE_SIZE, p.y * GameState.TILE_SIZE, GameState.TILE_SIZE, GameState.TILE_SIZE);
        }

        Point spawn = state.getSpawnTile();
        Point goal = state.getGoalTile();

        g2.setColor(SPAWN_COLOR);
        g2.fillRect(spawn.x * GameState.TILE_SIZE + 6, spawn.y * GameState.TILE_SIZE + 6,
                GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);

        g2.setColor(GOAL_COLOR);
        g2.fillRect(goal.x * GameState.TILE_SIZE + 6, goal.y * GameState.TILE_SIZE + 6,
                GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);
    }

    private void drawTowers(Graphics2D g2) {
        for (Tower tower : state.getTowers()) {
            Point t = tower.getTile();
            int x = t.x * GameState.TILE_SIZE;
            int y = t.y * GameState.TILE_SIZE;

            g2.setColor(TOWER_COLOR);
            g2.fillRect(x + 6, y + 6, GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);
            g2.setColor(Color.BLACK);
            g2.drawRect(x + 6, y + 6, GameState.TILE_SIZE - 12, GameState.TILE_SIZE - 12);
        }

        if (towerSelected && hoverTileX >= 0 && hoverTileY >= 0) {
            if (state.canPlaceTower(hoverTileX, hoverTileY)) {
                int cx = Path.tileCenterX(hoverTileX, GameState.TILE_SIZE);
                int cy = Path.tileCenterY(hoverTileY, GameState.TILE_SIZE);
                int r = (int) GameState.TOWER_RANGE_PX;

                g2.setColor(new Color(120, 180, 255, 90));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(170, 210, 255, 150));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for (Enemy enemy : state.getEnemies()) {
            int size = GameState.TILE_SIZE - 14;
            int x = (int) Math.round(enemy.getX()) - size / 2;
            int y = (int) Math.round(enemy.getY()) - size / 2;

            g2.setColor(ENEMY_COLOR);
            g2.fillOval(x, y, size, size);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, size, size);

            int barW = size;
            int barH = 5;
            int barX = x;
            int barY = y - 8;
            g2.setColor(new Color(70, 70, 70));
            g2.fillRect(barX, barY, barW, barH);

            double ratio = enemy.getMaxHp() <= 0 ? 0 : (enemy.getHp() / (double) enemy.getMaxHp());
            ratio = Math.max(0.0, Math.min(1.0, ratio));
            g2.setColor(new Color(80, 220, 100));
            g2.fillRect(barX, barY, (int) Math.round(barW * ratio), barH);
        }
    }

    private void drawEffects(Graphics2D g2) {
        for (Effect effect : state.getEffects()) {
            float alpha = (float) effect.getAlpha();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(new Color(255, 245, 180));
            g2.drawLine((int) Math.round(effect.getX1()), (int) Math.round(effect.getY1()),
                    (int) Math.round(effect.getX2()), (int) Math.round(effect.getY2()));
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    private void drawPlacementPreview(Graphics2D g2) {
        if (!towerSelected || hoverTileX < 0 || hoverTileY < 0 || state.isGameOver()) {
            return;
        }

        int x = hoverTileX * GameState.TILE_SIZE;
        int y = hoverTileY * GameState.TILE_SIZE;

        boolean canPlace = state.canPlaceTower(hoverTileX, hoverTileY) && state.getGold() >= GameState.TOWER_COST;
        Color c = canPlace ? new Color(100, 180, 255, 120) : new Color(230, 70, 70, 120);
        g2.setColor(c);
        g2.fillRect(x + 4, y + 4, GameState.TILE_SIZE - 8, GameState.TILE_SIZE - 8);
    }

    private void drawUI(Graphics2D g2) {
        int uiX = GameState.BOARD_COLS * GameState.TILE_SIZE;

        g2.setColor(UI_BG);
        g2.fillRect(uiX, 0, GameState.UI_WIDTH, GameState.SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Tower Defense", uiX + 20, 34);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString("Gold: " + state.getGold(), uiX + 20, 210);
        g2.drawString("Lives: " + state.getLives(), uiX + 20, 236);
        g2.drawString("Wave: " + state.getWave(), uiX + 20, 262);
        g2.drawString("Kills: " + state.getKills(), uiX + 20, 288);

        if (state.isWaveInProgress()) {
            g2.drawString("Spawning: " + state.getSpawnedThisWave() + "/" + state.getEnemiesToSpawn(), uiX + 20, 314);
        } else {
            g2.drawString("Wave Clear", uiX + 20, 314);
        }

        drawButton(g2, towerButton, "Basic Tower ($" + GameState.TOWER_COST + ")", towerSelected);

        String nextLabel = state.isWaveInProgress() ? "Wave Running..." : "Next Wave (Space)";
        drawButton(g2, nextWaveButton, nextLabel, !state.isWaveInProgress());

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.setColor(new Color(210, 210, 210));
        g2.drawString("Click board to place tower", uiX + 20, GameState.SCREEN_HEIGHT - 40);
        g2.drawString("R: Restart, ESC: Exit", uiX + 20, GameState.SCREEN_HEIGHT - 20);
    }

    private void drawButton(Graphics2D g2, Rectangle r, String label, boolean active) {
        g2.setColor(active ? new Color(80, 130, 200) : new Color(65, 65, 65));
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);

        FontMetrics fm = g2.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(label)) / 2;
        int ty = r.y + (r.height + fm.getAscent()) / 2 - 4;
        g2.drawString(label, tx, ty);
    }

    private void drawGameOver(Graphics2D g2) {
        int boardW = GameState.BOARD_COLS * GameState.TILE_SIZE;
        int boardH = GameState.BOARD_ROWS * GameState.TILE_SIZE;

        g2.setColor(new Color(0, 0, 0, 165));
        g2.fillRect(0, 0, boardW, boardH);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 42));
        drawCentered(g2, "Game Over", boardW / 2, boardH / 2 - 30);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        drawCentered(g2, "Wave: " + state.getWave() + "   Score: " + state.getKills(), boardW / 2, boardH / 2 + 10);
        drawCentered(g2, "R: Restart / ESC: Exit", boardW / 2, boardH / 2 + 42);
    }

    private void drawCentered(Graphics2D g2, String text, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }
}