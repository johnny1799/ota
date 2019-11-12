package com.asu.ota.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
    public static boolean isNetAvailable(Context context){
        //获得网络管理器
        ConnectivityManager cmager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //得到网络详情
        NetworkInfo netInfo = cmager.getActiveNetworkInfo();
        //判断当前是否有网络
        if(netInfo == null || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }
}
