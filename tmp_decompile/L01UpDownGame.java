package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L01UpDownGame {
   public static void main(String[] args) {
      Random random = new Random();
      int randomNum = random.nextInt(1, 11);
      Scanner scanner = new Scanner(System.in);
      System.out.print("숫자만 입력하세요: ");
      String inputStr = scanner.next();
      int inputNum = Integer.parseInt(inputStr);
      if (inputNum == randomNum) {
         System.out.println("정답!");
      } else {
         System.out.println("오답!");
      }

   }
}
