package com.smu8.javautil;

import java.util.Optional;

public class L18Optional3 {
    public static void main(String[] args) {
        //map : 기존의 데이터를 바꾼다
        //문자열 => 정수로
        Optional<String> strOpt=Optional.of("113");
        Optional<Integer>intOpt=strOpt.map((str)->Integer.parseInt(str)); //"113"=>113
        int num=intOpt.orElse(0);
        System.out.println(num);
        strOpt=Optional.of("2233");
        num=strOpt
                .map((str)->Integer.parseInt(str)) // empty 일 때 map은 실행 안 되고 Optional 반환
                .orElse(0);
        System.out.println(num);

        strOpt=Optional.empty();
        //체이닝함수 : .으로 연결된 함수, 같은 타입을 반환(중간함수) ==JQuery
        num=strOpt
                .map(Integer::parseInt) //함수참조
                .orElse(0);
        System.out.println(num);
        //flatMap 평평하게 하는 flat: Optional 중첩 시 한 개 지우기

        strOpt=Optional.of("-113.13");
        Optional<Optional<Integer>> intOpt2=strOpt.map((str)->parseInt(str));
        Optional<Integer> intOpt3=strOpt.flatMap((str)->parseInt(str));

        //filter(null이 아니고 내가 원하는 데이터를 추출)
        //age>=0 age<=140 -7
        Optional<Integer>ageOpt=Optional.of(-7);
        int age=ageOpt
                .filter((n)->n>=0)
                .orElse(0);
        System.out.println(age);

        Optional<String>ageStrOpt=Optional.of("40");
        age=ageStrOpt.map((s)->Integer.parseInt(s)) //중간 연산자
                .filter((n)->n>=0) //중간 연산자
                .orElse(0); //최종 연산자
        System.out.println(age);





    }
    public static Optional<Integer>parseInt(String s){
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return Optional.empty();
        }
    }
}
