package com.smu8.game;

import java.util.Scanner;

public class L16Game {
   public static void main(String[] args) {
      int PLUS = 1;
      int MINUS = 2;
      int TIMES = 3;
      int DIVIDED = 4;
      System.out.println("사칙연산 선택 계산기");
      Scanner scanner = new Scanner(System.in);
      System.out.print("첫번째 숫자: ");
      int firstNum = scanner.nextInt();
      new Scanner(System.in);
      System.out.print("두번째 숫자: ");
      int secondNum = scanner.nextInt();
      new Scanner(System.in);
      System.out.print("연산 선택 (1:+, 2:-, 3:*, 4:/): ");
      int inputNum = scanner.nextInt();
      switch (inputNum) {
         case 1 -> String var14 = "더하기";
         case 2 -> String var13 = "빼기";
         case 3 -> String var12 = "곱하기";
         case 4 -> String var10000 = "나누기";
         default -> throw new IllegalStateException("잘못된 선택입니다.");
      }

      if (inputNum == 1) {
         System.out.println(firstNum + " + " + secondNum + " = " + (firstNum + secondNum));
      } else if (inputNum == 2) {
         System.out.println(firstNum + " - " + secondNum + " = " + (firstNum - secondNum));
      } else if (inputNum == 3) {
         System.out.println(firstNum + " x " + secondNum + " = " + firstNum * secondNum);
      } else if (inputNum == 4) {
         System.out.println(firstNum + " / " + secondNum + " = " + firstNum / secondNum);
      } else {
         System.out.println("잘못된 선택입니다.");
      }

   }
}
