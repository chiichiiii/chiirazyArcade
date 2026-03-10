package com.smu8.javautil;
class StringTestThread extends Thread{
    static Object lock=new Object();
    @Override
    public void run() {
        for(int i=0; i<100_000;i++){
            //synchronized (lock){
              //  L38StringBuffer.str+="A";
           // }
                L38StringBuffer.str+="A";
            L38StringBuffer.sb.append("A");
            L38StringBuffer.sf.append("A");
        }
    }
}

public class L38StringBuffer {
        public static String str="";
        public static StringBuilder sb=new StringBuilder();
        public static StringBuffer sf=new StringBuffer();
    public static void main(String[] args) throws InterruptedException {
        Thread t1=new StringTestThread();
        Thread t2=new StringTestThread();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(str.length());
        System.out.println(sb.length());
        System.out.println(sf.length());
    }
}
