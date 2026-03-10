package com.smu8.game;

import java.util.Scanner;

public class L16Game {
    public static void main(String[] args) {

        final int PLUS=1;
        final int MINUS=2;
        final int TIMES=3;
        final int DIVIDED=4;

        System.out.println("사칙연산 선택 계산기");

        Scanner scanner=new Scanner(System.in);
        System.out.print("첫번째 숫자: ");
        int firstNum=scanner.nextInt();

        Scanner scanner1=new Scanner(System.in);
        System.out.print("두번째 숫자: ");
        int secondNum=scanner.nextInt();

        Scanner scanner2=new Scanner(System.in);
        System.out.print("연산 선택 (1:+, 2:-, 3:*, 4:/): ");
        int inputNum= scanner.nextInt();

        String inputstr= switch(inputNum){
            case PLUS -> "더하기";
            case MINUS -> "빼기";
            case TIMES -> "곱하기";
            case DIVIDED -> "나누기";
            default -> throw new IllegalStateException("잘못된 선택입니다.");
        };
        if(inputNum==1){
            System.out.println(firstNum+" + "+secondNum+" = "+(firstNum+secondNum));
        } else if (inputNum==2) {
            System.out.println(firstNum+" - "+secondNum+" = "+(firstNum-secondNum));
        } else if (inputNum==3) {
            System.out.println(firstNum+" x "+secondNum+" = "+(firstNum*secondNum));
        } else if (inputNum==4) {
            System.out.println(firstNum+" / "+secondNum+" = "+(firstNum/secondNum));
        } else {
            System.out.println("잘못된 선택입니다.");
        }

    }
}
