package com.smu8.chaechae;

public class C0108 {
    public static void main(String[] args) {
        System.out.println("실습 1. 데이터 타입 직접 출력하기");
        System.out.println(39); // int 정수
        System.out.println(89.39); // double 실수
        System.out.println('G'); // char 문자
        System.out.println("ty"); // String 문자열
        System.out.println(89==39); // boolean
        System.out.println('a'!='b');
        System.out.println("------------------------------");

        System.out.println("실습 2. 기본형 변수 만들고 값 바꾸기");
        int i=10;
        System.out.println(i);
        i=20;
        System.out.println(i);
        i=30;
        System.out.println(i);
        System.out.println("------------------------------");

        System.out.println("실습 3. var로 변수 선언하기");
        var t=8939;
        var y="KTY";
        System.out.println(t+y);
        System.out.println("------------------------------");

        System.out.println("실습 4. 문자열 결합 실습");
        String x="Hello";
        String v="Java";
        String z="";
        int w=10;
        int q=20;
        System.out.println(x+v);
        System.out.println(x+w);
        System.out.println(w+z+q+x);
        System.out.println("------------------------------");

        System.out.println("실습 5. 상수(final) 만들기");
        final double PI=3.14;
        System.out.println(5*2*PI);
        System.out.println(10*2*PI);
        System.out.println("------------------------------");

        System.out.println("실습 6. 표기법 실습 (정상 코드만 작성)");
        int stScore=95; //학생점수 카멜 표시법
        final int MAX_SCORE=100; //최대점수 상수 표기법
        boolean isLogin=true; //로그인여부 카멜 표기법
        System.out.println("------------------------------");

        System.out.println("실습 7. 정수 타입 직접 사용해보기");
        byte b=111;
        short s=30000;
        int in=1234567855;
        long l=1567814215784L;
        System.out.println("------------------------------");

        System.out.println("실습 8. 정수 연산 실습");
        int a1=89;
        int a2=39;
        System.out.println(a1+a2); //덧셈
        System.out.println(a1-a2); //뺄셈
        System.out.println(a1*a2); //곱셈
        System.out.println(a1/a2); //나눗셈
        System.out.println(a1%a2); //나머지
        System.out.println("------------------------------");

        System.out.println("실습 9. 정수 나눗셈 차이 확인");
        System.out.println(10/4); //정수/정수
        System.out.println(10/4.0); //정수/실수
        System.out.println("------------------------------");

        System.out.println("실습 10. 재사용 실습 (변수의 이유)");
        double pi=3.14;
        System.out.println(3*3*pi);
        System.out.println(4*4*pi);
        System.out.println(5*5*pi);
        System.out.println("------------------------------");

        System.out.println("실습 11. 종합 미니 실습");
        int studentScore=98; //학생점수
        final int MAX_SCORE2=100; //최대점수
        System.out.println(studentScore>MAX_SCORE2); //점수가 최대 점수를 넘는지 비교
        boolean isOver=studentScore>MAX_SCORE2; //결과를 boolean으로 출력
        System.out.println("점수는 "+studentScore+"점, 결과는 "+isOver); //점수와 결과를 함께 출력

    }
}
