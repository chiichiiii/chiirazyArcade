package com.smu8.oop;

import java.time.LocalDate;
import java.util.Date;
import java.util.Random;

//class Calc{} // com.smu8.oop.Calc가 이미 존재해서 오류
class Calculator{
    //오버로딩, 이름은 한 개인데 역할이 여러개 => 다형성(객체지향문법 특징)
    public int sum(int a,int b){
        return a+b;
    }
    public int sum(int a,int b,int c){
        return a+b+c;
    }
    int a; //전역변수
    //매개변수가 동일한 것을 여러개 처리하고 싶다면 매개변수르 배열로
    public int sum(int []arr){
        //ex) {10,11,30,20}
        int sum=0; //지역변수 : 함수가 호출되어야 만들어짐(함수명과는 무관)
        for(int i=0;i<arr.length;i++){
            sum+=arr[i];
        }
        return sum;
    }
    // ... 가변인자
    public int multiple(int...nums){ //== int [] nums
        int mult=1; //n*0=0 곱셈의 초기값은 1
        //System.out.println(nums); //[I@2f4d3709 [ : Array, I : int, 2f4d3709 : 주소
        for (int i=0; i<nums.length; i++){
            mult*=nums[i];
        }

        return mult;
    }
}
public class L03MethodArgs {
    public static void main(String[] args) {
        Calculator c=new Calculator();
        int result=c.multiple(10,20);
        System.out.println("10x20의 결과: "+result);
        result=c.multiple(10,20,30);
        System.out.println("10x20x30의 결과: "+result);
        result=c.multiple(10,20,30,40);
        System.out.println("10x20x30x40의 결과: "+result);
        result=c.multiple(10,20,30,40,50);
        System.out.println("10x20x30x40x50의 결과: "+result);




        int [] arr={100,200,121,-300,-17,99};
        int sum=c.sum(arr);
        System.out.println("arr의 아이템 총합: "+sum);
        sum=c.sum(new int[]{11,22,33});
        System.out.println("arr의 아이템 총합: "+sum);
        //c.sum(10,20,30,40,50,60)
        //c.sum(new int[]{10,200,300} x

        //오늘 날짜 java.util.Date
        Date now=new Date();
        System.out.println(now.toString()); //Fri Jan 23 10:58:07 KST 2026
        String nowStr= now.toLocaleString();
        System.out.println(nowStr); //2026. 1. 23. 오전 10:59:19

        Random random=new Random(); //랜덤한 숫자를 출력
        int num=random.nextInt(); //범위: int값 내
        System.out.println(num);
        num=random.nextInt(1,51); //범위: 1~50
        //1,51 전달인자
        System.out.println(num);



    }
}
