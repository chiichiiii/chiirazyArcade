package com.smu8.game;

import java.util.Scanner;

public class L09Game {
   public static void main(String[] args) {
      int sum = 0;
      Scanner scanner = new Scanner(System.in);
      System.out.print("숫자를 입력하세요: ");
      int inputNum = scanner.nextInt();

      for(int i = 0; inputNum >= i; ++i) {
         sum += i;
      }

      System.out.println("합계: " + sum);
   }
}
