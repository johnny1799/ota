package com.asu.ota.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.asu.ota.R;
import com.asu.ota.adapter.OtaListViewAdapter;
import com.asu.ota.database.DatabaseHelper;
import com.asu.ota.utils.CommonRequest;
import com.asu.ota.model.ImageBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OtaActivity extends AppCompatActivity
{

    /**
     * Context
     */
    public static Context sContext;


    /**
     * listview
     */
    private static ListView sListView;

    /**
     * 适配器
     */
    private static OtaListViewAdapter sListViewAdapter;

    /**
     * 保存数据
     */
    private static List<ImageBean> sVersionBeanList = new ArrayList<>();

    /**
     * 数据库操作驱动
     */
    public static DatabaseHelper sHelper;
    static int  sProductId=0;
    static String  sVersion="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.ota_main);

        //新页面接收数据
        Intent intent = getIntent();
        //产品id
        sProductId = intent.getIntExtra("productId",0);
        //版本号
        sVersion = intent.getStringExtra("version");

        //加载数据库
        sHelper = new DatabaseHelper(this);
        sHelper.getWritableDatabase();

        this.sContext = this;

        //加载listview
        sListView = (ListView) findViewById(R.id.otaListView);
        sListViewAdapter = new OtaListViewAdapter(sContext,sVersionBeanList);
        sListView.setAdapter(sListViewAdapter);

        sVersionBeanList.clear();
        //清空表数据,接口数据入库
        try{
            String url = "/image/ota/list?productId="+sProductId+"&version="+sVersion;
            String result = new CommonRequest().sendGet(url);
            JSONObject jo = new JSONObject(new String(result));
            JSONObject jo1 =(JSONObject)jo.get("data");
            JSONArray  jsonArray = (JSONArray)jo1.get("list");
            for(int i=0;i<jsonArray.length();i++) {
                String id =jsonArray.getJSONObject(i).get("id")+"";
                String version =jsonArray.getJSONObject(i).get("version")+"";
                String preVersion =jsonArray.getJSONObject(i).get("preVersion")+"";
                String location =jsonArray.getJSONObject(i).get("location")+"";
                String fileName =jsonArray.getJSONObject(i).get("fileName")+"";
                ImageBean versionBean = new ImageBean();
                versionBean.setId(Integer.parseInt(id));
                versionBean.setVersion(version);
                versionBean.setLocation(location);
                versionBean.setPreVersion(preVersion);
                versionBean.setFileName(fileName);
                sVersionBeanList.add(versionBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //查询刷新数据
        sListViewAdapter = new OtaListViewAdapter(sContext,sVersionBeanList);
        sListView.setAdapter(sListViewAdapter);
    }

}