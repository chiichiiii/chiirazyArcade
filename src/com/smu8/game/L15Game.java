package com.smu8.game;

import java.util.Scanner;

public class L15Game {
    public static void main(String[] args) {
        System.out.println("홀짝 판별 프로그램");
        boolean result=false;

        Scanner scanner=new Scanner(System.in);
        System.out.print("숫자를 입력하세요.: ");
        int inputNum=scanner.nextInt();
        if(inputNum%2==0){
            System.out.println("짝수입니다.");
        }else {
            System.out.println("홀수입니다.");
        }
    }
}
