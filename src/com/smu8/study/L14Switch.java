package com.smu8.study;

public class L14Switch {
    public static void main(String[] args) {
        // 0~6 월요일~일요일 요일 표현
        int weekNum = 1;
        String week = "";
        if (weekNum == 0) {
            week = "월요일";
        } else if (weekNum == 1) {
            week = "화요일";
        } else if (weekNum == 2) {
            week = "수요일";
        } else if (weekNum == 3) {
            week = "목요일";
        } else if (weekNum == 4) {
            week = "금요일";
        } else if (weekNum == 5) {
            week = "토요일";
        } else if (weekNum == 6) {
            week = "일요일";
        }
        System.out.println("오늘의 요일은 " + week + "입니다.");
        /* if문의 가독성이 떨어지는 이유
         * 1.복잡한 조건(비교연산, 논리연산_\)
         * 2. {} 블럭, scope가 너무 많다.
         */

        //switch => 가독성이 떨어지는 if문 대신 사용 (동등비교==만 가능)
        //switch(동등비교할 변수) : 변수는 기본형, 문자열, enum 타입만 올 수 있다.

        weekNum=15;
        week="";

        switch (weekNum){
            case 0://weekNum==0
                week="월요일";
                break; // 다음 조건은 실행하지 않음(switch 종료)
            case 1: week="화요일"; break;
                case 2: week="수요일"; break;
                case 3: week="목요일"; break;
                case 4: week="금요일"; break;
                case 5: week="토요일"; break;
                case 6: week="일요일"; break;
            default: week="요일은 0~6까지만 존재합니다.";
        }
        System.out.println("switch : 오늘은 "+week+"입니다.");
        // switch가 if 동등비교보다 명시적(가독성이 좋다)이기 때문에 개발자들이 많이 사용

        //break가 없으면 case가 같은 것부터 break가 있을 때까지 실행된다
        int num=1;
        switch (num){
            case 1: System.out.println("num은 1입니다.");
            case 2: System.out.println("num은 2입니다.");
            case 3: System.out.println("num은 3입니다.");
            case 4: System.out.println("num은 4입니다.");
            case 5: System.out.println("num은 5입니다.");
        }

        //default 기본의, 기본값 : ==else


    }



}
