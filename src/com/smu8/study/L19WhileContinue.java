package com.smu8.study;

public class L19WhileContinue {
    public static void main(String[] args) {
        //1~1000 합을 구하는데 4와 7의 배수는 제외하고... 구하라
        //1+2+3+[4]+5+6+[7]+[8]+9+10+.......+[1000]
        //continue => 특정 반복 실행을 건너 뜀
        int i=0;
        int sum=0;
        while(i<1000){
            ++i; //증감식이 continue보다 항상 위에 있어야 무한 반복을 피한다.
            if(i%4==0||i%7==0) continue;
            sum+=i;

        }
        System.out.println(sum);
    }
}
