package com.example.nslngiot.Security_Utill;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.RequiresApi;

public class KEYSTORE {

    private final static String alias = "NetworkSecurity"; // KeyStore alias
    private static KeyGenerator keyGenerator;
    private static KeyGenParameterSpec keyGenParameterSpec;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void keyStore_init(){

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);


            if(!keyStore.containsAlias(alias)){ // 지정된 별칭으로 키 미생성 시 새롭게 키 생성

                // 생성할 키 알고리즘
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
                keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                        // 별칭 / key사용목적 암호화&복호화
                        alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // 운용할 블록모드
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7) // 사용할 패딩 값
                        .setRandomizedEncryptionRequired(false) // 무작위 암호화 방지
                        .build(); // 초기화 완료

                keyGenerator.init(keyGenParameterSpec); // 키 생성 초기화
                keyGenerator.generateKey(); // 초기화된 키 생성을 통해 비밀키 반환
            }

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Keystore init NoSuchAlgorithmException error");
        } catch (NoSuchProviderException e) {
            System.err.println("Keystore init NoSuchProviderException error");
        } catch (KeyStoreException e) {
            System.err.println("Keystore init KeyStoreException error");
        } catch (CertificateException e) {
            System.err.println("Keystore init CertificateException error");
        } catch (IOException e) {
            System.err.println("Keystore init IOException error");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("Keystore init InvalidAlgorithmParameterException error");
        }
    }

    // KeyStore의 AES대칭키로 암호화
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String keyStore_Encryption(String str){
        String keyStore_Encryption_DATA="";
        String iv = "";
        Key keySpec;
        String key = "";

        try {
            KeyStore keyStore = java.security.KeyStore.getInstance("AndroidKeyStore"); // Android KeyStore 접근
            keyStore.load(null); // 로드
            KeyStore.SecretKeyEntry secretKeyEntry =
                    (KeyStore.SecretKeyEntry) keyStore.getEntry(alias,null); // 별칭에 맞게 비밀키 접근
            SecretKey secretKey = secretKeyEntry.getSecretKey(); // 비밀키 반환

            key = Base64.encodeBase64String(secretKey.getEncoded()); // 비밀키는 'String'형태로 반환

            iv = key.substring(0,16);
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if(len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len); // b의 0번지 부터 len길이 만큼 keybytes 0번지부터 복사
            keySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes())); // 암호화 준비

            // AES 암호화
            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));

            // 암호화된 데이터, 인코딩 후 'String'으로 반환
            keyStore_Encryption_DATA = Base64.encodeBase64String(encrypted);

        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("keyStore_Encryption InvalidAlgorithmParameterException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("keyStore_Encryption NoSuchPaddingException error");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("keyStore_Encryption NoSuchAlgorithmException error");
        } catch (InvalidKeyException e) {
            System.err.println("keyStore_Encryption InvalidKeyException error");
        } catch (BadPaddingException e) {
            System.err.println("keyStore_Encryption BadPaddingException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("keyStore_Encryption IllegalBlockSizeException error");
        } catch (UnsupportedEncodingException e) {
            System.err.println("keyStore_Encryption UnsupportedEncodingException error");
        } catch (CertificateException e) {
            System.err.println("keyStore_Encryption CertificateException error");
        } catch (KeyStoreException e) {
            System.err.println("keyStore_Encryption KeyStoreException error");
        } catch (UnrecoverableEntryException e) {
            System.err.println("keyStore_Encryption UnrecoverableEntryException error");
        } catch (IOException e) {
            System.err.println("keyStore_Encryption IOException error");
        }
        return keyStore_Encryption_DATA;
    }

    // KeyStore의 AES대칭키로 복호화
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String keyStore_Decryption(String str){
        String keyStore_Decryption_DATA="";
        String iv = "";
        Key keySpec;
        String key = "";

        try {
            KeyStore keyStore = java.security.KeyStore.getInstance("AndroidKeyStore"); // Android KeyStore 접근
            keyStore.load(null); // 로드
            KeyStore.SecretKeyEntry secretKeyEntry =
                    (KeyStore.SecretKeyEntry) keyStore.getEntry(alias,null); // 별칭에 맞게 비밀키 접근
            SecretKey secretKey = secretKeyEntry.getSecretKey(); // 비밀키 반환

            key = Base64.encodeBase64String(secretKey.getEncoded()); // 비밀키는 'String'형태로 반환

            iv = key.substring(0,16);
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if(len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len); // b의 0번지 부터 len길이 만큼 keybytes 0번지부터 복사
            keySpec = new SecretKeySpec(keyBytes, "AES");


            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));

            // 암호화된 인코딩 데이터 -> 디코딩
            byte[] byteStr = Base64.decodeBase64(str.getBytes());
            // 디코딩된 암호문 -> 복호화 후 'String'으로 반환
            keyStore_Decryption_DATA = new String(cipher.doFinal(byteStr),"UTF-8");

        } catch (KeyStoreException e) {
            System.err.println("keyStore_Encryption KeyStoreException error");
        } catch (CertificateException e) {
            System.err.println("keyStore_Encryption CertificateException error");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("keyStore_Encryption NoSuchAlgorithmException error");
        } catch (IOException e) {
            System.err.println("keyStore_Encryption IOException error");
        } catch (UnrecoverableEntryException e) {
            System.err.println("keyStore_Encryption UnrecoverableEntryException error");
        } catch (InvalidKeyException e) {
            System.err.println("keyStore_Encryption InvalidKeyException error");
        } catch (InvalidAlgorithmParameterException e) {
            System.err.println("keyStore_Encryption InvalidAlgorithmParameterException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("keyStore_Encryption NoSuchPaddingException error");
        } catch (BadPaddingException e) {
            System.err.println("keyStore_Encryption BadPaddingException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("keyStore_Encryption IllegalBlockSizeException error");
        }
        return keyStore_Decryption_DATA;
    }
}
