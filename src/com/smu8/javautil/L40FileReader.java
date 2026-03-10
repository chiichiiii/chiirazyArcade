package com.smu8.javautil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class L40FileReader {
    public static void main(String[] args) {
        try (
                FileInputStream fi=new FileInputStream("out.txt");
                InputStreamReader ir=new InputStreamReader(fi, StandardCharsets.UTF_8);
                BufferedReader br=new BufferedReader(ir);
                ){
            String line="";
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
