package com.smu8.javautil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class L33InputStreamEncoding2 {
    public static void main(String[] args) {
        byte[] bytes=new byte[20]; //[]
        int i=0;
        while (true){
            try {
                int input=System.in.read(); //InputStream => byte 단위로 처리
                bytes[i++]=(byte) input;
                if(input=='\n')break;
            } catch (IOException e) {
                e.printStackTrace(); //오류내역을 빨간색 폰트로 출력
            }
        }
        System.out.println(Arrays.toString(bytes));
        String str=new String(bytes, Charset.defaultCharset()); //터미널의 인코딩을 그대로 사용
        System.out.println("입력한 문자열: "+str);
    }
}
