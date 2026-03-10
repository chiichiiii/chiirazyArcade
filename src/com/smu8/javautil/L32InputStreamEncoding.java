package com.smu8.javautil;

import java.nio.charset.Charset;
import java.util.Arrays;

public class L32InputStreamEncoding {
    public static void main(String[] args) {
        //String(byte[] arr, ending) => 문자열
        byte[] bytes=new byte[20]; //{0,0,......0,0}
        //[234,178,189,235,175,188]
        bytes[0]=(byte) 234;
        bytes[1]=(byte) 178;
        bytes[2]=(byte) 189;
        bytes[3]=(byte) 235;
        bytes[4]=(byte) 175;
        bytes[5]=(byte) 188;
        System.out.println(Arrays.toString(bytes));
        String str=new String(bytes, Charset.forName("EUC-KR"));
        System.out.println(str); //寃쎈��
        String str2=new String(bytes, Charset.forName("UTF-8"));
        System.out.println(str2);

    }
}
