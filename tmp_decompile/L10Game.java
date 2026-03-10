package com.smu8.game;

import java.util.Scanner;

public class L10Game {
   public static void main(String[] args) {
      int i = 0;
      Scanner scanner = new Scanner(System.in);
      System.out.print("단을 입력하세요.: ");
      int inputNum = scanner.nextInt();

      while(i < 9) {
         ++i;
         System.out.println(inputNum + " x " + i + " = " + inputNum * i);
      }

   }
}
