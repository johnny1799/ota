package com.asu.ota;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asu.ota.database.DatabaseHelper;
import com.asu.ota.http.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
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
    private static ImageListViewAdapter listViewAdapter;

    /**
     * 保存数据
     */
    private static List<ImageBean> versionBeanList = new ArrayList<>();

    /**
     * 数据库操作驱动
     */
    public static DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_main);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收产品id
        int productId = bundle.getInt("productId");

        //加载数据库
        helper = new DatabaseHelper(this);
        helper.getWritableDatabase();

        this.mContext = this;

        //加载listview
        listView = (ListView) findViewById(R.id.imageListView);
        listViewAdapter = new ImageListViewAdapter(mContext,versionBeanList);
        listView.setAdapter(listViewAdapter);
//        listView.setOnItemClickListener(this);

        //save button的点击事件
        Button saveButton = (Button) findViewById(R.id.id);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveVersionMessage();
            }
        });

        SQLiteDatabase db = helper.getReadableDatabase();

        //清空表数据,接口数据入库
        try{
            clearTable("Package");
            String url = "http://192.168.11.220:8089/image/version/list?productId="+productId;
            String result = new Request().sendGet(url);
            JSONObject jo = new JSONObject(new String(result));
            JSONObject jo1 =(JSONObject)jo.get("data");
            JSONArray  jsonArray = (JSONArray)jo1.get("list");
            for(int i=0;i<jsonArray.length();i++) {
                String id =jsonArray.getJSONObject(i).get("id")+"";
                String version =jsonArray.getJSONObject(i).get("version")+"";
                //入库
                ContentValues values = new ContentValues();
                //添加第一组数据
                values.put("version", version);
                values.put("dbid", id);
                db.insert("Package", null, values);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //查询刷新数据
        query(db);
    }


    public static void query(SQLiteDatabase db){
        //查询Package表中所有的数据
        Cursor cursor = db.query("Package", null, null, null, null, null, null);
        versionBeanList.clear();
        //查询
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String version = cursor.getString(cursor.getColumnIndex("version"));
                ImageBean versionBean = new ImageBean();
                versionBean.setVersion(version);
                versionBeanList.add(versionBean);
            }
        }
        cursor.close();
        db.close();
        listViewAdapter = new ImageListViewAdapter(mContext,versionBeanList);
        listView.setAdapter(listViewAdapter);
    }



    /**
     * 保存产品的信息
     */
    private void saveVersionMessage()
    {
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

        if ("".equals(nameEditText.getText().toString()))
        {
            Toast.makeText(mContext,"产品名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //判断该产品是否存在
        for (ImageBean versionBean : versionBeanList)
        {
            if (versionBean.equals(nameEditText.getText().toString()))
            {
                Toast.makeText(mContext,nameEditText.getText().toString() + "已经存在",Toast.LENGTH_SHORT).show();
                return;
            }
        }


        ImageBean versionBean = new ImageBean(nameEditText.getText().toString());
        versionBeanList.add(versionBean);

        try {
            String url = "http://192.168.11.220:8089/product/add";
            String param = "name="+versionBean.getVersion()+"&comment=";
            String result = new Request().sendPost(url,param);
            JSONObject jo = new JSONObject(new String(result));
            Integer code = (Integer)jo.get("code");

            if(code == 0){
                Integer dbid = (Integer)jo.get("data");
                //入库
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //添加第一组数据
                values.put("name", versionBean.getVersion());
                values.put("dbid", dbid);
                db.insert("Package", null, values);

                //装载数据
                query(db);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearTable(String table){
        String sql = "DELETE FROM " + table +";";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
        revertSeq(table);
    }

    private void revertSeq(String table) {
        String sql = "update sqlite_sequence set seq=0 where name='"+table+"'";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView tv = (TextView)view.findViewById(R.id.showProName);
        String name  = tv.getText()+"";

        Intent intent =new Intent(ImageActivity.this, ImageActivity.class);

        //用Bundle携带数据
        Bundle bundle=new Bundle();
        //传递name参数为tinyphp
        bundle.putString("name", "name");
        intent.putExtras(bundle);
        startActivity(intent);

    }
}