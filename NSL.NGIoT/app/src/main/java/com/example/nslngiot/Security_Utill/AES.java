package com.example.nslngiot.Security_Utill;

import android.os.Build;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
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

    public static char[] secretKEY;

    // AES 대칭키 생성
    public static void aesKeyGen() {

        KeyGenerator generator; // 키생성에 사용할 암호 알고리즘
        SecureRandom secureRandom = new SecureRandom(); // 안전한 난수 생성 'Math Random'보다 보안 강도가 높음

        try {
            byte[] copy_secureKey;
            generator = KeyGenerator.getInstance("AES");
            generator.init(256, secureRandom); // 충분한 키 길이 및 난수를 이용하여 키 초기화
            Key initKey = generator.generateKey();
            copy_secureKey = initKey.getEncoded();
            copy_secureKey = Base64.encodeBase64(copy_secureKey);

            int size = copy_secureKey.length;
            secretKEY = new char[size];

            // convert byte to char array
            for(int i =0;i<size; i++)
                secretKEY[i] = (char)copy_secureKey[i];
        } catch (NoSuchAlgorithmException e) {
            System.err.println("AES KeyGen NoSuchAlgorithmException error");
        }
    }

    public static String aesEncryption(char[] str, char[] key) {

        Key keySpec;
        String iv = "";
        String str_enc = "";

        try {
            byte[] encrypted;
            byte[] keyBytes = new byte[16];
            byte[] b = String.valueOf(key).getBytes(StandardCharsets.UTF_8);
            iv = String.valueOf(key).substring(0,16);
            int len = b.length;

            if(len > keyBytes.length)
                len = keyBytes.length;

            System.arraycopy(b, 0, keyBytes, 0, len);
            // 암호화 준비
            keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));


            // AES 암호화
            encrypted = cipher.doFinal(String.valueOf(str).getBytes());
            // 암호화된 데이터, 인코딩 후 'String'으로 반환
            if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.N))
                str_enc = new String(Base64.encodeBase64(encrypted));
            else
                str_enc= Base64.encodeBase64String(encrypted);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("AES Encryption NoSuchAlgorithmException error");
        } catch (InvalidKeyException e) {
            System.err.println("AES Encryption InvalidKeyException error");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("AES Encryption InvalidAlgorithmParameterException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("AES Encryption NoSuchPaddingException error");
        } catch (BadPaddingException e) {
            System.err.println("AES Encryption BadPaddingException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("AES Encryption IllegalBlockSizeException error");
        }
        return str_enc;
    }


    public static String aesDecryption(char[] str, char[] key) {

        String iv = "";
        String str_dec = "";
        Key keySpec;

        try {
            byte[] decrypted;
            byte[] keyBytes = new byte[16];
            byte[] b = String.valueOf(key).getBytes(StandardCharsets.UTF_8);
            iv = String.valueOf(key).substring(0,16);
            int len = b.length;

            if(len > keyBytes.length)
                len = keyBytes.length;

            System.arraycopy(b, 0, keyBytes, 0, len);
            // 복호화 준비
            keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));

            // 암호화된 인코딩 데이터, 디코딩 변환
            decrypted = Base64.decodeBase64(String.valueOf(str).getBytes());
            // 디코딩된 암호화 데이터, 복호화 후 'String'으로 반환
            str_dec = new String(cipher.doFinal(decrypted), StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("AES Decryption NoSuchAlgorithmException error");
        } catch (InvalidKeyException e) {
            System.err.println("AES Decryption InvalidKeyException error");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("AES Decryption InvalidAlgorithmParameterException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("AES Decryption NoSuchPaddingException error");
        } catch (BadPaddingException e) {
            System.err.println("AES Decryption BadPaddingException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("AES Decryption IllegalBlockSizeException error");
        }
        return str_dec;
    }
}