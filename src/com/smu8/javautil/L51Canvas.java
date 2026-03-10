package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;

public class L51Canvas extends JFrame {
    class MyPanel extends JPanel{
        //JPanel 이 캔버스가 되려면 paintComponent를 구현
        public MyPanel(){
            this.setBackground(new Color(255,200,200));
        }

        @Override
        protected void paintComponent(Graphics g) {//Graphics g: 붓
            Graphics2D g2=(Graphics2D) g;
            //1. 붓의 색정하기 , 2.선을 그리면 붓의 두께, 3. 그림그리기
            g2.setStroke(new BasicStroke(10));
            g2.setColor(new Color(200,200,100));
            g2.drawLine(0,0,500,500);

            g2.setStroke(new BasicStroke(20));
            g2.setColor(new Color(100,200,200));
            g2.drawLine(500,0,0,500);


            g2.setColor(Color.RED);
            g2.drawOval(50,50,100,200);

            g2.setColor(new Color(0,255,0,120));
            g2.fillOval(200,50,200,100);

            g2.setColor(new Color(0,0,255,100));
            g2.fillRect(50,200,100,200);

        }
    }
    public L51Canvas(){
        super("캔버스(JPanel.paintComponent)를 포함하는 window");
        //this.add(new MyPanel());
        this.setContentPane(new MyPanel()); //명시적으로 화면전체를 캔버스로 채우겠다.
        setBounds(-1980,0,500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        JFrame frame=new L51Canvas();
        frame.setVisible(true);
    }
}