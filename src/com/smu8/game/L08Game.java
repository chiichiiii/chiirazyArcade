package com.smu8.game;

import java.util.Scanner;

public class L08Game {
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        System.out.print("점수를 입력하세요(0~100점): ");
        int inputNum=scanner.nextInt();
        if(inputNum>=90&&inputNum<=100){
            System.out.println("등급: A");
        } else if (inputNum>=80&&inputNum<90) {
            System.out.println("등급: B");
        } else if (inputNum>=70&&inputNum<80) {
            System.out.println("등급: C");
        } else if (inputNum<=60) {
            System.out.println("등급: F");
        } else{
            System.out.println("잘못된 점수입니다.");
        }
    }
}
