package com.smu8.oop;
import java.lang.*; //String, Integer, Exception, Object...
//자바의 기본 패키지: 자바 문법에 필요한 기본 class 제공


class ObjTest {

}
//모든 class는 자동으로 Object를 extends 한다.
public class L15Object {
    public static void main(String[] args) {
        ObjTest objTest=new ObjTest();
        ObjTest objTest2=new ObjTest();
        System.out.println(objTest.toString()); //com.smu8.oop.ObjTest@2f4d3709 (타입+식별자(16진수))
        System.out.println(objTest.hashCode()); //793589513 : 객체의 식별자(10진수)
        //자료형의 == 동등비교연산 : 식별자(주소)비교
        System.out.println(objTest2.hashCode()); //1313922862
        System.out.println(objTest==objTest2); //false
        //object.equals() : 두 객체가 논리적으로 같은지(같은 타입, 같은 필드(상태)) => 무조건 재정의
        System.out.println(objTest.equals(objTest2)); //false
        //String.equals()는 문자열의 값을 비교하도록 재정의되어있음
        String str="안녕하세요";
        String str2=new String("안녕하세요");
        System.out.println(str==str2); //false
        System.out.println(str.equals(str2)); //true, 모든 자료형은 equals로 비교
    }
}
