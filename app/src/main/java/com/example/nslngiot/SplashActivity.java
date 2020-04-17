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

/*
    보안 담당: 이주완
    2020.02~03 하이브리드 암호 모듈, KeyStore 암호 모듈, XSS, SQL injection, FileUpload 필터링 장착 
    2020.03.14 민감정보 메모리 덤프 이슈 - 로직에 맞게 민감정보 메모리 삭제
    2020.03.20 키보드 복사 캐시 이슈  - 키보드 복사를 통해 캐시에 복사 불가능하도록 캐시 비활성화
 */
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
                            // 1. 생성된 대칭키를 추후 삭제하기 위해 대칭키 레퍼런스(secretKey)를 복사
                            char[] clone_key = AES.secretKEY;

                            // 2. 생성된 개인키/대칭키 keystore의 비대칭암호로 암호화하여 static 메모리 레퍼런스 진행
                            AES.secretKEY = KEYSTORE.keyStore_Encryption(AES.secretKEY);

                            // 3. 1에서 미리 복사해둔 레퍼런스를 통해 기존의 대칭키를 메모리에서 삭제
                            java.util.Arrays.fill(clone_key,(char)0x20);
                            checkSecurity += 1;
                            break;
                        default:
                            break;
                    }
                    progressDialog.setProgress(i * 10);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                System.err.println("SplashActivity InterruptedException error ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            super.onPostExecute(result);
            if (checkSecurity == 2) { // 수정필요 추후
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SplashActivity.this, "암호화 설정에 실패했습니다. 다시 앱을 설치해주세요.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}