package com.smu8.game;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class L13BlackJackGPT {

    public static int setScore(String[] deckArr) {
        int sum = 0;
        int aceCount = 0;

        for (String card : deckArr) {
            if (card == null) break;

            String[] cardArr = card.split("_");
            int num = Integer.parseInt(cardArr[1]);

            if (num == 1) {
                aceCount++;
                sum += 11;
            } else if (num > 10) {
                sum += 10;
            } else {
                sum += num;
            }
        }

        // A가 21 초과 만들면 1로 조정
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }

        return sum;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        String[] deckArr = {
                "♠_1","♠_2","♠_3","♠_4","♠_5","♠_6","♠_7","♠_8","♠_9","♠_10","♠_11","♠_12","♠_13",
                "♥_1","♥_2","♥_3","♥_4","♥_5","♥_6","♥_7","♥_8","♥_9","♥_10","♥_11","♥_12","♥_13",
                "♦_1","♦_2","♦_3","♦_4","♦_5","♦_6","♦_7","♦_8","♦_9","♦_10","♦_11","♦_12","♦_13",
                "♣_1","♣_2","♣_3","♣_4","♣_5","♣_6","♣_7","♣_8","♣_9","♣_10","♣_11","♣_12","♣_13"
        };

        // 셔플
        String[] shuffle = new String[52];
        for (String card : deckArr) {
            while (true) {
                int idx = random.nextInt(52);
                if (shuffle[idx] == null) {
                    shuffle[idx] = card;
                    break;
                }
            }
        }

        String[] user = new String[11];
        String[] dealer = new String[11];
        int cardIndex = 0;

        // 최초 2장
        user[0] = shuffle[cardIndex++];
        dealer[0] = shuffle[cardIndex++];
        user[1] = shuffle[cardIndex++];
        dealer[1] = shuffle[cardIndex++];

        int userCnt = 2;
        int dealerCnt = 2;

        // 🎮 유저 턴
        while (true) {
            int userSum = setScore(user);
            System.out.println("\n유저 카드: " + Arrays.toString(user));
            System.out.println("유저 점수: " + userSum);

            if (userSum > 21) {
                System.out.println("💥 유저 버스트!");
                break;
            }

            System.out.print("카드를 더 받으시겠습니까? (1: 히트 / 0: 스탠드) ▶ ");
            int choice = sc.nextInt();

            if (choice == 1) {
                user[userCnt++] = shuffle[cardIndex++];
            } else {
                break;
            }
        }

        // 🃏 딜러 턴 (16 이하면 히트)
        while (setScore(dealer) < 17) {
            dealer[dealerCnt++] = shuffle[cardIndex++];
        }

        int userSum = setScore(user);
        int dealerSum = setScore(dealer);

        System.out.println("\n===== 결과 =====");
        System.out.println("유저 카드: " + Arrays.toString(user));
        System.out.println("딜러 카드: " + Arrays.toString(dealer));
        System.out.println("유저 점수: " + userSum);
        System.out.println("딜러 점수: " + dealerSum);

        // 🏆 승패 판정
        if (userSum > 21) {
            System.out.println("딜러 승!");
        } else if (dealerSum > 21) {
            System.out.println("유저 승!");
        } else if (userSum > dealerSum) {
            System.out.println("유저 승!");
        } else if (userSum < dealerSum) {
            System.out.println("딜러 승!");
        } else {
            System.out.println("무승부!");
        }

        sc.close();
    }
}
