package com.smu8.javautil;

public class L37StringBuilder {
    public static void main(String[] args) {
        //문자열이 불변성을 갖기때문에 문자열 누적 시 문제가 발생
        String str="";
        long start=System.nanoTime();
        for(int i=0; i<100_000;i++){
            str+=i;
        }
        long end=System.nanoTime();
        System.out.println(end-start); //2698220500
        //System.out.println(str);

        StringBuilder sb=new StringBuilder(); //""
        start=System.nanoTime();
        for(int i=0; i<10_000_000;i++){
            sb.append(i); //str+=i
        }
        end=System.nanoTime();
        System.out.println(end-start); //2776700
    }
}
