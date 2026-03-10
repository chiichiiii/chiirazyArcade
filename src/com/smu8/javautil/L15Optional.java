package com.smu8.javautil;

import java.util.Optional;

public class L15Optional{
    public static void main(String[] args) {
        String str=null;
        //System.out.println(str.indexOf("A")); //.NullPointerException
        if(str!=null){
            System.out.println(str.indexOf("A"));
        }else{
            System.out.println("str에 데이터가 없음");
        }
        //checked 오류 : try catch가 아니면 찾을 수 없는 오류
        try {

        System.out.println(str.toUpperCase()); //.NullPointerException
        }catch (NullPointerException e){
            System.out.println("str 존재하지 않습니다. null");
        }

        Optional<String>strOpt=Optional.of("java"); //ofNullable(null); null 넣고 싶으면
        //Optional<String>strOpt=Optional.empty(); 값을 비우고 싶으면
        //Optional 은 if로 null 인지 검사를 권장
        if(strOpt.isPresent()){//str!=null
            String s=strOpt.get();
            System.out.println(s.toUpperCase());
        }else {
            System.out.println("strOpt 데이터가 null");
        }
        strOpt.ifPresentOrElse((s)->{
            System.out.println(s.toUpperCase()+": ifPresent");
        },()->{
            System.out.println("strOpt 데이터 null: OrElse");
        });
    }
}
