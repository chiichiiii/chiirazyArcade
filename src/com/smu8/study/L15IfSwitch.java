package com.smu8.study;

import javax.swing.plaf.synth.SynthOptionPaneUI;

public class L15IfSwitch {
    public static void main(String[] args) {
        //삼항 연산자
        //짝수, 홀수를 구하는 if
        int num=22;
        String msg="";
        if(num%2==0){
            msg="짝수";
        }else{
            msg="홀수";
        }
        System.out.println(msg);


        msg=(num%2==0)?"짝수" : "홀수";

        //변수=(조건) ? 참일 때 : 거짓일 때;
        System.out.println("삼항연산자결과 : "+msg);

        // java는 삼항 연산자가 대입 연산과 함께 작성되어야 합니다.
        // (num%3==0) ? System.out.println("3의 배수") : System.out.println("3의 배수가 아님");

        // 삼항연산자를 복잡하게 사용하는 경우 (권장 x) >> 코드가 복잡해짐(스파게티 코드)
        int score=88;
        String grade=(score>=90)? "A"
                : (score>=80)? "B"
                : (score>=70)? "C"
                : "F";
        System.out.println(grade);


        //java 14 화살표switch문(사용 권장 x)
        int weekNum=6;
        //0 일요일 ~ 6 월요일
        String week=switch(weekNum){
            case 0 -> "일요일"; //break; 브레이크는 자동으로 입력됨
            case 1 -> "월요일";
            case 2 -> "화요일";
            case 3 -> "수요일";
            case 4 -> "목요일";
            case 5 -> "금요일";
            case 6 -> "토요일";
            default -> "0~6까지 입력";
        };
        System.out.println("오늘은 "+week+"입니다.");

        //1월~12월 봄 여름 가을 겨울을 표기
        int month=3;
        String s="";
        switch (month){
            case 3,4,5 : s="봄"; break;
            case 6,7,8 : s="여름"; break;
            case 9,10,11 : s="가을"; break;
            case 12,1,2 : s="겨울"; break;
            default: s="달은 1~12까지 입니다.";
        }
        System.out.println("계절은 "+s+"입니다.");



    }
}
