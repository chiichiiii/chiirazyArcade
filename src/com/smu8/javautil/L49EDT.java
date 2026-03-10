package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;

public class L49EDT {
    public static void main(String[] args) {
        JFrame frame=new JFrame("EDT");
        frame.setLayout(null);
        JButton btn=new JButton("안녕");
        btn.setBounds(200,270, 100,100);
        frame.add(btn);
        final JLabel label=new JLabel("라벨");
        label.setBounds(0,0,100,200);
        frame.add(label);
        btn.addActionListener((event)->{
            //ActionEvent event : 이벤트 정보 (어디서, 누가, 어떤 ..)
            try {
                label.setText("바뀜");
                Thread.sleep(3000);
                //EDT가 윈도우에서 발생하는 모든 이벤트를 총괄
                //3초간 모든 윈도우 이벤트가 발생하지 않음
                //오래 걸리는 일은 꼭 스레드 생성후 작업해야합니다.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(-1980,0,300,400);
        frame.setVisible(true);
    }
}