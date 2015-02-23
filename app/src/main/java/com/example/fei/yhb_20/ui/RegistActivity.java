package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Person;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.MD5;
import com.example.fei.yhb_20.utils.MyUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends ActionBarActivity implements View.OnClickListener{

    @InjectView(R.id.et_regist_email)EditText mEmail;
    @InjectView(R.id.et_regist_password)EditText mPassword;
    @InjectView(R.id.et_regist_username)EditText mUsername;
    @InjectView(R.id.tv_regist_protocol)TextView mProtocol;
    @InjectView(R.id.bt_regist_regist)Button mRegist;
    @InjectView(R.id.textview)TextView textView;

    char role ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        role = getIntent().getCharExtra("role", (char) 0);
        if (role==GV.MERCHANT){
            textView.setVisibility(View.INVISIBLE);
            mProtocol.setVisibility(View.INVISIBLE);
            mRegist.setText("下一步");
        }
        initEvents();
    }

    private void initEvents() {
        mProtocol.setOnClickListener(this);
        mRegist.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regist, menu);
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
        }else if(id == android.R.id.home){
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_regist_regist:
                    //注册
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();
                    String username = mUsername.getText().toString();

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)){
                        Toast.makeText(this,"请填写必要的注册信息",Toast.LENGTH_LONG).show();
                    }else if (!MyUtils.isEmail(email)){
                        Toast.makeText(this,"邮箱格式不正确",Toast.LENGTH_LONG).show();
                        mEmail.setText("");
                    }else if (!MyUtils.passwordNumberLength(password)){
                        Toast.makeText(this,"密码须6位以上18位以下",Toast.LENGTH_LONG).show();
                        mPassword.setText("");
                    }else {
                        switch (role) {
                            case GV.MERCHANT:
                                Intent intent = new Intent(this, MerchantRegist.class);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                break;
                            case GV.PERSON:
                                //个人应该是在这里注册成功了
                                Person person = new Person();
                                person.setEmail(email);
                                MD5 md5 = new MD5();
                                person.setPassword(md5.getMD5ofStr(password));
                                person.setUsername(username);
                                person.setAttribute(GV.PERSON);
                                person.signUp(RegistActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(RegistActivity.this, s, Toast.LENGTH_LONG).show();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        finish();
                    }
                    break;
                case R.id.tv_regist_protocol:

                    //在这里添加用户协议界面，暂时先不添加
                    break;
                default:
                    break;
            }
        }

}
