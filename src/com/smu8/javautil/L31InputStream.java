package com.smu8.javautil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class L31InputStream {
    public static void main(String[] args) throws IOException {
        //콘솔에서 입력을 받는다 : 입력받은 문자열은 인코딩 없이 byte 단위로 처리
        System.out.println(Charset.defaultCharset()); //콘솔에서 사용하는 인코딩 (==os) UTF-8
        //콘솔에서 입력되는 한굴 문자 UTF-8 3byte
        //int wordInt=System.in.read(); //글자 1개 읽어오기
        //입출력은 항상 오류를 동반 -> 예외처리
        //System.out.println(wordInt);
        //System.out.println(Integer.toBinaryString(wordInt)); //정수를 2진수의 문자열로 반환
        List<Integer> wordInts=new ArrayList<>();
        int word=0;
        String str="";
        while ((word=System.in.read())!='\n'){ //mac \n windows \r
            wordInts.add(word);
            char c=(char) word;
            str+=c;
        }
        System.out.println("입력한 문자열: "+str); //경민 => ê²½ë¯¼
        System.out.println(wordInts);
        wordInts.stream().forEach((n)->{

        });
    }
}
