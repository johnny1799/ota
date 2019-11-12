package com.asu.ota.activity;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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

import com.asu.ota.R;
import com.asu.ota.adapter.ListViewAdapter;
import com.asu.ota.database.DatabaseHelper;
import com.asu.ota.utils.CommonRequest;
import com.asu.ota.model.ProductBean;
import com.asu.ota.utils.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ProductActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /**
     * Context
     */
    public static Context sContext;

    /**
     * listview
     */
    private static ListView sListView;

    public static DatabaseHelper sHelper;

    /**
     * 适配器
     */
    private static ListViewAdapter sListViewAdapter;

    public static Uri sUri;
    private static Cursor sCursor;
    public static ContentResolver sContentResolver;

    /**
     * 保存数据
     */
    private static List<ProductBean> productBeanList = new ArrayList<ProductBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);
        sHelper = new DatabaseHelper(this);
        sContentResolver = getContentResolver();
        sUri = Uri.parse("content://com.asu.ota.service.ProductContentProvider");

        this.sContext = this;

        //加载listview
        sListView = (ListView) findViewById(R.id.listView);
        sListViewAdapter = new ListViewAdapter(sContext, productBeanList);
        sListView.setAdapter(sListViewAdapter);
        sListView.setOnItemClickListener(this);

        //save button的点击事件
        Button saveButton = (Button) findViewById(R.id.id);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductMessage();
            }
        });

        //清空表数据,接口数据入库
        try {
            //判断网路是否畅通加权限
            if(NetWorkUtil.isNetAvailable(sContext)){//网络畅通
                //开始请求数据
                clearTable();
                String url = "/product/list";
                String result = new CommonRequest().sendGet(url);
                JSONObject jo = new JSONObject(new String(result));
                JSONObject jo1 = (JSONObject) jo.get("data");
                JSONArray jsonArray = (JSONArray) jo1.get("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getJSONObject(i).get("id") + "";
                    String name = jsonArray.getJSONObject(i).get("name") + "";
                    //入库
                    ContentValues values = new ContentValues();
                    //添加第一组数据
                    values.put("name", name);
                    values.put("dbid", id);
                    sContentResolver.insert(sUri, values);

                }
            }else{
                Toast.makeText(sContext, "目前没网请检查网络权限", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //查询刷新数据
        query();
    }


    public static void query() {
        //判断网路是否畅通加权限
        if(NetWorkUtil.isNetAvailable(sContext)){//网络畅通
            //查询Product表中所有的数据
            sCursor = sContentResolver.query(sUri, new String[]{"name"}, null, null, null, null);
            productBeanList.clear();
            //查询
            if (sCursor.moveToFirst()) {
                while (sCursor.moveToNext()) {
                    String name = sCursor.getString(sCursor.getColumnIndex("name"));
                    ProductBean productBean = new ProductBean();
                    productBean.setName(name);
                    productBeanList.add(productBean);
                }
            }
            sCursor.close();
            sListViewAdapter = new ListViewAdapter(sContext, productBeanList);
            sListView.setAdapter(sListViewAdapter);
        }else{
            Toast.makeText(sContext, "目前没网请检查网络权限", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 保存产品的信息
     */
    private void saveProductMessage() {
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);

        if ("".equals(nameEditText.getText().toString())) {
            Toast.makeText(sContext, "产品名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //判断该产品是否存在
        for (ProductBean productBean : productBeanList) {
            if (productBean.getName().equals(nameEditText.getText().toString())) {
                Toast.makeText(sContext, nameEditText.getText().toString() + "已经存在", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        ProductBean productBean = new ProductBean(nameEditText.getText().toString());
        productBeanList.add(productBean);

        try {
            //判断网路是否畅通加权限
            if(NetWorkUtil.isNetAvailable(sContext)){//网络畅通
                //开始请求数据
                String url = "/product/add";
                String param = "name=" + productBean.getName() + "&comment=";
                String result = new CommonRequest().sendPost(url, param);
                JSONObject jo = new JSONObject(new String(result));
                Integer code = (Integer) jo.get("code");

                if (code == 0) {
                    Integer dbid = (Integer) jo.get("data");
                    //入库
                    ContentValues values = new ContentValues();
                    //添加第一组数据
                    values.put("name", productBean.getName());
                    values.put("dbid", dbid);
                    sContentResolver.insert(sUri, values);

                    //装载数据
                    query();
                }
            }else{
                Toast.makeText(sContext, "目前没网请检查网络权限", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        sContentResolver.delete(sUri, null, null);
        revertSeq();
    }

    //sequence置零
    private void revertSeq() {
        String sql = "update sqlite_sequence set seq=0 where name='Product'";
        SQLiteDatabase db = sHelper.getWritableDatabase();
        db.execSQL(sql);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //判断网路是否畅通加权限
        if(NetWorkUtil.isNetAvailable(sContext)){//网络畅通
            TextView tv = (TextView) view.findViewById(R.id.showProName);
            String name = tv.getText() + "";

            int productId = 0;
            sCursor = sContentResolver.query(sUri, new String[]{"dbid"}, "name=?", new String[]{name}, null, null);
            while (sCursor.moveToNext()) {
                productId = sCursor.getInt(0); //获取第一列的值,第一列的索引从0开始
            }

            sCursor.close();
            Intent intent = new Intent(ProductActivity.this, ImageActivity.class);
            intent.putExtra("productId", productId);
            intent.putExtra("version", name);
            startActivity(intent);
        }else{
            Toast.makeText(sContext, "目前没网请检查网络权限", Toast.LENGTH_SHORT).show();
        }
    }
}