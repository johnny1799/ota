package com.asu.ota;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.asu.ota.database.DatabaseHelper;
import com.asu.ota.http.CommonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OtaActivity extends AppCompatActivity
{

    /**
     * Context
     */
    public static Context mContext;


    /**
     * listview
     */
    private static ListView listView;

    /**
     * 适配器
     */
    private static OtaListViewAdapter listViewAdapter;

    /**
     * 保存数据
     */
    private static List<ImageBean> versionBeanList = new ArrayList<>();

    /**
     * 数据库操作驱动
     */
    public static DatabaseHelper helper;
    static int  productId=0;
    static String  version="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_main);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //产品id
        productId = bundle.getInt("productId");
        //版本号
        version = bundle.getString("version");

        //加载数据库
        helper = new DatabaseHelper(this);
        helper.getWritableDatabase();

        this.mContext = this;

        //加载listview
        listView = (ListView) findViewById(R.id.otaListView);
        listViewAdapter = new OtaListViewAdapter(mContext,versionBeanList);
        listView.setAdapter(listViewAdapter);

        versionBeanList.clear();
        //清空表数据,接口数据入库
        try{
            String url = "http://192.168.11.220:8089/image/ota/list?productId="+productId+"&version="+version;
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
                versionBeanList.add(versionBean);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //查询刷新数据
        listViewAdapter = new OtaListViewAdapter(mContext,versionBeanList);
        listView.setAdapter(listViewAdapter);
    }

}