package com.example.nslngiot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.nslngiot.Network_Utill.NetworkCheck;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import com.example.nslngiot.Security_Utill.RSA;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**     암호 로직    **/
// 모든 키는 최초 1회만 생성되어 계속 사용된다.
// Keystore 초기화 및 AES대칭키 생성 및 컨테이너에 저장
// 클라이언트의 공개키/비공개키 생성 Keystore에서 대칭키로 비공개키 암호화
// 데이터 전송 시 Keystore의 대칭키로 가져와 데이터 암호화 / 대칭키는 전달받은 서버의 공개키로 암호화
// 암호화된 대칭키/데이터, 클라이언트 공개키 전송
// 서버의 개인키로 암호화된 대칭키 복호화 -> 복호화된 대칭키로 암호문 복호화
// 서버는 전송할 데이터를 대칭키로 암호화, 대칭키는 클라이언트의 공개키로 암호화 후 전송
// Keystore의 대칭키로 클라이언트 비공개키 복호화, 전송받은 대칭키는 비공개키로 복호화, 복호화된 대칭키로 암호화 데이터 복호화

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            KEYSTORE.keyStore_init(); // 최초 1회 KeyStore에 저장할 AES 대칭키 생성
        }

        // 네트워크 연결 되어 있으면 진행
        if(NetworkCheck.networkCheck(SplashActivity.this)) {
            PrograssTask task = new PrograssTask();
            task.execute();
        }
        else{
            Toast.makeText(SplashActivity.this, "네트워크연결이 되지 않습니다.\n" + "네트워크 수신상태를 확인하세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private class PrograssTask extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog = new ProgressDialog(
                SplashActivity.this);

        @Override
        protected void onPreExecute(){
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("완전한 암호화 설정중입니다");
            progressDialog.setCanceledOnTouchOutside(false); // 프로그래스 끄기 방지
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                for(int i =0 ; i<10 ; i++) {
                    progressDialog.setProgress(i*10);
                    Thread.sleep(100);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            super.onPostExecute(result);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                try {

                    RSA.rsaKeyGen();
                    AES.aesKeyGen();
                    String aesTest ="네트워크 시큐리티";
                    String rsaTest = "네뚹";
                    String KeyTest = "이주완";
                    System.out.println("AES public Key:" + AES.secretKEY);
                    System.out.println("RSA public Key:" + RSA.publicKEY); // 비밀키는 'String'형태로 반환
                    System.out.println("RSA private Key:" +RSA.privateKEY); // 비밀키는 'String'형태로 반환
                    System.out.println("암호테스트");
                    aesTest = AES.aesEncryption(aesTest,AES.secretKEY);
                    rsaTest = RSA.rsaEncryption(rsaTest,RSA.publicKEY);
                    // KeyTest = KEYSTORE.keyStore_Encryption(KeyTest);
                    System.out.println("암호테스트 AES: "+aesTest);
                    System.out.println("암호테스트 RSA: "+rsaTest);
                    // System.out.println("암호테스트 Keystore:" + KeyTest); // 비밀키는 'String'형태로 반환

                    System.out.println("복호테스트");
                    aesTest = AES.aesDecryption(aesTest,AES.secretKEY);
                    rsaTest = RSA.rsaDecryption(rsaTest,RSA.privateKEY);
                    //  KeyTest = KEYSTORE.keyStore_Decryption(KeyTest);
                    System.out.println("복호테스트 AES: "+aesTest);
                    System.out.println("복호테스트 RSA: "+rsaTest);
                    //   System.out.println("복호테스트 Keystore: " + KeyTest); // 비밀키는 'String'형태로 반환
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(SplashActivity.this, "완전 암호화 설정완료", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    RSA.rsaKeyGen();
                    AES.aesKeyGen();
                    Toast.makeText(SplashActivity.this, "버전 부족으로 완전 암호화는 설정하지못했습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


//    private void rsaKeyRequest(){ // RSA암호화에 사용할 공개키를 서버에게 요청
//        StringBuffer url = new StringBuffer("http://210.125.212.191:8888/IoT/key받기.jsp");
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST, String.valueOf(url),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        String[] resPonse_split = response.split(" ");
//                        if("키받음".equals(resPonse_split[1])) {
//
//                            RSA.serverPublicKey=resPonse_split[0]; // 서버의 공개키 저장
//                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                            finish();
//
//                        }else if("키 못받음".equals(resPonse_split[1])) {
//                            Toast.makeText(getApplicationContext(), "암호화 셋팅 실패. 다시 실행해주세요.", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                // 회원가입 정보 push 진행
//                params.put("type","키주세요");
//                return params;
//            }
//        };
//
//        // 캐시 데이터 가져오지 않음 왜냐면 기존 데이터 가져올 수 있기때문
//        // 항상 새로운 데이터를 위해 false
//        stringRequest.setShouldCache(false);
//        VolleyQueueSingleTon.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
//    }

//            try {
//
//               // RSA.rsaKeyGen(); // 최초 한번 클라이언트의 RSA 비대칭키 생성
//
//               // rsaKeyRequest(); // 서버로부터 RSA공개키 요청
//
//            } catch (NoSuchAlgorithmException e) {
//                System.err.println("Splash Activty NoSuchAlgorithmException error");
//            }