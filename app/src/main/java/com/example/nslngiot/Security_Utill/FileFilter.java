package com.example.nslngiot.Security_Utill;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class FileFilter {

    final private static String AllowType[]={"jpg","png"};

    public static String fileFilter(File file){

        try
        {
            String file_path = file.getAbsolutePath(); // 파일 경로 가져오기
            file_path = URLDecoder.decode(file_path,"euc-kr"); // 인코딩 우회 방지
            file_path = file_path.toLowerCase(); //대문자 변경 공격 방지

            Long file_Size = file.length(); // 파일 사이즈 확인


        } catch (UnsupportedEncodingException e) {
            System.err.println("FileFilter error");
        }

        return;
    }
}
