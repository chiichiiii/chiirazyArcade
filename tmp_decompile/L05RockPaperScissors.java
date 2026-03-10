package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L05RockPaperScissors {
   public static void main(String[] args) {
      int SCISSORS = 0;
      int ROCK = 1;
      int PAPER = 2;
      int userWinCnt = 0;
      int comWinCnt = 0;
      System.out.println("가위 바위 보 게임입니다. 3번 이기세요.");

      while(userWinCnt < 3 && comWinCnt < 3) {
         int randomNum = (new Random()).nextInt(0, 3);
         Scanner scanner = new Scanner(System.in);
         System.out.print("가위=0, 바위=1, 보=2 중 1개를 입력: ");
         int inputNum = scanner.nextInt();
         String var10000;
         switch (inputNum) {
            case 0 -> var10000 = "가위";
            case 1 -> var10000 = "바위";
            case 2 -> var10000 = "보";
            default -> throw new IllegalStateException("오류");
         }

         String inputStr = var10000;
         switch (randomNum) {
            case 0 -> var10000 = "가위";
            case 1 -> var10000 = "바위";
            case 2 -> var10000 = "보";
            default -> throw new IllegalStateException("오류");
         }

         String randomStr = var10000;
         System.out.println(inputStr + " vs " + randomStr);
         if (inputNum == randomNum) {
            System.out.println("무승부");
         } else {
            int result = inputNum - randomNum;
            if (result != 1 && result != -2) {
               System.out.println("패배");
               ++comWinCnt;
            } else {
               System.out.println("승리");
               ++userWinCnt;
            }
         }
      }

      String msg = userWinCnt == 3 ? "유저 승리" : "컴퓨터 승리";
      System.out.println(msg + "\n유저 " + userWinCnt + " : 컴퓨터 " + comWinCnt);
   }
}
