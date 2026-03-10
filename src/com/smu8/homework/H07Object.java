package com.smu8.homework;

public class H07Object {
    public static void main(String[] args) {
/*실습 1. 합계 함수 만들기
* 함수명 : sumTo
* 매개변수 : int n
* 기능 설명 : 1부터 n까지의 합을 계산하여 반환한다.
* 요구사항
 - 반복문을 사용한다
 - 계산 결과를 return 한다
*/
    }
    public int sumTo(int n){
        int sum=0;
        for(int i=1;i<=n;i++){
            sum+=i;
        }
        return sum;
    }
/* 실습 2. 최대값 함수 만들기
* 함수명 : max
* 매개변수 : int a, int b
* 기능 설명 : 두 정수 중 더 큰 값을 반환한다.
* 요구사항
 - 조건문(if)을 사용한다
 - 두 값이 같을 경우 하나를 반환해도 무방하다
 */
    public int max(int a, int b){
        if(a>b){
            return a;
        } else {
            return b;
        }
    }
/* 실습 3. 검증 함수 만들기
* 함수명 : isEven
* 매개변수 : int n
* 기능 설명 : 전달된 정수가 짝수이면 true, 홀수이면 false를 반환한다.
* 요구사항
 - 나머지 연산자(%)를 사용한다
 - boolean 값을 반환한다
*/
    public boolean isEven(int n){
        boolean result=true;
        if(n%2==0){
            return result;
        } else {
            result=false;
            return result;
        }
    }
}
