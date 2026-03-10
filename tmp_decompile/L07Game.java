package com.smu8.game;

import java.util.Scanner;

public class L07Game {
   public static void main(String[] args) {
      int PLUS = 1;
      int MINUS = 2;
      int TIMES = 3;
      int DIVIDED = 4;
      Scanner scanner = new Scanner(System.in);
      System.out.print("첫번째 숫자를 입력하세요.: ");
      int firstNum = scanner.nextInt();
      Scanner scanner1 = new Scanner(System.in);
      System.out.print("두번째 숫자를 입력하세요.: ");
      int secondNum = scanner1.nextInt();
      Scanner scanner2 = new Scanner(System.in);
      System.out.print("+=1, -=2, x=3, /=4 중 1개를 선택하세요: ");
      int inputNum = scanner2.nextInt();
      String var10000;
      switch (inputNum) {
         case 1 -> var10000 = "더하기";
         case 2 -> var10000 = "빼기";
         case 3 -> var10000 = "곱하기";
         case 4 -> var10000 = "나누기";
         default -> throw new IllegalStateException("잘못된 선택입니다.");
      }

      String inputStr = var10000;
      System.out.println(firstNum + inputStr + secondNum + "=?");
      if (inputNum == 1) {
         System.out.println("결과: " + (firstNum + secondNum));
      } else if (inputNum == 2) {
         System.out.println("결과: " + (firstNum - secondNum));
      } else if (inputNum == 3) {
         System.out.println("결과: " + firstNum * secondNum);
      } else if (inputNum == 4) {
         System.out.println("결과: " + firstNum / secondNum);
      }

   }
}
