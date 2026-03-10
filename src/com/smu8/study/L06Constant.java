package com.smu8.study;

public class L06Constant {
    public static void main(String[] args) {
        int i=10;
        System.out.println(i);
        i=20;
        System.out.println(i);
        i=30;
        System.out.println(i);
        //변수 : 계속 데이터를 참조할 수 있는
        //상수 : 바뀌지 않는
        final int a=100;
        //a=200; : 컴파일 오류
        final double PI=3.14; // 상수임을 알리기 위해 대문자 표기법 사용 (개발자끼리 약속)
        //pi=33.14; / 원주율은 바뀔 수 없다.

        // 변수와 상수 표기법 (개발자끼리의 약속)
        int kmScore=95; // camelCase(카멜 표기법) = 자바의 변수는 대부분 카멜 선호
        int km_score=95; // 스네이크문법(소문자) = 파일 이름,파이썬의 변수, 폴더명
        final int KM_SCORE=95; //대문자 스네이크 문법 (모든 상수는 스네이크 문법 사용)

        // 잘못된 예시
        final int km_Score=95; // x 스네이크 문법은 대문자로만 사용하거나 소문자로만 사용
        //윈도우에서 파일명이나 폴더명을 대소문자+언더바 입력(os가 대소문자 구별을 잘 못함)
        int KmScore=95; // 파스칼표기법 (class명에서만 사용)
        //int int=10; // int, double, class, if, while == 예약어는 이름으로 사용 불가
        //int class=10;
        //int public=10;
        //int static=10;
        int 경민의성적은=95; // 권장x 영어로만 사용하길
        System.out.println(경민의성적은);
    }
}
