package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class L27Game extends JFrame {
    public L27Game() {
        super("L27 Breakout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BreakoutPanel panel = new BreakoutPanel();
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> panel.startNewGame());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bottom.add(new JLabel("Left/Right: Move, Space: Start/Pause, R: Restart"));
        bottom.add(newGameButton);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocation(560, 140);
        setResizable(false);
        setVisible(true);

        panel.requestFocusInWindow();
    }

    static class BreakoutPanel extends JPanel {
        private static final int WIDTH = 720;
        private static final int HEIGHT = 520;

        private static final int PADDLE_W = 120;
        private static final int PADDLE_H = 14;
        private static final int PADDLE_Y = HEIGHT - 42;

        private static final int BALL_R = 10;
        private static final double BALL_INIT_SPEED = 5.2;
        private static final double BALL_MAX_SPEED = 9.8;
        private static final double BALL_TICK_ACCEL = 1.00032;

        private static final int BRICK_ROWS = 10;
        private static final int BRICK_COLS = 14;
        private static final int BRICK_W = 44;
        private static final int BRICK_H = 16;
        private static final int BRICK_GAP = 4;
        private static final int BRICK_TOP = 72;

        private static final int EMPTY = -1;

        private final int[][] bricks = new int[BRICK_ROWS][BRICK_COLS];
        private final Color[] palette = {
                new Color(236, 95, 95),
                new Color(255, 173, 79),
                new Color(255, 215, 96),
                new Color(125, 212, 125),
                new Color(92, 173, 245),
                new Color(179, 136, 255)
        };

        private final Timer timer;
        private final Random random = new Random();

        private int paddleX;

        private double ballX;
        private double ballY;
        private double ballDx;
        private double ballDy;
        private int ballColor;

        private int score;
        private int lives;

        private boolean leftPressed;
        private boolean rightPressed;

        private boolean running;
        private boolean waitingServe;
        private boolean gameOver;
        private boolean cleared;

        BreakoutPanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(17, 22, 30));
            setFocusable(true);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        leftPressed = true;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        rightPressed = true;
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        onSpace();
                    } else if (e.getKeyCode() == KeyEvent.VK_R) {
                        startNewGame();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        leftPressed = false;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        rightPressed = false;
                    }
                }
            });

            timer = new Timer(16, this::onTick);
            timer.start();

            startNewGame();
        }

        void startNewGame() {
            for (int r = 0; r < BRICK_ROWS; r++) {
                for (int c = 0; c < BRICK_COLS; c++) {
                    bricks[r][c] = random.nextInt(palette.length);
                }
            }

            score = 0;
            lives = 3;
            gameOver = false;
            cleared = false;
            running = false;
            waitingServe = true;

            paddleX = (WIDTH - PADDLE_W) / 2;
            ballColor = random.nextInt(palette.length);
            resetBallOnPaddle();
            repaint();
            requestFocusInWindow();
        }

        private void onSpace() {
            if (gameOver || cleared) {
                return;
            }

            if (waitingServe) {
                launchBall();
                running = true;
                waitingServe = false;
                return;
            }

            running = !running;
        }

        private void launchBall() {
            double angle = Math.toRadians(55 + random.nextInt(70));
            if (random.nextBoolean()) {
                angle = Math.PI - angle;
            }
            ballDx = Math.cos(angle) * BALL_INIT_SPEED;
            ballDy = -Math.abs(Math.sin(angle) * BALL_INIT_SPEED);
        }

        private void resetBallOnPaddle() {
            ballX = paddleX + PADDLE_W / 2.0;
            ballY = PADDLE_Y - BALL_R - 2;
            ballDx = 0;
            ballDy = 0;
        }

        private int randomDifferentColor(int current) {
            int next = current;
            while (next == current) {
                next = random.nextInt(palette.length);
            }
            return next;
        }

        private void onTick(ActionEvent e) {
            updatePaddle();

            if (!running) {
                if (waitingServe) {
                    ballX = paddleX + PADDLE_W / 2.0;
                }
                repaint();
                return;
            }

            updateBall();
            repaint();
        }

        private void updatePaddle() {
            int speed = 13;
            if (leftPressed) {
                paddleX -= speed;
            }
            if (rightPressed) {
                paddleX += speed;
            }
            paddleX = Math.max(0, Math.min(WIDTH - PADDLE_W, paddleX));
        }

        private void updateBall() {
            double prevX = ballX;
            double prevY = ballY;

            ballX += ballDx;
            ballY += ballDy;
            accelerateBall();

            if (ballX - BALL_R <= 0) {
                ballX = BALL_R;
                ballDx = Math.abs(ballDx);
            } else if (ballX + BALL_R >= WIDTH) {
                ballX = WIDTH - BALL_R;
                ballDx = -Math.abs(ballDx);
            }

            if (ballY - BALL_R <= 0) {
                ballY = BALL_R;
                ballDy = Math.abs(ballDy);
            }

            Rectangle paddleRect = new Rectangle(paddleX, PADDLE_Y, PADDLE_W, PADDLE_H);
            Rectangle ballRect = ballBounds();

            if (ballDy > 0 && ballRect.intersects(paddleRect)) {
                ballY = PADDLE_Y - BALL_R;
                double speed = ballSpeed();
                double hit = (ballX - (paddleX + PADDLE_W / 2.0)) / (PADDLE_W / 2.0);
                ballDx = hit * speed * 0.95;
                double dyAbs = Math.sqrt(Math.max(4.5, speed * speed - ballDx * ballDx));
                ballDy = -dyAbs;
                accelerateBall();
            }

            if (handleBrickCollision(prevX, prevY)) {
            }

            if (ballY - BALL_R > HEIGHT) {
                lives--;
                if (lives <= 0) {
                    running = false;
                    waitingServe = false;
                    gameOver = true;
                } else {
                    running = false;
                    waitingServe = true;
                    ballColor = random.nextInt(palette.length);
                    resetBallOnPaddle();
                }
            }
        }

        private double ballSpeed() {
            return Math.sqrt(ballDx * ballDx + ballDy * ballDy);
        }

        private void accelerateBall() {
            double speed = ballSpeed();
            if (speed < 0.0001 || speed >= BALL_MAX_SPEED) {
                return;
            }
            double next = Math.min(BALL_MAX_SPEED, speed * BALL_TICK_ACCEL);
            double factor = next / speed;
            ballDx *= factor;
            ballDy *= factor;
        }

        private boolean handleBrickCollision(double prevX, double prevY) {
            Rectangle ballRect = ballBounds();

            for (int r = 0; r < BRICK_ROWS; r++) {
                for (int c = 0; c < BRICK_COLS; c++) {
                    if (bricks[r][c] == EMPTY) {
                        continue;
                    }

                    int bx = brickX(c);
                    int by = brickY(r);
                    Rectangle brickRect = new Rectangle(bx, by, BRICK_W, BRICK_H);

                    if (!ballRect.intersects(brickRect)) {
                        continue;
                    }

                    double prevBallLeft = prevX - BALL_R;
                    double prevBallRight = prevX + BALL_R;
                    double prevBallTop = prevY - BALL_R;
                    double prevBallBottom = prevY + BALL_R;

                    boolean hitFromLeft = prevBallRight <= bx;
                    boolean hitFromRight = prevBallLeft >= bx + BRICK_W;
                    boolean hitFromTop = prevBallBottom <= by;
                    boolean hitFromBottom = prevBallTop >= by + BRICK_H;

                    if (hitFromLeft || hitFromRight) {
                        ballDx = -ballDx;
                    } else if (hitFromTop || hitFromBottom) {
                        ballDy = -ballDy;
                    } else {
                        ballDy = -ballDy;
                    }

                    if (bricks[r][c] == ballColor) {
                        bricks[r][c] = EMPTY;
                        score += 120;
                        ballColor = randomDifferentColor(ballColor);

                        if (allBricksCleared()) {
                            running = false;
                            waitingServe = false;
                            cleared = true;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean allBricksCleared() {
            for (int r = 0; r < BRICK_ROWS; r++) {
                for (int c = 0; c < BRICK_COLS; c++) {
                    if (bricks[r][c] != EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }

        private int brickX(int col) {
            int totalWidth = BRICK_COLS * BRICK_W + (BRICK_COLS - 1) * BRICK_GAP;
            int startX = (WIDTH - totalWidth) / 2;
            return startX + col * (BRICK_W + BRICK_GAP);
        }

        private int brickY(int row) {
            return BRICK_TOP + row * (BRICK_H + BRICK_GAP);
        }

        private Rectangle ballBounds() {
            return new Rectangle((int) Math.round(ballX - BALL_R), (int) Math.round(ballY - BALL_R), BALL_R * 2, BALL_R * 2);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawBackground(g2);
            drawHud(g2);
            drawBricks(g2);
            drawPaddle(g2);
            drawBall(g2);
            drawOverlay(g2);
        }

        private void drawBackground(Graphics2D g2) {
            GradientPaint gp = new GradientPaint(0, 0, new Color(19, 26, 38), 0, HEIGHT, new Color(10, 14, 20));
            g2.setPaint(gp);
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }

        private void drawHud(Graphics2D g2) {
            g2.setColor(new Color(230, 237, 248));
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2.drawString("L27 Breakout", 20, 32);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g2.drawString("Score: " + score, 20, 58);
            g2.drawString("Lives: " + lives, WIDTH - 110, 58);

            g2.drawString("Ball:", WIDTH - 245, 58);
            g2.setColor(palette[ballColor]);
            g2.fillOval(WIDTH - 185, 43, 20, 20);
            g2.setColor(new Color(255, 255, 255, 180));
            g2.drawOval(WIDTH - 185, 43, 20, 20);
        }

        private void drawBricks(Graphics2D g2) {
            for (int r = 0; r < BRICK_ROWS; r++) {
                for (int c = 0; c < BRICK_COLS; c++) {
                    int colorIndex = bricks[r][c];
                    if (colorIndex == EMPTY) {
                        continue;
                    }

                    int x = brickX(c);
                    int y = brickY(r);

                    g2.setColor(palette[colorIndex]);
                    g2.fillRoundRect(x, y, BRICK_W, BRICK_H, 8, 8);
                    g2.setColor(new Color(255, 255, 255, 120));
                    g2.drawRoundRect(x, y, BRICK_W, BRICK_H, 8, 8);
                }
            }
        }

        private void drawPaddle(Graphics2D g2) {
            g2.setColor(new Color(241, 244, 250));
            g2.fillRoundRect(paddleX, PADDLE_Y, PADDLE_W, PADDLE_H, 10, 10);
        }

        private void drawBall(Graphics2D g2) {
            g2.setColor(palette[ballColor]);
            g2.fillOval((int) Math.round(ballX - BALL_R), (int) Math.round(ballY - BALL_R), BALL_R * 2, BALL_R * 2);
            g2.setColor(new Color(255, 255, 255, 170));
            g2.drawOval((int) Math.round(ballX - BALL_R), (int) Math.round(ballY - BALL_R), BALL_R * 2, BALL_R * 2);
        }

        private void drawOverlay(Graphics2D g2) {
            if (gameOver) {
                drawMessage(g2, "Game Over - Press R or New Game");
                return;
            }
            if (cleared) {
                drawMessage(g2, "Stage Clear - Press R or New Game");
                return;
            }
            if (waitingServe) {
                drawMessage(g2, "Press Space to Launch");
                return;
            }
            if (!running) {
                drawMessage(g2, "Paused");
            }
        }

        private void drawMessage(Graphics2D g2, String text) {
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(170, HEIGHT / 2 - 35, 380, 70, 16, 16);
            g2.setColor(new Color(255, 255, 255));
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            FontMetrics fm = g2.getFontMetrics();
            int x = (WIDTH - fm.stringWidth(text)) / 2;
            g2.drawString(text, x, HEIGHT / 2 + 8);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(L27Game::new);
    }
}
