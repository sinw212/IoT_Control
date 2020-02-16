package com.example.nslngiot.ManagerFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.nslngiot.R;
import com.github.chrisbanes.photoview.PhotoView;

import static android.app.Activity.RESULT_OK;

public class IpFragment extends Fragment {

    private RequestQueue mQueue;
    public PhotoView IPImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_ip, container, false);

        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


        ImageRequest imageRequest = new ImageRequest("http://sj50419.cafe24.com/min/img/CAM00739.jpg", responseListener, 295, 400, Bitmap.Config.ARGB_8888, errorListener);
        mQueue.add(imageRequest);
        IPImage = (PhotoView) view.findViewById(R.id.pho_manager_ip);

        Button gallery = getActivity().findViewById(R.id.btn_picture);
        Button upload = getActivity().findViewById(R.id.btn_add);

        gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode != 1||resultCode != RESULT_OK){
            return;
        }
        Uri dataUri = data.getData();
        IPImage.setImageURI(dataUri);

        try{
            //InputStream in = getContentResolver().openInputStream(dataUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    com.android.volley.Response.Listener<Bitmap> responseListener = new Response.Listener<Bitmap>() {

        @Override
        public void onResponse(Bitmap response) {
            IPImage.setImageBitmap(response);
        }
    };

    com.android.volley.Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("오류 발생");
        }
    };
}
