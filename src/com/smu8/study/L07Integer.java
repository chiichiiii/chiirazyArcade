package com.smu8.study;

public class L07Integer {
    public static void main(String[] args) {
        //정수 : 소수점이 없는 수
        //정수 기본형 data type : int, long, byte, short
        // byte : 1byte 크기의 정수 / -128~127
        // short : 2byte / -32768~32767
        // int : 4byte / -214783648~2147483647 / *** 정수를 입력하면 int
        // long : 8byte / 굉장히 큰 숫자
        byte b = 111; // 1byte = 256개의 경우의 수 / 2^8
        //b=256;
        b = 127;
        b = -128;
        short s = 32767;
        int i = 2147483647;
        long l = 12345678901L;

        System.out.println(333); // 크기는 short 값이지만 정수를 입력하면 무조건 int로 인식

        //정수는 수학적 연산이 가능(+-*/%)
        System.out.println(i * s);
        System.out.println(i - s);
        System.out.println((10 % 3)); //10/3 10을 3으로 나눈 나머지
        System.out.println(10 / 4); // 10/4 2.5지만 2가 나온 이유는 정수간 나온 나머지는 정수 반환
        System.out.println(10 / 4.0);
    }
}
