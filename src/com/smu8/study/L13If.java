package com.smu8.study;

public class L13If {
    public static void main(String[] args) {
        //app : 양수와 음수를 다르게 처리함
        int a=199;
        // a의 상태를 메세지로 표현
        String msg = "a는 음수입니다.";

        if(a>0){
            msg="a는 양수입니다.";
        }

        System.out.println(msg);


        a=-188;
        msg=""; //초기값 x
        if(a>0){
            msg="양수";
        }else{
            msg="음수";
        }
        System.out.println(msg);
        // a가 0일 때를 찾지 못함
        // 분기가 2개를 초과한 복잡한 상태를 else if로 처리

        a=0;
        msg="";
        if(a>0){
            msg="양수";
        } else if (a==0) {
            msg="제로";
        } else {
            msg="음수";
        }
        System.out.println(msg);

        // 실수에서 -0.0 != +0.0 하지만 값을 비교하기 때문에 true 발생
        System.out.println(0.0==-0.0);



        //**if 실습 중요**
        //로그인을 한 유저의 나이를 받아올 것임
        int birth=2007;
        int year=2026;
        int age=year-birth;
        msg=""; // 만 19세 이상은 주류 구입 가능, 19세 미만은 불가

        if(age>=19){
            msg="주류 구입이 가능합니다.";
        } else {
            msg="주류 구입이 불가합니다.";
        }
        System.out.println(msg);

        System.out.println(Integer.toHexString((int)'가'));


    }
}
