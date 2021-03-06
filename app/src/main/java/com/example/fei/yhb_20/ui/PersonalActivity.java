package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.adapter.ItemAdapter;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.OtherInfo;
import com.example.fei.yhb_20.bean.Post;
import com.marshalchen.common.uimodule.huitanScrollView.PullScrollView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class PersonalActivity extends ActionBarActivity implements View.OnClickListener, PullScrollView.OnTurnListener {

    private static final String TAG = "PersonalActivity";
    @InjectView(R.id.user_name)
    TextView name;
    @InjectView(R.id.user_follow)
    TextView followerNumber;  //粉丝
    @InjectView(R.id.user_following)
    TextView followingNumber;  //关注
    @InjectView(R.id.user_avatar)
    ImageView avatar;
    @InjectView(R.id.user_des)
    TextView des;
    @InjectView(R.id.attention_user)
    TextView follow;
    @InjectView(R.id.user_message)
    TextView message;
    @InjectView(R.id.background_img)
    ImageView background_img;
    @InjectView(R.id.scroll_view)
    PullScrollView mScrollView;
    @InjectView(R.id.rv_collections)
    RecyclerView recyclerView;

    private BaseUser user;

    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.inject(this);

        layoutManager = new LinearLayoutManager(this);

        initViews();
        initEvent();
    }

    private void initViews() {
        Intent intent = getIntent();
        user = (BaseUser) intent.getSerializableExtra("user");
        final Post post = (Post) intent.getSerializableExtra("post");
        if (user != null) {
            name.setText(user.getUsername());

            if (user.getMotto() != null) {
                des.setText(user.getMotto());
            }
            refreshAvatar();

            BmobQuery<OtherInfo> queryOtherInfo = new BmobQuery<>();
            String[] followerIds = {user.getObjectId()};
            queryOtherInfo.addWhereContainedIn("userId", Arrays.asList(followerIds));
            queryOtherInfo.findObjects(this, new FindListener<OtherInfo>() {
                @Override
                public void onSuccess(List<OtherInfo> otherInfos) {
                    Toast.makeText(PersonalActivity.this, "查询粉丝成功", Toast.LENGTH_LONG).show();
                    if (otherInfos != null) {
                        if (otherInfos.get(0).getFollowerIds() != null) {
                            followerNumber.setText("粉丝:" + otherInfos.get(0).getFollowerIds().size());
                        }
                        if (otherInfos.get(0).getFollowingIds() != null) {
                            followingNumber.setText("关注:" + otherInfos.get(0).getFollowingIds().size());
                        }
                    }
                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(PersonalActivity.this, "查询粉丝失败", Toast.LENGTH_LONG).show();
                }
            });

            /**
             * 这个是查询所发布的惠报的
             */
            //在这里查询所有的惠报
            BmobQuery<Post> queryPosts = new BmobQuery<>();
            queryPosts.include("user");
            queryPosts.addQueryKeys("content,updatedAt,paths,avatarPaht,username,user");
            queryPosts.addWhereRelatedTo("post", new BmobPointer(user));
            queryPosts.findObjects(this, new FindListener<Post>() {
                @Override
                public void onSuccess(List<Post> posts) {
                    Log.i(TAG, posts.get(0).getUser().getUsername());
                    recyclerView.setAdapter(new ItemAdapter(posts, PersonalActivity.this));
                }

                @Override
                public void onError(int i, String s) {
                    Log.i(TAG, s + i);
                }
            });
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }


    }

    private void refreshAvatar() {
        BmobQuery<Merchant> query = new BmobQuery<Merchant>();
        query.getObject(this, user.getObjectId(), new GetListener<Merchant>() {
            @Override
            public void onSuccess(Merchant merchant) {
                if (merchant.getAvatarPaht() != null) {
                    Picasso.with(PersonalActivity.this).load(merchant.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(avatar);
                } else {
                }
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    private void initEvent() {
        avatar.setOnClickListener(this);
        follow.setOnClickListener(this);
        message.setOnClickListener(this);
        mScrollView.setHeader(background_img);
        mScrollView.setOnTurnListener(this);
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
        switch (v.getId()) {
            case R.id.user_avatar:
                //点击图像会放大
                break;
            case R.id.attention_user:
                BaseUser baseUser = BmobUser.getCurrentUser(PersonalActivity.this, BaseUser.class);

                /**
                 * 分别获得id
                 */
                final String currentObjectId = baseUser.getObjectId();
                final String otherId = user.getObjectId();

                if (otherId.equals(currentObjectId)) {
                    Toast.makeText(PersonalActivity.this, "您不能关注自己哦", Toast.LENGTH_LONG).show();
                } else {
                    /**
                     * 向对方的otherInfo中加入关注信息
                     */
                    BmobQuery<OtherInfo> query = new BmobQuery<>();
                    query.addWhereContainedIn("userId", Arrays.asList(otherId));
                    query.findObjects(PersonalActivity.this, new FindListener<OtherInfo>() {
                        @Override
                        public void onSuccess(List<OtherInfo> otherInfos) {
                            OtherInfo otherInfo = otherInfos.get(0);
                            ArrayList<String> followerIds;
                            if (otherInfo.getFollowerIds() != null) {
                                followerIds = otherInfo.getFollowerIds();
                            } else {
                                followerIds = new ArrayList<String>();
                            }
                            if (otherInfos.get(0).getFollowerIds().contains(currentObjectId)) {
                                Toast.makeText(PersonalActivity.this, "已关注", Toast.LENGTH_LONG).show();
                                return;
                            }
                            followerIds.add(currentObjectId);
                            final int currentFollower = followerIds.size();
                            otherInfo.setFollowerIds(followerIds);
                            otherInfo.update(PersonalActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(PersonalActivity.this, "关注成功", Toast.LENGTH_LONG).show();
                                    followerNumber.setText("粉丝:" + currentFollower);
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(PersonalActivity.this, "关注成功失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "粉丝显示失败");
                        }
                    });

                    /**
                     * 向我的otherInfo中加入我关注的人
                     */
                    BmobQuery<OtherInfo> queryMyInfo = new BmobQuery<>();
                    queryMyInfo.addWhereContainedIn("userId", Arrays.asList(currentObjectId));
                    queryMyInfo.findObjects(PersonalActivity.this, new FindListener<OtherInfo>() {
                        @Override
                        public void onSuccess(List<OtherInfo> otherInfos) {
                            //这个otherInfo是当期用户的
                            OtherInfo otherInfo = otherInfos.get(0);
                            ArrayList<String> followingIds;
                            if (otherInfo.getFollowingIds() != null) {
                                followingIds = otherInfo.getFollowingIds();
                            } else {
                                followingIds = new ArrayList<String>();
                            }
                            if (otherInfos.get(0).getFollowingIds().contains(otherId)) {
                                Toast.makeText(PersonalActivity.this, "已关注此人", Toast.LENGTH_LONG).show();
                                return;
                            }
                            followingIds.add(otherId);
                            final int currentFollowing = followingIds.size();
                            otherInfo.setFollowingIds(followingIds);
                            otherInfo.update(PersonalActivity.this, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(PersonalActivity.this, "关注成功", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(PersonalActivity.this, "关注成功失败", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG, "粉丝显示失败");
                        }
                    });
                }
                break;
            case R.id.user_message:
                //进入私信界面
                break;
            default:
                break;
        }
    }

    @Override
    public void onTurn() {

    }
}
