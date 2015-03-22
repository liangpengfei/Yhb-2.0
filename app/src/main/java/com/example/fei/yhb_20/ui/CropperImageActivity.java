package com.example.fei.yhb_20.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bmob.BmobPro;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.utils.MyUtils;
import com.marshalchen.common.uimodule.cropperimage.CropImageView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class CropperImageActivity extends ActionBarActivity {

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final String TAG = "CropperImageActivity";

    @InjectView(R.id.CropImageView)CropImageView cropImageView;
    @InjectView(R.id.crop)Button crop;

    private Merchant merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper_image);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        merchant = (Merchant) intent.getSerializableExtra("merchant");
        int flag = intent.getIntExtra("flag",-1);
        if (flag==0){
            Uri bitmapUri = (Uri)intent.getParcelableExtra("bitmapUri");
            Log.e(TAG, bitmapUri.toString());
            if (bitmapUri!=null){
                ContentResolver resolver = getContentResolver();
                try {
                    Bitmap myBitmap = MyUtils.getPicFromBytes(MyUtils.readStream(resolver.openInputStream(Uri.parse(bitmapUri.toString()))), null);
                    cropImageView.setImageBitmap(myBitmap);
                    cropImageView.setFixedAspectRatio(true);
                    cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
                    cropImageView.setGuidelines(0);
                    crop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bitmap bitmap = cropImageView.getCroppedImage();
                            MyUtils.showProgressDialog(CropperImageActivity.this,"正在设置头像");
                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                            Log.e(TAG,getAbsoluteImagePath(uri));
                            BmobProFile.getInstance(CropperImageActivity.this).upload(getAbsoluteImagePath(uri),new UploadListener() {
                                @Override
                                public void onSuccess(String filename, String url) {
//                                    Merchant currentUser =BmobUser.getCurrentUser(CropperImageActivity.this,Merchant.class);
                                    merchant.setAvatarPaht(BmobProFile.getInstance(CropperImageActivity.this).signURL(filename, url, "54f197dc6dce11fc7c078c07420a080e", 0, null));
                                    merchant.update(CropperImageActivity.this,new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(CropperImageActivity.this,"成功设置头像",Toast.LENGTH_LONG).show();

                                            finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Log.e(TAG,s+i);
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int i) {
                                    Log.e(TAG, String.valueOf(i));
                                }

                                @Override
                                public void onError(int i, String s) {
                                    Log.e(TAG,s+i);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (flag==1){
            Bitmap bitmap = intent.getParcelableExtra("bitmap");
            if (bitmap!=null){
                cropImageView.setImageBitmap(bitmap);
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
                cropImageView.setGuidelines(0);
                crop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = cropImageView.getCroppedImage();
                        MyUtils.showProgressDialog(CropperImageActivity.this,"正在设置头像");
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                        Log.e(TAG,getAbsoluteImagePath(uri));
                    }
                });
            }
        }else{
            Toast.makeText(this,"未知错误",Toast.LENGTH_LONG).show();
        }

    }

    private String getAbsoluteImagePath(Uri uri) {
// can post image
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();


            return cursor.getString(column_index);
        } else {

//如果游标为空说明获取的已经是绝对路径了
            return uri.getPath();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cropper_image, menu);
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
