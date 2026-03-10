package com.smu8.javautil;

public class L30Synchronized {

    public static int count=0; //어디서든 접근가능한(public) 공유자원(static)
    //static : 컴파일러가 코드를 분석하면서 만들어놓고 삭제하지 않음 -> 공유자원
    public static Object lock=new Object(); //스레드가 참조하는 동안 다른 스레드가 참조하지 못하게 lock

    public static void main(String[] args) throws InterruptedException {
        Thread t1=new Thread(()->{
            for(int i=0; i<10_000_000;i++){
                synchronized (lock) {
                    count++;
                }
            }
        });
        Thread t2=new Thread(()->{
            for(int i=0; i<10_000_000;i++){
                synchronized (lock) {
                    count++;
                }
            }
        });
        t1.start();
        t2.start();

        t1.join(); //위 thread가 끝날 때까지 기다림 == thread 동기화
        t2.join();
        System.out.println(count); //20000000 => 19049171 : 스레드가 동시에 참조해서 증가시켜서 문제가 발생


    }
}
