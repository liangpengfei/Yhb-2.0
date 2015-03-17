package com.example.fei.yhb_20.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.ui.fragment.PersonalLikeFragment;
import com.example.fei.yhb_20.ui.fragment.PersonalShareFragment;
import com.marshalchen.common.uimodule.huitanScrollView.PullScrollView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

public class PersonalActivity extends ActionBarActivity implements View.OnClickListener,PullScrollView.OnTurnListener{

    private static final String TAG = "PersonalActivity";
    @InjectView(R.id.user_name)TextView name;
    @InjectView(R.id.user_follow)TextView followerNumber;  //粉丝
    @InjectView(R.id.user_following)TextView followingNumber;  //关注
    @InjectView(R.id.user_avatar)ImageView avater;
    @InjectView(R.id.user_des)TextView des;
    @InjectView(R.id.attention_user)TextView follow;
    @InjectView(R.id.user_message)TextView message;
    @InjectView(R.id.background_img)ImageView background_img;
    @InjectView(R.id.scroll_view)PullScrollView mScrollView;
    @InjectView(R.id.tv_personal_share)TextView tvShare;
    @InjectView(R.id.tv_personal_like)TextView tvLike;
    private  BaseUser user;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.inject(this);

        initViews();
        initEvent();
    }

    private void initViews() {
        Intent intent = getIntent();
        user= (BaseUser) intent.getSerializableExtra("user");
        if (user!=null){
            name.setText(user.getUsername());
            if (user.getFollowerId()!=null){
                followerNumber.setText("粉丝 "+user.getFollowerId().size());
            }else{
                followerNumber.setText("粉丝 "+"0");
            }
            if (user.getFolloingId()!=null){
                followingNumber.setText("关注 "+user.getFolloingId().size());
            }else{
                followingNumber.setText("关注 "+"0");
            }
            if (user.getMotto()!=null){
                des.setText(user.getMotto());
            }
        }

        //在这里设置照片,这里是所有的照片
//        String photoPath = user.get


        /**
         * 首先进行了初始化的操作
         */
        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        PersonalLikeFragment personalLikeFragment = new PersonalLikeFragment();
        transaction.add(R.id.container,personalLikeFragment,"LikeFragment");
        transaction.commit();

    }

    private void initEvent() {
        avater.setOnClickListener(this);
        follow.setOnClickListener(this);
        message.setOnClickListener(this);
        mScrollView.setHeader(background_img);
        mScrollView.setOnTurnListener(this);
        tvLike.setOnClickListener(this);
        tvShare.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal, menu);
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
            case R.id.user_avatar:
                //点击图像会放大
                break;
            case R.id.attention_user:
                final ArrayList<String> followerIds = new ArrayList<>();
                followerIds.add(user.getObjectId());
                user.setFollowerId(followerIds);
                //改变当前用户的followerIds，应该加一
                user.update(this,new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        BaseUser currentUser = BmobUser.getCurrentUser(PersonalActivity.this,BaseUser.class);
//                        currentUser.set
                        if (user.getFollowerId()!=null){
                            followerNumber.setText("粉丝 "+user.getFollowerId().size()+1);
                        }else{
                            followerNumber.setText("粉丝 "+1);
                        }
                        Toast.makeText(PersonalActivity.this,"关注成功",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e(TAG, "关注失败" + s + i);
                    }
                });

                break;
            case R.id.user_message:
                //进入私信界面
                break;
            case R.id.tv_personal_like:
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                PersonalLikeFragment likeFragment = new PersonalLikeFragment();
                transaction.remove(fragmentManager.findFragmentByTag("PersonalShareFragment"));
                transaction.replace(R.id.container,likeFragment,"LikeFragment");
                transaction.commit();
                break;
            case R.id.tv_personal_share:
                //应该判断fragment是否已经存在了，如果已经存在了，就不用创建
                FragmentTransaction transaction2 = fragmentManager.beginTransaction();
                PersonalShareFragment personalShareFragment = new PersonalShareFragment();
                transaction2.remove(fragmentManager.findFragmentByTag("LikeFragment"));
                transaction2.replace(R.id.container,personalShareFragment,"PersonalShareFragment");
                transaction2.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTurn() {

    }
}