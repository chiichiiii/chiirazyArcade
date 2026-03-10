package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L03UpDownGame {
   public static void main(String[] args) {
      System.out.println("Up Down 게임 1~50까지 수를 입력하세요!(기회는 5번)");
      Random random = new Random();
      int num = random.nextInt(1, 51);
      int MAX_COUNT = 5;
      int count = 0;
      boolean result = false;

      while(count++ < 5) {
         System.out.print(count + "번째 기회: ");
         Scanner scanner = new Scanner(System.in);
         int inputNum = scanner.nextInt();
         if (inputNum == num) {
            System.out.println("정답입니다.");
            result = true;
            break;
         }

         String msg = inputNum > num ? "Down" : "Up";
         System.out.println("오답" + msg);
      }

      if (result) {
         System.out.println("--------Win--------");
      } else {
         System.out.println("--------Lose-------- 답은 " + num + "입니다.");
      }

   }
}
