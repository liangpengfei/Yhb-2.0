package com.example.fei.yhb_20.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.DragEvent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.Merchant;
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
public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private Fragment fMain;
    private Fragment fClass;

    @InjectView(R.id.id_tab_class)LinearLayout mTabClass;
    @InjectView(R.id.id_tab_main)LinearLayout mTabMain;
    @InjectView(R.id.id_tab_post)LinearLayout mTabPost;
    @InjectView(R.id.id_tab_main_img)ImageButton mMainImage;
    @InjectView(R.id.id_tab_class_img)ImageButton mClassImage;
    @InjectView(R.id.iv_main_message)ImageView message;
    @InjectView(R.id.tv_main_choose_position)TextView choose_position;
    @InjectView(R.id.logout)TextView logout;
    @InjectView(R.id.setting)TextView setting;
    @InjectView(R.id.user_photo)ImageView avatar;
    @InjectView(R.id.user_name)TextView name;
    @InjectView(R.id.user_motto)TextView motto;
    @InjectView(R.id.user_integral)TextView integral;
    @InjectView(R.id.user_postNumber)TextView postNumber;
    @InjectView(R.id.user_follower)TextView follower;
    @InjectView(R.id.user_following)TextView following;
    @InjectView(R.id.user_albums)TextView albums;
    @InjectView(R.id.user_list)ListView list;
    @InjectView(R.id.drawer_layout)DrawerLayout drawerLayout;
    @InjectView(R.id.footer)FrameLayout footer;
    @InjectView(R.id.favShimmerReaLayout)LinearLayout rootView;
    @InjectView(R.id.user_cover)ImageView cover;

    private BaseUser user;
    private ActionBarDrawerToggle toggle;
    private String avatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initViews();

        NetUtil.checkForUpdate(this);
        initEvents();
        setSelect(GV.MAIN_PRESSED);
    }

    private void initViews() {

        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.ic_launcher,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.i(TAG,"closed");


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(TAG,"opened");
                rootView.setFocusable(false);
            }
        };
//        drawerLayout.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()){
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        Log.i(TAG,"ondrag entered");
//                        break;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        Log.i(TAG,"ondrag exited");
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });

        drawerLayout.setDrawerListener(toggle);

        SharedPreferences settings = getSharedPreferences("settings",0);
        if(settings.getBoolean("ever", false)){
            choose_position.setText(settings.getString("city","选择城市"));
        }else{
            if (NetUtil.isNetConnected(this)){
                //已经写入了
                MapUtil.getLocation(this,choose_position);
            }else {
                choose_position.setText(settings.getString("city","选择城市"));
            }
        }
        user= BmobUser.getCurrentUser(this, BaseUser.class);
        avatar.setImageResource(R.drawable.pull_scroll_view_avatar_default);
        name.setText(user.getUsername());
        motto.setText(user.getMotto());

        BmobQuery<OtherInfo> queryOtherInfo = new BmobQuery<>();
        String [] followerIds = {user.getObjectId()};
        queryOtherInfo.addWhereContainedIn("userId", Arrays.asList(followerIds));
        queryOtherInfo.findObjects(this,new FindListener<OtherInfo>() {
            @Override
            public void onSuccess(List<OtherInfo> otherInfos) {
                Toast.makeText(MainActivity.this,"查询成功",Toast.LENGTH_LONG).show();
                if (otherInfos!=null){
                    if (otherInfos.get(0).getFollowerIds()!=null){
                        follower.setText("粉丝:"+otherInfos.get(0).getFollowerIds().size());
                    }
                    if (otherInfos.get(0).getFollowingIds()!=null){
                        following.setText("关注"+otherInfos.get(0).getFollowingIds().size());
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this,"查询失败",Toast.LENGTH_LONG).show();
            }
        });

        BmobQuery<Post> myPosts = new BmobQuery<>();
        myPosts.addWhereRelatedTo("post",new BmobPointer(user));
        myPosts.findObjects(this,new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (posts!=null){
                    int photoNumber = 0 ;
                    for (Post post:posts){
                        if (post.getPaths()!=null){
                            String paths [] = post.getPaths().split("\\|");
                            photoNumber = photoNumber + paths.length;
                        }
                    }
                    postNumber.setText("惠报:"+posts.size());
                    albums.setText("相册:"+photoNumber);
                    Log.e(TAG,"成功查询惠报数量");
                }
            }
            @Override
            public void onError(int i, String s) {
                Log.e(TAG,"查询惠报数量失败"+s);
            }
        });
        String [] data ;
        if (user.getAttribute()==GV.MERCHANT){
            data = new String[]{"商户信息", "待完善的相关惠报","我的足迹", "我的收藏", "我的消息", "草稿箱"};
        }else{
            data = new String[]{ "我的足迹", "我的收藏", "我的消息", "草稿箱"};

        }
        list.setAdapter(new SlideAdapter(data,this) );

        refreshAvatar();

    }

    private void refreshAvatar(){
        BmobQuery<Merchant> query = new BmobQuery<Merchant>();
        query.getObject(this,user.getObjectId(),new GetListener<Merchant>() {
            @Override
            public void onSuccess(Merchant merchant) {
                if (merchant.getAvatarPaht()!=null){
                    avatarPath = merchant.getAvatarPaht();
                    Picasso.with(MainActivity.this).load(merchant.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(avatar);
                }else{
                    Toast.makeText(MainActivity.this,"test",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int i, String s) {

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
        String [] data;

        public SlideAdapter (String [] data,Context context){
            this.data = data;
            this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.slide_item,null);
            if (convertView==null){
                Log.e(TAG, "convertView is null");
            }else{
                TextView slide_content = (TextView) convertView.findViewById(R.id.slide_item_text);
                slide_content.setText(data[position]);
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
        switch (i)
        {
            case GV.MAIN_PRESSED:
                if (fMain == null)
                {
                    fMain = new MainFragment();
                    transaction.add(R.id.id_content,fMain);
                } else
                {
                    transaction.show(fMain);
                }
                mMainImage.setImageResource(R.drawable.main_pressed);
                break;
            case GV.CLASS_PRESSED:
                if (fClass == null)
                {
                    fClass = new ClassFragment();
                    transaction.add(R.id.id_content, fClass);
                } else
                {
                    transaction.show(fClass);
                }
                mClassImage.setImageResource(R.drawable.classification_pressed);
                break;
            default:
                break;
        }

        transaction.commit();
    }


    private void initEvents()
    {
        mTabMain.setOnClickListener(this);
        mTabClass.setOnClickListener(this);
        mTabPost.setOnClickListener(this);

        logout.setOnClickListener(this);
        setting.setOnClickListener(this);
        choose_position.setOnClickListener(this);
        message.setOnClickListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (user.getAttribute()==GV.PERSON){
                    switch (position){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        default:
                            break;
                    }
                }
                if (user.getAttribute()==GV.MAIN_PRESSED){
                    Intent intent = null;
                    switch (position){
                        case 0:
                            intent = new Intent(MainActivity.this,MerchantInfoPage.class);
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            break;
                        default:
                            break;
                    }
                    if (intent!=null){
                        startActivity(intent);
                    }

                }
            }
        });

        cover.setOnClickListener(this);
        avatar.setOnClickListener(this);


    }

    private void hideFragment(FragmentTransaction transaction)
    {
        if (fMain != null)
        {
            transaction.hide(fMain);
        }

        if (fClass != null)
        {
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
        BaseUser currentUser =BmobUser.getCurrentUser(this,BaseUser.class);
        Intent intent;
        switch (v.getId()){
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
                intent = new Intent(this,PostActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                BmobUser.logOut(this);
                Toast.makeText(this, "您已经成功退出", Toast.LENGTH_LONG).show();
                intent = new Intent(this,FirstActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_main_choose_position:
//                View popupWindow = LayoutInflater.from(this).inflate(R.layout.popupwindow_pos,null);
//                ListView list = (ListView) popupWindow.findViewById(R.id.listview_pos);
//                TextView currentLocation = (TextView) popupWindow.findViewById(R.id.currentLocation);
//                final SharedPreferences sharedPreferences = getSharedPreferences("settings",0);
//
//                currentLocation.setText("当前城市："+sharedPreferences.getString("city",""));
////
////                final String  []pos_data = this.getResources().getStringArray(R.array.hot_city);
////                final PopupWindow popup_pos = new PopupWindow(popupWindow, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
//////                list.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,pos_data));
////                list.setAdapter(new PosAdatper(this,pos_data));
////
//                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        choose_position.setText(pos_data[position]);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("city",pos_data[position]);
//                        editor.apply();
//                        popup_pos.dismiss();
//                    }
//                });
//
//
//                popup_pos.setTouchable(true);
//                //为了修复popupwindow的bug，
//                popup_pos.setBackgroundDrawable(getResources().getDrawable(
//                        R.drawable.popup_pos_selector));
//                popup_pos.showAsDropDown(choose_position);
                View menuView = View.inflate(this,R.layout.pos_popupwindow,null);
                final Dialog menuDialog = new Dialog(this,R.style.popupDialog);
                menuDialog.setContentView(menuView);

                final SharedPreferences sharedPreferences = getSharedPreferences("settings",0);

                final String  []pos_data = this.getResources().getStringArray(R.array.hot_city);

                ListView listView = (ListView) menuView.findViewById(R.id.listview);

                listView.setAdapter(new PosAdatper(this,pos_data));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        choose_position.setText(pos_data[position]);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city",pos_data[position]);
                        editor.apply();
                        menuDialog.dismiss();
                    }
                });
                menuDialog.show();
                break;
            case R.id.setting:
                if (user.getAttribute()== GV.MERCHANT){
                    intent = new Intent(MainActivity.this,SettingMerchantActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.user_photo:
                //得到大图
                if (avatarPath ==null || avatarPath.equals("")){
                    Toast.makeText(MainActivity.this,"您没有设置大头像",Toast.LENGTH_LONG).show();
                }else{
                    //这个地方是可以优化一下
                    String [] paths = {avatarPath};
                    intent = new Intent(MainActivity.this, GalleryUrlActivity.class);
                    intent.putExtra("photoUrls",paths);
                    MainActivity.this.startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    class PosAdatper extends BaseAdapter{

        private Context context;
        private String[] strings;

        private PosAdatper(Context context ,String [] strings) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.pos_item,null);
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
