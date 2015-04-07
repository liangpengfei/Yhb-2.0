package com.example.fei.yhb_20.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.MerchantInfo;
import com.example.fei.yhb_20.utils.MyUtils;
import com.fenjuly.combinationimageview.CombinationImageView;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class MerchantInfoPage extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "MerchantInfoPage";
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

    //同步与异步之间的关联有问题
//    @InjectView(R.id.combinationImage)CombinationImageView combinationImageView;
    private CombinationImageView combinationImageView;

    private Merchant merchant;
    private MerchantInfo merchantInfo;
    private View view;

    ArrayList<String> localpaths;
    ArrayList<String> photoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= LayoutInflater.from(this).inflate(R.layout.activity_merchant_info_page,null);
        setContentView(view);
        ButterKnife.inject(this);
        merchant = BmobUser.getCurrentUser(this,Merchant.class);
        BmobConfiguration config = new BmobConfiguration.Builder(this).customExternalCacheDir("目录名").build();
        BmobPro.getInstance(this).initConfig(config);
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
            if (merchant.getPhotoPaths()!=null){
                combinationImageView = (CombinationImageView) view.findViewById(R.id.combinationImage);
                combinationImageView.addImageView(R.drawable.example5);

                photoPaths = merchant.getPhotoPaths();
                localpaths = new ArrayList<>();

                for (int i = 0; i < photoPaths.size(); i++) {
                    BmobProFile.getInstance(MerchantInfoPage.this).download(photoPaths.get(i), new DownloadListener() {
                        @Override
                        public void onSuccess(String fullPath) {
                            Log.d(TAG, "下载成功：" + fullPath);
                            localpaths.add(fullPath);
                        }

                        @Override
                        public void onProgress(String localPath, int percent) {
                            Log.d(TAG, localPath + percent);
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            Log.d(TAG, "下载出错：" + statuscode + "--" + errormsg);
                            combinationImageView.addImageView(R.drawable.example5);
                        }
                    });
                }
                new TimerTask(1, combinationImageView);

            }else{
                Log.d(TAG, "还没有设置头像");
                combinationImageView = (CombinationImageView) findViewById(R.id.combinationImage);
                combinationImageView.addImageView(R.drawable.example5);
            }
        }
    }

    //TODO 请教
    public class TimerTask {
        int count = 0;
        Timer timer;

        public TimerTask(int seconds, final CombinationImageView combinationImageView) {
            timer = new Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, String.valueOf(count));
                    //do something
                    Log.d(TAG, localpaths.size() + ":" + photoPaths.size());
                    if (localpaths.size() == photoPaths.size()) {
                        combinationImageView.removeAllView();
                        String test = "/storage/emulated/0/Android/data/com.example.fei.yhb_20/cache/目录名/Download/98c4ae3b68cb434cb0405e54cc52aa98.png";
                        for (int i = 0; i < localpaths.size(); i++) {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(localpaths.get(i));
                            combinationImageView.addImageView(imageBitmap);
                            combinationImageView.refreshDrawableState();
                        }
                        if (combinationImageView.getNumbersOfView() > localpaths.size()) {
                            combinationImageView.removeView(0);
                        }
                    }
                }
            }, 1000, seconds * 1000);
        }

    }

    private void initEvents() {
        relativeLayout0.setOnClickListener(this);
        relativeLayout1.setOnClickListener(this);
        relativeLayout2.setOnClickListener(this);
        relativeLayout3.setOnClickListener(this);
        relativeLayout4.setOnClickListener(this);
        relativeLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                //查看所有的图片
                if (merchant.getPhotoPaths() != null) {
                    String[] paths = merchant.getPhotoPaths().toArray(new String[merchant.getPhotoPaths().size()]);
                    Intent intent = new Intent(this, GalleryUrlActivity.class);
                    intent.putExtra("photoUrls", paths);
                    startActivity(intent);
                }
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
