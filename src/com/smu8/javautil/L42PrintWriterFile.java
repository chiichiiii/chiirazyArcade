package com.smu8.javautil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class L42PrintWriterFile {
    public static void main(String[] args) {
        //바이트 입력 => 문자열로 변경 (출력)
        //OutputStream out=new OutputStream(); or FileOutStream : 바이트 출력
        //OutputStreamWriter writer=new OutputStreamWriter(out,인코딩); : 바이트->문자 출력
        //BufferedWriter br=new BufferedWriter(writer) : 문자 -> 라인개행까지의 문자열 출력
        //new BufferedWriter(new OutputStreamWriter(new FileOutputStream()))
        String path="filOut.txt";
        try (
                BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
        ){
            bw.write("파일 쓰기 복습입니다!");
            bw.newLine();
            bw.write("객체를 중첩해서 생성하는 것은 어렵네요~");
            bw.newLine();
            bw.write("바이트를 문자열로 저장하는 것도 어렵네요~");
            bw.flush(); //남은 버퍼를 비움(비우지 않으면 파일 작성이 안될 수 도 있다.)
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //PrintWriter : Scanner 처럼 문자열 출력처리를 자동으로 하는 유틸클래스
        path="pintWriterTest.txt";
        try (
                FileOutputStream file=new FileOutputStream(path);
                PrintWriter out=new PrintWriter(file,true,StandardCharsets.UTF_8); //자동 버퍼비움
        ){
            out.println("PrintWriter 로 파일 작성");
            out.println("생성도 편리하고 함수도 편리함");
            out.println("auto flush가 가능해서 안전함");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}