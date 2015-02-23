package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.ui.fragment.ClassFragment;
import com.example.fei.yhb_20.ui.fragment.MainFragment;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.NetUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private Fragment fMain;
    private Fragment fClass;

    @InjectView(R.id.bt_slide_logout)Button logout;
    @InjectView(R.id.id_tab_class)LinearLayout mTabClass;
    @InjectView(R.id.id_tab_main)LinearLayout mTabMain;
    @InjectView(R.id.id_tab_post)LinearLayout mTabPost;
    @InjectView(R.id.id_tab_main_img)ImageButton mMainImage;
    @InjectView(R.id.id_tab_class_img)ImageButton mClassImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        NetUtil.checkForUpdate(this);
        initEvents();
        setSelect(GV.MAIN_PRESSED);
    }

    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

        //把相应的图片点亮
        // 设置内容区域
        switch (i)
        {
            case GV.MAIN_PRESSED:
                if (fMain == null)
                {
                    fMain = new MainFragment();
                    transaction.add(R.id.id_content,fMain);
                } else
                {
                    transaction.show(fMain);
                }
                mMainImage.setImageResource(R.drawable.main_pressed);
                break;
            case GV.CLASS_PRESSED:
                if (fClass == null)
                {
                    fClass = new ClassFragment();
                    transaction.add(R.id.id_content, fClass);
                } else
                {
                    transaction.show(fClass);
                }
                mClassImage.setImageResource(R.drawable.classification_pressed);
                break;
            default:
                break;
        }

        transaction.commit();
    }

    private void initEvents()
    {
        mTabMain.setOnClickListener(this);
        mTabClass.setOnClickListener(this);
        mTabPost.setOnClickListener(this);

        logout.setOnClickListener(this);
    }

    private void hideFragment(FragmentTransaction transaction)
    {
        if (fMain != null)
        {
            transaction.hide(fMain);
        }

        if (fClass != null)
        {
            transaction.hide(fClass);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_tab_class:
                resetImgs();
                setSelect(GV.CLASS_PRESSED);
                break;
            case R.id.id_tab_main:
                resetImgs();
                setSelect(GV.MAIN_PRESSED);
                break;
            case R.id.id_tab_post:
                break;
            case R.id.bt_slide_logout:
                BmobUser.logOut(this);
                BmobUser currentUser = BmobUser.getCurrentUser(this);
                Toast.makeText(this, "您已经成功退出", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(this,FirstActivity.class);
                startActivity(intent2);
                finish();
                break;
            default:
                break;
        }
    }

    private void resetImgs() {
        mClassImage.setImageResource(R.drawable.classification);
        mMainImage.setImageResource(R.drawable.main);
    }
}
