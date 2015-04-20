package com.example.fei.yhb_20.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.OtherInfo;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.ui.fragment.ClassFragment;
import com.example.fei.yhb_20.ui.fragment.MainFragment;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.MapUtil;
import com.example.fei.yhb_20.utils.NetUtil;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

/**
 * 主界面，里面使用了fragment来处理不同的tab，框架已经搭好
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private Fragment fMain;
    private Fragment fClass;

    @InjectView(R.id.id_tab_class)
    LinearLayout mTabClass;
    @InjectView(R.id.id_tab_main)
    LinearLayout mTabMain;
    @InjectView(R.id.id_tab_post)
    LinearLayout mTabPost;
    @InjectView(R.id.id_tab_main_img)
    ImageButton mMainImage;
    @InjectView(R.id.id_tab_class_img)
    ImageButton mClassImage;
    @InjectView(R.id.iv_main_message)
    ImageView message;
    @InjectView(R.id.tv_main_choose_position)
    TextView choose_position;
    @InjectView(R.id.logout)
    TextView logout;
    @InjectView(R.id.system_setting)
    TextView system_setting;
    @InjectView(R.id.user_photo)
    ImageView avatar;
    @InjectView(R.id.user_name)
    TextView name;
    @InjectView(R.id.user_motto)
    TextView motto;
    @InjectView(R.id.user_integral)
    TextView integral;
    @InjectView(R.id.user_postNumber)
    TextView postNumber;
    @InjectView(R.id.user_follower)
    TextView follower;
    @InjectView(R.id.user_following)
    TextView following;
    @InjectView(R.id.user_albums)
    TextView albums;
    @InjectView(R.id.user_list)
    ListView list;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.footer)
    FrameLayout footer;
    @InjectView(R.id.favShimmerReaLayout)
    LinearLayout rootView;
    @InjectView(R.id.user_cover)
    ImageView cover;
    @InjectView(R.id.drawer)
    RelativeLayout drawer;
    @InjectView(R.id.main_top)
    RelativeLayout mainTop;
    @InjectView(R.id.class_top)
    RelativeLayout classTop;
    @InjectView(R.id.person_setting)
    ImageView person_setting;


    private BaseUser user;
    private ActionBarDrawerToggle toggle;
    private String avatarPath;
    private BaseUser currentUser;

    private char role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        currentUser = BmobUser.getCurrentUser(this, BaseUser.class);
        role = getIntent().getCharExtra("role", 'a');


        initViews();

        NetUtil.checkForUpdate(this);
        initEvents();
        setSelect(GV.MAIN_PRESSED);
    }

    private void initViews() {

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.i(TAG, "closed");


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(TAG, "opened");
                rootView.setFocusable(false);
            }
        };

        drawerLayout.setDrawerListener(toggle);

        SharedPreferences settings = getSharedPreferences("settings", 0);
        if (settings.getBoolean("ever", false)) {
            choose_position.setText(settings.getString("city", "选择城市"));
        } else {
            if (NetUtil.isNetConnected(this)) {
                //已经写入了
                MapUtil.getLocation(this, choose_position);
            } else {
                choose_position.setText(settings.getString("city", "选择城市"));
            }
        }
        user = BmobUser.getCurrentUser(this, BaseUser.class);
        avatar.setImageResource(R.drawable.pull_scroll_view_avatar_default);
        name.setText(user.getUsername());
        motto.setText(user.getMotto());

        BmobQuery<OtherInfo> queryOtherInfo = new BmobQuery<>();
        String[] followerIds = {user.getObjectId()};
        queryOtherInfo.addWhereContainedIn("userId", Arrays.asList(followerIds));
        queryOtherInfo.findObjects(this, new FindListener<OtherInfo>() {
            @Override
            public void onSuccess(List<OtherInfo> otherInfos) {
                if (otherInfos != null) {
                    if (otherInfos.get(0) != null) {
                        if (otherInfos.get(0).getFollowerIds() != null) {
                            follower.setText("粉丝:" + otherInfos.get(0).getFollowerIds().size());
                        }
                        if (otherInfos.get(0).getFollowingIds() != null) {
                            following.setText("关注" + otherInfos.get(0).getFollowingIds().size());
                        }
                    }

                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "查询失败", Toast.LENGTH_LONG).show();
            }
        });

        /**
         * 查询当前用户发布的惠报
         */
        BmobQuery<Post> myPosts = new BmobQuery<>();
        myPosts.addWhereRelatedTo("post", new BmobPointer(user));
        myPosts.findObjects(this, new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (posts != null) {
                    int photoNumber = 0;
                    for (Post post : posts) {
                        if (post.getPaths() != null) {
                            String paths[] = post.getPaths().split("\\|");
                            photoNumber = photoNumber + paths.length;
                        }
                    }
                    postNumber.setText("惠报:" + posts.size());
                    albums.setText("相册:" + photoNumber);
                    Log.e(TAG, "成功查询惠报数量");
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "查询惠报数量失败" + s);
            }
        });

        String[] Stringdata;
        int[] drawables;
        if (user.getAttribute() == GV.MERCHANT) {
            Stringdata = new String[]{"商户信息", "待完善惠报", "我的收藏", "我的消息", "草稿箱", "反馈意见"};
            drawables = new int[]{R.drawable.slide_merchant_info, R.drawable.slide_need_to_complete, R.drawable.slide_collection, R.drawable.slide_message, R.drawable.slide_draft, R.drawable.feedback};
        } else {
            Stringdata = new String[]{"我的收藏", "我的消息", "草稿箱", "反馈意见"};
            drawables = new int[]{R.drawable.slide_collection, R.drawable.slide_message, R.drawable.slide_draft, R.drawable.feedback};
        }
        list.setAdapter(new SlideAdapter(Stringdata, drawables, this));

        refreshAvatar();

    }

    /**
     * 刷新头像
     */
    private void refreshAvatar() {
        BmobQuery<BaseUser> query = new BmobQuery<BaseUser>();
        query.getObject(this, user.getObjectId(), new GetListener<BaseUser>() {
            @Override
            public void onSuccess(BaseUser baseUser) {
                if (baseUser.getMotto() != null) {
                    motto.setText(baseUser.getMotto());
                } else {
                    motto.setText("这个人很懒，什么都没有留下。。。");
                }
                if (baseUser.getAvatarPaht() != null) {
                    avatarPath = baseUser.getAvatarPaht();
                    Picasso.with(MainActivity.this).load(baseUser.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(avatar);
                } else {
                    Log.d(TAG, "没有设置头像");
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, s + i);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshAvatar();
    }

    private class SlideAdapter extends BaseAdapter {

        private Context context;
        String[] data;
        int[] drawables;

        public SlideAdapter(String[] data, int[] drawables, Context context) {
            this.data = data;
            this.context = context;
            this.drawables = drawables;
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int position) {
            return data[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //不必使用viewholer，因为数据量小
            convertView = LayoutInflater.from(context).inflate(R.layout.slide_item, null);
            if (convertView == null) {
                Log.e(TAG, "convertView is null");
            } else {
                TextView slide_content = (TextView) convertView.findViewById(R.id.slide_item_text);
                ImageView slide_imageview = (ImageView) convertView.findViewById(R.id.slide_item_icon);
                slide_content.setText(data[position]);
                slide_imageview.setImageResource(drawables[position]);

            }
            return convertView;
        }
    }

    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

        //把相应的图片点亮
        // 设置内容区域
        switch (i) {
            case GV.MAIN_PRESSED:
                if (fMain == null) {
                    fMain = new MainFragment();
                    transaction.add(R.id.id_content, fMain);
                } else {
                    transaction.show(fMain);
                }
                mainTop.setVisibility(View.VISIBLE);
                classTop.setVisibility(View.INVISIBLE);
                mMainImage.setImageResource(R.drawable.main_pressed);
                break;
            case GV.CLASS_PRESSED:
                if (fClass == null) {
                    fClass = new ClassFragment();
                    transaction.add(R.id.id_content, fClass);
                } else {
                    transaction.show(fClass);
                }
                mainTop.setVisibility(View.INVISIBLE);
                classTop.setVisibility(View.VISIBLE);
                mClassImage.setImageResource(R.drawable.classification_pressed);
                break;
            default:
                break;
        }

        transaction.commit();
    }


    private void initEvents() {
        mTabMain.setOnClickListener(this);
        mTabClass.setOnClickListener(this);
        mTabPost.setOnClickListener(this);

        logout.setOnClickListener(this);
        system_setting.setOnClickListener(this);
        person_setting.setOnClickListener(this);
        choose_position.setOnClickListener(this);
        message.setOnClickListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (user.getAttribute() == GV.PERSON) {
                    Intent intent = null;
                    switch (position) {
                        case 0:
                            //收藏界面
                            intent = new Intent(MainActivity.this, MyCollections.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("currentUser", currentUser);
                            intent.putExtra("bundle", bundle);
                            break;
                        case 1:

                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                            break;
                    }
                    startActivity(intent);

                }
                if (user.getAttribute() == GV.MERCHANT) {
                    Intent intent = null;
                    switch (position) {
                        case 0:
                            //商户信息界面
                            intent = new Intent(MainActivity.this, MerchantInfoPage.class);
                            break;
                        case 1:
                            break;
                        case 2:
                            //我的收藏
                            intent = new Intent(MainActivity.this, MyCollections.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("currentUser", currentUser);
                            intent.putExtra("bundle", bundle);
                            break;
                        case 3:

                            break;
                        case 4:
                            break;
                        case 5:
                        default:
                            break;
                    }
                    if (intent != null) {
                        startActivity(intent);
                    }

                }
            }
        });

        cover.setOnClickListener(this);
        avatar.setOnClickListener(this);

        drawer.setOnClickListener(this);


    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fMain != null) {
            transaction.hide(fMain);
        }

        if (fClass != null) {
            transaction.hide(fClass);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.user_cover:
                //do nothing
                break;
            case R.id.id_tab_class:
                resetImgs();
                setSelect(GV.CLASS_PRESSED);
                break;
            case R.id.id_tab_main:
                resetImgs();
                setSelect(GV.MAIN_PRESSED);
                break;
            case R.id.id_tab_post:
                intent = new Intent(this, PostActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                BmobUser.logOut(this);
                Toast.makeText(this, "您已经成功退出", Toast.LENGTH_LONG).show();
                intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.person_setting:
                //这里是个人信息的设置界面
                if (user.getAttribute() == GV.MERCHANT) {
                    intent = new Intent(MainActivity.this, SettingMerchantActivity.class);
                    startActivity(intent);
                } else {
                    //个人的设置会是怎么样的
//                    intent = new Intent(MainActivity.this,SettingMerchantActivity.class);
//                    startActivity(intent);
                }
                break;
            case R.id.tv_main_choose_position:
                //以后可能会用到要更改的地方
//
//                View menuView = View.inflate(this, R.layout.pos_popupwindow, null);
//                final Dialog menuDialog = new Dialog(this, R.style.popupDialog);
//                menuDialog.setContentView(menuView);
//
//                final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
//
//                final String[] pos_data = this.getResources().getStringArray(R.array.hot_city);
//
//                ListView listView = (ListView) menuView.findViewById(R.id.listview);
//
//                listView.setAdapter(new PosAdatper(this, pos_data));
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        choose_position.setText(pos_data[position]);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("city", pos_data[position]);
//                        editor.apply();
//                        menuDialog.dismiss();
//                    }
//                });
//                menuDialog.show();
                Log.d(TAG, "choose_postition");
                View menuView = View.inflate(this, R.layout.choosepos_view, null);
                final Dialog menuDialog = new Dialog(this, R.style.popupDialog);
                menuDialog.setContentView(menuView);

                final LinearLayout beijing, shanghai, nanjing, guangzhou, chengdu;
                beijing = (LinearLayout) menuView.findViewById(R.id.beijing);
                shanghai = (LinearLayout) menuView.findViewById(R.id.shanghai);
                nanjing = (LinearLayout) menuView.findViewById(R.id.nanjing);
                guangzhou = (LinearLayout) menuView.findViewById(R.id.guangzhou);
                chengdu = (LinearLayout) menuView.findViewById(R.id.chengdu);

                beijing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("北京");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "北京");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                shanghai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("上海");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "上海");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                nanjing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("南京");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "南京");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                chengdu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("成都");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "成都");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                guangzhou.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("广州");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "广州");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                beijing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences sharedPreferences = getSharedPreferences("settings", 0);
                        choose_position.setText("北京");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city", "北京");
                        editor.apply();
                        menuDialog.dismiss();
                    }


                });
                menuDialog.show();

                break;
            case R.id.system_setting:
                //在这里应该是系统的设置界面，而不是个人信息的设置界面
                break;
            case R.id.iv_main_message:
                Intent infoIntent = new Intent(MainActivity.this, MessagePage.class);
                startActivity(infoIntent);
                break;
            case R.id.user_photo:
                //得到大图
                if (avatarPath == null || avatarPath.equals("")) {
                    Toast.makeText(MainActivity.this, "您没有设置大头像", Toast.LENGTH_LONG).show();
                } else {
                    //这个地方是可以优化一下
                    String[] paths = {avatarPath};
                    intent = new Intent(MainActivity.this, GalleryUrlActivity.class);
                    intent.putExtra("photoUrls", paths);
                    MainActivity.this.startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    class PosAdatper extends BaseAdapter {

        private Context context;
        private String[] strings;

        private PosAdatper(Context context, String[] strings) {
            this.strings = strings;
            this.context = context;
        }

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public Object getItem(int position) {
            return strings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pos_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.textview);
            textView.setText(strings[position]);

            return convertView;
        }
    }

    private void resetImgs() {
        mClassImage.setImageResource(R.drawable.classification);
        mMainImage.setImageResource(R.drawable.main);
    }

}
