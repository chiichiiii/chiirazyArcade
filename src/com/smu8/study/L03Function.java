package com.smu8.study;

public class L03Function {

    public static void a(){
//        a : 개발자가 작성한 함수의 이름 (시그니처) 중복x 에스파는 나야 둘이 될 수 없어.. (오버로드 제외)
        System.out.println("a 함수입니당");
        System.out.println("안녕~");
        System.out.println("잘가,,");
//        함수는 실행의 집합
    }
    public static void sum(int a, int b, int c){
        System.out.println("sum : a+b+c 의 결과");
        System.out.println(a+b+c);
    }
    // main 함수 (실행=어플실행)
    public static void main(String[] args) {
//        main 줄은 실제 자바 어플이라고 생각해야됨
        a(); // L03Function class 내부에서 a를 호출하기 때문에 생략 가능
// a는 위에 있는 함수당
        System.out.println("------------------------------------------------");

        L03Function.a();
        System.out.println("------------------------------------------------");

//        "문자열" 1, 1.3 (연산 +-*/)
        System.out.println(7*111); // 정수 int(eger) 매개변수==7*111
        System.out.println(7.1*111.1); // 실수 float 매개변수==7.1*111.1
        System.out.println("------------------------------------------------");

//        sum(); //sum 함수는 정수 데이터를 3개 받아야 실행 가능(현재 함수 기준)
        sum(10,20,30); // Parameter(전달인자;매개변수)
        // sum 함수를 실행하는데 10,20,30을 전달했다. (매개변수==10,20,30)
        System.out.println("------------------------------------------------");

//      데이터에는 종류가 있다. --> (데이터(초급,고급), 변수)
        System.out.println("abc"); // 문자열 string
        System.out.println('a'); //문자 char
    }
}
