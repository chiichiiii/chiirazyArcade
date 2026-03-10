package com.smu8.oop;

//계산기 타입
class Calc{
    //com.smu8.oop.Calc : 클래스의 이름은 패키지(회사 도메인)+클래스명
    // 우리가 만든 class(지적재산)을 유일한 이름으로 만들기 위해
    // {} 전체영역(전역,루트영역)
    int a; //전역에 선언된 변스 == 필드(객체의 상태)
    int b;
    public Calc(){}
    public int sum(){ //함수: 객체의 기능
        return a+b;
        // return int : sum()을 실행하면 int가 나온다
    }
    //public double sum(){}
    /*오류
    1. 함수 이름 앞에 void 대신 type을 쓰면 무조건 return을 작성 : 함수가 반환을 약속했기 때문
    2. sum이 존재하기 때문에 같은 이름의 sum을 사용할 수 없다.
        └ 매개변수가 다르면 이름이 같아도 허용(오버로드,오버로딩)
    */
    public double sum(double a,double b){
        double result=0.0;
        result=a+b;
        return result;
    }
    public void sum(int a,int b,int c){
        System.out.println(a+b+c);
        return; //void는 return을 작성할 수 있지만 아무것도 작성하지 않음
        //return: 함수의 종료
        //System.out.println("입니다."); 도달할 수 없는 구문
    }

}
public class L02Method {
    //main : JVM을 호출하는 함수 -> java 앱
    //main 앱을 실행한다 : JVM(java)을 호출해서 main에 작성된 코드를 순서대로 실행
    public static void main(String[] args) {
        System.out.println("계산기 어플입니다.");
        new Calc(); //객체(Calc 자료형[타입]으로 만든 data)
        int sum=new Calc().sum();
        System.out.println(sum);

        Calc calc=new Calc(); //new 연산자로 생성자를 호출하면 생성자를 포함하는 타입의 객체를 반환
        calc.a=10;
        calc.b=20;
        sum= calc.sum();
        System.out.println(sum);
        System.out.println(calc.sum(11.1,22.2));
        calc.sum(100,200,300);
    }
}
