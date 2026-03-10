package com.smu8.javautil;

public class L01Exception {
    public static void main(String[] args) {
        //JVM 자동으로 처리하는 예외
        int [] nums={100,200,300};
        //nums[3]=400; //ArrayIndexOutOfBoundsException
         try {
             nums[4]=500;
             System.out.println("오류가 발생하면 다음 코드는 실행되지 않습니다.");
             }catch (ArrayIndexOutOfBoundsException e){
             System.out.println(e.getMessage());
             System.out.println("배열의 범위가 아닙니다.");
         }
        System.out.println("예외를 처리하면 jvm이 멈추지 않는다.");
    }
}
