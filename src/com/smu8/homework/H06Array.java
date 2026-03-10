package com.smu8.homework;

public class H06Array {
    public static void main(String[] args) {
        int[] scores = {80, 75, 90, 100, 65};
        //1.모든 점수의 합계를 구하여 출력하시오.
        //2.평균 점수를 구하여 출력하시오.  (평균은 정수로 출력)
        //요구 조건
        //*for 문을 사용할 것
        //*배열의 길이를 직접 숫자로 쓰지 말 것

        int sum = 0;
        int avg = 0;
        for (int i = 0; i < scores.length; i++) {
            sum += scores[i];
        }
        avg = sum / scores.length;
        System.out.println("모든 점수의 합은 " + sum + "점이다.");
        System.out.println("평균 점수는 " + avg + "점이다.");


        int[] productCodes = {101, 203, 305, 410, 512};
        //사용자가 찾고 싶은 상품 코드가 305일 때,
        //배열 안에 해당 값이 존재하면 "상품이 존재합니다"
        //존재하지 않으면 "상품이 없습니다"
        //를 출력하시오.
        //요구 조건
        //*반복문과 조건문을 함께 사용할 것
        //*break 사용 가능
        boolean result = false;

        for (int i = 0; i < productCodes.length; i++) {
            if (productCodes[i] == 305) {
                result = true;
                break;
            }
        }
        if (result) {
            System.out.println("상품이 존재합니다");
        } else {
            System.out.println("상품이 없습니다");
        }


        int[] memberIds = {101, 205, 309, 412, 550};
        boolean rst = false;
        for (int i = 0; i < memberIds.length; i++) {
            if (memberIds[i] == 309) {
                rst = true;
                break;
            }
        }
        if (rst) {
            System.out.println("회원이 존재합니다");
        } else {
            System.out.println("회원이 존재하지 않습니다");
        }


        int[] orders = {12000, 8000, 15000, 3000, 7000};
        boolean rest = false;
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] >= 10000) {
                rest = true;
                break;
            }
        }
        if (rest) {
            System.out.println("무료 배송 대상 주문이 있습니다");
        } else {
            System.out.println("무료 배송 대상 주문이 없습니다");
        }


        int[] ages = {25, 30, -3, 45, 200, 18};
        int count = 0;
        for (int i = 0; i < ages.length; i++) {
            if (ages[i] < 0 || ages[i] > 120) {
                System.out.println("잘못된 나이: " + ages[i]);
                count++;
            }
        }
        System.out.println("총 " + count + "개");


        String[] names = {"kim", "lee", "park", "choi"};
        result = false;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("park")) {
                result = true;
                break;
            }
        }
        if (result) {
            System.out.println("회원 존재");
        } else {
            System.out.println("회원 없음");
        }

        System.out.println("\ngpt 문제 시작");


        String[] words = {"java", "array", "loop", "string"};
        int sum2 = 0;
        for (int i = 0; i < words.length; i++) {
            int len = words[i].length();
            System.out.println(words[i] + ": " + len);
            sum2 += len;
        }
        System.out.println("총 길이: " + sum2);

        String[] inputs = {"hello", "hi", "hello123", "bye", "hell"};
        int cnt = 0;
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i].contains("hello")) {
                System.out.println(inputs[i]);
                cnt++;
            }
        }
        System.out.println("hello를 포함하는 문자는 총 " + cnt + "개");

        String[] emails = {
                "user@test.com",
                "admin@test.com",
                "guesttest.com",
                "hello@site",
                "@mail.com"
        };
        //이메일에는 반드시 @ 문자가 포함되어야 한다.
        int normal=0;
        int invalid=0;
        for (int i=0;i< emails.length;i++){
            if (!emails[i].contains("@")){
                System.out.println(emails[i]);
                invalid++;
            } else{
                normal++;
            }
        }
            System.out.println("정상 이메일: "+normal+"개");
            System.out.println("비정상 이메일: "+invalid+"개");


    }




}
