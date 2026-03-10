package com.smu8.homework;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class H04Variable {
    public static void main(String[] args) {
        System.out.println(39);
        System.out.println(89.39);
        System.out.println('T');
        System.out.println("TY");
        System.out.println(89==39);
        System.out.println("---------------------------------------");
        int i=10;
        System.out.println(i);
        i=20;
        System.out.println(i);
        i=30;
        System.out.println(i);
        System.out.println("---------------------------------------");
        var a=39;
        var b="TY";
        System.out.println(a+b);
        System.out.println("---------------------------------------");
        var c="Hello";
        var d="Java";
        var e="10";
        var f="20";
        char n='\0';
        System.out.println(c+d);
        System.out.println(c+e);
        System.out.println(e+f+c);
        System.out.println(n);
        System.out.println("---------------------------------------");
        final double PI=3.14;
        System.out.println(2*5*PI);
        System.out.println(2*10*PI);
        System.out.println("---------------------------------------");
        int kmScore=95;//학생 점수 : kmScore=95;
        final int MAXIMUM_SCORE=100;//최대 점수 : MAXIMUM_SCORE=100;
        boolean isLogin=false;//로그인 여부 : isLogin=false;
        System.out.println("---------------------------------------");
        byte by=111;
        short sh=30000;
        int in=2147483647;
        long l=123456789010L;
        System.out.println(by);
        System.out.println(sh);
        System.out.println(in);
        System.out.println(l);
        System.out.println(Integer.MAX_VALUE);
        System.out.println(in+1);
        System.out.println("---------------------------------------");
        int x=9;
        int y=3;
        System.out.println(x+y);
        System.out.println(x-y);
        System.out.println(x*y);
        System.out.println(x/y);
        System.out.println(x%y);
        System.out.println("---------------------------------------");
        System.out.println(10/4);
        System.out.println(10/4.0);
        System.out.println("---------------------------------------");
        double pi=3.14;
        System.out.println(pi*3*3);
        System.out.println(pi*4*4);
        System.out.println(pi*5*5);
        System.out.println("---------------------------------------");
        int studentScore=95;
        final int MAX_SCORE=100;
        System.out.println(studentScore>MAX_SCORE); // == 같다 != 다른가
        boolean isScoreMax=studentScore>MAX_SCORE; // > 초과 < 미만 >= 이상 <= 이하
        System.out.println("점수는 "+studentScore+"점, "+"결과는 "+isScoreMax);
        String msg="학생의 점수는:"+studentScore+", 비교결과: "+isScoreMax;
        System.out.println(msg);
        System.out.println("---------------------------------------");

    }
}
