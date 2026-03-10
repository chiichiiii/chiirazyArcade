package com.smu8.oop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//public action(){} //자바는 객체지향 언어기 때문에 함수 혼자 존재할 수 없다.
//js=> 함수형언 : 함수 혼자 존재 가능(함수 자체가 타입)

/*class BtnHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) { //버튼을 누르면 실행되는 기능

    }
}*/

class HelloFrame extends JFrame{
    private JButton btn;
    private JTextArea ta;
    class BtnHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String text=ta.getText();
            text+="안녕\n";
            ta.setText(text);
        }
    }
    public HelloFrame(){
        super("안녕 윈도우");
        btn=new JButton("say 안녕");
        btn.addActionListener(new BtnHandler());
        super.add(btn, BorderLayout.NORTH);
        btn=new JButton("say 잘가");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text=ta.getText();
                text+="잘가\n";
                ta.setText(text);
            }
        });
        super.add(btn, BorderLayout.EAST);
        btn=new JButton("say 호우");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text=ta.getText();
                text+="호우";
                ta.setText(text);
            }
        });
        super.add(btn, BorderLayout.WEST);
        btn=new JButton("say 예에");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text=ta.getText();
                text+="예에";
                ta.setText(text);
            }
        });
        //btn.addActionListener((e)->{ //람다식; 위 actionPerformed(ActionEvent e)함수를 줄인 거});
        super.add(btn, BorderLayout.SOUTH);
        ta=new JTextArea();
        super.add(ta,BorderLayout.CENTER);
        super.setDefaultCloseOperation(3);//x버튼을 누르면 어떻게 할거
        //DISPOSE_ON_CLOSE : 상태를 나타내는 상수
        super.setBounds(740,250,500,500);
        super.setVisible(true);
    }
}

public class L23AnonymousEx {
    public static void main(String[] args) {
        new HelloFrame();
    }
}
