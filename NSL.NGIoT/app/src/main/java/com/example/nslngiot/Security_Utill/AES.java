package com.example.nslngiot.Security_Utill;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String secretKEY="";

    // AES 대칭키 생성
    public static void aesKeyGen() throws NoSuchAlgorithmException {

        KeyGenerator generator = KeyGenerator.getInstance("AES"); // 키생성에 사용할 암호 알고리즘
        SecureRandom secureRandom = new SecureRandom(); // 안전한 난수 생성 'math random'보다 보안 강도가 높음
        generator.init(256, secureRandom); // 충분한 키 길이 및 난수를 이용하여 키 초기화
        Key secureKey = generator.generateKey();

        secretKEY = new String(Base64.encodeBase64(secureKey.getEncoded()));
        //secretKEY = Base64.encodeBase64String(secureKey.getEncoded()); // 대칭키 객체를 'String'으로 변환
    }


    public static String aesEncryption(String str, String key) throws UnsupportedEncodingException,
            NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        String iv = "";
        Key keySpec;

        iv = key.substring(0,16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len); // b의 0번지 부터 len길이 만큼 keybytes 0번지부터 복사
        keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes())); // 암호화 준비

        // AES 암호화
        byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));

        // 암호화된 데이터, 인코딩 후 'String'으로 반환
        //return Base64.encodeBase64String(encrypted);
        return new String (Base64.encodeBase64(encrypted));
    }

    public static String aesDecryption(String str, String key) throws UnsupportedEncodingException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        String iv = "";
        Key keySpec;

        iv = key.substring(0,16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len); // b의 0번지 부터 len길이 만큼 keybytes 0번지부터 복사
        keySpec = new SecretKeySpec(keyBytes, "AES");


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8"))); // 복호화 준비

        // 암호화된 인코딩 데이터, 디코딩 변환
        byte[] byteStr = Base64.decodeBase64(str.getBytes());
        // 디코딩된 암호화 데이터, 복호화 후 'String'으로 반환
        return new String(cipher.doFinal(byteStr),"UTF-8");
    }
}