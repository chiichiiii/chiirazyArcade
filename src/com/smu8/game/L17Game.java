package com.smu8.game;

import java.util.Scanner;

public class L17Game {
    public static void main(String[] args) {
        System.out.println("시험 점수 등급 판별 프로그램");

        Scanner scanner=new Scanner(System.in);
        System.out.print("점수를 입력하세요: ");
        int inputNum=scanner.nextInt();

        if(inputNum<0||inputNum>100){
            System.out.println("잘못된 점수입니다.");
        } else if (inputNum>=90) {
            System.out.println("등급: A");
        } else if (inputNum>=80) {
            System.out.println("등급: B");
        } else if (inputNum>=70) {
            System.out.println("등급: C");
        } else {
            System.out.println("등급: F");
        }
    }
}
