package com.example.fei.yhb_20.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.ui.fragment.ClassFragment;
import com.example.fei.yhb_20.ui.fragment.MainFragment;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.MapUtil;
import com.example.fei.yhb_20.utils.NetUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;

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
    @InjectView(R.id.user_photo)ImageView avatar;
    @InjectView(R.id.user_name)TextView name;
    @InjectView(R.id.user_motto)TextView motto;
    @InjectView(R.id.user_integral)TextView integral;
    @InjectView(R.id.user_postNumber)TextView postNumber;
    @InjectView(R.id.user_follower)TextView follower;
    @InjectView(R.id.user_following)TextView following;
    @InjectView(R.id.user_albums)TextView albums;
    @InjectView(R.id.user_list)ListView list;

    private BaseUser user;

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
        if (user.getFolloingId()!=null){
            follower.setText("粉丝 "+user.getFollowerId().size());
        }
        if (user.getFolloingId()!=null){
            following.setText("关注 "+user.getFolloingId().size());
        }

        String [] data ;
        if (user.getAttribute()==GV.MERCHANT){
            data = new String[]{"商户信息", "待完善的相关惠报", "我享受过的", "我喜欢的", "我的足迹", "我的收藏", "我的消息", "草稿箱"};
        }else{
            data = new String[]{"我享受过的", "我喜欢的", "我的足迹", "我的收藏", "我的消息", "草稿箱"};

        }
        list.setAdapter(new SlideAdapter(data,this) );
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
                    startActivity(intent);
                }
            }
        });

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
        switch (v.getId()){
            case R.id.id_tab_class:
                resetImgs();
                setSelect(GV.CLASS_PRESSED);
                break;
            case R.id.id_tab_main:
                resetImgs();
                setSelect(GV.MAIN_PRESSED);
                break;
            case R.id.id_tab_post:
                Intent intent = new Intent(this,PostActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                BmobUser.logOut(this);
                BmobUser currentUser = BmobUser.getCurrentUser(this);
                Toast.makeText(this, "您已经成功退出", Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(this,FirstActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.tv_main_choose_position:
                View popupWindow = LayoutInflater.from(this).inflate(R.layout.popupwindow_pos,null);
                ListView list = (ListView) popupWindow.findViewById(R.id.listview_pos);
                TextView currentLocation = (TextView) popupWindow.findViewById(R.id.currentLocation);
                final SharedPreferences sharedPreferences = getSharedPreferences("settings",0);

                currentLocation.setText("当前城市："+sharedPreferences.getString("city",""));

                final String  []pos_data = this.getResources().getStringArray(R.array.hot_city);
                final PopupWindow popup_pos = new PopupWindow(popupWindow, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
                list.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,pos_data));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        choose_position.setText(pos_data[position]);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("city",pos_data[position]);
                        editor.apply();
                        popup_pos.dismiss();
                    }
                });
                popup_pos.setTouchable(true);
                //为了修复popupwindow的bug，
                popup_pos.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.popup_pos_selector));
                popup_pos.showAsDropDown(choose_position);
                break;
            default:
                break;
        }
    }

    private void resetImgs() {
        mClassImage.setImageResource(R.drawable.classification);
        mMainImage.setImageResource(R.drawable.main);
    }
}
