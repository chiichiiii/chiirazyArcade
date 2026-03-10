package com.smu8.study;

import java.util.Arrays;

public class L29ArrayFor {
    public static void main(String[] args) {
        //선생님 점수(정수)
        int[] scoreArr=new int[7];
        // int[]: 정수 배열 타입
        // new int[7] : Array 객체를 생성, 길이가 7
        scoreArr[0]=88;
        scoreArr[1]=90;
        scoreArr[2]=75;
        scoreArr[3]=85;
        scoreArr[4]=99;
        scoreArr[5]=65;
        scoreArr[6]=78;
        //scoreArr[7]=100; //ArrayIndexOutOfBoundsException
        System.out.println(Arrays.toString(scoreArr)); //[88, 90, 75, 85, 99, 65, 78]

        //Q. 90점 이상은 몇 명인가?
        int cnt=0;
        if(scoreArr[0]>=90)cnt++;
        if(scoreArr[1]>=90)cnt++;
        if(scoreArr[2]>=90)cnt++;
        if(scoreArr[3]>=90)cnt++;
        if(scoreArr[4]>=90)cnt++;
        if(scoreArr[5]>=90)cnt++;
        if(scoreArr[6]>=90)cnt++;
        System.out.println(cnt+"명");
        System.out.println(scoreArr.length); //길이 :7


        cnt=0;
        for(int i=0;i<scoreArr.length;i++){
            //System.out.println(i);
            if(scoreArr[i]>=90) cnt++;
        }
        System.out.println("90점 이상을 받은 학생: "+cnt+"명");

        int[] numArr={39,89,-8,56,12,21,-94};
        //Q.numArr 에 음수가 몇 개인가?
        cnt=0;
        for(int i=0;i<numArr.length;i++){
            if(numArr[i]<0) cnt++;
        }
        System.out.println("음수는 "+cnt+"개다.");

        //리터럴 표기 시 변수를 선언 할때만 가능
        //변수를 ㄹ재사용하고 싶으면 객체생성 후 사용
        String[] strArr={}; //strArr 변수를 선언 시 리터럴 표기 가능
        strArr=new String[]{}; //변수를 재사용시는 객체 생성 후 리터럴 표기 사용
        scoreArr=new int[]{55,99,100,77,120,-39,20}; // 100점이 넘는 친구와 점수가 음수인 경우가 포함
        //Q. 0~100점 사이의 점수만 합하시오.
        int sum=0;
        for(int i=0;i<scoreArr.length;i++){
            if(scoreArr[i]<0||scoreArr[i]>100) continue;
            sum+=scoreArr[i];
        }
        System.out.println(sum);
    }
}
