package com.smu8.javautil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class L45SocketClient {
    public static void main(String[] args) {
        //서버에 접속
        String ip="192.168.0.65";
        String username="내일 로또 1등은 나";
        int port=7777;
        try (Socket socket=new Socket(ip,port)){
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            Thread msgOutThread = new Thread(()->{
            Scanner msgIn=new Scanner(System.in);
                while (true){
                    String msg=msgIn.nextLine(); //콘솔에서 입력받은 메세지
                    out.println(username+":"+msg);
                }
            });
            msgOutThread.start();

            Scanner in=new Scanner(socket.getInputStream());
            while (in.hasNext()){
                String msg=in.nextLine();
                System.out.println(msg);
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    }
