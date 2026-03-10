package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;

public class L50Timer extends JFrame {

    private JLabel label;
    private JButton jButton;
    private int time=10;
    private Timer timer;
    public L50Timer(){
        super("타이머");
        label=new JLabel(Integer.toString(time),SwingConstants.CENTER);
        this.add(label);
        jButton=new JButton("시작");
        jButton.addActionListener((event)->{
//            new Thread(()->{
//                while (true){
//                    threadSleep(1000);
//                    time--;
//                    //SWING의 그래픽과 이벤트는 EDT가 제어
//                    //label.setText(Integer.toString(time));
//                    // 다른 스레드가 swing을 제어하는 것은 권장하지 않음 (반영되지않음,깜박임,..)
//                    SwingUtilities.invokeLater(()->{
//                        label.setText(Integer.toString(time));
//                    }); //EDT에게 실행을 위임
//                    if(time==0) break;
//                }
//            }).start();
            //Timer EDT 가 주기가 있는 반복실행
            if(timer!=null) return;
            timer=new Timer(1000,(e)->{ ///Thread.sleep(1000)
                label.setText(--time+""); //invokeLater 생략가능!!(해당 스레드가 EDT기때문)
                if(time<=0) timer.stop(); //생성이 완료되지 않았을때 stop을 하면 오류
//                if(time==0){ //지역변수
//                    Timer t=(Timer) e.getSource();
//                    t.stop();
//                }
            });
            timer.start();
        });
        this.add(jButton, BorderLayout.SOUTH);
        this.setBounds(-1980,0,200,200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    void threadSleep(long sec) {
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{ //Window 생성을 EDT에게 위임하는 것이 더 안전!
            L50Timer timer=new L50Timer();
        });
    }
}