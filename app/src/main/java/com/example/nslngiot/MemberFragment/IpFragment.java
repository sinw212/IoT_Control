package com.example.nslngiot.MemberFragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class IpFragment extends Fragment {

    private RequestQueue mQueue;
    public PhotoView IPImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_ip, container, false);

        mQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


        ImageRequest imageRequest = new ImageRequest("http://sj50419.cafe24.com/min/img/CAM00739.jpg", responseListener, 295, 400, Bitmap.Config.ARGB_8888, errorListener);
        mQueue.add(imageRequest);
        IPImage = (PhotoView) view.findViewById(R.id.pho_member_ip);

        return view;
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