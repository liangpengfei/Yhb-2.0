package com.example.fei.yhb_20.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.MyInfo;
import com.example.fei.yhb_20.utils.MyUtils;
import com.marshalchen.common.uimodule.huitanScrollView.PullScrollView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingMerchantActivity extends FragmentActivity implements View.OnClickListener ,PullScrollView.OnTurnListener{

    private static final String TAG = "SettingMerchantActivity";
    @InjectView(R.id.rl_setting_gender)RelativeLayout rlGender;
    @InjectView(R.id.rl_setting_hometown)RelativeLayout rlHometown;
    @InjectView(R.id.rl_setting_certification)RelativeLayout rlCertification;

    @InjectView(R.id.tv_setting_gender)TextView mGender;
    @InjectView(R.id.tv_setting_hometown)TextView mHometown;

    @InjectView(R.id.scroll_view)PullScrollView pullScrollView;
    @InjectView(R.id.background_img)ImageView background_img;
    @InjectView(R.id.user_avatar)ImageView avatar;

    @InjectView(R.id.user_name)TextView mUsername;
    @InjectView(R.id.user_follow)TextView mFollower;
    @InjectView(R.id.user_following)TextView mFollowing;
    @InjectView(R.id.user_des)TextView mMotto;
    @InjectView(R.id.tv_setting_certification)TextView mCertification;

    private Merchant merchant;
    private PopupWindow pop = null;
    private View parentView ;
    private LinearLayout ll_popup;

    private byte[] mContent;
    Bitmap myBitmap;
    private Picasso picasso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentView = getLayoutInflater().inflate(R.layout.activity_setting,null);
        setContentView(parentView);
        ButterKnife.inject(this);
        merchant = BmobUser.getCurrentUser(this,Merchant.class);
        picasso = Picasso.with(this);
        initViews();
        initEvents();
    }

    private void initViews() {
        if (merchant!=null){
            mUsername.setText(merchant.getUsername());
            if (merchant.getMyInfo().getGender()!=null){
                mGender.setText(merchant.getMyInfo().getGender());
            }else{
                mGender.setText("未填写");
            }
            if (merchant.getFollowerId()!=null){
                mFollower.setText("粉丝 "+merchant.getFollowerId().size());
            }else{
                mFollower.setText("粉丝 "+"0");
            }
            if (merchant.getFolloingId()!=null){
                mFollowing.setText("关注 "+merchant.getFolloingId().size());
            }else{
                mFollowing.setText("关注 "+"0");
            }
            if (merchant.getMotto()!=null){
                mMotto.setText(merchant.getMotto());
            }
            if (merchant.getMerchantInfo().isAuthenticated()){
                mCertification.setText("已认证");
            }else{
                mCertification.setText("未认证");
            }
            if (merchant.getMyInfo().getHometown()!=null){
                mHometown.setText(merchant.getMyInfo().getHometown());
            }else{
                mHometown.setText("未填写");
            }
            refreshAvatar();
        }
    }

    /**
     * 头像更改后刷新
     */
    private void refreshAvatar(){
        BmobQuery<Merchant> query = new BmobQuery<Merchant>();
        query.getObject(this,merchant.getObjectId(),new GetListener<Merchant>() {
            @Override
            public void onSuccess(Merchant merchant) {
                if (merchant.getAvatarPaht()!=null){
                    Picasso.with(SettingMerchantActivity.this).load(merchant.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(avatar);
                }else{
                    Toast.makeText(SettingMerchantActivity.this,"test",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    private void initEvents() {
        avatar.setOnClickListener(this);
        rlGender.setOnClickListener(this);
        rlCertification.setOnClickListener(this);
        rlHometown.setOnClickListener(this);
        pullScrollView.setHeader(background_img);
        pullScrollView.setOnTurnListener(this);
        mMotto.setOnClickListener(this);
        background_img.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
    protected void onRestart() {
        super.onRestart();
        refreshAvatar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_setting_gender:
                new AlertDialog.Builder(this)
                        .setTitle("性别")
                        .setSingleChoiceItems(new String[]{"男士", "女士"}, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    mGender.setText("男士");
                                    setGenderInCloud("男士");
                                } else {
                                    mGender.setText("女士");
                                    setGenderInCloud("女士");
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.rl_setting_hometown:
                showChangeHometown(mHometown.getText().toString());
                break;
            case R.id.rl_setting_certification:
                //暂不实现
                break;
            case R.id.user_des:
                showChangeMotto();
                break;
            case R.id.background_img:
                //TODO 点击可以更改背景，未实现
                break;
            case R.id.user_avatar:
                //TODO 需要对照片进行裁剪，这个学要另一个类来实现，裁剪之后上传服务器
                //要先联网来获取图片，然后才传送

                pop = new PopupWindow(SettingMerchantActivity.this);
                View view = LayoutInflater.from(this).inflate(R.layout.change_avatar_pop,null);
                Button change = (Button) view.findViewById(R.id.change_avatar_change);
                Button show = (Button) view.findViewById(R.id.change_avatar_show);
                Button cancel = (Button) view.findViewById(R.id.change_avatar_cancel);
                RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
                ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

                change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 先要改变，有图像,一个大致的计划是：研究怎么从相册选择照片；返回照片之后得到这个照片并掉转到另一个activity中进行图像的修剪
                        pop.dismiss();
                        final PopupWindow subPop = new PopupWindow(SettingMerchantActivity.this);
                        View subView = LayoutInflater.from(SettingMerchantActivity.this).inflate(R.layout.choose_way_to_get_picture,null);
                        Button take = (Button) subView.findViewById(R.id.choose_picture_take);
                        Button gallery = (Button) subView.findViewById(R.id.choose_picture_gallery);
                        Button cancel = (Button) subView.findViewById(R.id.choose_picture_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pop.dismiss();
                                ll_popup.clearAnimation();
                            }
                        });
                        gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                getImage.setType("image/jpeg");
                                startActivityForResult(getImage, 0);
                                subPop.dismiss();
                            }
                        });
                        take.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent getImageByCamera= new Intent("android.media.action.IMAGE_CAPTURE");
                                startActivityForResult(getImageByCamera, 1);
                                subPop.dismiss();
                            }
                        });

                        subPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                        subPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        subPop.setBackgroundDrawable(new BitmapDrawable());
                        subPop.setFocusable(true);
                        subPop.setOutsideTouchable(true);
                        subPop.setContentView(subView);
                        subPop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);



                    }
                });
                show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击后放大
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        ll_popup.clearAnimation();
                    }
                });

                /**
                 * 设置点击空白部分后消失
                 */
                parent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        ll_popup.clearAnimation();
                    }
                });


                pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                pop.setBackgroundDrawable(new BitmapDrawable());
                pop.setFocusable(true);
                pop.setOutsideTouchable(true);
                pop.setContentView(view);
                pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

                break;
            default:
                break;


        }
    }

    /**
     * 得到相应的信息后跳转过去
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        if (requestCode==0){
            try {
                //通过相册
                //获得图片的uri
                Uri originalUri = data.getData();
                //将图片内容解析成字节数组
                mContent= MyUtils.readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
                Intent intent = new Intent(SettingMerchantActivity.this,CropperImageActivity.class);
                intent.putExtra("bitmapUri",originalUri);
                intent.putExtra("flag",0);
                intent.putExtra("merchant",merchant);
                startActivity(intent);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }else if (requestCode==1){
            //通过照相
            try {
                Bundle extras = data.getExtras();
                myBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mContent=baos.toByteArray();
                Intent intent = new Intent(SettingMerchantActivity.this,CropperImageActivity.class);
                intent.putExtra("bitmap",myBitmap);
                intent.putExtra("flag",1);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更改云端性别属性
     * @param str
     */
    private void setGenderInCloud(String str){
        MyInfo myInfo = new MyInfo();
        myInfo.setGender(str);
        merchant.setMyInfo(myInfo);
        merchant.update(SettingMerchantActivity.this,new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(SettingMerchantActivity.this,"更改成功",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(SettingMerchantActivity.this,"更改失败",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onTurn() {

    }

    /**
     * 更改常住地
     * @param from
     */
    private void showChangeHometown(final String from){
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
                    mHometown.setText(mTo.getText());
                    //
                    MyInfo myInfo;
                    if (merchant.getMyInfo()!=null){
                        myInfo= merchant.getMyInfo();
                    }else{
                        myInfo = new MyInfo();
                    }
                    myInfo.setHometown(mTo.getText().toString());
                    merchant.setMyInfo(myInfo);
                    merchant.update(SettingMerchantActivity.this,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(SettingMerchantActivity.this,"更改成功",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(SettingMerchantActivity.this,"更改失败",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(SettingMerchantActivity.this,"您还没有填入更改信息",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     *更改签名
     */
    private void showChangeMotto(){
        View  view = LayoutInflater.from(this).inflate(R.layout.change_motto,null);
        final Dialog dialog = new Dialog(this,R.style.normal_dialog_style);
        dialog.setContentView(view);
        final EditText etMotto = (EditText) view.findViewById(R.id.et_change_info);
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
                if (etMotto.getText().toString()!=null){
                    merchant.setMotto(etMotto.getText().toString());
                    merchant.update(SettingMerchantActivity.this,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            mMotto.setText(etMotto.getText().toString());
                            Toast.makeText(SettingMerchantActivity.this,"签名发布成功",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(SettingMerchantActivity.this,"签名未发布",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toast.makeText(SettingMerchantActivity.this,"您还没有填入签名",Toast.LENGTH_LONG).show();
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
