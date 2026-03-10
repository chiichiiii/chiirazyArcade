package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;

public class L52MoveCanvas extends JFrame {
    public L52MoveCanvas(){
        super("움직이는 원");
        this.setContentPane(new MyPanel());
        this.setBounds(-1980,0,500,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    class MyPanel extends JPanel{
        int x=0;
        Timer moveTimer;
        public MyPanel(){
            this.setBackground(new Color(0,0,0));
            moveTimer=new Timer(10,(e)->{
                if(x<=470-50){
                    ++x;
                    repaint();
                }
            });
            moveTimer.start();
//            new Thread(()->{
//                while (true) {
//                    try {Thread.sleep(10);} catch (InterruptedException e) {}
//                    ++x;
//                    SwingUtilities.invokeLater(() -> {
//                        this.repaint();
//                    });
//                }
//            }).start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //repain로 paintComponent 호출시 기존에 그림(그래픽)을 지우고 다시 그림
            g.setColor(new Color(255,200,0));
            g.fillOval(x,200, 50,50);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame frame=new L52MoveCanvas();
            frame.setVisible(true);
        });
    }
}