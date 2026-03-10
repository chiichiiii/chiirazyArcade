package com.smu8.study;

public class L20For {
    public static void main(String[] args) {
        //1~10 출력
        int n=0;
        while(n<10){
            n++;
            System.out.print(n);
            if(n!=10)System.out.print(",");
        }
        System.out.println("\n for 출력");
        for(int i=0;i<11;i++){
            System.out.print(i+",");

        }
        System.out.println("\n");
        //10~1 for로 거꾸로 출력
        for(int i=10;i>0;i--){
            System.out.print(i+",");
        }
        System.out.println("\n");
        // 1~20 까지 출력하다가 6의 배수를 제외하세요
        for (int i=0; i<21;i++){
            if(i%6==0) continue;
            System.out.print(i+",");
        }
        System.out.println("\n1~10000까지의 누적합 구하던 중 20000 이상일 때 중지");
        //1~50 합, 총합이 20000 이상이 될 때 멈추시오
        int sum=0;
        for (int i=1; i<=10000; i++){
            sum+=i;
            if (sum>=20000){
                System.out.println(i+"번째에 끝"); break;
            }
        }
        System.out.println("\n 1~10000까지의 누적합: "+sum);


    }
}
