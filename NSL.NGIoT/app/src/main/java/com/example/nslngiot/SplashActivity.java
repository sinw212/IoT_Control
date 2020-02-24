package com.example.nslngiot;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.nslngiot.Network_Utill.NetworkCheck;
import com.example.nslngiot.Security_Utill.AES;
import com.example.nslngiot.Security_Utill.KEYSTORE;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends Activity {

    private int checkSecurity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 네트워크 연결 되어 있으면 진행
        if (NetworkCheck.networkCheck(SplashActivity.this)) {
            PrograssTask task = new PrograssTask();
            task.execute();
        } else {
            Toast.makeText(SplashActivity.this, "네트워크연결이 되지 않습니다.\n" + "네트워크 수신상태를 확인하세요.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class PrograssTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(
                SplashActivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("완전한 암호화 설정중입니다");
            progressDialog.setCanceledOnTouchOutside(false); // 프로그래스 끄기 방지
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int i = 0; i < 10; i++) {
                    switch (i * 10) {
                        case 10:
                            KEYSTORE keystore = new KEYSTORE();
                            keystore.keyStore_init(getApplicationContext()); // 최초 1회 KeyStore에 저장할 AES 대칭키 생성
                            checkSecurity += 1;
                            break;
                        case 20:
                            AES.aesKeyGen();
                            AES.secretKEY = KEYSTORE.keyStore_Encryption(AES.secretKEY);
                            // 생성된 개인키/대칭키 keystore의 비대칭암호로 암호화하여 static 메모리 적재
                            checkSecurity += 1;
                            break;
                        default:
                            break;
                    }
                    progressDialog.setProgress(i * 10);
                    Thread.sleep(100);
                }
            } catch (InterruptedException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            super.onPostExecute(result);
            if (checkSecurity == 2) { // 수정필요 추후
                Toast.makeText(SplashActivity.this, "완전 암호화 설정완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SplashActivity.this, "암호화 설정에 실패했습니다. 다시 앱을 설치해주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}