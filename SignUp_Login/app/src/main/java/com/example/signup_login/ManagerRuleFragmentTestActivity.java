package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.widget.Toast.LENGTH_LONG;

public class ManagerRuleFragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_rule_fragment_test);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.ManagerRuleFragment, new ManagerMypageFragment());//넣고싶은 Fragment를 넣는다.
        fragmentTransaction.commit();

        final Button backButton = (Button) findViewById(R.id.backButton);
        final Button modifyButton = (Button) findViewById(R.id.modifyButton1);
        final Button saveButton = (Button) findViewById(R.id.saveButton);

        final EditText ruleEditText = (EditText) findViewById(R.id.editText);

        backButton.setOnClickListener(new View.OnClickListener() {//backButton클릭시 뒤로간다.
            @Override
            public void onClick(View v) {

            }
        });
////////
        //수정버튼을 누르면 EditText의 내용이 "memo.txt"에 저장이 된다.
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String memo = ruleEditText.getText().toString();

                try{
                    FileOutputStream fos = openFileOutput("memo.txt", Activity.MODE_WORLD_WRITEABLE);
                    fos.write(memo.getBytes());
                    Toast.makeText(getApplicationContext(), "수정 완료.", Toast.LENGTH_SHORT).show();
                    ruleEditText.setText(memo);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        //saveButton은 파일에서 내용을 읽어서 ruleEditText에 적는다.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo = ruleEditText.getText().toString();
                ruleEditText.setText(memo);

                FileInputStream fis = null;
                try{
                    fis = openFileInput("memo.txt");
                    byte[] data = new byte[fis.available()];
                    String d = "";

                    while(fis.read(data) != -1){
                        d += data[fis.read(data)];
                    }
                    ruleEditText.setText(new String(d));
                    //Toast.makeText(getApplicationContext(),"저장 완료", Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    e.printStackTrace();
                }

                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //관리자가 정한 규칙을 멤버모드 규칙으로 보냄.
                // Intent intent = new Intent(getApplicationContext(), MemberRuleFragment.class);
                //intent.putExtra("rule",memo);
                //startActivity(intent);
            }
        });
    }
}
