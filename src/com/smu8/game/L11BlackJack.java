package com.smu8.game;
import java.util.Arrays;
import java.util.Random;

public class L11BlackJack {

    public static int setScore(String []deckArr){
        int sum = 0;
        for (int i = 0; i < deckArr.length; i++) {
            String card=deckArr[i];
            if(card==null) break;
            String [] cardArr=card.split("_"); //♣_13 => {"♣","13"}
            int num=Integer.parseInt(cardArr[1]);//"13"=>13
            if(num==1){ //카드 A 는 1점 OR 11점
                if(sum+11<=21){ //11점으로 취급했을시 21점을 넘으면 1점 취금
                    sum+=11;
                }else {
                    sum+=1;
                }
            } else if (num>10) { //J 11,Q 12,K 13 모두 10점
                sum+=10;
            }else {
                sum+=num;
            }
        }
        return sum;
    }
    public static void main(String[] args) {
        String[] deckArr = {
                "♠_1","♠_2","♠_3","♠_4","♠_5","♠_6","♠_7","♠_8","♠_9","♠_10","♠_11","♠_12","♠_13",           "♥_1","♥_2","♥_3","♥_4","♥_5","♥_6","♥_7","♥_8","♥_9","♥_10","♥_11","♥_12","♥_13",      "♦_1","♦_2","♦_3","♦_4","♦_5","♦_6","♦_7","♦_8","♦_9","♦_10","♦_11","♦_12","♦_13",   "♣_1","♣_2","♣_3","♣_4","♣_5","♣_6","♣_7","♣_8","♣_9","♣_10","♣_11","♣_12","♣_13"
        };//52개 0~51
        String[] shuffleDeckArr=new String[52]; //섞어서 참조할 곳
        //유저와 딜러는 각각 최대 11개까지의 카드를 받을 수 있다.
        String[] userDeckArr=new String[11];
        String[] dealerDeckArr=new String[11];
        Random random=new Random();
        int cnt=0;


        //카드 셔플
        for (int i = 0; i < deckArr.length; i++) {
            String card=deckArr[i];
            while (true){
                int randomIndex=random.nextInt(52);
                if(shuffleDeckArr[randomIndex]==null){
                    //shuffleDeckArr의 랜덤한 위치에 null 일때만 카드를 추가
                    shuffleDeckArr[randomIndex]=card;
                    break;
                }
            }
        }
        System.out.println("셔플 : "+Arrays.toString(shuffleDeckArr));
        //최초 유저와 딜러는 카드 2개씩가짐
        userDeckArr[0]=shuffleDeckArr[0];
        dealerDeckArr[0]=shuffleDeckArr[1];
        userDeckArr[1]=shuffleDeckArr[2];
        dealerDeckArr[1]=shuffleDeckArr[3];
        System.out.println("유저 :"+Arrays.toString(userDeckArr));
        System.out.println("딜러 :"+Arrays.toString(dealerDeckArr));
        int userSum=setScore(userDeckArr);
        System.out.println("유저 점수 합 :"+userSum);
        int dealerSum=setScore(dealerDeckArr); //점수 계산을 함수로 변경
        System.out.println("딜러 점수 합 :"+dealerSum);

    }
}
