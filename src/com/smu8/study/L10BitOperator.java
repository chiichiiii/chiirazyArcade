package com.smu8.study;

public class L10BitOperator {
    public static void main(String[] args) {
        //비트연산자
        //쉬프트연산자 ==> >> <<
        int a=8; // 0 0 0 0 1 0 0 0 (2)
        a=a<<1;  // 0 0 0 1 0 0 0 0 (2)
        System.out.println(a);

        a<<=2; // 0 1 0 0 0 0 0 0 (2)
        System.out.println(a);
        //쉬프트연산으로 1 만들기
        a>>=6; // 0 0 0 0 0 0 0 1 (2)
        System.out.println(a);
        a=29;
        System.out.println(Integer.toBinaryString(a)); // 0 0 0 1 1 1 0 1 (2)
        // Integer : int를 도와주고 int의 자료형 데이터 (랩퍼클래스)
        a>>=2; // 0 0 0 0 0 1 1 1 (2)
        System.out.println(a);

        a=-2;
        System.out.println(Integer.toBinaryString(a)); // 11111111111111111111111111111110
        a>>=2;//001111111111111111111111111111111
        System.out.println(a); //음수의 나머지는 1
        System.out.println(Integer.toBinaryString(a)); //11111111111111111111111111111111

        //~ 보수 not 연산
        // 1111 -> 0000
        // 0100 -> 1011

        System.out.println(~5); //5를 보수
        System.out.println(Integer.toBinaryString(5)); //0...0101
        System.out.println(Integer.toBinaryString(~5)); // 11...1010
        System.out.println(~5789102); // -5789103

        //비트 논리연산 &(곱) |(합)
        // 1*1=1
        // 1*0=0
        // 1&1=1
        // 1&0=0

        // 1+1=2
        // 1|1=1
        // 1+0=1
        // 1|0=1
        // 0|0=0 ********

        int i=7; // 0 1 1 1
        int j=8; // 1 0 0 0
        System.out.println(i|j); // 1 1 1 1
        System.out.println(Integer.toBinaryString(i|j));
        j=11; // 1 0 1 1
        System.out.println(i|j);
        System.out.println(Integer.toBinaryString(i|j));
        System.out.println(i&j);
        System.out.println(7&8);


    }
}
