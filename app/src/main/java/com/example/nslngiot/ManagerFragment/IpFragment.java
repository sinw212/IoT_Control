package com.example.nslngiot.ManagerFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.github.chrisbanes.photoview.PhotoView;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class IpFragment extends Fragment {


    private PhotoView IPImage;
    private Button upload;
    private ImageButton gallery;
    private Bitmap setImage; // 화면상 등록되는 이미지 파일
    private String encodeImage; // 서버로 전송 할 이미지 String
    private static final int REQUEST_CODE = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_ip, container, false);
        return view;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IPImage = (PhotoView)getView().findViewById(R.id.pho_manager_ip);
        gallery = getView().findViewById(R.id.btn_picture);
        upload = getView().findViewById(R.id.btn_add);

        FileUploadUtils(2);

        gallery.setOnClickListener(new View.OnClickListener() {//갤러리 열기
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //이미지 서버 업로드
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                encodeImage = BitmapToString(setImage);
                FileUploadUtils(1);
            }
        });
    }


    // 갤러리에서 이미지 선택 및 포토뷰로 설정
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(setImage !=null){
            // 사용하지않는 Bitmap을 recucle 가용메모리 늘림.
            setImage.recycle();
            setImage = null;
            ((BitmapDrawable) IPImage.getDrawable()).getBitmap().recycle();

        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    System.out.println("이미지 설정 진입");
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());


                    // 이미지 크기 1/8 로 축소, 리사이즈
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    setImage = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                    IPImage.setImageBitmap(setImage);

                } catch (Exception e) {

                }
            }
        }
    }


    //이미지 전송 및 조회
    private void FileUploadUtils(final int menu) {

        final StringBuffer url =  new StringBuffer("http://210.125.212.191:8888/IoT/ImageUpload.jsp");

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (menu) {
                            case 1:
                                Respon(response);
                                break;
                            case 2:
                                setImage = StringToBitmap(response);
                                IPImage.setImageBitmap(setImage);
                                break;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                switch (menu) {
                    case 1: // 이미지 전송
                        params.put("type", "ipUpload");
                        params.put("imgFile", encodeImage);
                        break;
                    case 2: // 이미지 조회
                        params.put("type", "ipShow");
                        break;
                }
                return params;
            }
        };

        stringRequest.setShouldCache(true);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    // Bitmap을 String로 변경
    private String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 98, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);

        return temp;
    }

    //String을 Bitmap으로 변환
    private Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void Respon(String respon) {
        switch (respon) {
            case "ipUploaded":
                Toast.makeText(getActivity(), "업로드 성공", Toast.LENGTH_SHORT).show();
                break;
            case "error":
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                break;
            case "fileNotExist":
                Toast.makeText(getActivity(), "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}