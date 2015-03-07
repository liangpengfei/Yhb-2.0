package com.example.fei.yhb_20.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.utils.MyUtils;
import com.marshalchen.common.uimodule.cardsSwiped.view.CardContainer;
import com.marshalchen.common.uimodule.cropimage.util.Log;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private RecyclerView recyclerView;
    private static Picasso picasso;
    private DrawerLayout drawerLayout;



    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);

        picasso = Picasso.with(getActivity());



        //TODO 不要在这里联网查询
//        recyclerView.setAdapter(new MyAdapter(data));
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //TODO 在这里联网
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.include("user");
        query.order("-createdAt");
        query.findObjects(getActivity(),new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                recyclerView.setAdapter(new MyAdapter(posts,getActivity(),drawerLayout));
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG,s);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private static final int SHARE = 0;
        private static final int LIKE = 1;
        private static final int DISLIKE = 2;
        private List<Post> data;
        private Context context;
        private PopupWindow meun;
        private View view;
        private SharedPreferences settings;

        public MyAdapter(List<Post> data,Context context,View view) {
            this.data = data;
            this.context = context;
            this.view = view;
            settings =context.getSharedPreferences("settings", 0);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item, viewGroup, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            //从data中获取数据，填充入视图中
            final Post post = data.get(i);
            if (post!=null){
                final String objectId = post.getObjectId();
                final ArrayList<Integer> numberFooter = post.getNumberFooter();
                final ArrayList booleanFooter = post.getBooleanArray();
//                final Footer footer = post.getFooter();
//                viewHolder.tvConment.setText("评论" + footer.getiComment());
//                viewHolder.tvDislike.setText("没有帮助"+footer.getiDislike());
//                viewHolder.tvLike.setText("喜欢"+footer.getiLike());
//                viewHolder.tvShared.setText("享受过"+footer.getiShare());
                viewHolder.content.setText(post.getContent());
//            viewHolder.time.setText(post.getTime());
                viewHolder.userName.setText(post.getUser().getUsername());// 级联查询查找username
                viewHolder.merchantName.setText(post.getMerchantName());



                /**
                 * 定义事件
                 */
                viewHolder.list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View contentView = inflater.inflate(R.layout.popupwindow,null);
                        meun = new PopupWindow(contentView, CardContainer.LayoutParams.WRAP_CONTENT, CardContainer.LayoutParams.WRAP_CONTENT);
                        meun.showAtLocation(view, Gravity.CENTER,0,0);
                    }
                });

                viewHolder.shared.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (booleanFooter.get(SHARE)==0){
                            //执行取消的代码,并且要写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("shared",false);
//                            editor.commit();

                            viewHolder.ivShare.setImageResource(R.drawable.thumbs_up);
                            //更新数据
                            booleanFooter.set(SHARE,1);
                            numberFooter.set(SHARE,numberFooter.get(SHARE)-1);
                            post.setNumberFooter(numberFooter);
                            post.setBooleanArray(booleanFooter);

                            post.update(context, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG, "成功dis shared");
                                    Toast.makeText(context, "成功dis shared", Toast.LENGTH_LONG).show();
                                    viewHolder.tvShared.setText("享受过" + (numberFooter.get(SHARE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG, "失败shared");
                                }
                            });
                        }else{
                            //执行添加的代码，写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("shared",true);
//                            editor.apply();


                            viewHolder.ivShare.setImageResource(R.drawable.thumbs_up_pressed);

                            booleanFooter.set(SHARE,0);
                            numberFooter.set(SHARE, (numberFooter.get(SHARE) + 1));
                            post.setNumberFooter(numberFooter);
                            post.setBooleanArray(booleanFooter);
                            post.update(context, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG, "成功shared");
                                    Toast.makeText(context,"成功shared",Toast.LENGTH_LONG).show();
                                    viewHolder.tvShared.setText("享受过"+(numberFooter.get(SHARE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG,"失败shared");
                                }
                            });
                        }

                    }
                });

                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (booleanFooter.get(LIKE)==0){
                            //执行取消的代码,并且要写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("like",false);
//                            editor.commit();

                            viewHolder.ivLike.setImageResource(R.drawable.icon_heart);

                            booleanFooter.set(LIKE,1);
                            numberFooter.set(LIKE,numberFooter.get(LIKE)-1);
                            post.setBooleanArray(booleanFooter);
                            post.setNumberFooter(numberFooter);
                            post.update(context, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG,"成功dis like");
                                    Toast.makeText(context,"成功dis like",Toast.LENGTH_LONG).show();
                                    viewHolder.tvLike.setText("喜欢"+(numberFooter.get(LIKE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG,"失败dis like");
                                }
                            });
                        }else{
                            //执行添加的代码，写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("like",true);
//                            editor.commit();


                            viewHolder.ivLike.setImageResource(R.drawable.icon_heart_pressed);

                            booleanFooter.set(LIKE,0);
                            numberFooter.set(LIKE,numberFooter.get(LIKE)+1);
                            post.setNumberFooter(numberFooter);
                            post.setBooleanArray(booleanFooter);
                            post.update(context, objectId, new UpdateListener() {

                                @Override
                                public void onSuccess() {
                                    Log.e(TAG,"成功like");
                                    Toast.makeText(context,"成功like",Toast.LENGTH_LONG).show();
                                    viewHolder.tvLike.setText("喜欢"+(numberFooter.get(LIKE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG,"失败like");
                                }
                            });
                        }

                    }
                });

                viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (booleanFooter.get(DISLIKE)==0){
                            //执行取消的代码,并且要写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("dislike",false);
//                            editor.commit();

                            viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike);

                            booleanFooter.set(DISLIKE,1);
                            numberFooter.set(DISLIKE, numberFooter.get(DISLIKE) - 1);
                            post.setNumberFooter(numberFooter);
                            post.setBooleanArray(booleanFooter);
                            post.update(context, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG,"成功dis dislike");
                                    Toast.makeText(context,"成功dis dislike",Toast.LENGTH_LONG).show();
                                    viewHolder.tvDislike.setText("没有帮助" + (numberFooter.get(DISLIKE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG,"失败dislike");
                                }
                            });
                        }else{
                            //执行添加的代码，写入SharedPreferences
//                            SharedPreferences.Editor editor = settings.edit();
//                            editor.putBoolean("dislike",true);
//                            editor.commit();

                            viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike_pressed);

                            booleanFooter.set(DISLIKE,0);
                            numberFooter.set(DISLIKE, numberFooter.get(DISLIKE) + 1);
                            post.setNumberFooter(numberFooter);
                            post.setBooleanArray(booleanFooter);
                            post.update(context, objectId, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.e(TAG,"成功dislike");
                                    Toast.makeText(context,"成功dislike",Toast.LENGTH_LONG).show();
                                    viewHolder.tvDislike.setText("没有帮助" + (numberFooter.get(DISLIKE)));
                                }

                                @Override
                                public void onFailure(int code, String msg) {
                                    Log.e(TAG,"失败dislike");
                                }
                            });
                        }

                    }
                });

                if (booleanFooter.get(DISLIKE)==0){
                    viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike_pressed);
                }
                if (booleanFooter.get(LIKE)==0){
                    viewHolder.ivLike.setImageResource(R.drawable.icon_heart_pressed);
                }
                if (booleanFooter.get(SHARE)==0){
                    viewHolder.ivShare.setImageResource(R.drawable.thumbs_up_pressed);
                }


                //格式化时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = simpleDateFormat.parse(post.getCreatedAt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewHolder.time.setText(MyUtils.timeLogic(date,context));

                //获取图片，使用Picasso可以缓存
                String paths [] = post.getPaths().split("\\|");
                int t = paths.length;
                Log.e(TAG, String.valueOf(t));
                ImageView imageView ;
                for (int i1 = 0 ;i1 <paths.length; i1++) {
                    imageView = new ImageView(context);
                    picasso.load(paths[i1]).placeholder(R.drawable.ic_launcher).resize(200, 200).into(imageView);
                    imageView.setPadding(2,2,2,2);
                    viewHolder.gallery.addView(imageView);
                }
            }


        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView merchantName,userName,content,time,tvShared,tvLike,tvDislike,tvConment;
            ImageView icon,share,list,ivShare,ivLike,ivDislike,ivComment;
            LinearLayout shared,like,dislike,conment,gallery;



            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                merchantName = (TextView) itemView.findViewById(R.id.tv_main_merchantName);
                userName = (TextView) itemView.findViewById(R.id.tv_main_postName);
                content = (TextView) itemView.findViewById(R.id.tv_main_content);
                time = (TextView) itemView.findViewById(R.id.tv_main_time);
                icon = (ImageView) itemView.findViewById(R.id.iv_main_logo);
                shared = (LinearLayout) itemView.findViewById(R.id.ll_main_shared);
                like = (LinearLayout) itemView.findViewById(R.id.ll_main_like);
                dislike = (LinearLayout) itemView.findViewById(R.id.ll_main_dislike);
                conment = (LinearLayout) itemView.findViewById(R.id.ll_main_conment);
                tvShared = (TextView) itemView.findViewById(R.id.tv_main_shared);
                tvLike = (TextView) itemView.findViewById(R.id.tv_main_like);
                tvDislike = (TextView) itemView.findViewById(R.id.tv_main_dislike);
                tvConment = (TextView) itemView.findViewById(R.id.tv_main_comment);
                share = (ImageView) itemView.findViewById(R.id.iv_main_share);
                list = (ImageView) itemView.findViewById(R.id.iv_main_list);
                gallery = (LinearLayout) itemView.findViewById(R.id.ll_gallery);
                ivComment = (ImageView) itemView.findViewById(R.id.iv_main_comment);
                ivDislike = (ImageView) itemView.findViewById(R.id.iv_main_dislike);
                ivLike = (ImageView) itemView.findViewById(R.id.iv_main_like);
                ivShare = (ImageView) itemView.findViewById(R.id.iv_main_shared);
            }
        }
    }

}
