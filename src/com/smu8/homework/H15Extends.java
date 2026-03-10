package com.smu8.homework;

import javax.swing.*;
import java.awt.*;

class Memo extends JFrame{
    JButton btn=new JButton("버튼");
    JTextArea ja=new JTextArea();
    public Memo(){
        super("메모장");
        //JFrame super=new JFrame("메모장");
        super.add(btn, BorderLayout.NORTH);
        super.add(ja, BorderLayout.CENTER);

        super.setBounds(100,200,500,500); //해상도 기준
        super.setVisible(true);
    }
}


public class H15Extends {
    public static void main(String[] args) {
        Memo memo=new Memo();
       /* JFrame frame=new JFrame("윈도우");
        frame.setSize(500,500);
        JButton btn=new JButton("확인");
        frame.add(btn);
        frame.setVisible(true);*/

    }
}
