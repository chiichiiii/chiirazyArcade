package com.smu8.oop;

import javax.swing.*;
import java.awt.*;

class CounterApp extends JFrame{
    private JLabel label;
    private JButton button;
    private JButton button2;
    int count=0;
    public CounterApp(){
        super("카운터");
        //int count=0;
        //지역변수 카운터를 람다식이나 익명클래스에서 접근하면 캡쳐가 되면서 상수로 변경

        label=new JLabel();
        Font font=new Font("나눔고딕",Font.BOLD,100);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(font);
        label.setText(count+"");
        button=new JButton("+");
        button2=new JButton("-");
        button.addActionListener((e)->{
            count++;
            label.setText(count+"");
        });
        this.add(label);
        this.add(button, BorderLayout.SOUTH);
        button2.addActionListener((e)->{
            if(count-1>=0) {
                count--;
                label.setText(count + "");
            }
        });
        this.add(button2,BorderLayout.NORTH);
        this.setVisible(true);
        super.setDefaultCloseOperation(3);
        this.setBounds(740,250,300,300);
        this.setVisible(true);
    }
}
public class L26CaptureEx {
    public static void main(String[] args) {
        CounterApp counterApp=new CounterApp();
    }
}
