package com.smu8.javautil;

public class L02ThrowException {

    static void exceptionTest(){
        throw new IllegalArgumentException("매개변수가 잘못됨");
    }

    public static void main(String[] args) {
        System.out.println("오류를 강제로 발생");
        //error, exception(예상하는 오류 => 객체)
        //오류를 강제로 발생: 예상하는 오류를 발생시키기 위해
        //throw new Exception("");
        try{
            exceptionTest();
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        System.out.println("오류가 발생하면 다음 코드는 실행되지 않음!");
    }
}
