package com.smu8.javautil;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class L43ScannerFile {
    public static void main(String[] args) {
        String path="filOut.txt";
        try (
                FileInputStream file=new FileInputStream(path);
                InputStreamReader isr=new InputStreamReader(file, StandardCharsets.UTF_8);
                BufferedReader br=new BufferedReader(isr);
        ){
            String line="";
            StringBuilder sb=new StringBuilder();
            while ((line=br.readLine())!=null){
                //System.out.println(line);
                sb.append(line+"\n");
            }
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        path="pintWriterTest.txt";
        try (
                Scanner scanner=new Scanner(new FileInputStream(path));
        ){
            StringBuilder sb=new StringBuilder();
            while (scanner.hasNext()){
                sb.append(scanner.nextLine()+"\n");
            }
            System.out.println(sb);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}