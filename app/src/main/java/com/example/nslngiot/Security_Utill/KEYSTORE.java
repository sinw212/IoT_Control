package com.example.nslngiot.Security_Utill;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class KEYSTORE {

    private final static String alias = "com.example.nslngiot"; // KeyStore alias

    public void keyStore_init(Context context){
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if(!keyStore.containsAlias(alias)){ // 지정된 별칭으로 키 미생성 시 새롭게 키 생성

                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                Calendar start = Calendar.getInstance(Locale.ENGLISH);
                Calendar end = Calendar.getInstance(Locale.ENGLISH);
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                kpg.initialize(spec);
                kpg.generateKeyPair();
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

    // KeyStore의 RSA로 암호화
    public static char[] keyStore_Encryption(char[] str){

        String keyStore_Encryption_DATA="";
        byte[] encrypted = new byte[2060];

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore"); // Android KeyStore 접근
            keyStore.load(null); // 로드

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias,null);
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey(); // KeyStore의 공개키 반환

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey); // 암호화 준비

            // RSA 암호화
            encrypted = cipher.doFinal(String.valueOf(str).getBytes());

            // 암호화된 데이터, 인코딩 후 'String'으로 반환
            if((Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)){
                keyStore_Encryption_DATA = new String(Base64.encodeBase64(encrypted));
            }else
                keyStore_Encryption_DATA = Base64.encodeBase64String(encrypted);

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
        } finally {
            java.util.Arrays.fill(encrypted, (byte) 0x20);
        }
        return keyStore_Encryption_DATA.toCharArray();
    }

    // KeyStore의 RSA로 복호화
    public static String keyStore_Decryption(char[] str){

        String keyStore_Decryption_DATA="";
        byte[] decrypted = new byte[2060];

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore"); // Android KeyStore 접근
            keyStore.load(null); // 로드
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias,null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey(); // KeyStore의 개인키 반환
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호화된 인코딩 데이터 -> 디코딩
            decrypted = Base64.decodeBase64(String.valueOf(str).getBytes());

            // 디코딩된 암호문 -> 복호화 후 'String'으로 반환
            keyStore_Decryption_DATA = new String(cipher.doFinal(decrypted));

        } catch (KeyStoreException e) {
            System.err.println("keyStore_Decryption KeyStoreException error");
        } catch (CertificateException e) {
            System.err.println("keyStore_Decryption CertificateException error");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("keyStore_Decryption NoSuchAlgorithmException error");
        } catch (IOException e) {
            System.err.println("keyStore_Decryption IOException error");
        } catch (UnrecoverableEntryException e) {
            System.err.println("keyStore_Decryption UnrecoverableEntryException error");
        } catch (InvalidKeyException e) {
            System.err.println("keyStore_Decryption InvalidKeyException error");
        } catch (NoSuchPaddingException e) {
            System.err.println("keyStore_Decryption NoSuchPaddingException error");
        } catch (BadPaddingException e) {
            System.err.println("keyStore_Decryption BadPaddingException error");
        } catch (IllegalBlockSizeException e) {
            System.err.println("keyStore_Decryption IllegalBlockSizeException error");
        }finally {
            java.util.Arrays.fill(decrypted, (byte) 0x20);
        }
        return keyStore_Decryption_DATA;
    }
}