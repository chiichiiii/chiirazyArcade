package com.smu8.homework;
import java.util.Scanner;
//String, int, boolean 기본 라이브러리 외의 lib 사용 시 import 사용 (다른 package)


public class H05WhileFor {
    public static void main(String[] args) {
        //반복문 2가지 while for
        boolean f=true;
        // 안녕을 5번만 출력
        int n=0;
        while (f){
            n++;
            System.out.println("하이");
            if(n==5) { //break;
                f=false;
            }
        }
        //for(int i): for의 내부에서만 사용되는 변수 (지역변수)
        for(int i=0;i<5;i++){
            System.out.print(i);
        }
        System.out.println("\n");
        for(int i=5;i>0;i--){ //상단의 i가 지역변수기 때문에 다른 지역에서도 선언 가능
            System.out.println(i);
        }
        System.out.println("\n");
        for(int i=1;i<=10;i++){
            if(i%2==0)
                System.out.println(i);
        }
        System.out.print("\n");
        for(int i=2;i<=10;i+=2){
            System.out.println(i);
        }
        System.out.print("\n");
        for(int i=1;i<=10;i++){
            if(i%2!=0) continue;
            System.out.println(i);
        }

        System.out.println("\n정수를 입력하세요.");
        //반복문 사용 이유 : 입력 대기
        //Scanner: 입력을 대기하는 반복문 (입력할 때까지 계속 대기)
        //객체 new 생성자(입력 받을 곳)
        Scanner sc=new Scanner(System.in); //콘솔에서 입력 받을 준비
        int inputNum=sc.nextInt(); //콘솔에서 입력하는 것을 대기하는 반복문 생성
        System.out.println("입력완료: "+inputNum);
        for(int i=1;i<=inputNum;i++){
            System.out.print("안녕!"+i);
        }

        String[] words = {"java", "array", "loop", "string"};
        int sum=0;
        for (int i=0;i<words.length;i++){
            int len=words[i].length();
            System.out.println(words[i]+": "+len);
            sum+=len;
        }
        System.out.println("총 길이: "+sum);


    }
}
