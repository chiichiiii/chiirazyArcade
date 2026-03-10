import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private final GameState state = new GameState();
    private final Timer timer;

    private boolean p1Up;
    private boolean p1Down;
    private boolean p1Left;
    private boolean p1Right;
    private boolean p2Up;
    private boolean p2Down;
    private boolean p2Left;
    private boolean p2Right;
    private boolean p1BombQueued;
    private boolean p2BombQueued;

    private long lastNanos;
    private long aPressedAt;

    public GamePanel() {
        int w = GameState.MAP_W * GameState.TILE_SIZE + GameState.HUD_W;
        int h = GameState.MAP_H * GameState.TILE_SIZE;
        setPreferredSize(new Dimension(w, h));
        setFocusable(true);
        setBackground(new Color(30, 30, 30));
        setupKeyBindings();

        lastNanos = System.nanoTime();
        timer = new Timer(16, e -> onTick());
        timer.start();
    }

    private void onTick() {
        long now = System.nanoTime();
        double dt = (now - lastNanos) / 1_000_000_000.0;
        lastNanos = now;
        if (dt > 0.05) {
            dt = 0.05;
        }

        state.update(dt,
                p1Up, p1Down, p1Left, p1Right,
                p2Up, p2Down, p2Left, p2Right,
                p1BombQueued, p2BombQueued);

        p1BombQueued = false;
        p2BombQueued = false;
        repaint();
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        bindHold(im, am, "W", () -> p1Up = true, () -> p1Up = false);
        bindHold(im, am, "S", () -> p1Down = true, () -> p1Down = false);
        bindAKey(im, am);
        bindHold(im, am, "D", () -> p1Right = true, () -> p1Right = false);

        bindHold(im, am, "UP", "UP", () -> p2Up = true, () -> p2Up = false);
        bindHold(im, am, "DOWN", "DOWN", () -> p2Down = true, () -> p2Down = false);
        bindHold(im, am, "LEFT", "LEFT", () -> p2Left = true, () -> p2Left = false);
        bindHold(im, am, "RIGHT", "RIGHT", () -> p2Right = true, () -> p2Right = false);

        bindPress(im, am, "SPACE", "SPACE", () -> {
            if (state.phase == GameState.Phase.PLAYING) {
                p1BombQueued = true;
            }
        });

        bindPress(im, am, "ENTER_KEY", "ENTER", () -> {
            if (state.phase == GameState.Phase.PLAYING) {
                p2BombQueued = true;
            } else {
                state.onEnterPressed();
            }
        });

        bindPress(im, am, "PAUSE", "P", state::togglePause);
        bindPress(im, am, "RESTART", "R", state::onRestartPressed);
        bindPress(im, am, "ESC", "ESCAPE", () -> System.exit(0));
    }

    private void bindAKey(InputMap im, ActionMap am) {
        im.put(KeyStroke.getKeyStroke("pressed A"), "A_PRESS");
        im.put(KeyStroke.getKeyStroke("released A"), "A_RELEASE");

        am.put("A_PRESS", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p1Left = true;
                aPressedAt = System.currentTimeMillis();
            }
        });

        am.put("A_RELEASE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p1Left = false;
                long held = System.currentTimeMillis() - aPressedAt;
                if (held < 150) {
                    state.toggleAI();
                }
            }
        });
    }

    private void bindPress(InputMap im, ActionMap am, String id, String key, Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), id);
        am.put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void bindHold(InputMap im, ActionMap am, String key, Runnable onPress, Runnable onRelease) {
        bindHold(im, am, key, key, onPress, onRelease);
    }

    private void bindHold(InputMap im, ActionMap am, String id, String key, Runnable onPress, Runnable onRelease) {
        String pressId = id + "_PRESS";
        String releaseId = id + "_RELEASE";

        im.put(KeyStroke.getKeyStroke("pressed " + key), pressId);
        im.put(KeyStroke.getKeyStroke("released " + key), releaseId);

        am.put(pressId, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPress.run();
            }
        });
        am.put(releaseId, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRelease.run();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawMap(g2);
        drawPowerUps(g2);
        drawBombs(g2);
        drawExplosions(g2);
        drawPlayers(g2);
        drawHud(g2);
        drawOverlay(g2);

        g2.dispose();
    }

    private void drawMap(Graphics2D g2) {
        int ts = GameState.TILE_SIZE;
        for (int y = 0; y < GameState.MAP_H; y++) {
            for (int x = 0; x < GameState.MAP_W; x++) {
                TileType t = state.map.get(x, y);
                Color c = switch (t) {
                    case EMPTY, POWERUP -> new Color(225, 230, 216);
                    case SOLID -> new Color(80, 80, 80);
                    case BREAKABLE -> new Color(150, 100, 60);
                };
                g2.setColor(c);
                g2.fillRect(x * ts, y * ts, ts, ts);
                g2.setColor(new Color(0, 0, 0, 28));
                g2.drawRect(x * ts, y * ts, ts, ts);
            }
        }
    }

    private void drawPowerUps(Graphics2D g2) {
        int ts = GameState.TILE_SIZE;
        g2.setFont(new Font("Dialog", Font.BOLD, 14));
        for (PowerUp p : state.powerUps) {
            int px = p.tx * ts;
            int py = p.ty * ts;
            g2.setColor(p.color());
            g2.fillOval(px + 8, py + 8, ts - 16, ts - 16);
            g2.setColor(Color.BLACK);
            g2.drawString(p.shortName(), px + 12, py + ts - 12);
        }
    }

    private void drawBombs(Graphics2D g2) {
        int ts = GameState.TILE_SIZE;
        for (Bomb b : state.bombs) {
            int px = b.tx * ts;
            int py = b.ty * ts;
            g2.setColor(Color.BLACK);
            g2.fillOval(px + 7, py + 7, ts - 14, ts - 14);
            g2.setColor(Color.WHITE);
            g2.fillOval(px + 14, py + 10, 6, 6);
        }
    }

    private void drawExplosions(Graphics2D g2) {
        int ts = GameState.TILE_SIZE;
        for (Explosion e : state.explosions) {
            for (Point p : e.cells) {
                int px = p.x * ts;
                int py = p.y * ts;
                g2.setColor(new Color(255, 140, 35, 210));
                g2.fillRect(px + 2, py + 2, ts - 4, ts - 4);
                g2.setColor(new Color(255, 228, 110, 210));
                g2.fillRect(px + 10, py + 10, ts - 20, ts - 20);
            }
        }
    }

    private void drawPlayers(Graphics2D g2) {
        int ts = GameState.TILE_SIZE;
        long ms = System.currentTimeMillis();
        for (Player p : state.allPlayers()) {
            if (!p.alive || !p.visibleForRender(ms)) {
                continue;
            }

            int cx = (int) Math.round(p.x * ts);
            int cy = (int) Math.round(p.y * ts);
            int r = ts / 2 - 6;
            g2.setColor(p.color);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(Color.BLACK);
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            g2.drawString(p.name, cx - 14, cy + 4);
        }
    }

    private void drawHud(Graphics2D g2) {
        int left = GameState.MAP_W * GameState.TILE_SIZE;
        int h = GameState.MAP_H * GameState.TILE_SIZE;

        g2.setColor(new Color(32, 37, 52));
        g2.fillRect(left, 0, GameState.HUD_W, h);
        g2.setColor(new Color(80, 100, 140));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(left, 0, left, h);

        g2.setColor(new Color(240, 245, 255));
        g2.setFont(new Font("Dialog", Font.BOLD, 20));
        g2.drawString("Arcade Bomber", left + 24, 34);

        g2.setFont(new Font("Dialog", Font.PLAIN, 15));
        g2.drawString("Round: " + state.round, left + 24, 66);
        g2.drawString("AI: " + (state.aiEnabled ? "ON" : "OFF"), left + 24, 88);
        g2.drawString("State: " + state.phase, left + 24, 110);

        drawPlayerHud(g2, state.p1, left + 24, 155);
        drawPlayerHud(g2, state.p2, left + 24, 260);
        drawPlayerHud(g2, state.bot, left + 24, 365);

        g2.setFont(new Font("Dialog", Font.PLAIN, 13));
        g2.setColor(new Color(205, 215, 232));
        g2.drawString("P1: WASD + SPACE", left + 24, h - 88);
        g2.drawString("P2: ARROW + ENTER", left + 24, h - 68);
        g2.drawString("P pause, tap A AI, R restart", left + 24, h - 48);
        g2.drawString("ENTER next/start, ESC exit", left + 24, h - 28);
    }

    private void drawPlayerHud(Graphics2D g2, Player p, int x, int y) {
        g2.setColor(p.color);
        g2.fillRect(x, y - 16, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, 15));
        g2.drawString(p.name + (p == state.bot && !state.aiEnabled ? " (OFF)" : ""), x + 24, y - 2);

        g2.setFont(new Font("Dialog", Font.PLAIN, 14));
        g2.drawString("Lives: " + p.lives + "   Wins: " + p.wins, x, y + 22);
        g2.drawString("Bomb: " + p.bombCapacity + "  Range: " + p.bombRange, x, y + 42);
        g2.drawString(String.format("Speed: %.1f", p.speed), x, y + 62);
    }

    private void drawOverlay(Graphics2D g2) {
        String line1 = null;
        String line2 = null;
        String line3 = null;

        if (state.phase == GameState.Phase.START) {
            line1 = "Arcade Bomber";
            line2 = "ENTER: Start, tap A: AI Toggle, ESC: Exit";
        } else if (state.phase == GameState.Phase.PAUSED) {
            line1 = "Paused";
            line2 = "P: Resume";
        } else if (state.phase == GameState.Phase.ROUND_OVER) {
            line1 = state.winnerText();
            line2 = "R: Restart Match / ENTER: Next Round / ESC: Exit";
        } else if (state.phase == GameState.Phase.GAME_OVER) {
            line1 = state.championText();
            line2 = "R: Restart Match / ENTER: New Match / ESC: Exit";
            line3 = "First to 3 wins";
        }

        if (line1 == null) {
            return;
        }

        int mapW = GameState.MAP_W * GameState.TILE_SIZE;
        int mapH = GameState.MAP_H * GameState.TILE_SIZE;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, mapW, mapH);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Dialog", Font.BOLD, 34));
        drawCentered(g2, line1, mapW / 2, mapH / 2 - 24);

        g2.setFont(new Font("Dialog", Font.PLAIN, 17));
        drawCentered(g2, line2, mapW / 2, mapH / 2 + 14);

        if (line3 != null) {
            g2.setFont(new Font("Dialog", Font.PLAIN, 15));
            drawCentered(g2, line3, mapW / 2, mapH / 2 + 40);
        }
    }

    private void drawCentered(Graphics2D g2, String text, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(text) / 2;
        g2.drawString(text, x, y);
    }
}
