package com.smu8.javautil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ChattingClient{ //서버에 접속해서 소켓을 반환, 메세지 보내는 스레드, 메세지 받는 스레드 생성
    private Socket socket;
    private PrintWriter out;
    private Scanner in;
    public ChattingClient(String ip,int port) throws IOException { //접속할 서버의 주소
        socket=new Socket(ip,port);
        in=new Scanner(socket.getInputStream(),StandardCharsets.UTF_8); //다른 유저 메세지 받기
        out=new PrintWriter(socket.getOutputStream(),true, StandardCharsets.UTF_8); //다른 유저에게 메세지 보내기
        Thread t=new Thread(()->sendMsg());
        t.start();
        Thread t2=new Thread(()->receiveMsg());
        t2.start();
    }
    private void sendMsg(){
        Scanner consoleIn=new Scanner(System.in);
        while (consoleIn.hasNext()){
            String line=consoleIn.nextLine();
            out.println(line);
        }
    } //메세지 보내는 스레드에서 실행할 콜백함수
    private void receiveMsg(){
        while (in.hasNext()){
            System.out.println(in.nextLine());
        }
    } //메세지 받는 스레드에서 실행할 콜백함수
}

public class L47SocketClientObj {
    public static void main(String[] args) {
        //192.168.0.65 : 강사컴
        //127.0.0.1 : 내 컴퓨터 서버
        try {
            new ChattingClient("192.168.0.65",8888);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
