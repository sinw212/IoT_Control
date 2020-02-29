package com.example.nslngiot.Security_Utill;

import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class FileFilter {

    public static boolean fileFilter(File fileData){
        final int fileAllowSize= 10240000; //10MB이하
        boolean result= true; //default 셋팅

        if(!"".equals(fileData.getName())){
            String file= "";
            try{
                file= URLDecoder.decode(fileData.getName(),"euc-kr"); // 1단계 인코딩 우회 검증

                if(fileData.length()>fileAllowSize) { // 2단계 파일의 사이즈
                    result = true;
                }else{
                    if(fileData.canExecute()){ // 3단계 파일 실행여부 검증
                        result = true;
                    }else{

                        String [] array=file.split("\\.");
                        if(array[1].toLowerCase().endsWith("jpg") || array[1].toLowerCase().endsWith("png") ||
                                array[1].toLowerCase().endsWith("jpeg")) // 4단계 문자열의 마지막 확장자 검증
                        {
                            // 5단계 mime type 검증
                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                            String extenstion = MimeTypeMap.getFileExtensionFromUrl(fileData.getName());
                            String mime_Type = mimeTypeMap.getMimeTypeFromExtension(extenstion);
                            if(mime_Type != null){
                                if(mime_Type.toLowerCase().startsWith("image")){
                                    result = false; // 안전한 파일
                                }else
                                    result = true; // 위험한 형식의 파일 발견
                            } else // mime 타입 없을 시 null 반환
                                result = true; // 위험한 형식의 파일 발견
                        }else
                            result = true; // 위험한 형식의 파일 발견
                    }
                }
            }catch (UnsupportedEncodingException e) {
                System.err.println("FileFilter UnsupportedEncodingException error");
            }
        }
        else {
            result =true; //공백 파일
        }
        return  result;
    }
}