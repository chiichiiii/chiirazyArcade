package com.smu8.chaechae;

public class C0112 {
    public static void main(String[] args) {
        int a='A';
        System.out.println(a);
        a='Z';
        System.out.println(a);
        a='a';
        System.out.println(a);
        a='0';
        System.out.println(a);
        a='가';
        System.out.println(a);



        char b=65;
        System.out.println(b);
        b=90;
        System.out.println(b);
        b=97;
        System.out.println(b);
        b=48;
        System.out.println(b);
        b=44032;
        System.out.println(b);



        char c = 'A';
        System.out.println(c + 1);
        System.out.println((char)(c + 1));


        System.out.println((int)'A');  // 65
        System.out.println((int)'Z');  // 90
        System.out.println((int)'a');  // 97
        System.out.println((int)'0');  // 48
        System.out.println((int)'가'); // 44032


        int n=8;
        String s="";
        if(n%2==0){
            s="짝수";
        }else if(n%2!=0){
            s="홀수";
        }
        System.out.println(s);
        int score=80;
        String msg="";
        if(score>=90){
            msg="우수";
        }else if(score>=70&&score<90){
            msg="보통";
        }else if(score<70){
            msg="보통";
        }
        System.out.println(msg);

        int month=13;
        String ms="";
        if(month>=1&&month<=12) {
            if (month >= 3 && month <= 5) {
                ms = "봄";
            } else if (month >= 6 && month <= 8) {
                ms = "여름";
            } else if (month >= 9 && month <= 11) {
                ms = "가을";
            } else {
                ms = "겨울";
            }
        }
        else {
            ms="month는 1~12월까지입니다.";
        }
        System.out.println(ms);


    }

}
