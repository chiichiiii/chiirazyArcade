package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class L53KeyMoveCanvas extends JFrame {
    int width=500;
    int height=500;
    Color bgColor=new Color(0,0,0);
    public L53KeyMoveCanvas(){
        super("awsd 로 움직이는 원 만들기");
        this.setContentPane(new MyPanel());
        this.setBounds(-1980,0,width,height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    class MyPanel extends JPanel{
        int x=200;
        int y=200;
        int r=50;
        public MyPanel(){
            this.setBackground(L53KeyMoveCanvas.this.bgColor);
            this.setFocusable(true);
            this.requestFocusInWindow(); //키보드 이벤트를 받기 위해
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) { //KeyEvent e 누른 키를 알수 있다.
                    switch (e.getKeyCode()){
                        case KeyEvent.VK_S :  y+=10; repaint(); break; //s 키를 누르면
                    }
                }
            });

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); //기존의 그림을 지우려면 그대로 둔다.
            g.setColor(new Color(255,20,20));
            g.fillOval(x,y,r,r);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame frame=new L53KeyMoveCanvas();
            frame.setVisible(true);
        });
    }
}