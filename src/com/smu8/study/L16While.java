package com.smu8.study;

public class L16While {
    public static void main(String[] args) throws InterruptedException {
        //while 반복문 (반복적으로 일을 수행하는 것)
        /* "안녕"을 너무 빠르게 출력해서 메모리가 넘칠 수도 있다.(메모리 오버플로우)
        while (true){
            System.out.println("안녕");
        }
         */
        while (true){
            //일 == Thread
            Thread.sleep(1000); //밀리초 == 1/1000초
            //일꾼이 쉬면 오류 발생할 수 있기 때문에 오류 처리를 해야된다.(예외를 강제)
            System.out.println("잘가");
        }
    }
}
