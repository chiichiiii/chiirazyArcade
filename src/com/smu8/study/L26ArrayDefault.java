package com.smu8.study;

import java.util.Arrays;
import java.util.Date;

public class L26ArrayDefault {
    public static void main(String[] args) {
        //배열의 기본값
        double [] nums=new double[3]; // {0.0,0.0,0.0}
        System.out.println(Arrays.toString(nums)); //[0.0, 0.0, 0.0]

        int[]intNums=new int[3];
        System.out.println(Arrays.toString(intNums)); //[0, 0, 0]

        boolean[]bArr=new boolean[3];
        System.out.println(Arrays.toString(bArr)); //[false, false, false]

        char []cArr=new char[3];
        System.out.println(Arrays.toString(cArr)); //[ ,  ,  ] \u0000 \0

        int i=0;
        String s="";
        s=null; //참조하는 것이 없다. (자료형에서 0=>null)
        String[]strArr=new String[3];
        System.out.println(Arrays.toString(strArr)); //[null, null, null]

        Date[]dateArr=new Date[3];
        System.out.println(Arrays.toString(dateArr)); //[null, null, null]

        //기본형의 배열 기본값 = 0
        //자료형의 배열 기본값 = null

    }
}

//필드의 기본값
class A{
    int a; // 0
    String s; // null
}