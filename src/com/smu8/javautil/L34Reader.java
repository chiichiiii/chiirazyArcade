package com.smu8.javautil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class L34Reader {
    public static void main(String[] args) {
        //Reader + close
        //입출력은 스트림이 있어서 사용완료 시 close로 닫아야함
        InputStreamReader isr=null;
        try {
            isr=new InputStreamReader(System.in, Charset.defaultCharset());
            int input=isr.read(); //한 글자씩 처리 (인코딩에 맞게 바이트의 수를 정해서 처리
            //InputStream 1byte, InputStreamReader 한글자(1~4byte)
            System.out.println(input);
            System.out.println((char)input);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
