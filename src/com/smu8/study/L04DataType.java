package com.smu8.study;

public class L04DataType {
    public static void main(String[] args) {
        //data : 기본형, 참조형(자료형)
        //기본형 data : 기본적인 data == 수로 된 data (이진수, 문자 등)
        //참조형(자료형) data : 참조(자식이 있는;.필드 접근자)하는 data가 있는

        System.out.println("안녕하세요."); //자료형 문자열 String
        System.out.println('a'); //문자 char(Character)
        System.out.println((int)'A');
        System.out.println((int)'호');
        // java 문자 2byte=>16bit (8bit = 1byte)
        // utf(유니코드)-16 (uft-8, utf-16은 국제 표준 문자표로 서로 호환 가능)
        // 아스키 코드를 만든 이유 : 컴퓨터가 수만 처리할 수 있기 때문에 문자를 수로 표현
        // 이미지, 영상, 소리 등 세상의 모든 것은 데이터화 할 수 있다.
        System.out.println(1444444437); // int 4byte
        System.out.println(1234567890115484125L); // long 8byte
        System.out.println(13.121212); // double 8byte
        System.out.println(13.121212F); // float 4byte
        System.out.println(1==1); // == 비교연산 (두 값이 같은지) ==> boolean
        System.out.println(1!=1); // != (두 값이 다른지)
        // true(1), false(0) : boolean 은 1bit 데이터지만 크기는 1byte(메모리를 byte로 나누기 때문)
    }
}
