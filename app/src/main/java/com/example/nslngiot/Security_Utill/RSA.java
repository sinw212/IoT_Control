package com.example.nslngiot.Security_Utill;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;


public class RSA {

    public static String publicKEY="";
    public static String privateKEY="";

    public static void rsaKeyGen() throws NoSuchAlgorithmException {

        SecureRandom secureRandom = new SecureRandom(); // 안전한 난수 생성
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, secureRandom); // 충분한 키길이 및 난수를 이용하여 키 초기화
        KeyPair keyPair = keyPairGenerator.genKeyPair(); // 키 쌍 생성

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // publicKey객체를 'String'으로 변환
        String stringPublicKey = Base64.encodeBase64String(publicKey.getEncoded());
        // PrivateKey객체를 'String'으로 변환
        String stringPrivateKey = Base64.encodeBase64String(privateKey.getEncoded());
    }

    /*암호화*/
    public static String rsaEncryption(String plainData, String stringPublicKey) {

        String encryptedData = null;

        try {

            // 평문으로 전달받은 'String공개키'를 '공개키 객체'로 만드는 과정
            byte[] bytePublicKey = Base64.decodeBase64(stringPublicKey.getBytes());
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 만들어진 공개키객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 암호화 진행
            byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes());
            // 암호화 데이터를 인코딩
            encryptedData = Base64.encodeBase64String(byteEncryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    /*복호화*/
    public static String rsaDecryption(String encryptedData, String stringPrivateKey) {

        String decryptedData = "";

        try {
            //평문으로 전달받은 개인키를 개인키객체로 만드는 과정

            byte[] bytePrivateKey =  Base64.decodeBase64(stringPrivateKey.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //만들어진 개인키객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            //암호문을 평문화하는 과정
            byte[] byteEncryptedData = Base64.decodeBase64(encryptedData.getBytes());
            byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
            decryptedData = new String(byteDecryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedData;
    }
}