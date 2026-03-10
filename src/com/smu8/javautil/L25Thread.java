package com.smu8.javautil;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class L25Thread {
    public static void main(String[] args) throws InterruptedException {
        //시계(시간 출력 + 현재 온도)

        LocalDate nowDate=LocalDate.now();
        System.out.println(nowDate.toString()); //yyyy-MM-dd 2026-02-09
        LocalDateTime nowDateTime=LocalDateTime.now(); //2026-02-09T10:59:12.480474900
        System.out.println(nowDateTime); //yyyy-MM-dd'T'HH:mm:ss.SSS == 시간의 문자열 포맷
        while (true){
            Thread.sleep(1000); //InterruptedException : 일꾼 충돌
            System.out.println(LocalDateTime.now());
        } //일꾼 즉 Thread가 1개 뿐이라 윗 반복문이 종료되지 않는 이상 아래 반복문에 도달 할 수가 없음
        //while (true){
        //    Thread.sleep(1000);
        //    System.out.println("현재 온도는 27도");
        //}

    }
}
