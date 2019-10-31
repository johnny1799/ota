package com.asu.ota;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asu.ota.database.DatabaseHelper;

import java.util.HashMap;
import java.util.List;

public class ProductActivity extends AppCompatActivity
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

        //入库
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //添加第一组数据
        values.put("name", productBean.getName());
        db.insert("Product", null, values);

        //装载数据
        query(db);
        //listViewAdapter.notifyDataSetChanged();
    }


}