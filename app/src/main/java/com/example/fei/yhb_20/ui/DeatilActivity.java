package com.example.fei.yhb_20.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.CommentItem;
import com.example.fei.yhb_20.bean.Merchant;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.utils.ACache;
import com.example.fei.yhb_20.utils.ExpressionUtil;
import com.example.fei.yhb_20.utils.MyUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class DeatilActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "DeatilActivity";
    @InjectView(R.id.tv_main_postName)TextView postUserName;
    @InjectView(R.id.tv_main_time)TextView time;
    @InjectView(R.id.iv_main_share)ImageView share;
    @InjectView(R.id.iv_main_list)ImageView list;
    @InjectView(R.id.tv_main_content)TextView content;
    @InjectView(R.id.tv_main_like)TextView like;
    @InjectView(R.id.tv_main_dislike)TextView dislike;
    @InjectView(R.id.tv_main_comment)TextView comment;
    @InjectView(R.id.ll_main_like)LinearLayout llLike;
    @InjectView(R.id.ll_main_dislike)LinearLayout llDislike;
    @InjectView(R.id.ll_main_conment)LinearLayout llComment;
    @InjectView(R.id.ratingbar)RatingBar ratingBar;
    @InjectView(R.id.location)TextView location;
    @InjectView(R.id.lastTime)TextView lastTime;
    @InjectView(R.id.iv_main_like)ImageView ivLike;
    @InjectView(R.id.iv_main_dislike)ImageView ivDislike;
    @InjectView(R.id.ll_gallery)LinearLayout gallery;
    @InjectView(R.id.iv_main_logo)ImageView mAvatar;
    @InjectView(R.id.tv_main_merchantName)
    TextView merchantName;


    private static final int LIKE = 0;
    private static final int DISLIKE = 1;
    private static final int COMMENT = 2;
    private static ACache aCache;
    private Post post;
    private String objectId;
    private String userId;
    private ArrayList numberFooter;
    private byte[] footerBoolean;
    private Picasso picasso;
    private ListView listView;
    private ImageView face;
    private Button send;
    private EditText edComment;
    private RelativeLayout frameLayout;
    private LinearLayout llFoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil);
        aCache =  ACache.get(this);
        picasso = Picasso.with(this);
        initViews();

    }

    /**
     * 以后可能要接受来自不同界面的post文件，都要在这里进行一些处理
     */
    private void initViews() {
        face = (ImageView) findViewById(R.id.team_singlechat_id_expression);
        edComment = (EditText) findViewById(R.id.team_singlechat_id_edit);
        send = (Button) findViewById(R.id.team_singlechat_id_send);
        frameLayout = (RelativeLayout) findViewById(R.id.container);
        llFoot = (LinearLayout) findViewById(R.id.team_singlechat_id_foot);
        Intent intent = getIntent();
        if (intent!=null){
            String souceActivity = intent.getStringExtra("sourceActivity");
            if (souceActivity != null) {
                if (souceActivity.equals("MyCollections")) {
                    //去联网查询
                    String postId = intent.getStringExtra("postId");
                    BmobQuery<Post> query = new BmobQuery<>();
                    query.include("user");
                    query.getObject(DeatilActivity.this, postId, new GetListener<Post>() {
                        @Override
                        public void onSuccess(Post mPost) {
                            post = mPost;
                            deliverPost(mPost);
                            initEvents();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.d(TAG, s + i);
                        }
                    });
                } else if (souceActivity.equals("MainFragment")) {
                    post = (Post) intent.getSerializableExtra("post");
                    deliverPost(post);
                    initEvents();
                }
            }
        }
    }

    /**
     * 处理来自不同界面的post
     *
     * @param post
     */
    private void deliverPost(Post post) {
        View header = LayoutInflater.from(this).inflate(R.layout.header, null);
        ButterKnife.inject(this, header);

        final String paths[] = post.getPaths().split("\\|");
        int t = paths.length;
        objectId = post.getObjectId();

        footerBoolean = aCache.getAsBinary(post.getObjectId() + "footerBoolean");
        postUserName.setText(post.getUser().getUsername());

        String SmerchantName = "";
        if (post.getMerchantName().equals("") || post.getMerchantName() == null) {
            //do nothing
            Log.d(TAG, "merchantName is null");
        } else {
            SmerchantName = "  " + post.getMerchantName() + "  ";
        }
        merchantName.setText(SmerchantName);

        userId = post.getUser().getObjectId();

        //格式化时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(post.getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        time.setText(MyUtils.timeLogic(date, this));

        String zhengze = "f0[0-9]{2}|f10[0-7]";
        SpannableString spannableString = ExpressionUtil.getExpressionString(this, post.getContent(), zhengze);
        content.setText(spannableString);

        numberFooter = post.getNumberFooter();
        like.setText(String.valueOf(numberFooter.get(LIKE)));
        dislike.setText(String.valueOf(numberFooter.get(DISLIKE)));
        comment.setText(String.valueOf(numberFooter.get(COMMENT)));
        ratingBar.setRating(post.getRating());
        lastTime.setText("活动时间:" + post.getActivityTiem());
        if (footerBoolean != null) {
            if (footerBoolean[DISLIKE] == 0) {
                ivDislike.setImageResource(R.drawable.icon_dislike_pressed);
            }
            if (footerBoolean[LIKE] == 0) {
                ivLike.setImageResource(R.drawable.icon_heart_pressed);
            }
        }
        for (int i1 = 0; i1 < paths.length; i1++) {
            final ImageView imageView;
            imageView = new ImageView(this);
            picasso.load(paths[i1]).placeholder(R.drawable.ic_launcher).resize(300, 300).into(imageView);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DeatilActivity.this, GalleryUrlActivity.class);
                    intent.putExtra("photoUrls", paths);
                    DeatilActivity.this.startActivity(intent);
                }
            });
            gallery.addView(imageView);
        }

        /**
         * 处理一下多余的数据
         */
        ArrayList<CommentItem> data = post.getCommentItems();


        data.add(0, new CommentItem());
        listView = (ListView) findViewById(R.id.comment_list);
        listView.setAdapter(new commentAdapter(this, data, header));
        refreshAvatar(post);
        send.setEnabled(false);
    }

    private void refreshAvatar(Post post){
        BmobQuery<Merchant> query = new BmobQuery<Merchant>();
        query.getObject(this,post.getUser().getObjectId(),new GetListener<Merchant>() {
            @Override
            public void onSuccess(Merchant merchant) {
                if (merchant.getAvatarPaht()!=null){
                    Picasso.with(DeatilActivity.this).load(merchant.getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(mAvatar);
                }else{
                    Toast.makeText(DeatilActivity.this,"test",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Log.e(TAG,s+i);
            }
        });
    }

    class commentAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<CommentItem> commentItems;
        private View header;

        public commentAdapter(Context context,ArrayList<CommentItem> commentItems,View header){
            this.context = context;
            ArrayList<CommentItem> realists = new ArrayList<>();
            for (int i = commentItems.size()-1;i>commentItems.size()/2-1;i--){
                realists.add(commentItems.get(i));
            }
            this.commentItems = realists;
            this.header = header;
        }

        @Override
        public int getCount() {
            return commentItems.size();
        }

        @Override
        public Object getItem(int position) {
            return commentItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position==0){
                return header;
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.comment_item,null);
                final ImageView avatar = (ImageView) convertView.findViewById(R.id.comment_avatar);
                TextView comment = (TextView) convertView.findViewById(R.id.comment);

                if (commentItems.get(position).getComment() == null || commentItems.get(position).getComment().equals("")){
                    comment.setText("");
                }else{
                    String zhengze = "f0[0-9]{2}|f10[0-7]";
                    SpannableString spannableString = ExpressionUtil.getExpressionString(context, commentItems.get(position).getComment(), zhengze);
                    comment.setText(spannableString);
                }

                TextView name = (TextView) convertView.findViewById(R.id.comment_username);
                ImageView reply = (ImageView) convertView.findViewById(R.id.reply);
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        edComment.requestFocus();
                        edComment.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                Log.e(TAG,"before"+s);
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.e(TAG,"onTextChanged"+s);
                                if (TextUtils.isEmpty(s)) {
                                    send.setEnabled(false);
                                }else{
                                    send.setEnabled(true);
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.e(TAG,"after");
                            }
                        });

                        //强制显示键盘
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
                //在这里设置
                name.setText(commentItems.get(position).getName());

                if (commentItems.get(position).getObjectId()!=null){
                    BmobQuery<Merchant> query = new BmobQuery<Merchant>();
                    query.addQueryKeys("avatarPaht");
                    String []ids = {commentItems.get(position).getObjectId()};
                    query.addWhereContainedIn("objectId", Arrays.asList(ids));
                    query.findObjects(DeatilActivity.this, new FindListener<Merchant>() {
                        @Override
                        public void onSuccess(List<Merchant> merchants) {
                            if (merchants.get(0).getAvatarPaht()!=null){
                                Log.e(TAG,merchants.get(0).getAvatarPaht());
                                picasso.load(merchants.get(0).getAvatarPaht()).placeholder(R.drawable.ic_launcher).resize(45,45).into(avatar);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e(TAG,s + i);
                        }
                    });
                }
                return convertView;
            }

        }
    }

    private void initEvents() {
        share.setOnClickListener(this);
        list.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llLike.setOnClickListener(this);
        llDislike.setOnClickListener(this);
        face.setOnClickListener(this);
        send.setOnClickListener(this);
        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deatil, menu);
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
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (v.getId())
        {
            case R.id.iv_main_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "我有好东西与您分享"+post.getContent());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.iv_main_list:
                MyUtils.showPopupMenu(this, objectId, userId);
                break;
            case R.id.ll_main_conment:
                edComment.requestFocus();
                edComment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Log.e(TAG,"before"+s);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.e(TAG,"onTextChanged"+s);
                        if (TextUtils.isEmpty(s)) {
                            send.setEnabled(false);
                        }else{
                            send.setEnabled(true);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                        Log.e(TAG,"after");
                    }
                });

                //强制显示键盘
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.team_singlechat_id_send:
                llFoot.setVisibility(View.INVISIBLE);
                MyUtils.commentSend(post,edComment,this);

                //强制隐藏
                imm.hideSoftInputFromWindow(edComment.getWindowToken(),0);
                edComment.setText(null);
                break;
            case R.id.team_singlechat_id_expression:
                MyUtils.showFaceDialog(this, edComment);
                break;
            case R.id.ll_main_dislike:
                MyUtils.footerCommand(footerBoolean,DISLIKE,ivDislike,dislike,numberFooter,aCache,post,this,objectId);
                break;
            case R.id.ll_main_like:
                MyUtils.footerCommand(footerBoolean,LIKE,ivLike,like,numberFooter,aCache,post,this,objectId);
                break;
            default:
                break;
        }
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            //比较Activity根布局与当前布局的大小
            Log.e(TAG,"onGlobalLayout");
            int heightDiff = frameLayout.getRootView().getHeight()- frameLayout.getHeight();
            if(heightDiff >200){
                //大小超过100时，一般为显示虚拟键盘事件
//                getActivity().findViewById(R.id.footer).setVisibility(View.GONE);
                llFoot.setVisibility(View.VISIBLE);
                Log.e(TAG,"显示");
            }else{
                Log.e(TAG, "隐藏");
//                getActivity().findViewById(R.id.footer).setVisibility(View.VISIBLE);
                llFoot.setVisibility(View.GONE);
//                LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_main,null).findViewById(R.id.team_singlechat_id_foot).setVisibility(View.GONE);
                //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
            }
        }
    };

}
