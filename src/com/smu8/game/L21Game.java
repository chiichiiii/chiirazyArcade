package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class L21Game extends JFrame {
    static class Player {
        int x;
        int y;
        int size = 50;
        int speed = 6;
        Color color = new Color(0, 0, 0);

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void update(boolean[] keys, int panelWidth, int panelHeight) {
            if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
                y -= speed;
            }
            if (keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN]) {
                y += speed;
            }
            if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
                x -= speed;
            }
            if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
                x += speed;
            }

            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x > panelWidth - size) {
                x = panelWidth - size;
            }
            if (y > panelHeight - size) {
                y = panelHeight - size;
            }
        }

        public Rectangle getBounds() {
            int hitSize = Math.max(14, (int) (size * 0.45));
            int offset = (size - hitSize) / 2;
            return new Rectangle(x + offset, y + offset, hitSize, hitSize);
        }

        public void draw(Graphics g, Image image) {
            if (image != null) {
                g.drawImage(image, x, y, size, size, null);
                return;
            }
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }
    }

    static class Enemy {
        double x;
        double y;
        int size;
        double vx;
        double vy;
        Color color;

        public Enemy(double x, double y, int size, double vx, double vy, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
        }

        public void update() {
            x += vx;
            y += vy;
        }

        public Rectangle getBounds() {
            int hitSize = Math.max(12, (int) (size * 0.60));
            int offset = (size - hitSize) / 2;
            return new Rectangle((int) x + offset, (int) y + offset, hitSize, hitSize);
        }

        public void draw(Graphics g, Image image) {
            if (image != null) {
                g.drawImage(image, (int) x, (int) y, size, size, null);
                return;
            }
            g.setColor(color);
            g.fillOval((int) x, (int) y, size, size);
        }
    }

    enum ItemType {
        FIRE,
        PUSH,
        SHIELD
    }

    static class Item {
        int x;
        int y;
        int size;
        ItemType type;
        Color color;

        public Item(int x, int y, int size, ItemType type, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.type = type;
            this.color = color;
        }

        public Rectangle getBounds() {
            int hitSize = Math.max(12, (int) (size * 0.50));
            int offset = (size - hitSize) / 2;
            return new Rectangle(x + offset, y + offset, hitSize, hitSize);
        }

        public void draw(Graphics g, Image image) {
            if (image != null) {
                g.drawImage(image, x, y, size, size, null);
                return;
            }
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }
    }

    class GamePanel extends JPanel {
        Player player;
        List<Enemy> enemies = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        Timer timer;
        Random random = new Random();
        boolean[] keys = new boolean[256];
        int spawnCounter = 0;
        int score = 0;
        boolean gameOver = false;
        int roachSize = 30;
        int itemSize = 50;
        int shieldItemSize = 40;
        int fireRadius = 120;
        int pushRadius = 140;
        double pushPower = 4.5;
        int invincibleTicks = 0;
        int invincibleDuration = 180;
        Image roachImage;
        Image backgroundImage;
        Image playerImage;
        Image playerLeftImage;
        Image playerRightImage;
        int playerFacingDirection = 0;
        Image fireItemImage;
        Image pushItemImage;
        Image shieldItemImage;

        public GamePanel() {
            setBackground(new Color(200, 200, 200));
            setFocusable(true);
            loadRoachImage();
            loadBackgroundImage();
            loadPlayerImage();
            loadItemImages();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code >= 0 && code < keys.length) {
                        keys[code] = true;
                    }
                    if (gameOver && code == KeyEvent.VK_R) {
                        initGame();
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

        private void loadRoachImage() {
            URL url = L21Game.class.getResource("/com/smu8/game/roach.png");
            if (url == null) {
                roachImage = null;
                return;
            }
            ImageIcon icon = new ImageIcon(url);
            roachImage = icon.getImage().getScaledInstance(roachSize, roachSize, Image.SCALE_SMOOTH);
        }

        private void loadBackgroundImage() {
            backgroundImage = loadImage("/com/smu8/game/floor.png");
        }

        private void loadPlayerImage() {
            playerImage = loadImageFirstAvailable(
                    "/com/smu8/game/player_idle.png",
                    "/com/smu8/game/player.png",
                    "/com/smu8/game/player_custom.png"
            );
            playerLeftImage = loadImage("/com/smu8/game/player_left.png");
            playerRightImage = loadImage("/com/smu8/game/player_right.png");
        }

        private void loadItemImages() {
            fireItemImage = loadImage("/com/smu8/game/item_fire.png");
            pushItemImage = loadImage("/com/smu8/game/item_push.png");
            shieldItemImage = loadImage("/com/smu8/game/item_shield.png");
        }

        private Image loadImage(String path) {
            URL url = L21Game.class.getResource(path);
            if (url == null) {
                return null;
            }
            return new ImageIcon(url).getImage();
        }

        private Image loadImageFirstAvailable(String... paths) {
            for (String path : paths) {
                Image image = loadImage(path);
                if (image != null) {
                    return image;
                }
            }
            return null;
        }

        private int safeWidth() {
            return Math.max(1, getWidth() == 0 ? 500 : getWidth());
        }

        private int safeHeight() {
            return Math.max(1, getHeight() == 0 ? 500 : getHeight());
        }

        private void initGame() {
            int startX = 240;
            int startY = 240;
            player = new Player(startX, startY);
            enemies.clear();
            items.clear();
            spawnCounter = 0;
            score = 0;
            gameOver = false;
            invincibleTicks = 0;
            playerFacingDirection = 0;
            for (int i = 0; i < 3; i++) {
                items.add(spawnItem());
            }
            repaint();
            requestFocusInWindow();
        }

        private void spawnEnemy() {
            int size = roachSize;
            int width = getWidth();
            int height = getHeight();

            int side = random.nextInt(4);
            double x;
            double y;
            switch (side) {
                case 0:
                    x = random.nextInt(Math.max(1, width - size));
                    y = -size;
                    break;
                case 1:
                    x = random.nextInt(Math.max(1, width - size));
                    y = height + size;
                    break;
                case 2:
                    x = -size;
                    y = random.nextInt(Math.max(1, height - size));
                    break;
                default:
                    x = width + size;
                    y = random.nextInt(Math.max(1, height - size));
                    break;
            }

            double targetX = random.nextInt(Math.max(1, width));
            double targetY = random.nextInt(Math.max(1, height));
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance == 0) {
                distance = 1;
            }

            double baseSpeed = 1.6 + random.nextDouble() * 2.2 + score / 900.0;
            double vx = dx / distance * baseSpeed;
            double vy = dy / distance * baseSpeed;
            Color color = new Color(220, 50, 50);
            enemies.add(new Enemy(x, y, size, vx, vy, color));
        }

        private Item spawnItem() {
            int width = safeWidth();
            int height = safeHeight();
            int maxItemSize = Math.max(itemSize, shieldItemSize);
            int maxX = Math.max(1, width - maxItemSize);
            int maxY = Math.max(1, height - maxItemSize);
            int x = random.nextInt(maxX);
            int y = random.nextInt(maxY);
            ItemType type;
            Color color;
            int size;
            int pick = random.nextInt(3);
            if (pick == 0) {
                type = ItemType.FIRE;
                color = new Color(220, 40, 40);
                size = itemSize;
            } else if (pick == 1) {
                type = ItemType.PUSH;
                color = new Color(40, 90, 220);
                size = itemSize;
            } else {
                type = ItemType.SHIELD;
                color = new Color(230, 210, 40);
                size = shieldItemSize;
            }
            return new Item(x, y, size, type, color);
        }

        private void applyFireItem() {
            int centerX = player.x + player.size / 2;
            int centerY = player.y + player.size / 2;
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                double dx = (enemy.x + enemy.size / 2.0) - centerX;
                double dy = (enemy.y + enemy.size / 2.0) - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= fireRadius) {
                    enemies.remove(i);
                }
            }
        }

        private void applyPushItem() {
            int centerX = player.x + player.size / 2;
            int centerY = player.y + player.size / 2;
            for (Enemy enemy : enemies) {
                double dx = (enemy.x + enemy.size / 2.0) - centerX;
                double dy = (enemy.y + enemy.size / 2.0) - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= pushRadius) {
                    if (distance == 0) {
                        distance = 1;
                    }
                    enemy.vx += dx / distance * pushPower;
                    enemy.vy += dy / distance * pushPower;
                }
            }
        }

        private void applyShieldItem() {
            invincibleTicks = invincibleDuration;
        }

        private Image getItemImage(ItemType type) {
            if (type == ItemType.FIRE) {
                return fireItemImage;
            }
            if (type == ItemType.PUSH) {
                return pushItemImage;
            }
            return shieldItemImage;
        }
        private Image getCurrentPlayerImage() {
            if (playerFacingDirection < 0 && playerLeftImage != null) {
                return playerLeftImage;
            }
            if (playerFacingDirection > 0 && playerRightImage != null) {
                return playerRightImage;
            }
            return playerImage;
        }


        private void onTick() {
            if (gameOver) {
                repaint();
                return;
            }

            if (invincibleTicks > 0) {
                invincibleTicks--;
            }

            player.update(keys, getWidth(), getHeight());
            if (keys[KeyEvent.VK_A] && !keys[KeyEvent.VK_D]) {
                playerFacingDirection = -1;
            } else if (keys[KeyEvent.VK_D] && !keys[KeyEvent.VK_A]) {
                playerFacingDirection = 1;
            } else {
                playerFacingDirection = 0;
            }
            spawnCounter++;
            score++;

            if (spawnCounter % 8 == 0) {
                for (int i = 0; i < 2; i++) {
                    spawnEnemy();
                }
            }

            int width = getWidth();
            int height = getHeight();
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                enemy.update();
                if (enemy.x < -enemy.size * 2
                        || enemy.x > width + enemy.size * 2
                        || enemy.y < -enemy.size * 2
                        || enemy.y > height + enemy.size * 2) {
                    enemies.remove(i);
                }
            }

            Rectangle playerBounds = player.getBounds();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (playerBounds.intersects(item.getBounds())) {
                    if (item.type == ItemType.FIRE) {
                        applyFireItem();
                    } else if (item.type == ItemType.PUSH) {
                        applyPushItem();
                    } else {
                        applyShieldItem();
                    }
                    items.set(i, spawnItem());
                }
            }

            if (invincibleTicks == 0) {
                for (Enemy enemy : enemies) {
                    if (playerBounds.intersects(enemy.getBounds())) {
                        gameOver = true;
                        break;
                    }
                }
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            }
            player.draw(g, getCurrentPlayerImage());
            for (Item item : items) {
                item.draw(g, getItemImage(item.type));
            }
            for (Enemy enemy : enemies) {
                enemy.draw(g, roachImage);
            }

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);

            if (invincibleTicks > 0) {
                g.setColor(new Color(230, 210, 40));
                g.drawString("INVINCIBLE", 10, 40);
            }

            if (gameOver) {
                g.setFont(new Font("SansSerif", Font.BOLD, 28));
                g.setColor(Color.BLACK);
                g.drawString("GAME OVER", 150, 220);
                g.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g.drawString("Press R to restart", 170, 250);
            }
        }
    }

    public L21Game() {
        super("Dodge Game");
        GamePanel canvas = new GamePanel();
        setContentPane(canvas);
        setBounds(550, 250, 500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(L21Game::new);
    }
}