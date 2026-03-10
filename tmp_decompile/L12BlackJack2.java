package com.smu8.game;

import java.util.Arrays;
import java.util.Random;

public class L12BlackJack2 {
   public static int setScore(String[] deckArr) {
      int sum = 0;

      for(int i = 0; i < deckArr.length; ++i) {
         String card = deckArr[i];
         if (card == null) {
            break;
         }

         String[] cardArr = card.split("_");
         int num = Integer.parseInt(cardArr[1]);
         if (num == 1) {
            if (sum + 11 <= 21) {
               sum += 11;
            } else {
               ++sum;
            }
         } else if (num > 10) {
            sum += 10;
         } else {
            sum += num;
         }
      }

      return sum;
   }

   public static void main(String[] args) {
      String[] deckArr = new String[]{"♠_1", "♠_2", "♠_3", "♠_4", "♠_5", "♠_6", "♠_7", "♠_8", "♠_9", "♠_10", "♠_11", "♠_12", "♠_13", "♥_1", "♥_2", "♥_3", "♥_4", "♥_5", "♥_6", "♥_7", "♥_8", "♥_9", "♥_10", "♥_11", "♥_12", "♥_13", "♦_1", "♦_2", "♦_3", "♦_4", "♦_5", "♦_6", "♦_7", "♦_8", "♦_9", "♦_10", "♦_11", "♦_12", "♦_13", "♣_1", "♣_2", "♣_3", "♣_4", "♣_5", "♣_6", "♣_7", "♣_8", "♣_9", "♣_10", "♣_11", "♣_12", "♣_13"};
      String[] shuffleDeckArr = new String[52];
      String[] userDeckArr = new String[11];
      String[] dealerDeckArr = new String[11];
      Random random = new Random();
      int cnt = 0;

      for(int i = 0; i < deckArr.length; ++i) {
         String card = deckArr[i];

         int randomIndex;
         do {
            randomIndex = random.nextInt(52);
         } while(shuffleDeckArr[randomIndex] != null);

         shuffleDeckArr[randomIndex] = card;
      }

      System.out.println("셔플: " + Arrays.toString(shuffleDeckArr));
      userDeckArr[0] = shuffleDeckArr[0];
      dealerDeckArr[0] = shuffleDeckArr[1];
      userDeckArr[1] = shuffleDeckArr[2];
      dealerDeckArr[1] = shuffleDeckArr[3];
      System.out.println("유저: " + Arrays.toString(userDeckArr));
      System.out.println("딜러: " + Arrays.toString(dealerDeckArr));
      int userSum = setScore(userDeckArr);
      System.out.println("유저 점수 합: " + userSum);
      int dealerSum = setScore(dealerDeckArr);
      System.out.println("딜러 점수 합: " + dealerSum);
   }
}
