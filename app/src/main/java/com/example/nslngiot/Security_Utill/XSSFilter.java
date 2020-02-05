package com.example.nslngiot.Security_Utill;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class XSSFilter {

    public static String xssFilter(String value){
        try {
            value = URLDecoder.decode(value,"euc-kr"); // 인코딩 우회 방지 적용
            value = value.replaceAll("<","&lt");
            value = value.replaceAll(">","&gt");
            value = value.replaceAll("/","&#x2F");
            value = value.replaceAll("&","&#38");
            value = value.replaceAll("#","&#35");
            value = value.replace("script","");

        } catch (UnsupportedEncodingException e) {
            System.err.println("XSSFilter error");
        }
        return value;
    }
}
