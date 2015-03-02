package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.utils.MD5;
import com.marshalchen.common.uimodule.materialdesign.views.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.listener.SaveListener;

/**
 * 老用户登录的界面
 */
public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.bt_login)Button login;
    @InjectView(R.id.tv_login_forget)TextView forget;
    @InjectView(R.id.qq)ImageView qq;
    @InjectView(R.id.weixin)ImageView weixin;
    @InjectView(R.id.weibo)ImageView weibo;
    @InjectView(R.id.et_username)EditText username;
    @InjectView(R.id.et_password)EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        initEvents();
    }

    private void initEvents() {
        login.setOnClickListener(this);
        forget.setOnClickListener(this);
        qq.setOnClickListener(this);
        weixin.setOnClickListener(this);
        weibo.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                /**
                 * 得到当前用户，并登录
                 */
                MD5 md5 = new MD5();
                BaseUser user = new BaseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(md5.getMD5ofStr(password.getText().toString()));
                user.login(LoginActivity.this,new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(LoginActivity.this,"登录失败，您输入的密码有误！",Toast.LENGTH_LONG).show();
                        username.setText("");
                        password.setText("");
                    }
                });
                break;
            case R.id.qq:

                break;
            case R.id.weixin:

                break;
            case R.id.weibo:

                break;
            case R.id.tv_login_forget:

                break;
            default:
                break;
        }
    }
}
