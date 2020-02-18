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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.Network_Utill.VolleyQueueSingleTon;
import com.example.nslngiot.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class StructureFragment extends Fragment {

    public PhotoView StructureImage;
    public Button upload;
    public ImageButton gallery;
    public Bitmap setImage;//화면상 등록되는 이미지 파일
    public String encodeImage;//서버로 전송 할 이미지 String
    private static final int REQUEST_CODE = 0;
    private String url = "http://210.125.212.191:8888/IoT/ImageUpload.jsp";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_structure, container, false);

        StructureImage = (PhotoView) view.findViewById(R.id.pho_manager_structure);
        gallery = view.findViewById(R.id.btn_picture);
        upload = view.findViewById(R.id.btn_add);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        FileUploadUtils(2);
        gallery.setOnClickListener(new View.OnClickListener() {//갤러리 열기
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {//이미지 서버 업로드

            @Override
            public void onClick(View view) {

                encodeImage = BitmapToString(setImage);
                FileUploadUtils(1);

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {//갤러리에서 이미지 선택 및 포토뷰로 설정

        if(setImage !=null){
            //사용하지않는 Bitmap을 recucle 가용메모리 늘림.
            setImage.recycle();
            setImage = null;
            ((BitmapDrawable) StructureImage.getDrawable()).getBitmap().recycle();

        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    System.out.println("이미지 설정 진입");
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());


                    //이미지 크기 1/8 로 축소, 리사이즈
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    setImage = BitmapFactory.decodeStream(in, null, options);

                    in.close();
                    StructureImage.setImageBitmap(setImage);

                } catch (Exception e) {

                }
            }
        }
    }


    //이미지 전송 및 조회
    public void FileUploadUtils(final int menu) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, String.valueOf(url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("리스폰 : " + response);
                        switch (menu) {
                            case 1:
                                Respon(response);
                                break;
                            case 2:
                                setImage = StringToBitmap(response);
                                StructureImage.setImageBitmap(setImage);
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
                    case 1://이미지 전송
                        params.put("type", "strUpload");
                        params.put("imgFile", encodeImage);
                        break;
                    case 2://이미지 조회
                        params.put("type", "strShow");
                        break;
                }

                return params;
            }
        };

        stringRequest.setShouldCache(true);
        VolleyQueueSingleTon.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public String BitmapToString(Bitmap bitmap) { //Bitmap을 String로 변경
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 98, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);

        return temp;
    }


    public static Bitmap StringToBitmap(String encodedString) {//String을 Bitmap으로 변환
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void Respon(String respon) {

        switch (respon) {
            case "strUploaded":
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