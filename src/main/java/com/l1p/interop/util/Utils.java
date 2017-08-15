package com.l1p.interop.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by murthy on 7/24/17.
 */
public class Utils {

    public static String getExpiresAt(){
        Date dNow = new Date(System.currentTimeMillis()+5*60*1000);
        SimpleDateFormat ft = new SimpleDateFormat ("YYYY-MM-dd'T'HH.mm.ss.SSS'Z'");
        return ft.format(dNow);
    }

    public static void main(String[] args){
        System.out.println(Utils.getExpiresAt());
    }
}
