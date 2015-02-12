package com.example.fei.yhb_20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.ui.FirstActivity;
import com.example.fei.yhb_20.ui.MainActivity;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;


public class Splash extends Activity {

    private TextView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Bmob.initialize(this, "7f5bdcbed568053f7db01753228cd960");
        splash = (TextView) findViewById(R.id.tv_splash);
        TranslateAnimation animation = new TranslateAnimation(0,0,0,-200);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkForLogin(Splash.this);
                splash.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splash.setAnimation(animation);

    }

    /**
     * 检查当前是否已经登录
     * @param context
     */
    private void checkForLogin(Context context) {
        BmobUser person = BmobUser.getCurrentUser(context);
        if(person!=null){
            //进入主页
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "you can use the app", Toast.LENGTH_LONG).show();
            finish();
        }else{
            //进入注册界面
            Intent intent = new Intent(context, FirstActivity.class);
            startActivity(intent);
            finish();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}

