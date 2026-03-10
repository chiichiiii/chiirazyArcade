package com.smu8.game;

import java.util.Scanner;

public class L06Game {
   public static void main(String[] args) {
      boolean result = false;
      Scanner scanner = new Scanner(System.in);
      System.out.print("숫자를 입력하세요: ");
      int inputNum = scanner.nextInt();
      if (inputNum % 2 == 0) {
         result = true;
         System.out.println("짝수입니다.");
      } else {
         System.out.println("홀수입니다.");
      }

   }
}
