package com.asu.ota;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.asu.ota.database.DatabaseHelper;
import com.asu.ota.http.Request;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private TextView tv_main_title;//标题
    private TextView tv_back,tv_register,tv_find_psw;//返回键,显示的注册，找回密码
    private Button btn_login;//登录按钮
    private String userName,psw;//获取的用户名，密码
    private EditText et_user_name,et_psw;//编辑框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    //获取界面控件
    private void init() {
        //从main_title_bar中获取的id
        tv_main_title=findViewById(R.id.tv_main_title);
        tv_main_title.setText("登录");
        btn_login=findViewById(R.id.btn_login);
        et_user_name=findViewById(R.id.et_user_name);
        et_psw=findViewById(R.id.et_psw);

        //登录按钮的点击事件
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                userName=et_user_name.getText().toString().trim();
                psw=et_psw.getText().toString().trim();

                // TextUtils.isEmpty
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(psw)){
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    try {
                        String url = "http://192.168.11.220:8089/home/login";
                        String param = "username="+userName+"&password="+psw;
                        String result = new Request().sendPost(url,param);
                        JSONObject jo = new JSONObject(new String(result));
                        Integer code = (Integer)jo.get("code");
                        if(code == 0){
                            //一致登录成功
                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            //登录成功后关闭此页面进入主页
                            Intent data=new Intent();
                            //datad.putExtra( ); name , value ;
                            data.putExtra("isLogin",true);
                            //RESULT_OK为Activity系统常量，状态码为-1
                            // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                            setResult(RESULT_OK,data);
                            //跳转到主界面，登录成功的状态传递到 MainActivity 中
                            startActivity(new Intent(MainActivity.this, ProductActivity.class));
                            return;
                        }else{
                            Toast.makeText(MainActivity.this, "账户或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        Log.e("login",e.getMessage());
                    }
                }
            }
        });
    }
}
