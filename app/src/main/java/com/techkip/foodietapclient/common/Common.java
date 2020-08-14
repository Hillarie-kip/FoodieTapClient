package com.techkip.foodietapclient.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.techkip.foodietapclient.Remote.APIService;
import com.techkip.foodietapclient.Remote.RetrofitClient;
import com.techkip.foodietapclient.Service.MyFirebaseIdService;
import com.techkip.foodietapclient.model.User;

/**
 * Created by hillarie on 28/05/2018.
 */

public class Common {

    public static User currentUser;


    public static final String BASE_URL = "https://fcm.googleapis.com/";
    public static final String googleAPIUrl = "https://maps.googleapis.com";

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }


    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "Order on Its Way";
        else
            return "Shipped";
    }

    public  static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos !=null){
                for (int i =0;i<infos.length;i++){
                    if (infos[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
