package com.yellow.petshop.util;

import java.util.ArrayList;
import java.util.List;

public class MyUtil {
    public static List<String> addPrefix(List<String> list, String prefix) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            newList.add(prefix + s);
        }
        return newList;
    }
}
