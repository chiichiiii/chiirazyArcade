package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L04RockPaperScissors {
    public static void main(String[] args) {
        final int SCISSORS=0;
        final int ROCK=1;
        final int PAPER=2;
        int randomNum=new Random().nextInt(0,3);
        System.out.println("가위 바위 보 게임입니다. 3번 이기세요.");
        Scanner scanner=new Scanner(System.in);
        System.out.print("가위=0, 바위=1, 보=2 중 1개를 입력: ");
        int inputNum=scanner.nextInt();

        String inputStr=switch(inputNum) {
            case ROCK -> "바위";
            case SCISSORS -> "가위";
            case PAPER -> "보";
            default -> throw new IllegalStateException("오류 발생");
        };
        String randomStr=switch(randomNum){
            case ROCK -> "바위";
            case SCISSORS -> "가위";
            case PAPER -> "보";
            default -> throw new IllegalStateException("오류 발생");
        };

        System.out.println(inputStr+" vs "+randomStr);

        if(inputNum==randomNum){
            System.out.println("무승부");
        } else {
            int result=inputNum-randomNum;
            if(result==1||result==2){
                System.out.println("승리");
            }else {
                System.out.println("패배");
            }
        }
    }
}
