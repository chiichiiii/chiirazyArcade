package com.smu8.study;

public class L32PrimitiveCasting {
    public static void main(String[] args) {
        //기본형의 형변환과 캐스팅
        char c='안';
        System.out.println(c); // char 2byte(unsigned)
        System.out.println((short)c); //-15032 오버플로우
        System.out.println((int)c); //50504
        //캐스팅형변환 : 자연스럽지 않은 형변환(강제성)

        int i=2000000000;
        long l=2000000000000000000l;
        float f=200000000000000000000000000000000000000f; //38승까지 가능
        double d=1000000000000000000000000000000000000000000000000000000.0; //308승까지 가능
        //11비트 지수부 -> 2의 1024~-1024승까지 => 10의 308승~-308승
        long castLong=i; // long이 i보다 더 큰 수를 표현해서 자연스럽게 형변환
        //int castInt=l;
        int castInt=(int)l; // 큰 수가 작은 수가 되려면 버려야됨(overflow) 부자연스러운 형변환 casting
        System.out.println(castInt);
        //2000000000000000000l => 1321730048
        castLong=(long)f;
        System.out.println(castLong);
        //200000000000000000000000000000000000000f => 9223372036854775807
        //long 보다 큰 수의 실수를 long으로 바꾸면 overflow가 아닌 최대 수
        castInt=(int)f;
        System.out.println(castInt); //2147483647


        double castDouble=f; //작은 실수타입 => 큰 실수타입
        float castFloat=(float) d;
        System.out.println(castFloat);
        //Casting
        //큰 정수 -> 작은 정수 : 버림(Overflow)
        //실수 -> 정수 : 최대, 최소 값
        //큰 실수 -> 작은 수 : +-Infinity

    }
}

