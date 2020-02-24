package com.example.nslngiot.Security_Utill;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class FileFilter {

    public static boolean fileFilter(File fileData){
        final int fileAllowSize= 10240000; //10MB이하
        boolean result= false; //default 셋팅

        if(fileData != null && "".equals(fileData.getName())){
            String file= "";
            try{
                file= URLDecoder.decode(fileData.getName(),"euc-kr"); // 1단계 인코딩 우회 방지

                if(fileData.length()>fileAllowSize || fileData.canExecute()) // 2단계 파일의 사이즈 & 파일 실행여부 확인
                    result = true; // 위험한 형식의 파일 발견
                else{
                    if(file.toLowerCase().endsWith(".jpg") || file.toLowerCase().endsWith(".png") ||
                            file.toLowerCase().endsWith(".jpeg"))
                    { // 3단계 문자열의 마지막 확장자 확인
                        // test.jpg.asp의 '.asp'를 막기위해 파일 전체이름 리딩
                        // 리딩된 파일 이름을 통해 끝부분의 확장자 .jpg 또는 .png가 맞다면 2단계 검증 완료
                        result = false; // 안전한 파일
                    } else
                        result = true; // 위험한 형식의 파일 발견

                }
            }catch (UnsupportedEncodingException e) {
                System.err.println("FileFilter error");
            }
        }
        else {
            result =true; //파일 null & 공백 파일
        }
        return  result;
    }
}