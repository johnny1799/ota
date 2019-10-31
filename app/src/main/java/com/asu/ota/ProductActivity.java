package com.asu.ota;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProductActivity extends AppCompatActivity
{

    /**
     * Context
     */
    private Context mContext;


    /**
     * listview
     */
    private ListView listView;

    /**
     * 适配器
     */
    private ListViewAdapter listViewAdapter;

    /**
     * 保存数据
     */
    private List<ProductBean> productBeanList = new ArrayList<ProductBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_main);

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
    }



    /**
     * 保存学生的信息
     */
    private void saveProductMessage()
    {
        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        EditText descEditText = (EditText) findViewById(R.id.descEditText);

        if ("".equals(nameEditText.getText().toString())||"".equals(descEditText.getText().toString()))
        {
            Toast.makeText(mContext,"姓名和描述信息都不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        //判断该学生是否存在
        for (ProductBean productBean : productBeanList)
        {
            if (productBean.getName().equals(nameEditText.getText().toString()))
            {
                Toast.makeText(mContext,nameEditText.getText().toString() + "已经存在",Toast.LENGTH_SHORT).show();
                return;
            }
        }


        ProductBean productBean = new ProductBean(nameEditText.getText().toString(),descEditText.getText().toString());
        productBeanList.add(productBean);

        listViewAdapter.notifyDataSetChanged();
    }

}