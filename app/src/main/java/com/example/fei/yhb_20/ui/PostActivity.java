package com.example.fei.yhb_20.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;
import com.example.fei.yhb_20.LocationApplication;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.adapter.MyAdapter;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.CommentItem;
import com.example.fei.yhb_20.bean.MyListItem;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.utils.Bimp;
import com.example.fei.yhb_20.utils.DBManager;
import com.example.fei.yhb_20.utils.FileUtils;
import com.example.fei.yhb_20.utils.GV;
import com.example.fei.yhb_20.utils.ImageItem;
import com.example.fei.yhb_20.utils.MyUtils;
import com.example.fei.yhb_20.utils.NetUtil;
import com.example.fei.yhb_20.utils.PublicWay;
import com.example.fei.yhb_20.utils.Res;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PostActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private LocationClient mLocationClient;
    private static final java.lang.String TAG = "PostActivity";
    @InjectView(R.id.iv_post_back)
    ImageView back;
    @InjectView(R.id.iv_post_ok)
    ImageView ok;
    @InjectView(R.id.ratingbar)
    RatingBar ratingBar;
    @InjectView(R.id.et_post_merchantName)
    EditText merchantName;
    @InjectView(R.id.time)
    Spinner time;
    @InjectView(R.id.iv_post_dingwei)
    ImageView dingwei;
    @InjectView(R.id.detailPos)
    EditText detailPos;
    @InjectView(R.id.position1)
    Spinner position1;
    @InjectView(R.id.position2)
    Spinner position2;
    @InjectView(R.id.position3)
    Spinner position3;
    @InjectView(R.id.noScrollgridview)
    GridView noScrollgridview;
    @InjectView(R.id.et_post_content)
    EditText content;
    @InjectView(R.id.face_bar)
    LinearLayout faceBar;
    @InjectView(R.id.face)
    ImageView face;
    @InjectView(R.id.container)
    RelativeLayout frameLayout;


    private DBManager dbm;
    private SQLiteDatabase db;
    private String province = null;
    private String city = null;
    private String district = null;
    private String sTime = null;
    private String filename;
    public static Bitmap bimap;
    private View parentView;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    private GridAdapter adapter;

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
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_post, null);
        setContentView(parentView);
        ButterKnife.inject(this);

        if (GV.getContent() != null) {
            content.setText(GV.getContent());
        }
        if (GV.getMerchantName() != null) {
            merchantName.setText(GV.getMerchantName());
        }
        if (GV.getRating() != 0) {
            ratingBar.setRating(GV.getRating());
        }


        position1.setPrompt("省");
        position2.setPrompt("市");
        position3.setPrompt("地区");
        time.setPrompt("选择时间");
        mLocationClient = ((LocationApplication) getApplication()).mLocationClient;

        /**
         * 初始化地理位置控件
         */
        ((LocationApplication) getApplication()).position1 = position1;
        ((LocationApplication) getApplication()).position2 = position2;
        ((LocationApplication) getApplication()).position3 = position3;

        initEvents();
        initSpinner1();
        initTimeSpinner();
        Init();
    }


    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //比较Activity根布局与当前布局的大小
            Log.e(TAG, "onGlobalLayout");
            int heightDiff = frameLayout.getRootView().getHeight() - frameLayout.getHeight();
            if (heightDiff > 200) {
                if (merchantName.isFocused()) {
                    faceBar.setVisibility(View.INVISIBLE);
                } else {
                    faceBar.setVisibility(View.VISIBLE);
                    Log.e(TAG, "显示");
                }
            } else {
                Log.e(TAG, "隐藏");
                faceBar.setVisibility(View.GONE);
            }
        }
    };

    private void updateIcons() {
        final String[] files = getPhotoPath().split("\\|");
        if (getPhotoPath().contains("|")) {
            MyUtils.showProgressDialog(this, "正在发布");
            BmobProFile.getInstance(PostActivity.this).uploadBatch(files, new UploadBatchListener() {
                @Override
                public void onSuccess(boolean isFinished, String[] fileNames, String[] urls) {
                    if (isFinished) {
                        Log.e(TAG, "上传图片成功");
                        //得到图片路径的字符串
                        StringBuilder stringBuilder = new StringBuilder("");
                        for (int i = 0; i < fileNames.length; i++) {
                            Log.e(TAG, fileNames[i]);
                            stringBuilder.append(BmobProFile.getInstance(PostActivity.this).signURL(fileNames[i], urls[i], "54f197dc6dce11fc7c078c07420a080e", 0, null));
                            stringBuilder.append("|");
                        }
                        Log.e(TAG, stringBuilder.toString());
                        //post与user关联
                        final Post post = new Post();

                        post.setContent(content.getText().toString());
                        post.setMerchantName(merchantName.getText().toString());
                        post.setActivityTiem(time.getSelectedItem().toString());
                        post.setProvince(province);
                        post.setCity(city);
                        post.setDistrict(district);
                        post.setRating(ratingBar.getRating());
                        post.setPaths(stringBuilder.toString());
                        post.setDetailPos(detailPos.getText().toString());
                        ArrayList<String> arrayList = new ArrayList<String>();
                        Collections.addAll(arrayList, fileNames);
                        post.setThumnailsName(arrayList);

                        final BaseUser user = BmobUser.getCurrentUser(PostActivity.this, BaseUser.class);

                        ArrayList numberFooter = new ArrayList();
                        numberFooter.add(0, 0);
                        numberFooter.add(1, 0);
                        numberFooter.add(2, 0);
                        numberFooter.add(3, 0);


                        post.setNumberFooter(numberFooter);

                        ArrayList<CommentItem> commentItems = new ArrayList<CommentItem>();
                        post.setCommentItems(commentItems);

                        post.setUser(user);
                        post.setOwnerId(user.getObjectId());
                        post.save(PostActivity.this, new SaveListener() {
                            @Override
                            public void onSuccess() {
                                //到这里只能是说post发送成功了，还没有更新user表中的数据
                                if (TextUtils.isEmpty(post.getObjectId()) || TextUtils.isEmpty(post.getObjectId())) {
                                    Log.d(TAG, "还没有发布内容");
                                    return;
                                }
                                BmobRelation posts = new BmobRelation();
                                posts.add(post);
                                user.setPost(posts);
                                user.update(PostActivity.this, new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        //更新user表中的数据成功，最终成功
                                        Toast.makeText(PostActivity.this, "发布成功！", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "成功添加到用户的posts中");
                                        adapter.clearAll();
                                        content.setText("");
                                        PostActivity.this.finish();
                                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        PostActivity.this.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.e(TAG, "没有添加成功" + s);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e(TAG, "发布失败" + s);
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
                    Log.e(TAG, "上传图片失败，请检查网络连接" + s + i);
                    Toast.makeText(PostActivity.this, "上传图片失败，请检查网络连接", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            showPostView();
        }

    }

    private void showPostView() {
        View view = LayoutInflater.from(this).inflate(R.layout.toast_view, null);
        TextView message = (TextView) view.findViewById(R.id.message);
        message.setText("亲，添加一张照片会使您的惠报增分不少哦～");
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(view);
        toast.show();
    }

    /**
     * 初始化照相popupwindwo
     */
    public void Init() {

        GV.setMyClass(PostActivity.class);
        pop = new PopupWindow(PostActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
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
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 点击拍照按钮
         */
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePhoto();
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        /**
         * 点击图库
         */
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //先保存数据
                GV.setContent(content.getText().toString());
                GV.setRating(ratingBar.getRating());
                GV.setMerchantName(merchantName.getText().toString());

                Intent intent = new Intent(PostActivity.this,
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
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                /**
                 * 如果是没有满到9张，就是添加图标
                 */
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(PostActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    content.setFocusable(false);
                    ll_popup.startAnimation(AnimationUtils.loadAnimation(PostActivity.this, R.anim.activity_translate_in));
                    pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                } else {
                    Intent intent = new Intent(PostActivity.this,
                            GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * 得到上传照片的文件路径
     *
     * @return
     */
    public String getPhotoPath() {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            builder.append(Bimp.tempSelectBitmap.get(i).getImagePath());
            builder.append("|");

        }
        return builder.toString();
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;
        ViewHolder holder = null;


        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }


        public void clearAll() {
            holder = null;
            Bimp.tempSelectBitmap.clear();
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
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

            if (position == Bimp.tempSelectBitmap.size()) {
                if (Bimp.tempSelectBitmap.size() == 0) {
                    holder.image.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.add));
                } else {
                    holder.image.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.icon_addpic_unfocused));
                }
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

    /**
     * 拍照
     */
    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setCoorType("gcj02");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 初始化时间控件
     */
    private void initTimeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.time_array));
        time.setAdapter(adapter);
        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "parent.getItemAtPosition(position).toString()");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initEvents() {
        back.setOnClickListener(this);
        ok.setOnClickListener(this);
        dingwei.setOnClickListener(this);

        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    faceBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceBar.setVisibility(View.VISIBLE);
            }
        });

        face.setOnClickListener(this);
    }

    /**
     * 以下是一连串的动作
     */
    public void initSpinner1() {
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from province";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                String code = cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[] = cursor.getBlob(2);
                String name = new String(bytes, "gbk");
                MyListItem myListItem = new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code = cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[] = cursor.getBlob(2);
            String name = new String(bytes, "gbk");
            MyListItem myListItem = new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this, list);
        position1.setAdapter(myAdapter);
        position1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
    }

    public void initSpinner2(String pcode) {
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from city where pcode='" + pcode + "'";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                String code = cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[] = cursor.getBlob(2);
                String name = new String(bytes, "gbk");
                MyListItem myListItem = new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code = cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[] = cursor.getBlob(2);
            String name = new String(bytes, "gbk");
            MyListItem myListItem = new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this, list);
        position2.setAdapter(myAdapter);
        position2.setOnItemSelectedListener(new SpinnerOnSelectedListener2());
    }

    public void initSpinner3(String pcode) {
        dbm = new DBManager(this);
        dbm.openDatabase();
        db = dbm.getDatabase();
        List<MyListItem> list = new ArrayList<MyListItem>();

        try {
            String sql = "select * from district where pcode='" + pcode + "'";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            while (!cursor.isLast()) {
                String code = cursor.getString(cursor.getColumnIndex("code"));
                byte bytes[] = cursor.getBlob(2);
                String name = new String(bytes, "gbk");
                MyListItem myListItem = new MyListItem();
                myListItem.setName(name);
                myListItem.setPcode(code);
                list.add(myListItem);
                cursor.moveToNext();
            }
            String code = cursor.getString(cursor.getColumnIndex("code"));
            byte bytes[] = cursor.getBlob(2);
            String name = new String(bytes, "gbk");
            MyListItem myListItem = new MyListItem();
            myListItem.setName(name);
            myListItem.setPcode(code);
            list.add(myListItem);

        } catch (Exception e) {
        }
        dbm.closeDatabase();
        db.close();

        MyAdapter myAdapter = new MyAdapter(this, list);
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
            province = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
            String pcode = ((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

            initSpinner2(pcode);
            initSpinner3(pcode);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    class SpinnerOnSelectedListener2 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                   long id) {
            city = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
            String pcode = ((MyListItem) adapterView.getItemAtPosition(position)).getPcode();

            initSpinner3(pcode);
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    class SpinnerOnSelectedListener3 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                   long id) {
            district = ((MyListItem) adapterView.getItemAtPosition(position)).getName();
            Log.d(TAG, province + " " + city + " " + district);
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
        switch (v.getId()) {
            case R.id.iv_post_back:
                finish();
                break;
            case R.id.iv_post_ok:
                updateIcons();
                break;
            case R.id.iv_post_dingwei:
                Log.d(TAG, "dingwei");
                if (NetUtil.isNetConnected(this)) {
                    position1.setOnItemSelectedListener(this);
                    position2.setOnItemSelectedListener(this);
                    position3.setOnItemSelectedListener(this);
                    InitLocation();
                    mLocationClient.start();
                } else {
                    Toast.makeText(this, "无网络连接，请检测您的网络设置！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.face:
                /**
                 * 显示表情选择
                 */
                MyUtils.showFaceDialog(PostActivity.this, content);
                break;
            default:
                break;

        }
    }

    /**
     * 选取图片的两种方式得到的图片的显示
     *
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

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < PublicWay.activityList.size(); i++) {
                if (null != PublicWay.activityList.get(i)) {
                    PublicWay.activityList.get(i).finish();
                }
            }
            System.exit(0);
        }
        return true;
    }
}
