package com.smu8.game;

import java.util.Scanner;

public class L19Game {
    public static void main(String[] args) {
        System.out.println("구구단 출력 프로그램");

        int i=1;

        Scanner scanner=new Scanner(System.in);
        System.out.print("단을 입력하세요: ");
        int inputNum=scanner.nextInt();

            System.out.println(inputNum+"단");
        while (i<=9){
            System.out.println(inputNum+" x "+i+" = "+(inputNum*i));
            i++;
        }
    }
}
