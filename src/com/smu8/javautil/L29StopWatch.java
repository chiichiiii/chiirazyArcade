package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;

public class L29StopWatch extends JFrame{
    JButton resetBtn;
    JButton stratBtn;
    JButton stopBtn;
    JLabel timeLabel;
    long time=0;
    boolean isRun=true;
    WatchThread watchThread;

    public L29StopWatch() {
    super("스탑워치");
    resetBtn=new JButton("리셋");
    stratBtn=new JButton("시작");
    stratBtn.setPreferredSize(new Dimension(0,70));
    stopBtn=new JButton("멈춤");
    stopBtn.setPreferredSize(new Dimension(0,70));
    String timeStr=String.format("%.2f",time/100.0); //0->0.00

    timeLabel=new JLabel(timeStr,SwingConstants.CENTER);
    timeLabel.setFont(new Font("dialog",Font.BOLD,40));
    this.add(timeLabel, BorderLayout.CENTER);
    this.add(stratBtn, BorderLayout.NORTH);
    this.add(stopBtn, BorderLayout.SOUTH);
    this.add(resetBtn,BorderLayout.EAST);


    //boolean isRun=true; //지역변수
        //지역변수를 람다식으로 참조하면 capture 복사해오기 때문에
    stratBtn.addActionListener((event)-> { //객체
        //isRun=true;
        //new WatchThread().start();
        //Thread 문제 : btn의 무한 반복때문에 Thread가 빠져나오지 못해서 GUI 동작이 멈춤
//        while (isRun){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            time+=1;
//            String strTime=String.format("%.2f",time/100.0);
//            timeLabel.setText(strTime);
//        }
        if(watchThread==null || !watchThread.isAlive()){
              watchThread=new WatchThread();
              watchThread.start();
          }
    });
    stopBtn.addActionListener((event)->{
        watchThread.interrupt();
    });
    resetBtn.addActionListener((e)->{
        stopBtn.doClick();
        time=0;
        String strTime=String.format("%.2f",time/100.0);
        timeLabel.setText(strTime);
    });

    this.setBounds(800,250,400,400);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
    }
    class WatchThread extends Thread{
        @Override
        public void run() {
            while (isRun){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                time+=1;
                String strTime=String.format("%.2f",time/100.0);
                timeLabel.setText(strTime);
            }
        }
    }

    public static void main(String[] args){
            new L29StopWatch();
    }
}
