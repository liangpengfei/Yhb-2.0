package com.example.fei.yhb_20.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

public class MerchantInfoPage extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.tv_info_merchantName)TextView mMerchantName;
    @InjectView(R.id.tv_info_address)TextView mAddress;
    @InjectView(R.id.tv_info_phone)TextView mPhone;
    @InjectView(R.id.tv_info_onTime)TextView mOnTime;
    @InjectView(R.id.tv_info_sort)TextView mSort;

    @InjectView(R.id.rl_info_0)RelativeLayout relativeLayout0;
    @InjectView(R.id.rl_info_1)RelativeLayout relativeLayout1;
    @InjectView(R.id.rl_info_2)RelativeLayout relativeLayout2;
    @InjectView(R.id.rl_info_3)RelativeLayout relativeLayout3;
    @InjectView(R.id.rl_info_4)RelativeLayout relativeLayout4;
    @InjectView(R.id.rl_info_5)RelativeLayout relativeLayout5;

    private Merchant merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_info_page);
        ButterKnife.inject(this);
        merchant = BmobUser.getCurrentUser(this,Merchant.class);
        if (merchant!=null){
            mMerchantName.setText(merchant.getMerchantName());
        }
        initViews();
        initEvents();
    }

    private void initViews() {

    }

    private void initEvents() {
        relativeLayout0.setOnClickListener(this);
        relativeLayout1.setOnClickListener(this);
        relativeLayout2.setOnClickListener(this);
        relativeLayout3.setOnClickListener(this);
        relativeLayout4.setOnClickListener(this);
        relativeLayout5.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merchant_info_page, menu);
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
            case R.id.rl_info_0:
                break;
            case R.id.rl_info_1:
                break;
            case R.id.rl_info_2:
                break;
            case R.id.rl_info_3:
                break;
            case R.id.rl_info_4:
                break;
            case R.id.rl_info_5:
                break;
        }
    }
}
