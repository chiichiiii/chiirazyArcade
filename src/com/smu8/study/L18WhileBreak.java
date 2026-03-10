package com.smu8.study;

public class L18WhileBreak {
    public static void main(String[] args) {
        //0부터 2의 배수를 더하고 싶은데 10,000이 될 때까지 >> break
        int num=0;
        int sum=0;
        while (true){
            num+=2; //2씩 증가
            sum+=num;//sum=sum+num; => sum에 2의 배수 누적
            if(sum>=10000) break; // 반복문 종료
        }
        // 반복문의 반복수와 상관없이 종료될 때 : break
        System.out.println(sum); //10100

        //3의 배수의 누적합을 구하는데 2000이상일 때 종료
        // 종료할때까지 몇 번 걸렸나 알고 싶을 때
        num=0;
        sum=0;
        int i=0;
        while(true){
            i++;
            num+=3;
            sum+=num;
            if(sum>=2000) break; //2109 // if문은 실행문이 1줄일 때 {}중괄호 생략 가능
        }
        System.out.println(sum);
        System.out.println("누적 횟수: "+i);
    }
}
