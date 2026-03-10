package com.smu8.javautil;

import java.time.LocalDateTime;

class ClockThread extends Thread{
    @Override
    public void run() {//새로 만들어질 Thread에게 할 일을 지정 (콜백함수)
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(LocalDateTime.now());
        }

    }
}
class Thermo implements Runnable{
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("현재 온도는 -1도");
        }
    }
}

public class L27Clock {
    static void hygro(){
        while (true){
            try {
                Thread.sleep(1000);
                System.out.println("현재 습도는 45%");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread clockThread=new ClockThread();
        clockThread.start();//스레드 생성 후 재정의한 run을 실행 시킴
        //System.out.println("스레드가 2개면 실행됨");

        Thread thermo = new Thread(new Thermo());
        thermo.start();
        Thread hygro = new Thread(()->hygro());
        hygro.start();


    }
}
