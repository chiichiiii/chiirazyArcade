package com.smu8.javautil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class L24StreamAPIMethod3 {
    public static void main(String[] args) {
        List<Integer> numList=new ArrayList<>(Arrays.asList(1,2,3,4,0,-6,5,0,6,-1,-2,-3));
        //stream => Map collect(Collectors.*)
        //partitioningBy : map 인데 두가지 그룹으로 반환
        Map <Boolean,List<Integer>> result=numList.stream()
                .collect(Collectors.partitioningBy((num)->{
                    return num>=0;
                }));
        System.out.println(result);
        //groupingBy : map 여러개의 그룹(List)으로 반환
        Map<String,List<Integer>> result2=numList.stream()
                .collect(Collectors.groupingBy((num)->{
                    if(num>0){
                        return "양수";
                    }else if(num<0){
                        return "음수";
                    }else {
                        return "제로";
                    }
                }));
        System.out.println(result2);


    }
}
