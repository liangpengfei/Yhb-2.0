package com.example.fei.yhb_20.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.Comment;
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
import cn.bmob.v3.listener.UpdateListener;

public class DeatilActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "DeatilActivity";
    @InjectView(R.id.tv_main_postName)TextView postUserName;
    @InjectView(R.id.tv_main_time)TextView time;
    @InjectView(R.id.iv_main_share)ImageView share;
    @InjectView(R.id.iv_main_list)ImageView list;
    @InjectView(R.id.tv_main_content)TextView content;
    @InjectView(R.id.tv_main_shared)TextView shared;
    @InjectView(R.id.tv_main_like)TextView like;
    @InjectView(R.id.tv_main_dislike)TextView dislike;
    @InjectView(R.id.tv_main_comment)TextView comment;
    @InjectView(R.id.ll_main_shared)LinearLayout llShare;
    @InjectView(R.id.ll_main_like)LinearLayout llLike;
    @InjectView(R.id.ll_main_dislike)LinearLayout llDislike;
    @InjectView(R.id.ll_main_conment)LinearLayout llComment;
    @InjectView(R.id.team_singlechat_id_foot)LinearLayout llFoot;
    @InjectView(R.id.team_singlechat_id_send)Button send;
    @InjectView(R.id.team_singlechat_id_edit)EditText edComment;
    @InjectView(R.id.team_singlechat_id_expression)ImageView face;
    @InjectView(R.id.ratingbar)RatingBar ratingBar;
    @InjectView(R.id.location)TextView location;
    @InjectView(R.id.lastTime)TextView lastTime;
    @InjectView(R.id.iv_main_shared)ImageView ivShared;
    @InjectView(R.id.iv_main_like)ImageView ivLike;
    @InjectView(R.id.iv_main_dislike)ImageView ivDislike;
    @InjectView(R.id.iv_main_comment)ImageView ivComment;
    @InjectView(R.id.ll_gallery)LinearLayout gallery;
    @InjectView(R.id.comment_list)ListView listview;

    private static final int SHARE = 0;
    private static final int LIKE = 1;
    private static final int DISLIKE = 2;
    private static final int COMMENT = 3;
    private static ACache aCache;
    private Post post;
    private String objectId;
    private ArrayList numberFooter;
    private byte[] footerBoolean;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatil);
        ButterKnife.inject(this);
        aCache =  ACache.get(this);
        picasso = Picasso.with(this);
        initViews();
        initEvents();

    }

    private void initViews() {
        Intent intent = getIntent();
        if (intent!=null){
            post = (Post) intent.getSerializableExtra("post");

            final String paths [] = post.getPaths().split("\\|");
            int t = paths.length;
            objectId = post.getObjectId();

            footerBoolean= aCache.getAsBinary(post.getObjectId()+"footerBoolean");
            postUserName.setText(post.getUser().getUsername());
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

            numberFooter= post.getNumberFooter();
            shared.setText("我享受过 "+numberFooter.get(SHARE));
            like.setText("我喜欢 "+numberFooter.get(LIKE));
            dislike.setText("没有帮助"+numberFooter.get(DISLIKE));
            comment.setText("评论" + numberFooter.get(COMMENT));
            ratingBar.setRating(post.getRating());
            lastTime.setText("活动时间:"+post.getActivityTiem());
            if (footerBoolean!=null){
                if (footerBoolean[DISLIKE]==0){
                    ivDislike.setImageResource(R.drawable.icon_dislike_pressed);
                }
                if (footerBoolean[LIKE]==0){
                    ivLike.setImageResource(R.drawable.icon_heart_pressed);
                }
                if (footerBoolean[SHARE]==0){
                    ivShared.setImageResource(R.drawable.thumbs_up_pressed);
                }
            }
            for (int i1 = 0 ;i1 <paths.length; i1++) {
                final ImageView imageView;
                imageView = new ImageView(this);
                picasso.load(paths[i1]).placeholder(R.drawable.ic_launcher).into(imageView);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DeatilActivity.this, GalleryUrlActivity.class);
                        intent.putExtra("photoUrls",paths);
                        DeatilActivity.this.startActivity(intent);
                    }
                });
                gallery.addView(imageView);
            }
            String [] allComments = post.getComments();
            final String [] comments = new String[allComments.length/2+1];
            String [] userIds = new String[allComments.length/2+1];
            //
            for (int i = 0 ;i<allComments.length;i++){
                if (i%2==0){
                    comments[i/2] = allComments[i];
                }else{
                    userIds[i/2] = allComments[i];
                }
            }
            BmobQuery<BaseUser> query = new BmobQuery<BaseUser>();
            query.addWhereContainedIn("objectId", Arrays.asList(userIds));
            query.findObjects(this, new FindListener<BaseUser>() {
                @Override
                public void onSuccess(List<BaseUser> baseUsers) {
//                    List<Comment> objComments = null;
                    ArrayList objComments = new ArrayList();
                    for (int i = 0;i<baseUsers.size();i++){
                        Comment objComment = new Comment();
                        objComment.setBaseUser(baseUsers.get(i));
                        objComment.setComment(comments[i]);
                        objComments.add(objComment);
                    }
                    listview.setAdapter(new commentAdapter(DeatilActivity.this,objComments));
                    MyUtils.setListViewHeightBasedOnChildren(listview);
                    listview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(TAG,"错误"+s+i);
                }
            });

        }


    }

    class commentAdapter extends BaseAdapter{

        private Context context;
        private ArrayList<Comment> comments;

        public commentAdapter(Context context,ArrayList<Comment> objComents){
            this.context = context;
            this.comments = objComents;

        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item,null);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.comment_avatar);
            TextView comment = (TextView) convertView.findViewById(R.id.comment);
            String zhengze = "f0[0-9]{2}|f10[0-7]";
            SpannableString spannableString = ExpressionUtil.getExpressionString(context, comments.get(position).getComment(), zhengze);
            comment.setText(spannableString);
            TextView name = (TextView) convertView.findViewById(R.id.comment_username);
            //在这里设置
            name.setText(comments.get(position).getBaseUser().getUsername());
            return convertView;
        }
    }

    private void initEvents() {
        share.setOnClickListener(this);
        list.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llLike.setOnClickListener(this);
        llDislike.setOnClickListener(this);
        llShare.setOnClickListener(this);
        face.setOnClickListener(this);
        send.setOnClickListener(this);
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
               MyUtils.showPopupMenu(this);
                break;
            case R.id.ll_main_conment:
                llFoot.setVisibility(View.VISIBLE);
                edComment.requestFocus();

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
                MyUtils.showFaceDialog(this,edComment);
                break;
            case R.id.ll_main_dislike:
                MyUtils.footerCommand(footerBoolean,DISLIKE,ivDislike,dislike,numberFooter,aCache,post,this,objectId);
                break;
            case R.id.ll_main_like:
                MyUtils.footerCommand(footerBoolean,LIKE,ivLike,like,numberFooter,aCache,post,this,objectId);
                break;
            case R.id.ll_main_shared:
                MyUtils.footerCommand(footerBoolean,SHARE,ivShared,shared,numberFooter,aCache,post,this,objectId);
                break;
            default:
                break;
        }
    }



}
