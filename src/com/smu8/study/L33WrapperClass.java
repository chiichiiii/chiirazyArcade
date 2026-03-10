package com.smu8.study;

public class L33WrapperClass {
    public static void main(String[] args) {
        //랩퍼 클래스 : 기본형의 자료형 (기본형을 자료형으로 사용해야해서, 유형한 기능 사용)
        String str="333";
        String str2="444";
        //문자열을 정수로 바꾼 후 더한 결과
        System.out.println(str+str2);
        int a=Integer.parseInt(str);
        int b=Integer.parseInt(str2);
        System.out.println(a+b);
        //Byte,Short,Integer,Long,Float,Double,Character,Boolean
        //Integer 가 문자열을 정수로

        a=45678;
        b=90;
        //System.out.println(a+""+b);
        System.out.println(Integer.toString(a)+Integer.toString(b));
        //Integer 가 정수를 문자열로

        //int 의 최대, 최소값 확인
        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MIN_VALUE);

        System.out.println(Double.parseDouble("10.001")+Double.parseDouble("11.11"));

        //parse* (*전부:와일드카드, 조커) : 강제형 변환 (오류 동반)
        //int i=Integer.parseInt("A"); //.NumberFormatException
        //런타입오류 : jvm 이 종료되기 때문에 위험한 오류(예외처리해야함try-catch)


        //기본형 => 자료형
        Integer obj=10; //자동형변환 Auto Boxing
        Integer obj2=Integer.valueOf(10); // 정상적인 방법
        //Integer obj3=new Integer(10); // 값과 상관없이 매번 객체 생성 (사용 권장 x)
        System.out.println(obj.toString());
        System.out.println(obj.shortValue());
        //System.out.println(obj.hashCode()); //hashCode == 저장된 주소
        System.out.println(obj+obj2); //자료형 -> 기본형으로 형변환 후 연산

    }
}
