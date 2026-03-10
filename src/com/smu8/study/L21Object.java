package com.smu8.study;
class Person{
 //public Person(){} // 기본 생성자 : 작성하지 않아도 컴파일러가 자동으로 생성
    // calss에 존재하는 요소
    // 필드, 메소드, 생성자
    String id="941221-0000000";
    String name="최채철";
    int birth=1994;
    // . 필드 접근자(자식, 속성)로 필드를 접근할 수 있을까? = 있다
    public void say(){ //함수 => 동작 (함수도 필드지만 필드와 함수를 구분해서 말함)
        System.out.println("나는 "+name+"이야."); // name -> this.name 가능
    }

}
public class L21Object {
    public static void main(String[] args) {
        new com.smu8.study.Person(); //객체생성
        new Person(); //같은 package에 있거나 import를 하면 생략 가능

        //변수 : data 재사용
        Person p=new Person(); //객체를 만들고 변수가 참조
        var p2=new Person(); // var : 타입을 생략
        System.out.println(p.id+", "+p.name+", "+p.birth);
        p.name="김태연";
        p.say();
        int i=10;
        System.out.println(i);
        // 기본형과 자료형의 차이
        // 1. 소문자로 시작
        // 2. 리터럴(보이는 그대로)하게 표기
        // 3. 필드가 없다. (기본형=원시 데이터)
    }
}
