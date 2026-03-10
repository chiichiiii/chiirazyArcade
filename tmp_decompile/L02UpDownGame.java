package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L02UpDownGame {
   public static void main(String[] args) {
      Random random = new Random();
      int num = random.nextInt(1, 51);
      Scanner scanner = new Scanner(System.in);
      System.out.println("랜덤 게임 숫자 맞추기");
      System.out.print("1~50 중에 숫자를 맞춰보세요.");
      int inputNum = scanner.nextInt();
      if (inputNum == num) {
         System.out.println("정답입니다.");
      } else {
         System.out.println("오답입니다.");
      }

   }
}
