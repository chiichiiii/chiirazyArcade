package com.smu8.javautil;

public class L03ThrowsException {

    public static void setAge(int age) throws Exception {
        //나이는 0~140까지로 약속
        if (age >= 0 && age <= 140) {
            System.out.println("당신의 나이는 " + age + "세입니다.");
        } else {
            throw new IllegalArgumentException("나이는 0~140만 입력 가능");
        }
    }
    public static void tester() throws Exception{
        try {
            setAge(2000);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            System.out.println("오류 처리");
            throw new Exception("다시 오류 발생");
        }
    }

        public static void main (String[]args){
        //메인은 최종 사용자이기 때문에 예외를 위임하면 예외를 처리하지 않겠다는 뜻
            try {
                setAge(2000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //tester();
        }
    }

