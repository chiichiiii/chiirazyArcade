package com.smu8.javautil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class L08Map {
    public static void main(String[] args) {
        //HashSet, LinkedHashSet,TreeSet
        //HashMap, LinkedHashMap, TreeMap
        //key: value로 된 데이터가 Map으로 Key가 Set으로 되어 있어서 절대 중복되지 않는다.
        Map<String,Object> person=new HashMap<>();
        person.put("이름","김태연");
        person.put("나이",40);
        person.put("이름","탱구"); //Key가 중복을 허용하면 이름이 2개 / 중복 허용 x(Set) 이름 바뀜
        //Key는 Set이기 때문에 equals가 구현된 자료형이거나 기본형으로 작성
        System.out.println(person); //{이름=탱구, 나이=40} or {이름:탱구, 나이:40}

        Set<String> keys=person.keySet();//key만 가져옴
        System.out.println(keys); //[이름, 나이]
        Collection<Object> values=person.values();
        System.out.println(values); //[탱구, 40]




    }
}
