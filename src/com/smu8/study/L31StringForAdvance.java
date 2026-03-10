package com.smu8.study;

import java.util.Arrays;

public class L31StringForAdvance {
    public static void main(String[] args) {
        //2차원 배열
        int [][] numArr={{0,1},{2,3},{4,5}}; //배열 안에 배열

        System.out.println(numArr[0][1]); //1
        System.out.println(numArr[2][0]); //4


        for (int i=0; i<numArr.length; i++){
            int[]arr=numArr[i];
            System.out.println(Arrays.toString(arr)); //[0, 1][2, 3][4, 5]
        for(int j=0;j<numArr[i].length;j++){
            System.out.println(numArr[i][j]);
            }
        }

        String []strArr={"ab","cd","efA","gh"}; //2차원 배열
        //{"ab","cd","ef","gh"} == {{'a','b'},{'c','d'},{'e','f','A'},{'g','h'}}
        boolean result=true;
        // 반복문에 이름 부여
        outer: for(int i=0;i<strArr.length;i++){
            String s=strArr[i];
            System.out.println(s);
            for (int j=0;j<s.length();j++){
                char c=s.charAt(j);
                if (c>='A'&&c<='Z'){
                    result=false; break outer;
                }
            }
        }
        System.out.println("\nstrArr의 문자열은 모두 소문자다: "+result);






    }
}
