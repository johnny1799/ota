package com.asu.ota.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;

import com.asu.ota.R;
import com.asu.ota.utils.CommonRequest;
import com.asu.ota.utils.NetWorkUtil;
import com.idescout.sql.SqlScoutServer;

import org.json.JSONObject;

/**Application
 * @author lq
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTv_main_title;//标题
    private Button mBtn_login;//登录按钮
    private String mUserName, mPsw;//获取的用户名，密码
    private EditText mEt_user_name, mEt_psw;//编辑框

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //允许存储在sd卡中
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        //可进行sqlsout操作sqlite
        SqlScoutServer.create(this, getPackageName());

        //网络连接不能放在主线程
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);
        //设置为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
    }

    //获取界面控件
    private void init() {
        //从main_title_bar中获取的id
        mTv_main_title = findViewById(R.id.tv_main_title);
        mTv_main_title.setText("登录");
        mBtn_login = findViewById(R.id.btn_login);
        mEt_user_name = findViewById(R.id.et_user_name);
        mEt_psw = findViewById(R.id.et_psw);

        //登录按钮的点击事件
        mBtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始登录，获取用户名和密码 getText().toString().trim();
                mUserName = mEt_user_name.getText().toString().trim();
                mPsw = mEt_psw.getText().toString().trim();

                // TextUtils.isEmpty
                if (TextUtils.isEmpty(mUserName)) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(mPsw)) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        //判断网路是否畅通加权限
                        if(NetWorkUtil.isNetAvailable(MainActivity.this)){//网络畅通
                            //开始请求数据
                            String url = "/home/login";
                            String param = "username=" + mUserName + "&password=" + mPsw;
                            String result = new CommonRequest().sendPost(url, param);
                            JSONObject jo = new JSONObject(new String(result));
                            Integer code = (Integer) jo.get("code");
                            if (code == 0) {
                                //一致登录成功
                                Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                //登录成功后关闭此页面进入主页
                                Intent data = new Intent();
                                //datad.putExtra( ); name , value ;
                                data.putExtra("isLogin", true);
                                //RESULT_OK为Activity系统常量，状态码为-1
                                // 表示此页面下的内容操作成功将data返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值
                                setResult(RESULT_OK, data);
                                //跳转到主界面，登录成功的状态传递到 MainActivity 中
                                startActivity(new Intent(MainActivity.this, ProductActivity.class));
                                return;
                            } else {
                                Toast.makeText(MainActivity.this, "账户或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "目前没网请检查网络权限", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("login", e.getMessage());
                    }
                }
            }
        });
    }
}
