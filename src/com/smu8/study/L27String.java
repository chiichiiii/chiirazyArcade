package com.smu8.study;

public class L27String {
    public static void main(String[] args) {
        String s="안녕";
        s+="하세요";
        System.out.println(s);
        //"안녕" -> "안녕하세요"로 바뀌지 않는다. (불변성, 불변데이터[기본형, 문자열])
        //"안녕" "하세요" "안녕하세요" 모두 존재
        s="안녕";
        String s2="하세요";
        // 문자열 +연산 (배열의 더하기 concat)
        System.out.println(s.concat(s2)); //s+s2
        System.out.println(s);
        System.out.println(s2);
        //불변성 증명 s가 안녕하세요로 바뀌지 않았다.
        System.out.println("태연".concat("입니다")); // .은 객체만 접근 가능

        s="hello";
        s2="hello";
        //기본형 == : 값을 비교 (0==-0.0, 8==8.0) 다른 데이터지만 값은 같기 때문에 true
        //자료형 == : 주소를 비교 (완전히 같은 data)
        System.out.println(s==s2); // true 문자열은 문자열 상수풀에서 똑같은 문자를 찾아서 참조
        s2=new String("hello"); //그냥 객체를 만들거임
        System.out.println(s==s2); // false


    }
}
