package com.smu8.javautil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class L44SocketServer {
    //스레드가 참조하는 필드는 전역이거나 static 으로 선언
    static List<Socket> socketList= Collections.synchronizedList(new ArrayList<>());
    //Collections.synchronizedList : 멀티스레드에서 동기화하는 콜렉션 객체 생성
    public static void main(String[] args) {
        try (ServerSocket server=new ServerSocket(7777);){
            while (true){
                Socket socket=server.accept(); // 접속하면 소켓을 반환
                socketList.add(socket); //접속한 유저 리스트 참조
                String clientId=(socket.getInetAddress().getHostAddress());
                System.out.println(clientId+"님 접속");
                Scanner in=new Scanner(socket.getInputStream()); //유저가 보내는 메세지 받기
                Thread msgThread=new Thread(()->{
                    while (in.hasNext()){
                        String line=in.nextLine();
                        System.out.println(line);
                        String msg=clientId+" : "+line;
                        for(Socket client : socketList){
                            try {
                                PrintWriter out=new PrintWriter(client.getOutputStream(),true);
                                // auto close 하면 더이상 메세지 보낼 수 없음
                                out.println(msg);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
                msgThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}