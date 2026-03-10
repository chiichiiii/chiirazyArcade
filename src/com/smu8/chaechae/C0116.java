package com.smu8.chaechae;

public class C0116 {
    public static void main(String[] args) {
        System.out.println("gpt 문제 시작");

        int[] numbers = {3, 7, 12, 19, 25};
        boolean result=false;
        for (int i=0;i<numbers.length;i++){
            if(numbers[i]%2==0){
                result=true;
            }
        }
        if (result){
            System.out.println("짝수가 있습니다.");
        }else {
            System.out.println("짝수가 없습니다.");
        }
        System.out.println("-------------------");

        int[] numbers2 = {3, 7, 12, 19, 25};
        result=false;
        for (int i=0;i<numbers2.length;i++){
            if(numbers2[i]==0){
                result=true;
            }
            }
            if (result){
                System.out.println("품절 상품이 있습니다.");
            }else {
                System.out.println("모든 상품 재고가 있습니다.");
            }
        System.out.println("-------------------");


    }
}
