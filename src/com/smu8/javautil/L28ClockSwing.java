package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class DigitalClock extends JFrame {
    JLabel dataLabel; //날짜출력
    JLabel clockLabel; //시간출력
    JLabel tempLabel; //온도출력
    JLabel humLabel; //습도출력
    JButton startBtn;
    JButton stopBtn;
    JPanel p; //온도,습도 포함하는 패널
    public DigitalClock(){
        Font font=new Font("나눔고딕",Font.BOLD,25);
        Font font2=new Font("나눔고딕",Font.TRUETYPE_FONT,15);
        Font font3=new Font("나눔고딕",Font.ITALIC,15);
        dataLabel=new JLabel("2026년 02월 09일 (월)",SwingConstants.CENTER);
        dataLabel.setFont(font);
        startBtn =new JButton("시작");
        startBtn.setFont(font2);
        stopBtn =new JButton("종료");
        stopBtn.setFont(font2);
        clockLabel=new JLabel("",SwingConstants.CENTER);
        clockLabel.setFont(font2);
        tempLabel=new JLabel("온도 -1도",SwingConstants.CENTER);
        tempLabel.setFont(font3);
        humLabel=new JLabel("습도 40%",SwingConstants.CENTER);
        humLabel.setFont(font3);
        p=new JPanel();
        p.add(tempLabel);
        p.add(humLabel);
        this.add(startBtn,BorderLayout.WEST);
        this.add(stopBtn,BorderLayout.EAST);
        this.add(dataLabel, BorderLayout.NORTH);
        this.add(clockLabel);
        this.add(p,BorderLayout.SOUTH);
        //시간을 1초에 한 번씩 바꾸기
        //while (true){}
        Thread clockThread=new Thread(()->{
            DateTimeFormatter dtf=DateTimeFormatter.ofPattern("HH시 mm분 ss초"); //"HH:mm:ss.S" => HH시 mm분 ss초
            while (true){
               threadSleep(1000);
                LocalTime time=LocalTime.now();
                //System.out.println(time);
                clockLabel.setText(time.format(dtf));
            }
        });
        clockThread.start();
        new Thread(()->dateThread()).start();
        this.setBounds(800,250,300,200);
        this.setVisible(true);
        this.setDefaultCloseOperation(3);
    }
    void dateThread(){
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yy년 MM월 dd일 (E)", Locale.KOREAN);
        while (true){
            threadSleep(1000);
            LocalDate now=LocalDate.now();
            dataLabel.setText(now.format(dtf)); //2026-02-09 : yyyy-MM-dd => yy년 MM월 dd일
        }
    }
    void threadSleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
public class L28ClockSwing {
    public static void main(String[] args) {
        new DigitalClock();
    }
}
