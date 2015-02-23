package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.MD5;
import com.marshalchen.common.ui.NumberProgressBar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.LogRecord;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.listener.SaveListener;

public class MerchantRegist extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "MerchantRegist";
    private static final int REQUEST_IMAGE_CAPTURE = 2000;
    private static final int RESULT_LOAD_IMAGE = 0;
    private File imageFile;
    private String mCurrentPhotoPath;
    private Merchant merchant;
    private StringBuilder stringBuilder ;
    private ArrayList array;
    


    @InjectView(R.id.et_mr_name)EditText name;
    @InjectView(R.id.et_mr_merchantName)EditText merchantNmae;
    @InjectView(R.id.et_mr_license)EditText license;
    @InjectView(R.id.iv_mr_add)ImageView add;
    @InjectView(R.id.tv_mr_protocol)TextView protocol;
    @InjectView(R.id.bt_mr_regist)Button regist;
    @InjectView(R.id.ll_gallery)LinearLayout gallery;
    @InjectView(R.id.numberbar)NumberProgressBar progressBar;
    @InjectView(R.id.tv_notice)TextView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_regist);
        ButterKnife.inject(this);
        initEvents();
        stringBuilder = new StringBuilder("");
        merchant = new Merchant();
        array = new ArrayList();
    }

    private void initEvents() {
        protocol.setOnClickListener(this);
        add.setOnClickListener(this);
        regist.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merchant_regist, menu);
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
            case R.id.tv_mr_protocol:
                //go to the protocol view page
                break;
            case R.id.bt_mr_regist:
                regist.setText("上传中，请稍后。。。");
                setBean();
                updatePhotos();
                break;
            case R.id.iv_mr_add:
//                Photo photo = new Photo();
//                Bundle args = new Bundle();
//                args.putInt(SupportBlurDialogFragment.BUNDLE_KEY_BLUR_RADIUS, 0);
//                args.putFloat(SupportBlurDialogFragment.BUNDLE_KEY_DOWN_SCALE_FACTOR, 0);
//                photo.setArguments(args);
//                photo.show(getFragmentManager(),"add_photo");
                initPopup();
                break;
            default:
                break;
        }
    }

    private void updatePhotos() {
        progressBar.setVisibility(View.VISIBLE);
        notice.setVisibility(View.VISIBLE);
//        String [] files = stringBuilder.toString().split("|");
        String [] files = (String[]) array.toArray(new String[array.size()]);
        Log.d(TAG, String.valueOf(files.length));
        BmobProFile.getInstance(MerchantRegist.this).uploadBatch(files, new UploadBatchListener() {
            @Override
            public void onSuccess(boolean isfinished, String[] strings, String[] strings2) {
                if (isfinished) {
                    progressBar.setVisibility(View.INVISIBLE);
                    notice.setVisibility(View.INVISIBLE);

                    Toast.makeText(MerchantRegist.this, "成功上传", Toast.LENGTH_LONG).show();
                    StringBuilder stringBuilder = new StringBuilder("");
                    for (int i = 0 ;i<strings.length;i++){
                        stringBuilder.append(BmobProFile.getInstance(MerchantRegist.this).signURL(strings[i], strings2[i], "54f197dc6dce11fc7c078c07420a080e", 0, null));
                        stringBuilder.append("|");
                    }
                    merchant.setPhotoPath(stringBuilder.toString());
                    merchant.signUp(MerchantRegist.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(MerchantRegist.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(MerchantRegist.this, s, Toast.LENGTH_LONG).show();
                        }
                    });
                    
                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                String sMsg = "curIndex:"+curIndex+"curPercent:"+curPercent+"total:"+total+"totalParcent:"+totalPercent;
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("curIndex",curIndex);
                bundle.putInt("curPercent",curPercent);
                bundle.putInt("total",total);
                bundle.putInt("totalPercent",totalPercent);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                Log.d(TAG, sMsg);
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG,s);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            progressBar.setProgress(bundle.getInt("curPercent"));
        }
    };

    private void setBean() {
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String email = intent.getStringExtra("email");

        String sName = name.getText().toString();
        String sMerchantName = merchantNmae.getText().toString();
        String sLicense = license.getText().toString();

        merchant.setUsername(username);
        merchant.setEmail(email);
        MD5 md5 = new MD5();
        merchant.setPassword(md5.getMD5ofStr(password));
        merchant.setMerchantName(sMerchantName);
        merchant.setLicense(sLicense);
        merchant.setName(sName);
        merchant.setAttribute(GV.MERCHANT);
    }


    private void initPopup(){
        PopupMenu popupMenu = new PopupMenu(this,add);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_cancel:
                        break;
                    case R.id.menu_takephoto:
                        dispatchTakePictureIntent();
                        break;
                    case R.id.menu_gallery:
                        dispatchGalleryPictureIntent();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void dispatchGalleryPictureIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(this.getPackageManager()) != null){
            startActivityForResult(intent,RESULT_LOAD_IMAGE);

        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));

            Picasso.with(this).load(new File(mCurrentPhotoPath)).resize(200, 200).into(imageView);

            imageView.setPadding(2,2,2,2);
            imageView.setMaxHeight(200);
            imageView.setMaxWidth(200);
            gallery.addView(imageView);
        }else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            String [] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            
            stringBuilder.append(picturePath);
            stringBuilder.append("|");
            array.add(picturePath);
            
            Picasso.with(this).load(new File(picturePath)).resize(200, 200).into(imageView);
            Log.d(TAG,picturePath);
            imageView.setPadding(2,2,2,2);
            imageView.setMaxHeight(200);
            imageView.setMaxWidth(200);
            gallery.addView(imageView);
        }
    }


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



}
