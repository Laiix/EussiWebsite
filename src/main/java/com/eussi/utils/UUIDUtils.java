package com.eussi.utils;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by wangxueming on 2018/10/12.
 */
public class UUIDUtils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static  Integer getDigestUUID(){
        Calendar calendar = Calendar.getInstance();
        String distetNum = String.valueOf(calendar.getTime().getTime());
        return Integer.valueOf(distetNum.substring(distetNum.length() - 9, distetNum.length()));
    }

    public static void main(String[] args) {
        System.out.println("UUID ：" + getDigestUUID());
    }

}
