package com.smu8.javautil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Client{
    private Socket socket;
    private Scanner input; //msg 받기
    private PrintWriter out; //msg 보내기
    private volatile String nickname; //멀티스레드환경에서 공유될 때 가시성 확보
    //채팅으로 "@nickname 경민" 보내면 경민을 등록
    public Client(Socket socket) throws IOException {
        this.socket=socket;
        input=new Scanner(socket.getInputStream(), StandardCharsets.UTF_8);
        out=new PrintWriter(socket.getOutputStream(),true, StandardCharsets.UTF_8);
        nickname="익명";
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public Scanner getInput() {
        return input;
    }
    public void setInput(Scanner input) {
        this.input = input;
    }
    public PrintWriter getOut() {
        return out;
    }
    public void setOut(PrintWriter out) {
        this.out = out;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
class ChattingServer{
    private int port;
    List<Client> clientList= Collections.synchronizedList(new ArrayList<>());

    public ChattingServer(int port) throws IOException {
        this.port=port;
        try (ServerSocket server=new ServerSocket(port);) {
            while (true){
                Socket socket=server.accept();
                Client client=new Client(socket); //in,out,nickname 생성 후 관리
                clientList.add(client);
                Thread msgHandleThread=new Thread(()->msgHandle(client));
                msgHandleThread.start();
            }
        }
    }
    private void msgHandle(Client client){
        Scanner in=client.getInput();
        while (in.hasNext()){
            String msg=in.nextLine(); //유저가 보낸 메세지 "안녕!"
            final String finalMsg =client.getNickname()+" : "+msg; //"경민 : 안녕!"
            //final String finalMsg = msg;
            clientList.stream().forEach((c)->{
                c.getOut().println(finalMsg);
            });
        }
    }
}
public class L46SocketServerObj {
    public static void main(String[] args) {
        try {
            ChattingServer chattingServer=new ChattingServer(8888);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
