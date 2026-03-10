package com.smu8.study;

public class L25ArrayError {
    public static void main(String[] args) {
        //오류 : 컴파일 오류, 런타임 오류
        // 컴파일 오류 : 컴파일러(javac)가 발견해서 미리 조치
        // 런타임 오류 : 컴파일러가 발견하지 못해서 실행 중에 발생하는 오류(대위험)
        // 런타임 오류가 발생하면 프로그램이 멈춤
        //int i=""; // 컴파일 오류(배포(서비스)되지 않아 안전)
        int [] nums={10,20,30,40}; //길이가 4, 순서(index) 0~3

        //nums[4]=50; //배열은 처음 생성한 길이가 변경되지 않음
        //System.out.println(nums[4]);

        //System.out.println("이건 되나?"); // 오류 전 코드 실행은 됨
        //System.out.println(nums[4]); //런타임 오류
        //ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 4
        //Exception : 오류 (예외)
        System.out.println("오류발생 시 오류 다음 코드 실행 x");
    }
}
