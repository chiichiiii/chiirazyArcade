package com.smu8.study;

public class L08Float {
    public static void main(String[] args) {
        //실수의 종류와 저장 원리
        float f=123456789012345678901234567890.0F;
        System.out.println(f);
        //1.2345679E29 ==> 부동소수점 표기법
        //1.23456789012345678901234567890E29
        //가수부가 표현할 수 있는 길이를 넘어가서 반올림함.

        double d=123456789012345678901234567890.0;
        System.out.println(d);
        //1.2345678901234568E29
        //double의 가수부가 더 크기 때문에 정밀도가 높다
        //면접 : 왜 자바는 실수의 기본형으로 double을 사용하나요?
        //=> float 보다 double의 가수부가 크기 때문에 정밀도가 높다.

        System.out.println(0.1+0.4); // 1/10+2/5
        System.out.println(0.5+0.25); // 1/2+1/4
        // 0.1을 더하기하면 무한 소수가 나오는 이유?
        // => 실수 변환 과정에서 0.1이 2진수로 맞아 떨어지지 않기 때문 / 0.1을 실수로 변환하면서 0.1의 2진수는 무한 소수이기 때문
        System.out.println(0.3==(0.1+0.2)); // false
        System.out.println(0.1+0.2);
        System.out.println(0.3);

        f=1E32F;
        d=1E300;
        //실수는 지수부가 있기 때문에 천문학적인 수를 다룰 수 있다.
        System.out.println(f);
        System.out.println(d);


    }
}
