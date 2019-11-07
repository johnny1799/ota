package com.asu.ota;

import java.util.ArrayList;

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
import com.asu.ota.http.CommonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ProductActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
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
    private static ListViewAdapter listViewAdapter;

    /**
     * 保存数据
     */
    private static List<ProductBean> productBeanList = new ArrayList<ProductBean>();

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
        setContentView(R.layout.product_main);

        //加载数据库
        helper = new DatabaseHelper(this);
        helper.getWritableDatabase();

        this.mContext = this;

        //加载listview
        listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new ListViewAdapter(mContext,productBeanList);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(this);

        //save button的点击事件
        Button saveButton = (Button) findViewById(R.id.id);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveProductMessage();
            }
        });

        SQLiteDatabase db = helper.getReadableDatabase();

        //清空表数据,接口数据入库
        try{
            clearTable("Product");
            String url = "http://192.168.11.220:8089/product/list";
            String result = new CommonRequest().sendGet(url);
            JSONObject jo = new JSONObject(new String(result));
            JSONObject jo1 =(JSONObject)jo.get("data");
            JSONArray  jsonArray = (JSONArray)jo1.get("list");
            for(int i=0;i<jsonArray.length();i++) {
                String id =jsonArray.getJSONObject(i).get("id")+"";
                String name =jsonArray.getJSONObject(i).get("name")+"";
                //入库
                ContentValues values = new ContentValues();
                //添加第一组数据
                values.put("name", name);
                values.put("dbid", id);
                db.insert("Product", null, values);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //查询刷新数据
        query(db);
    }


    public static void query(SQLiteDatabase db){
        //查询Product表中所有的数据
        Cursor cursor = db.query("Product", null, null, null, null, null, null);
        productBeanList.clear();
        //查询
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                ProductBean productBean = new ProductBean();
                productBean.setName(name);
                productBeanList.add(productBean);
            }
        }
        cursor.close();
        db.close();
        listViewAdapter = new ListViewAdapter(mContext,productBeanList);
        listView.setAdapter(listViewAdapter);
    }



    /**
     * 保存产品的信息
     */
    private void saveProductMessage()
    {
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

        if ("".equals(nameEditText.getText().toString()))
        {
            Toast.makeText(mContext,"产品名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //判断该产品是否存在
        for (ProductBean productBean : productBeanList)
        {
            if (productBean.getName().equals(nameEditText.getText().toString()))
            {
                Toast.makeText(mContext,nameEditText.getText().toString() + "已经存在",Toast.LENGTH_SHORT).show();
                return;
            }
        }


        ProductBean productBean = new ProductBean(nameEditText.getText().toString());
        productBeanList.add(productBean);

        try {
            String url = "http://192.168.11.220:8089/product/add";
            String param = "name="+productBean.getName()+"&comment=";
            String result = new CommonRequest().sendPost(url,param);
            JSONObject jo = new JSONObject(new String(result));
            Integer code = (Integer)jo.get("code");

            if(code == 0){
                Integer dbid = (Integer)jo.get("data");
                //入库
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //添加第一组数据
                values.put("name", productBean.getName());
                values.put("dbid", dbid);
                db.insert("Product", null, values);

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
        SQLiteDatabase db = ProductActivity.helper.getWritableDatabase();

        int productId = 0;
        Cursor cursor = db.rawQuery("select dbid from product where name=?",new String[]{name});
        while (cursor.moveToNext()) {
            productId = cursor.getInt(0); //获取第一列的值,第一列的索引从0开始
        }

        Intent intent =new Intent(ProductActivity.this,ImageActivity.class);

        //用Bundle携带数据
        Bundle bundle=new Bundle();
        //传递name参数为tinyphp
        bundle.putInt("productId", productId);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}