package com.smu8.study;

import java.util.Arrays;

public class L28StringMethod {
    public static void main(String[] args) {
        String s="안녕";
        char[] cArr={'안','녕'};
        //문자로 된 열 == 문자열
        //s==cArr : false
        //자료형간의 == 비교 : 두개가 동일한 데이터? 주소가 같나?
        //System.out.println(s==cArr); 타입이 달라서 비교 불가
        String s2="안녕";
        System.out.println(s==s2); //true
        String s3=new String("안녕");
        System.out.println(s==s3); //false

        System.out.println(s.equals(s3)); //true;; equals=>자료형의 데이터가 값이 같은지 확인
        s="hello";
        s3=new String("hello");
        System.out.println(s==s3); //false
        System.out.println(s.equals(s3)); //true

        System.out.println("hello".equals("Hello")); //false
        System.out.println("hello".equalsIgnoreCase("Hello")); //true;; equalsIgnoreCase=>대소문자 무시

        s="hello";
        System.out.println(s.toUpperCase()); //UpperCase:대문자
        s=s.toUpperCase();
        System.out.println(s);
        System.out.println(s.toLowerCase()); //LowerCase:소문자


        String [] strArr=s.split("");
        System.out.println(Arrays.toString(strArr));
        s="010-1234-5678";
        String[] phoneNumArr=s.split("-");
        System.out.println(Arrays.toString(phoneNumArr));


        s="안녕하세요 날씨가 안 좋네요. 집에 가고 싶어요.";
        System.out.println(s.contains("날씨")); //contains: 특정 문자열 포함 여부
        System.out.println(s.indexOf("날씨")); //indexOf: 특정 문자열 첫 위치 확인
        System.out.println(s.indexOf("태연")); //-1(없음)
        System.out.println(s.substring(6,11)); //요청 위치 문자열 반환




    }
}
