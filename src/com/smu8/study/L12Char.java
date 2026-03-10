package com.smu8.study;

public class L12Char {
    public static void main(String[] args) {
        char c='a'; // "큰 따옴표", '작은 따옴표', `백틱`(js)
        System.out.println(c);
        c=77;
        System.out.println(c); // 77은 아스키 코드에서 M
        c=17891;
        System.out.println(c); // 17891은 utf-16에서 䗣
        // c=111111; 문자는 16비트 정수기때문에 더 큰 수는 표현 불가
        // c='😃'; java는 고정길이 utf-16이기 때문에 4byte 크기의 이모지는 문자로 사용 불가(문자열로 사용 가능)
        String s="이모지는 4byte기 때문에 문자열로 사용: 😊"; // Surrogate Pair (서로게이트 페어)
        System.out.println(s);
        c='\u0041'; // == 0041(16진수) == 65(10진수)
        System.out.println(c);
        c='\u9999';
        System.out.println(c);
        c='\uA9FC';
        System.out.println(c);

    }
}
