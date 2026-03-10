package com.smu8.game;

import java.util.Random;
import java.util.Scanner;

public class L03UpDownGame {
    public static void main(String[] args) {
        System.out.println("Up Down 게임 1~50까지 수를 입력하세요!(기회는 5번)");
        Random random=new Random();
        int num=random.nextInt(1,51);

        final int MAX_COUNT=5; //입력 받는 최대 수
        // for(int i=0;i<5;i++){}
        int count=0; // 입력 받은 수
        boolean result=false;

        while(count++<MAX_COUNT){
            System.out.print(count+"번째 기회: ");
            Scanner scanner=new Scanner(System.in);
            int inputNum=scanner.nextInt();

            if(inputNum==num){
                System.out.println("정답입니다.");
                result=true;
                break;
            }else {
                String msg=(inputNum>num)? "Down":"Up";
                System.out.println("오답"+msg);
            }
        }

        if(result){
            System.out.println("--------Win--------");
        }else {
            System.out.println("--------Lose-------- 답은 "+num+"입니다.");
        }
    }
}
