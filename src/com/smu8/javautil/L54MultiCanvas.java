package com.smu8.javautil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class ShapeRect{
    int x=0;
    int y=0;
    int width=50;
    int height=50;
    double speed=1.5;
    //int r,g,b;
    Color color=Color.blue;
    public static Color createRandomColor(){
        Random random=new Random();
        int r=random.nextInt(256); //0~255
        int g=random.nextInt(256);
        int b=random.nextInt(256);
        Color color=new Color(r,g,b);
        return color;
    }
    //오버로드 : 함수(생성자)의 이름이 같은데 매개변수가 다른 것
    public ShapeRect(int x, int y, int size){
        this(x,y,size,size,createRandomColor());
    }
    public ShapeRect(int x, int y, int size, Color color){
        this(x,y,size,size,color);
    }

    public ShapeRect(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void chase(Player player){
        int dx=this.x-player.x;
        int dy=this.y-player.y;
        //순간이동으로 바로 따라감
        //this.x+=dx;
        //this.y+=dy;
        double distance=Math.sqrt(dx*dx+dy*dy); //피타고라스 정리로 두 객체의 거리 구하기
        double mx=dx/distance*speed;
        double my=dy/distance*speed;
        this.x-=mx;
        this.y-=my;

    }

    public void draw(Graphics g){
        g.setColor(this.color);
        g.fillRect(x,y,width,height);
    }
}

class Player{
    int x,y;
    int r=50;
    int speed=10;
    Color color=new Color(79, 163, 79);
    public Player(int x,int y){
        this.x=x;
        this.y=y;
    }
    //key event를 받아서 움직임을 정의(키 이벤트의 콜백함수)
    public void move(int keyCode){
        //a w s d
        switch (keyCode){
            case KeyEvent.VK_W : y-=speed; break;
            case KeyEvent.VK_S : y+=speed; break;
            case KeyEvent.VK_D : x+=speed; break;
            case KeyEvent.VK_A : x-=speed; break;
        }
    }


    public void draw(Graphics g){
        g.setColor(color);
        g.fillOval(x,y,r,r);
    }
}


//복수의 도형을 그리고 움직이는 그래픽 예제
//1. JFrame + JPanel(canvas 역할)
public class L54MultiCanvas extends JFrame {
    class MyPanel extends JPanel{
        List<ShapeRect> shapeRectList=new ArrayList<>();
        Player player;
        Timer chaseTimer; //블럭이 player 쫓아다니는 타이머
        public MyPanel(){
            this.setBackground(new Color(150,200,50));
            ShapeRect rect=new ShapeRect(20,20,100,70,Color.PINK);
            ShapeRect rect1=new ShapeRect(150,150,120,Color.MAGENTA);
            ShapeRect rect2=new ShapeRect(200,250,110);
            shapeRectList.add(rect);
            shapeRectList.add(rect1);
            shapeRectList.add(rect2);
            player=new Player(225,410);
            this.setFocusable(true);
            this.requestFocusInWindow();
            chaseTimer=new Timer(16,(a)->{
                for(ShapeRect shapeRect : shapeRectList){
                    shapeRect.chase(player);
                }
                repaint();
            });
            chaseTimer.start();
            this.addKeyListener(new KeyAdapter(){
                @Override
                public void keyPressed(KeyEvent e) {
                    player.move(e.getKeyCode());
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            player.draw(g);
            for(ShapeRect shapeRect: shapeRectList){
                shapeRect.draw(g);
            }


 //           g.setColor(new Color(194,66,66));
 //           g.fillRect(0,0,50,50);
 //           g.setColor(Color.GRAY);//255,255,255
 //           g.fill3DRect(100,100,100,100,false);//사각형 버튼처럼 눌리거나 나와있어보이게 출력
 //           g.fill3DRect(210,100,100,100,true);//사각형 버튼처럼 눌리거나 나와있어보이게 출력
        }
    }
    public L54MultiCanvas(){
        super("멀티 도형 캔버스");
        MyPanel canvas=new MyPanel();
        this.setContentPane(canvas);
        this.setBounds(0,0,500,500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            JFrame frame=new L54MultiCanvas();
        });

    }
}
