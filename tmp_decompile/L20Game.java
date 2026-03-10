package com.smu8.game;

import java.util.Arrays;
import java.util.Random;

public class L20Game {
   public static void main(String[] args) {
      Random random = new Random();
      int[] numArr = new int[]{1, 2, 3, 4, 5, 6};

      for(int i = 0; i < numArr.length; ++i) {
         int num = numArr[i];
         int randomIndex = random.nextInt(numArr.length);
         int randomNum = numArr[randomIndex];
         numArr[i] = randomNum;
         numArr[randomIndex] = num;
      }

      System.out.println(Arrays.toString(numArr));
      String[] deckArr = new String[]{"◆_A", "◆_2", "◆_3", "◆_4", "◆_5", "◆_6", "◆_7", "◆_8", "◆_9", "◆_10", "◆_J", "◆_Q", "◆_K", "♥_A", "♥_2", "♥_3", "♥_4", "♥_5", "♥_6", "♥_7", "♥_8", "♥_9", "♥_10", "♥_J", "♥_Q", "♥_K", "♠_A", "♠_2", "♠_3", "♠_4", "♠_5", "♠_6", "♠_7", "♠_8", "♠_9", "♠_10", "♠_J", "♠_Q", "♠_K", "♣_A", "♣_2", "♣_3", "♣_4", "♣_5", "♣_6", "♣_7", "♣_8", "♣_9", "♣_10", "♣_J", "♣_Q", "♣_K"};

      for(int i = 0; i < deckArr.length; ++i) {
         String card = deckArr[i];
         int randomNum = random.nextInt(deckArr.length);
         String randomCard = deckArr[randomNum];
         deckArr[i] = randomCard;
         deckArr[randomNum] = card;
      }

      System.out.println(Arrays.toString(deckArr));
   }
}
