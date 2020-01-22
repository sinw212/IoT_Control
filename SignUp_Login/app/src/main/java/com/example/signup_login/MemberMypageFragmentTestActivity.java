package com.example.signup_login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MemberMypageFragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_mypage_fragment_test);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.membermypagefragment, new MemberMypageFragment());//fragment랑 Activity연결
        fragmentTransaction.commit();

        Button backButton = (Button) findViewById(R.id.backButton);
        Button modifyButton = (Button) findViewById(R.id.modifyButton);
        Button logoutButton = (Button) findViewById(R.id.logoutButton);

        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText studentnumberEditText = (EditText) findViewById(R.id.studentnumberEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
        final EditText pwmodifyEditText = (EditText) findViewById(R.id.pwmodifyEditText);

        final String pw = pwEditText.getText().toString();
        final String pwmodify = pwmodifyEditText.getText().toString();
        final String name = nameEditText.getText().toString();
        final String studentnumber = studentnumberEditText.getText().toString();

        backButton.setOnClickListener(new View.OnClickListener(){//뒤로가기
            @Override
            public void onClick(View v) {

            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {//수정버튼 누르면 id,pw를
            @Override
            public void onClick(View v) {

                for(int i = 0; i < studentnumber.length();i++){//여기 실행안됨...
                    if(48<=(byte)studentnumber.charAt(i)&&(byte)studentnumber.charAt(i)<=57) {//학번이 숫자로만 돼있는 경우.
                        Toast.makeText(getApplicationContext(), "숫자 이외의 문자가 학번에 있습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if(nameEditText.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }else if(studentnumberEditText.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "학번을 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }else if(pwEditText.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해 주세요.",Toast.LENGTH_SHORT).show();
                }else{

                    //이름, 학번, 비밀번호를 백앤드로 보내는 것 추가

                    if(pwEditText.getText().toString().equals(pwmodifyEditText.getText().toString())){//비밀번호, 이름, 학번을 수정하여 저장시킨다.
                        Toast.makeText(getApplicationContext(), "수정되었습니다.",Toast.LENGTH_SHORT).show();
                        nameEditText.setText("");
                        studentnumberEditText.setText("");
                        pwEditText.setText("");
                        pwmodifyEditText.setText("");
                    }else{
                        Toast.makeText(getApplicationContext(), "입력한 비밀번호가 서로 다릅니다.",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SelectModeActivity.class);
                startActivity(intent);
            }
        });
    }
}
