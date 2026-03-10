package com.smu8.javautil;

import java.time.LocalTime;

public class L26MultiThread {
    public static void main(String[] args) throws InterruptedException {
        //일꾼(Thread)를 한 개 만들어서 시간 출력을 시키고 나머지 일꾼이 온도를 출력 (일꾼2)
        Thread thread=new Thread(()->{//Runnable.run
            while (true){
                System.out.println(LocalTime.now());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }); //일꾼이 할 일(함수)을 미리 정함
        //버튼을 누를 때 어떤 일을 할 건지 미리 정함 (콜백함수)
        thread.start();
        while (true){
            Thread.sleep(1000);
            System.out.println("현재 온도와 습도는 27도/50%");
        }


    }
}
