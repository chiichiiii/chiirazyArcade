package com.smu8.javautil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class L41Scanner {
    public static void main(String[] args) {
        //Scanner 는 바이트 입력 (inputStream)을 버퍼가 있는 문자열 입력으로 변경
        //바이트 입력 => 문자열로 변경
        //InputStream is=new InputStream(); or System.in : 바이트
        //InputStreamReader isr=new InputStreamReader(is,인코딩); : 바이트->문자
        //BufferedReader br=new BufferedReader(isr) : 문자 -> 라인개행까지의 문자열
        try (
                InputStream is=System.in; //콘솔에서 바이트단위 입력
                InputStreamReader isr=new InputStreamReader(is, Charset.defaultCharset());
                BufferedReader br=new BufferedReader(isr);
        ) {
            String str=br.readLine(); //콘솔에서 입력한 문자열
            System.out.println("br 로 입력받은 문자열 :"+str);

            //문자열 입력을 간단하게 처리하게 도와주는 유틸클래스 Scanner
            Scanner scanner=new Scanner(System.in); //Scanner 는 예외를 생락할 수 있다(권장x)
            String str2=scanner.nextLine();
            System.out.println("스캐너로 입력받은 문자열 :"+str2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

