package com.example.fei.yhb_20.ui;

import android.app.Dialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.MerchantInfo;
import com.example.fei.yhb_20.utils.MyUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

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
    private MerchantInfo merchantInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_info_page);
        ButterKnife.inject(this);
        merchant = BmobUser.getCurrentUser(this,Merchant.class);

        initViews();
        initEvents();
    }

    private void initViews() {
        if (merchant!=null){
            mMerchantName.setText(merchant.getMerchantName());
            //在这里用反射来实现
            merchantInfo= (MerchantInfo) MyUtils.formatObjectforTextView(MerchantInfo.class, merchant.getMerchantInfo());
            if (merchantInfo!=null){
                mAddress.setText(merchantInfo.getAddress());
                mPhone.setText(merchantInfo.getPhone());
                mOnTime.setText(merchantInfo.getOnTime());
                mSort.setText(merchantInfo.getSort());
            }
        }
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
                showChangeInfo(mAddress.getText().toString(),2);
                break;
            case R.id.rl_info_3:
                showChangeInfo(mPhone.getText().toString(),3);
                break;
            case R.id.rl_info_4:
                showChangeInfo(mOnTime.getText().toString(),4);
                break;
            case R.id.rl_info_5:
                showChangeInfo(mSort.getText().toString(),5);
                break;
            default:
                break;
        }
    }

    private void showChangeInfo(final String from, final int index){
        View  view = LayoutInflater.from(this).inflate(R.layout.change_info_dialog,null);
        final Dialog dialog = new Dialog(this,R.style.normal_dialog_style);
        dialog.setContentView(view);
        TextView mFrom = (TextView) view.findViewById(R.id.tv_change_info);
        mFrom.setText(from);
        final EditText mTo = (EditText) view.findViewById(R.id.et_change_info);
        Button cancel = (Button) view.findViewById(R.id.change_info_cancel);
        Button ok = (Button) view.findViewById(R.id.change_info_ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTo.getText().toString()!=null){
                    switch (index){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            merchantInfo.setAddress(mTo.getText().toString());
                            break;
                        case 3:
                            merchantInfo.setPhone(mTo.getText().toString());
                            break;
                        case 4:
                            merchantInfo.setOnTime(mTo.getText().toString());
                            break;
                        case 5:
                            merchantInfo.setSort(mTo.getText().toString());
                            break;
                        default:
                            break;
                    }
                    merchant.setMerchantInfo(merchantInfo);
                    merchant.update(MerchantInfoPage.this,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MerchantInfoPage.this,"更新信息成功",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            switch (index){
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    mAddress.setText(mTo.getText());
                                    break;
                                case 3:
                                    mPhone.setText(mTo.getText());
                                    break;
                                case 4:
                                    mOnTime.setText(mTo.getText());
                                    break;
                                case 5:
                                    mSort.setText(mTo.getText());
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(MerchantInfoPage.this,"更新信息失败",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(MerchantInfoPage.this,"您还没有填入更改信息",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
