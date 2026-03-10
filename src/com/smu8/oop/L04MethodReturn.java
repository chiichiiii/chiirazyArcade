package com.smu8.oop;

import java.util.Arrays;

class Validator{
    //배열 검사
    //1. 수가 모두 even(짝수)인지 검사
    public boolean isEven(int[]arr){
        boolean isEven=true;
        //배열탐색: 배열 중 홀수가 있으면 isEven=false
        //ex){2,4,6,8,9,10}
        for(int i=0;i<arr.length;i++){
            if(arr[i]%2!=0){
                isEven=false;
                break; //홀수가 발견되면 더이상 반복탐색을 할 이유가 없음 ==> 반복문 종료
            }
        }
        return isEven;
    }
    //배열 홀수 검사
    public boolean isOdd(int...arr){
    //    boolean isOdd=true;
        // 짝수가 1개라도 있으면 종료 => false 반환
        for(int i=0;i< arr.length;i++){
            if(arr[i]%2==0) return false;
            //return : 함수 종료 = 자동으로 반복문 break = 코드 길이가 줄어듦
        }
        return true;
    }



}

public class L04MethodReturn {

    public static void main(String[] args) {
        //return과 종료 시점
        int [] nums={2,4,6,8,9,10}; //리터럴 표기법(new 연산자 생략, 변수 선언(ex/ int[]nums=) 필수)
        int [] nums2=new int[5]; //일반적인 배열 선언[ Array 배열
        nums2[0]=2;
        nums2[1]=4;
        nums2[2]=6;
        nums2[3]=8;
        nums2[4]=10;
        //new int[]{10,30,40,50};
        System.out.println(Arrays.toString(new int[]{10,20,30}));

        Validator validator=new Validator();
        System.out.println(validator.isEven(nums)); //2,4,6,8,[9],10 false
        System.out.println(validator.isEven(nums2)); // 2,4,6,8,10 true
        System.out.println(validator.isEven(new int[]{10,20,30})); //10,20,30 true

        System.out.println("1,3,5,7,9는 모두 홀수인가?: "+validator.isOdd(1,3,5,7,9));
        System.out.println("1,3,5,6,7,9는 모두 홀수인가?: "+validator.isOdd(1,3,5,6,7,9));
    }
}
