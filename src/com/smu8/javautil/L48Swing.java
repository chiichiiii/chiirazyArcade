package com.smu8.javautil;

import javax.swing.*; //플랫폼간의 차이를 줄이도록 개선된 GUI
import java.awt.*; //자바의 초기 GUI
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class L48Swing {
    public static void main(String[] args) {
        //window(창)>container(배치)>component(요소)
        JFrame frame=new JFrame("awt 창");

        WindowListener windowListener=new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose(); //window 자원 정리 => 종료
                System.exit(0); //jvm 콘솔 종료
            }
        };
        frame.addWindowListener(windowListener);
        frame.setBounds(-1980,0,200,200);
        frame.setVisible(true);
    }
}