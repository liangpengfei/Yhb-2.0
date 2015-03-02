package com.example.fei.yhb_20.ui;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.fei.yhb_20.LocationApplication;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.adapter.MyAdapter;
import com.example.fei.yhb_20.bean.MyListItem;
import com.example.fei.yhb_20.utils.DBManager;
import com.example.fei.yhb_20.utils.ImageTools;
import com.example.fei.yhb_20.utils.NetUtil;
import com.marshalchen.common.uimodule.cropimage.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostActivity extends ActionBarActivity implements View.OnClickListener ,AdapterView.OnItemSelectedListener {

    private LocationClient mLocationClient;
    private static final int SCALE = 5;
    private static final java.lang.String TAG = "PostActivity";
    @InjectView(R.id.iv_post_back)ImageView back;
    @InjectView(R.id.iv_post_cancel)ImageView cancel;
    @InjectView(R.id.iv_post_ok)ImageView ok;
    @InjectView(R.id.iv_post_add)ImageView add;
    @InjectView(R.id.ratingbar)RatingBar ratingBar;
    @InjectView(R.id.et_post_merchantName)EditText merchantName;
    @InjectView(R.id.time)Spinner time;
    @InjectView(R.id.iv_post_dingwei)ImageView dingwei;
    @InjectView(R.id.position1)Spinner position1;
    @InjectView(R.id.position2)Spinner position2;
    @InjectView(R.id.position3)Spinner position3;
    @InjectView(R.id.gallery_1)LinearLayout gallery1;
    @InjectView(R.id.gallery_2)LinearLayout gallery2;
    @InjectView(R.id.gallery_3)LinearLayout gallery3;



    private DBManager dbm;
    private SQLiteDatabase db;
    private String province=null;
    private String city=null;
    private String district=null;
    private String sTime=null;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.inject(this);
        position1.setPrompt("省");
        position2.setPrompt("市");
        position3.setPrompt("地区");
        time.setPrompt("选择时间");
        mLocationClient = ((LocationApplication)getApplication()).mLocationClient;

        ((LocationApplication)getApplication()).position1 = position1;
        ((LocationApplication)getApplication()).position2 = position2;
        ((LocationApplication)getApplication()).position3 = position3;

        initEvents();
        if (NetUtil.isNetConnected(this)){
            position1.setOnItemSelectedListener(this);
            position2.setOnItemSelectedListener(this);
            position3.setOnItemSelectedListener(this);
            Log.e(TAG,"1");
            InitLocation();
            mLocationClient.start();
        }else{
            initSpinner1();
        }

        initTimeSpinner();
    }

    private void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);//ÉèÖÃ¶šÎ»Ä£Êœ
        option.setCoorType("gcj02");//·µ»ØµÄ¶šÎ»œá¹ûÊÇ°Ù¶ÈŸ­Î³¶È£¬Ä¬ÈÏÖµgcj02
        int span=1000;
        option.setScanSpan(span);//ÉèÖÃ·¢Æð¶šÎ»ÇëÇóµÄŒäžôÊ±ŒäÎª5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void initTimeSpinner() {
        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PostActivity.this,parent.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initEvents() {
        back.setOnClickListener(this);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        add.setOnClickListener(this);
        dingwei.setOnClickListener(this);
    }

    public void initSpinner1(){
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from province";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            while (!cursor.isLast()){
                String code=cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[]=cursor.getBlob(2);
                String name=new String(bytes,"gbk");
                MyListItem myListItem=new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code=cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[]=cursor.getBlob(2);
            String name=new String(bytes,"gbk");
            MyListItem myListItem=new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this,list);
        position1.setAdapter(myAdapter);
        position1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
    }
    public void initSpinner2(String pcode){
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from city where pcode='"+pcode+"'";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            while (!cursor.isLast()){
                String code=cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[]=cursor.getBlob(2);
                String name=new String(bytes,"gbk");
                MyListItem myListItem=new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code=cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[]=cursor.getBlob(2);
            String name=new String(bytes,"gbk");
            MyListItem myListItem=new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this,list);
        position2.setAdapter(myAdapter);
        position2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
    }
    public void initSpinner3(String pcode){
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from district where pcode='"+pcode+"'";
            Cursor cursor = db.rawQuery(sql,null);
            cursor.moveToFirst();
            while (!cursor.isLast()){
                String code=cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[]=cursor.getBlob(2);
                String name=new String(bytes,"gbk");
                MyListItem myListItem=new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code=cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[]=cursor.getBlob(2);
            String name=new String(bytes,"gbk");
            MyListItem myListItem=new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this,list);
        position3.setAdapter(myAdapter);
        position3.setOnItemSelectedListener(new SpinnerOnSelectedListener3());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView view1 = (TextView) view;
        view1.setTextColor(Color.BLACK);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class SpinnerOnSelectedListener1 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                   long id) {
            province=((MyListItem) adapterView.getItemAtPosition(position)).getName();
            String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

            initSpinner2(pcode);
            initSpinner3(pcode);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    class SpinnerOnSelectedListener2 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                   long id) {
            city=((MyListItem) adapterView.getItemAtPosition(position)).getName();
            String pcode =((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

            initSpinner3(pcode);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    class SpinnerOnSelectedListener3 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                   long id) {
            district=((MyListItem) adapterView.getItemAtPosition(position)).getName();
            Toast.makeText(PostActivity.this, province + " " + city + " " + district, Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
            case R.id.iv_post_back:
                finish();
                break;
            case R.id.iv_post_cancel:
                finish();
                break;
            case R.id.iv_post_ok:

                break;
            case R.id.iv_post_add:
                showPicturePicker(PostActivity.this);
                break;
            case R.id.iv_post_dingwei:
                //MapUtil.getLocation(this,c);
                break;
            default:
                break;

        }
    }

    private void showPicturePicker(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        setFilename(imageFileName);
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),getFilename()));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    //TODO 修复照片的问题，让文节想定位的实现

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Log.e(TAG,getFilename());
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +getFilename());
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(newBitmap);
                    gallery1.addView(imageView);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            ImageView imageView2 = new ImageView(this);
                            imageView2.setImageBitmap(smallBitmap);
                            gallery1.addView(imageView2);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
