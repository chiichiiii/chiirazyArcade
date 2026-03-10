package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/*
 * 메인 프레임
 * 단순히 게임 패널을 화면에 올려주는 역할만 한다.
 */
public class E20ShuttingGame extends JFrame {

    public E20ShuttingGame() {
        setTitle("Infinite Space Shooter");   // 창 제목
        setSize(800, 600);                    // 창 크기
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);          // 화면 중앙 배치
        setContentPane(new GamePanel());      // 게임 패널 장착
        setVisible(true);
    }

    public static void main(String[] args) {
        // Swing은 반드시 EDT에서 실행
        SwingUtilities.invokeLater(E20ShuttingGame::new);
    }
}

/*
 * 실제 게임이 동작하는 패널
 * - 게임 상태 관리
 * - 입력 처리
 * - 게임 루프
 * - 충돌 처리
 * - 화면 그리기
 */
class GamePanel extends JPanel {

    private Player player;               // 플레이어
    private List<Enemy> enemies;         // 적 목록
    private List<Missile> missiles;      // 미사일 목록
    private Random random = new Random();

    private boolean gameOver = false;    // 게임 종료 상태
    private int gameOverTimer = 0;       // 사망 후 재시작 카운트

    private int spawnTimer = 0;          // 적 생성 타이머
    private int score = 0;               // 점수

    public GamePanel() {

        setBackground(Color.BLACK);
        setFocusable(true); // 키 이벤트 받기 위해 필요

        initGame(); // 게임 초기화

        /*
         * 키 입력 처리
         * - 방향키 / WASD 이동
         * - 스페이스 → 미사일 발사
         */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (gameOver) return; // 게임 오버 상태면 입력 무시

                player.keyPressed(e.getKeyCode());

                // 스페이스바 → 미사일 생성
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    missiles.add(new Missile(player.x + 12, player.y));
                }
            }
        });

        /*
         * 게임 루프 (약 60FPS)
         * 16ms ≈ 1초 / 60
         */
        Timer timer = new Timer(16, e -> {

            if (!gameOver) {
                updateGame();      // 위치 이동 및 생성
                checkCollision();  // 충돌 처리
            } else {
                // 사망 후 일정 시간 뒤 재시작
                gameOverTimer++;
                if (gameOverTimer > 120) { // 약 2초
                    initGame();
                }
            }

            repaint(); // 화면 다시 그리기
        });

        timer.start();
    }

    /*
     * 게임 상태 초기화
     * 재시작 시에도 호출된다.
     */
    private void initGame() {
        player = new Player(380, 500);
        enemies = new ArrayList<>();
        missiles = new ArrayList<>();
        score = 0;
        gameOver = false;
        gameOverTimer = 0;
        spawnTimer = 0;
    }

    /*
     * 게임 상태 업데이트
     * - 플레이어 이동
     * - 미사일 이동
     * - 적 이동
     * - 적 생성
     */
    private void updateGame() {

        // 플레이어 이동
        player.move(getWidth(), getHeight());

        // ----------------------
        // 미사일 이동 및 제거
        // ----------------------
        Iterator<Missile> mIter = missiles.iterator();
        while (mIter.hasNext()) {
            Missile m = mIter.next();
            m.move();

            // 화면 밖으로 나가면 제거
            if (m.y < 0) {
                mIter.remove();
            }
        }

        // ----------------------
        // 적 이동 (플레이어 추적)
        // ----------------------
        for (Enemy enemy : enemies) {
            enemy.chase(player);
        }

        // ----------------------
        // 적 무한 생성
        // ----------------------
        spawnTimer++;

        // 약 1초마다 적 생성
        if (spawnTimer > 60) {
            enemies.add(new Enemy(random.nextInt(750), 0));
            spawnTimer = 0;
        }
    }

    /*
     * 충돌 검사
     * - 미사일 ↔ 적
     * - 플레이어 ↔ 적
     */
    private void checkCollision() {

        // ----------------------
        // 미사일과 적 충돌
        // ----------------------
        Iterator<Missile> mIter = missiles.iterator();
        while (mIter.hasNext()) {

            Missile m = mIter.next();

            Iterator<Enemy> eIter = enemies.iterator();
            while (eIter.hasNext()) {

                Enemy enemy = eIter.next();

                // 두 객체 사이 거리 계산
                double dx = m.x - enemy.x;
                double dy = m.y - enemy.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // 일정 거리 이하이면 충돌
                if (distance < 20) {
                    mIter.remove();   // 미사일 제거
                    eIter.remove();   // 적 제거
                    score += 10;      // 점수 증가
                    break;
                }
            }
        }

        // ----------------------
        // 플레이어와 적 충돌
        // ----------------------
        for (Enemy enemy : enemies) {

            double dx = player.x - enemy.x;
            double dy = player.y - enemy.y;

            if (Math.sqrt(dx * dx + dy * dy) < 25) {
                gameOver = true; // 게임 종료 상태로 전환
            }
        }
    }

    /*
     * 화면 그리기
     * 이 메서드는 Swing이 자동 호출한다.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 플레이어 그리기
        player.draw(g);

        // 미사일 그리기
        for (Missile m : missiles) {
            m.draw(g);
        }

        // 적 그리기
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // 점수 출력
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);

        // 게임 오버 메시지 출력
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("YOU DIED", 250, 300);
        }
    }
}

/*
 * 플레이어 클래스
 * - 입력 방향 저장
 * - 실제 이동 처리
 */
class Player {

    int x, y;            // 위치
    int speed = 20;      // 이동 속도

    int dx = 0;          // x 이동량
    int dy = 0;          // y 이동량

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 키 입력 시 이동 방향 설정
    public void keyPressed(int keyCode) {

        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> dy = -speed;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> dy = speed;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> dx = speed;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> dx = -speed;
        }
    }

    // 실제 이동 처리 + 경계 제한
    public void move(int width, int height) {

        x += dx;
        y += dy;

        // 좌우 경계
        if (x < 0) x = 0;
        if (x > width - 30) x = width - 30;

        // 상하 경계
        if (y < 0) y = 0;
        if (y > height - 20) y = height - 20;

        // 다음 프레임 대비 초기화
        dx = 0;
        dy = 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, 30, 20);
    }
}

/*
 * 미사일 클래스
 * - 위쪽 방향으로 이동
 */
class Missile {

    double x, y;
    double speed = 8;

    public Missile(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        y -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect((int)x, (int)y, 5, 10);
    }
}

/*
 * 적 클래스
 * - 플레이어를 향해 단위벡터로 추적
 */
class Enemy {

    double x, y;
    double speed = 1.5;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 플레이어 방향으로 이동
    public void chase(Player player) {

        double dx = player.x - x;
        double dy = player.y - y;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance != 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int)x, (int)y, 25, 25);
    }
}