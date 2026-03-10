package com.smu8.javautil;

import java.util.Optional;

public class L17Optional2 {
    public static void main(String[] args) throws Throwable {
        //orElse(기본값이 간단할 때) orElseGet(기본값을 생성해야할 때: 비용이 든다)
        //Optional<String>strOpt=Optional.of("안녕!");
        Optional<String>strOpt=Optional.empty();
        //String str=strOpt.get(); //물어보지않고 그냥 가져오기(권장x)
        //System.out.println(str); //.NoSuchElementException

        String str=strOpt.orElse("인사");
        System.out.println(str);

        Optional<Integer>intOpt=Optional.empty();
        //System.out.println(intOpt.get());
        Integer i=intOpt.orElseGet(()->{
            //통신, 파일, 계산...... -> 기본값 생성
            return 0;
        });
        System.out.println(i);

        //orElseThrow: 없으면 오류! throw 오류를 생성, throws: 오류 처리를 위임
        // 404: 페이지가 없다 orElseThrow 구현됨
        Optional objOpt=Optional.empty();
        //Object obj=objOpt.orElseThrow(); //.NoSuchElementException
        //new IllegalArgumentException;
        Object obj=objOpt.orElseThrow(()->{
            return new IllegalArgumentException("받아온 데이터가 잘못됨"); //.IllegalArgumentException
        });

    }
}
