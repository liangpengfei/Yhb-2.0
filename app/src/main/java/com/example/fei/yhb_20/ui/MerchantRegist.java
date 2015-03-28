


package com.example.fei.yhb_20.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.fei.yhb_20.LocationApplication;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.MerchantInfo;
import com.example.fei.yhb_20.bean.MyInfo;
import com.example.fei.yhb_20.bean.OtherInfo;
import com.example.fei.yhb_20.utils.Bimp;
import com.example.fei.yhb_20.utils.FileUtils;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.ImageItem;
import com.example.fei.yhb_20.utils.MD5;
import com.example.fei.yhb_20.utils.MyUtils;
import com.example.fei.yhb_20.utils.PublicWay;
import com.example.fei.yhb_20.utils.Res;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.listener.SaveListener;

/**
 * 商户注册界面，这个是单独为商户抽象出来的，有一些独特的地方
 */
public class MerchantRegist extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "MerchantRegist";
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int TAKE_PICTURE = 0x000001;
    private String mCurrentPhotoPath;
    private Merchant merchant;
    private StringBuilder stringBuilder ;
    private ArrayList array;

    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private GridAdapter adapter;
    private View parentView;
    public static Bitmap bimap ;
    private LocationApplication dataApp;

    @InjectView(R.id.et_mr_name)EditText name;
    @InjectView(R.id.et_mr_merchantName)EditText merchantNmae;
    @InjectView(R.id.et_mr_license)EditText license;
    @InjectView(R.id.tv_mr_protocol)TextView protocol;
    @InjectView(R.id.bt_mr_regist)Button regist;
    @InjectView(R.id.noScrollgridview)GridView noScrollgridview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_merchant_regist, null);
        setContentView(parentView);
        ButterKnife.inject(this);
        initEvents();
        stringBuilder = new StringBuilder("");
        merchant = new Merchant();
        array = new ArrayList();
        Init();
        dataApp = (LocationApplication) getApplication();

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String email = intent.getStringExtra("email");


        if (password!=null){
            dataApp.setEmail(email);
            dataApp.setUsername(username);
            dataApp.setPassword(password);
        }

    }

    private void initEvents() {
        protocol.setOnClickListener(this);
        regist.setOnClickListener(this);
    }

    public void Init() {

        GV.setMyClass(MerchantRegist.class);
        pop = new PopupWindow(MerchantRegist.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        /**
         * 设置点击空白部分后消失
         */
        parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 点击拍照按钮
         */
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 点击图库
         */
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MerchantRegist.this,
                        AlbumActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 点击取消
         */
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        /**
         *  得到显示图片的网格布局
         */
        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                /**
                 * 如果是没有满到9张，就是添加图标
                 */
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MerchantRegist.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(MerchantRegist.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(MerchantRegist.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    public String getPhotoPath(){
        StringBuilder builder = new StringBuilder("");
        for (int i =0 ;i<Bimp.tempSelectBitmap.size();i++){
            builder.append(Bimp.tempSelectBitmap.get(i).getImagePath());
            builder.append("|");

        }
        return builder.toString().trim();
    }

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merchant_regist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_mr_protocol:
                //go to the protocol view page
                break;
            case R.id.bt_mr_regist:
                Log.e(TAG,"registtest");
                if (getPhotoPath()!=null){
                    MyUtils.showProgressDialog(this, "注册中，请稍后。。。");
                    regist.setEnabled(false);
                    setBean();
                    updatePhotos();
                }else {
                    Toast.makeText(this,"请添加执照照片",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 首先上传相应的图片，上传图片成功之后再进行用户信息的注册
     */
    private void updatePhotos() {
        String [] files = getPhotoPath().split("\\|");
        Log.d(TAG, getPhotoPath()+"liangtest");
        BmobProFile.getInstance(MerchantRegist.this).uploadBatch(files, new UploadBatchListener() {
            @Override
            public void onSuccess(boolean isFinished, String[] fileNames, String[] urls) {
                if (isFinished) {
                    Log.e(TAG,"88");
                    Toast.makeText(MerchantRegist.this, "成功上传", Toast.LENGTH_LONG).show();
                    ArrayList<String> photoPaths = new ArrayList<String>();
                    for (int i = 0 ;i < fileNames.length;i++){
                        photoPaths.add(BmobProFile.getInstance(MerchantRegist.this).signURL(fileNames[i], urls[i], "54f197dc6dce11fc7c078c07420a080e", 0, null));
                    }
                    merchant.setPhotoPaths(photoPaths);
                    merchant.signUp(MerchantRegist.this,new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG,"success really");
                            //注册成功，跳转到主页之前添加与OtherInfo的关联

                            final OtherInfo otherInfo = new OtherInfo();
                            otherInfo.setUserId(merchant.getObjectId());
                            ArrayList<String> followerIds = new ArrayList<String>();
                            otherInfo.setFollowerIds(followerIds);
                            otherInfo.save(MerchantRegist.this,new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(MerchantRegist.this,"关注成功",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(MerchantRegist.this,"关注失败",Toast.LENGTH_LONG).show();
                                }
                            });

//                            otherInfo.setUser(merchant);
//                            otherInfo.save(MerchantRegist.this,new SaveListener() {
//                                @Override
//                                public void onSuccess() {
//                                    BmobRelation otherInfos = new BmobRelation();
//                                    otherInfos.add(otherInfo);
//                                    merchant.setOtherInfo(otherInfos);
//                                    merchant.update(MerchantRegist.this,new UpdateListener() {
//                                        @Override
//                                        public void onSuccess() {
//                                            Log.e(TAG,"成功添加otherInfo和baseuser的关联");
//                                        }
//
//                                        @Override
//                                        public void onFailure(int i, String s) {
//                                            Log.e(TAG,s+i);
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onFailure(int i, String s) {
//                                    Log.e(TAG,s+i);
//                                }
//                            });


                            Intent intent = new Intent(MerchantRegist.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e(TAG,s+i);
                        }
                    });

                }
            }
            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                Log.e(TAG, String.valueOf(curPercent));
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG,"上传图片失败"+s+i);
                Toast.makeText(MerchantRegist.this,s,Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 设置当前获取到的用户输入的注册信息，并封装为一个bean，以供注册的时候使用
     */
    private void setBean() {

        //不能在这里进行，应为还没有注册成功，没有用户id


        String sName = name.getText().toString();
        String sMerchantName = merchantNmae.getText().toString();
        String sLicense = license.getText().toString();

        Log.e(TAG,dataApp.getPassword());
        merchant.setUsername(dataApp.getUsername());
        merchant.setEmail(dataApp.getEmail());
        MD5 md5 = new MD5();
        merchant.setPassword(md5.getMD5ofStr(dataApp.getPassword()));
        merchant.setMerchantName(sMerchantName);
        merchant.setLicense(sLicense);
        merchant.setName(sName);
        merchant.setAttribute(GV.MERCHANT);
        merchant.setMerchantInfo(new MerchantInfo());
        merchant.setMyInfo(new MyInfo());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name",name.getText().toString());
        outState.putString("merchantName",merchantNmae.getText().toString());
        outState.putString("license",license.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name.setText(savedInstanceState.getString("name"));
        merchantNmae.setText(savedInstanceState.getString("merchantName"));
        license.setText(savedInstanceState.getString("license"));
        Log.e(TAG,"3");
    }


    /**
     * 选取图片的两种方式得到的图片的显示
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);

                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }


    /**
     * 创建一个唯一的file对象
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        stringBuilder.append(mCurrentPhotoPath);
        stringBuilder.append("|");
        array.add(mCurrentPhotoPath);
        Log.d(TAG,mCurrentPhotoPath);
        return image;
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if(Bimp.tempSelectBitmap.size() == 9){
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position ==Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for(int i=0;i<PublicWay.activityList.size();i++){
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            System.exit(0);
        }
        return true;
    }

}
