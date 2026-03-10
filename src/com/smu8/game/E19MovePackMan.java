package com.smu8.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class E19MovePackMan extends JFrame {
    private int width=500;
    private int height=500;
    private Color backColor=new Color(0,0,0);
    public E19MovePackMan(){
        super("팩맨");
        this.setContentPane(new MyPanel());
        setBounds(0,0,width,height+30);
        setVisible(true);
        setDefaultCloseOperation(3);
    }

    class MyPanel extends JPanel{
        int x=0;
        int y=0;
        int width=100;
        int height=100;
        int startAngle=20;
        int arcAngle=320;
        Timer moveTimer;
        Timer angleTimer;
        Color bgColor=new Color(255,205,0);
        enum Status{
            STOP,TOP,BOTTOM,LEFT,RIGHT
        }
        Status status= Status.STOP;

        public MyPanel(){
            setFocusable(true);
            requestFocusInWindow();
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    System.out.println(e.getKeyCode());
                    System.out.println(e.getKeyChar());
                    status=switch (e.getKeyCode()){
                        case KeyEvent.VK_W -> Status.TOP;
                        case KeyEvent.VK_A -> Status.LEFT;
                        case KeyEvent.VK_D -> Status.RIGHT;
                        case KeyEvent.VK_S-> Status.BOTTOM;
                        case KeyEvent.VK_SPACE -> Status.STOP;
                        default -> Status.STOP;
                    };
                }
            });

            moveTimer =new Timer(10,(e)->move());
            moveTimer.start();
            angleTimer =new Timer(200,(e)->turn());
            angleTimer.start();
        }
        void move(){
            switch (status){
                case status.TOP: if(y>0) y-=1; break;
                case status.BOTTOM: if(y+height< E19MovePackMan.this.height) y+=1; break;
                case status.LEFT: if(x>0) x-=1; break;
                case status.RIGHT: if(x+width< E19MovePackMan.this.width ) x+=1; break;
            }
            this.repaint();

        }
        void turn(){
            switch (status){
                case status.RIGHT: startAngle=0+30; break;
                case status.TOP: startAngle=90+30; break;
                case status.LEFT: startAngle=180+30; break;
                case status.BOTTOM: startAngle=270+30; break;
            }
            arcAngle=(arcAngle!=360)?360:300;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            this.setBackground(Color.white);
            g.setColor(new Color(255,205,0));
            g.fillArc(x,y,width,height,startAngle,arcAngle);
            g.setColor(Color.black);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            E19MovePackMan packMan=new E19MovePackMan();
        });
    }
}