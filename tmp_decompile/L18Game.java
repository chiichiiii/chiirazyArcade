package com.smu8.game;

import java.util.Scanner;

public class L18Game {
   public static void main(String[] args) {
      System.out.println("1부터 N까지의 합 구하기");
      int sum = 0;
      Scanner scanner = new Scanner(System.in);
      System.out.print("숫자를 입력하세요: ");
      int inputNum = scanner.nextInt();

      for(int i = 1; i <= inputNum; ++i) {
         sum += i;
      }

      System.out.println("합계: " + sum);
   }
}
