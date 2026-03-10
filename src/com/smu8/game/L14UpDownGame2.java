package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L14UpDownGame2 {
    public static void main(String[] args) {
        System.out.println("Up Down 게임! 1~50까지 숫자를 맞춰보세요!(기회 5번)");
        Random random=new Random();
        int num= random.nextInt(1,51);
        final int MAX_COUNT=5;
        int count=0;
        boolean result=false;

        while(count++<MAX_COUNT) {

            System.out.print(count + "번째 기회: ");
            Scanner scanner = new Scanner(System.in);
            int intputNum = scanner.nextInt();
            if (intputNum == num) {
                System.out.println("정답!");
                result = true;
                break;
            } else {
                String msg = (intputNum > num) ? "Down" : "Up";
                System.out.println("오답 " + msg);
            }
        }
            if(result){
                System.out.println("----------WIN----------");
            }else {
                System.out.println("----------LOSE---------- 정답은: "+num);
            }

    }
}
