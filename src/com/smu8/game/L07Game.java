package com.smu8.game;

import java.util.Scanner;

public class L07Game {
    public static void main(String[] args) {
        final int PLUS=1;
        final int MINUS=2;
        final int TIMES=3;
        final int DIVIDED=4;

        Scanner scanner=new Scanner(System.in);
        System.out.print("첫번째 숫자를 입력하세요.: ");
        int firstNum=scanner.nextInt();
        Scanner scanner1=new Scanner(System.in);
        System.out.print("두번째 숫자를 입력하세요.: ");
        int secondNum=scanner1.nextInt();
        Scanner scanner2=new Scanner(System.in);
        System.out.print("+=1, -=2, x=3, /=4 중 1개를 선택하세요: ");
        int inputNum=scanner2.nextInt();
        String inputStr=switch (inputNum){
            case PLUS -> "더하기";
            case MINUS -> "빼기";
            case TIMES -> "곱하기";
            case DIVIDED -> "나누기";
            default -> throw new IllegalStateException("잘못된 선택입니다.");
        };
        System.out.println(firstNum+inputStr+secondNum+"=?");
        if(inputNum==1){
            System.out.println("결과: "+(firstNum+secondNum));
        }else if(inputNum==2){
            System.out.println("결과: "+(firstNum-secondNum));
        } else if(inputNum==3){
            System.out.println("결과: "+(firstNum*secondNum));
        }else if(inputNum==4){
            System.out.println("결과: "+(firstNum/secondNum));
        }
    }
}
