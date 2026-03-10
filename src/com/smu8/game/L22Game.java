package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class L22Game extends JFrame {
    static class Player {
        int x;
        int y;
        int size;
        int speed = 6;
        Image image;

        public Player(int x, int y, int size, Image image) {
            this.x = x;
            this.y = y;
            this.size = 80;
            this.image = image;
        }

        public void update(boolean[] keys, int panelWidth, int panelHeight) {
            if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
                x -= speed;
            }
            if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
                x += speed;
            }

            if (x < 0) {
                x = 0;
            }
            if (x > panelWidth - size) {
                x = panelWidth - size;
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, size, size);
        }

        public void draw(Graphics g) {
            if (image != null) {
                g.drawImage(image, x, y, size, size, null);
                return;
            }
            g.setColor(new Color(60, 120, 200));
            g.fillOval(x, y, size, size);
        }
    }

    static class Money {
        double x;
        double y;
        int width;
        int height;
        int value;
        double speed;
        Image image;

        public Money(double x, double y, int width, int height, int value, double speed, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.value = value;
            this.speed = speed;
            this.image = image;
        }

        public void update() {
            y += speed;
        }

        public Rectangle getBounds() {
            return new Rectangle((int) x, (int) y, width, height);
        }

        public void draw(Graphics g) {
            if (image != null) {
                g.drawImage(image, (int) x, (int) y, width, height, null);
                return;
            }
            g.setColor(value == 100 ? new Color(90, 180, 90) : new Color(220, 180, 40));
            if (value == 100) {
                g.fillRoundRect((int) x, (int) y, width, height, 8, 8);
                g.setColor(Color.BLACK);
                g.drawString("100", (int) x + 6, (int) y + height - 6);
            } else {
                g.fillOval((int) x, (int) y, width, height);
                g.setColor(Color.BLACK);
                g.drawString("50", (int) x + 4, (int) y + height - 4);
            }
        }
    }

    class GamePanel extends JPanel {
        Player player;
        List<Money> moneyList = new ArrayList<>();
        Timer timer;
        Random random = new Random();
        boolean[] keys = new boolean[256];
        int score = 0;

        int playerSize = 50;
        int billWidth = 40;
        int billHeight = 22;
        int coinSize = 22;

        Image playerImage;
        Image billImage;
        Image coinImage;

        public GamePanel() {
            setBackground(new Color(220, 235, 200));
            setFocusable(true);
            loadImages();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code >= 0 && code < keys.length) {
                        keys[code] = true;
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code >= 0 && code < keys.length) {
                        keys[code] = false;
                    }
                }
            });

            initGame();
            timer = new Timer(16, (e) -> onTick());
            timer.start();
        }

        private void loadImages() {
            playerImage = loadScaled("/com/smu8/game/player.png", playerSize, playerSize);
            billImage = loadScaled("/com/smu8/game/bill.png", billWidth, billHeight);
            coinImage = loadScaled("/com/smu8/game/coin.png", coinSize, coinSize);
        }

        private Image loadScaled(String path, int width, int height) {
            URL url = L22Game.class.getResource(path);
            if (url == null) {
                return null;
            }
            ImageIcon icon = new ImageIcon(url);
            return icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }

        private void initGame() {
            int startX = 240;
            int startY = 400;
            player = new Player(startX, startY, playerSize, playerImage);
            moneyList.clear();
            score = 0;
            for (int i = 0; i < 12; i++) {
                moneyList.add(createMoney());
            }
            repaint();
            requestFocusInWindow();
        }

        private Money createMoney() {
            int width;
            int height;
            int value;
            Image image;
            if (random.nextBoolean()) {
                width = billWidth;
                height = billHeight;
                value = 100;
                image = billImage;
            } else {
                width = coinSize;
                height = coinSize;
                value = 50;
                image = coinImage;
            }

            int maxX = Math.max(1, getWidth() - width);
            double x = random.nextInt(maxX);
            double y = -random.nextInt(300) - height;
            double speed = 2.0 + random.nextDouble() * 2.2;
            return new Money(x, y, width, height, value, speed, image);
        }

        private void resetMoney(Money money) {
            int maxX = Math.max(1, getWidth() - money.width);
            money.x = random.nextInt(maxX);
            money.y = -random.nextInt(300) - money.height;
            money.speed = 2.0 + random.nextDouble() * 2.2;
        }

        private void onTick() {
            player.update(keys, getWidth(), getHeight());

            Rectangle playerBounds = player.getBounds();
            int height = getHeight();
            for (Money money : moneyList) {
                money.update();
                if (money.y > height + money.height) {
                    resetMoney(money);
                    continue;
                }
                if (playerBounds.intersects(money.getBounds())) {
                    score += money.value;
                    resetMoney(money);
                }
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            player.draw(g);
            for (Money money : moneyList) {
                money.draw(g);
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        }
    }

    public L22Game() {
        super("Money Eat Game");
        GamePanel canvas = new GamePanel();
        setContentPane(canvas);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(L22Game::new);
    }
}




