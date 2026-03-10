package com.smu8.javautil;

import java.io.*;

public class L39FileWriter {
    public static void main(String[] args) {
        //파일: 이미지, 음악, 영상, .... 텍스트 (txt,hwp,doc,csv,md....)
        //FileWriter : 문자열을 저장하는 객체
        //FileOutStream(byte)+BufferedWriter(문자 단위로 버퍼 처리)
            try (
                    // /:root (c:\)
                    // ./  :현재 경로 기준 jvm이 실행하는 프로젝트 경로
                    FileOutputStream fos=new FileOutputStream("out.txt");
                    OutputStreamWriter osw=new OutputStreamWriter(fos); //문자단위로 바이트를 처리
                    BufferedWriter bw=new BufferedWriter(osw); //버퍼로 문자열을 처리
            ){
                bw.write("안녕하세요!");
                bw.newLine();
                bw.write("파일 출력 수업입니다.");
                bw.newLine();
                bw.write("writer는 바이트를 인코딩처리\n");
                bw.write("버퍼는 임시 저장 공간으로 라인개행 전까지 문자열을 처리 \n");

            } catch (FileNotFoundException e) { //파일이나 경로가 없을 때 발생하는 오류
                throw new RuntimeException(e);
            } catch (IOException e) { //입출력 시 발생
                throw new RuntimeException(e);
            }
    }
}
