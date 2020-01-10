package com.example.signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ManagerIpkeyFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manager_ipkey,container,false);//fragment_manage_ipkey와 연결..
    }

    public void onCLick(View v) {

        EditText editText = (EditText) getView().findViewById(R.id.editText);

        switch(v.getId()){

            case R.id.deleteButton:{
                editText.setText("");
                break;
            }
            case R.id.backButton:{

                break;
            }
            case R.id.saveButton:{

                break;
            }
            case R.id.imageView:{

                int PICK_IMAGE = 1;

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        }

    }



}
