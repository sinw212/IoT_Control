package com.example.nslngiot.Security_Utill;

import android.os.Build;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

    public final static String serverPublicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMII" +
            "BCgKCAQEAxAOjnqBqDg2Zd97aqtztzA+JSwOex9e6R0d8LsNMnjxMC" +
            "LAxF9dPKE/uDOO7YdE2O9ApwUVPci5pXO0KFm/dtGR0iLXV6ZKl09" +
            "A2Rf4Tk94gLZwcyfV+eWInUEa3TaHWcvjh3M/QeaHpnBn4QzW0pfZJM" +
            "8WSuoYptkHXDp4R7HSpqLjXk6LRyhF6Fpk4KzZHP4CAtxJAPOw1hMi6M9v+V" +
            "jCzTTFSMJADucTD7X0pLzrWCnrwxuCVQKpl3mpgQEgXVC/OE76IvYSHzKJ31UuzF" +
            "1m+9Qux4POgNpakCTRNz3jhvqbPeeHOBQjK3MRil+qqm+H1KrJjhSYJRc3bhFraAwIDAQAB";

    public static String publicKEY="";
    public static String privateKEY=""; // android KeyStore의 '대칭키'에 의해 암호화 저장/유출방지

//    public static void rsaKeyGen() { // RSA 비대칭키 생성
//
//        SecureRandom secureRandom = new SecureRandom(); // 안전한 난수 생성
//        KeyPairGenerator keyPairGenerator = null;
//        try {
//            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048, secureRandom); // 충분한 키길이 및 난수를 이용하여 키 초기화
//            KeyPair keyPair = keyPairGenerator.genKeyPair(); // 키 쌍 생성
//            PublicKey publicKey = keyPair.getPublic();
//            PrivateKey privateKey = keyPair.getPrivate();
//
//            // 누가버전까지는 Base64.encodeBase64String NotMethod 이슈발생
//            if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)){
//                publicKEY = new String(Base64.encodeBase64(publicKey.getEncoded()));
//                privateKEY = new String(Base64.encodeBase64(privateKey.getEncoded()));
//            }else{
//                publicKEY = Base64.encodeBase64String(publicKey.getEncoded()); // 공개키 객체를 'String'으로 변환
//                privateKEY = Base64.encodeBase64String(privateKey.getEncoded()); // 개인키 객체를 'String'으로 변환
//            }
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    } 해당 앱에서는 사용되지 않음

    /*암호화*/
    public static String rsaEncryption(char[] plainData, char[] stringPublicKey) {

        // 평문으로 전달받은 'String공개키'를 '공개키 객체'로 만드는 과정
        byte[] bytePublicKey = Base64.decodeBase64(String.valueOf(stringPublicKey).getBytes());
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
        KeyFactory keyFactory;
        String str_enc = "";

        try {
            byte[] byteEncryptedData;
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            // 만들어진 공개키객체를 기반으로 암호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey); // 암호화 준비

            // 암호화 진행
            byteEncryptedData = cipher.doFinal(String.valueOf(plainData).getBytes());
            // 암호화 데이터, 인코딩 후 'String'으로 반환
            if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.N))
                str_enc = new String(Base64.encodeBase64(byteEncryptedData));
            else
                str_enc = Base64.encodeBase64String(byteEncryptedData);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("RSA Encryption NoSuchAlgorithmException error");
        } catch (InvalidKeyException e) {
            System.err.println("RSA Encryption InvalidKeyException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("RSA Encryption NoSuchPaddingException error");
        } catch (BadPaddingException e) {
            System.err.println("RSA Encryption BadPaddingException error");
        } catch (InvalidKeySpecException e) {
            System.err.println("RSA Encryption InvalidKeySpecException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("RSA Encryption IllegalBlockSizeException error");
        }finally {
            java.util.Arrays.fill(bytePublicKey, (byte) 0x20);
        }
        return str_enc;
    }

    /*복호화*/
    public static String rsaDecryption(char[] encryptedData, char[] stringPrivateKey) {

        // 평문으로 전달받은 'String개인키'를 '개인키 객체'로 만드는 과정
        byte[] bytePrivateKey =  Base64.decodeBase64(String.valueOf(stringPrivateKey).getBytes());
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
        KeyFactory keyFactory;
        String str_dec="";

        try {
            byte[] byteEncryptedData;
            byte[] byteDecryptedData;
            keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
            // 만들어진 개인키객체를 기반으로 복호화모드로 설정하는 과정
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey); // 복호화 준비

            // 암호화된 인코딩 데이터를 디코딩 진행
            byteEncryptedData = Base64.decodeBase64(String.valueOf(encryptedData).getBytes());
            // 복호화 진행
            byteDecryptedData = cipher.doFinal(byteEncryptedData);
            // 복호화 후 'String'으로 반환
            str_dec = new String(byteDecryptedData);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("RSA Decryption NoSuchAlgorithmException error");
        } catch (InvalidKeySpecException e) {
            System.err.println("RSA Decryption InvalidKeySpecException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("RSA Decryption NoSuchPaddingException error");
        } catch (InvalidKeyException e) {
            System.err.println("RSA Decryption InvalidKeyException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("RSA Decryption IllegalBlockSizeException error");
        } catch (BadPaddingException e) {
            System.err.println("RSA Decryption BadPaddingException error");
        }finally {
            java.util.Arrays.fill(bytePrivateKey, (byte) 0x20);
        }
        return str_dec;
    }
}