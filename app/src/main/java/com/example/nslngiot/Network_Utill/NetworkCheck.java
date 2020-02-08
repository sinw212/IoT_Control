package com.example.nslngiot.Network_Utill;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkCheck {


    public static boolean networkCheck(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        boolean isWifiConn = false;
        boolean isMobileConn = false;

        if (connectivityManager != null)
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (networkInfo != null)
            isWifiConn = networkInfo.isConnectedOrConnecting();


        if (connectivityManager != null)
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if (networkInfo != null)
            isMobileConn = networkInfo.isConnectedOrConnecting();


        if (isWifiConn)
            Toast.makeText(context, "Wi-Fi망에 접속중입니다.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "3G망에 접속중입니다.", Toast.LENGTH_SHORT).show();


        if (!isMobileConn && !isWifiConn) {
            /*
             * 네트워크 연결이 되지 않을경우 종료
             */
            Toast.makeText(context, "네트워크에 연결할수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
