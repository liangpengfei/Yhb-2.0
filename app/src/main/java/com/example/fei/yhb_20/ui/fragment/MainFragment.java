package com.example.fei.yhb_20.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.ThumbnailListener;
import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.ui.PersonalActivity;
import com.example.fei.yhb_20.utils.ACache;
import com.example.fei.yhb_20.utils.ExpressionUtil;
import com.example.fei.yhb_20.utils.MyUtils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private RecyclerView recyclerView;
    private static Picasso picasso;
    private static DrawerLayout drawerLayout;
    private static ACache aCache;
    LinearLayoutManager layoutManager;
    private static int[] imageIds = new int[107];
    private SharedPreferences sharedPreferences ;

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
        sharedPreferences = getActivity().getSharedPreferences("settings",0);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.include("user");
        query.order("-createdAt");
        query.findObjects(getActivity(),new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                //在这里写入缓存
                for (int i = 0 ;i<posts.size();i++){
                    aCache.put(String.valueOf(i),posts.get(i));
                    if (aCache.getAsBinary(posts.get(i).getObjectId()+"footerBoolean")==null){
                        byte[] footerBoolean = {1,1,1,1};
                        aCache.put(posts.get(i).getObjectId()+"footerBoolean",footerBoolean);
                    }
                    //根据ObjectId来
                    Log.e(TAG, "write success " + i);
                }
                aCache.put("cacheSize",String.valueOf(posts.size()));
                recyclerView.setAdapter(new MyAdapter(posts,getActivity(),drawerLayout));
            }

            @Override
            public void onError(int code, String s) {
                Log.e(TAG,s);
                if (sharedPreferences.getBoolean("ever",false)){
                    List<Post> objects = new ArrayList<Post>();

                    int size = Integer.parseInt(aCache.getAsString("cacheSize"));
                    Post post;
                    for (int i = 0;i<size;i++){
                        post= (Post) aCache.getAsObject(String.valueOf(i));
                        if (post==null){
                            android.util.Log.e(TAG, "post is null");
                        }
                        objects.add(post);
                    }
                    recyclerView.setAdapter(new MyAdapter(objects,getActivity(),drawerLayout));
                }else{
                    Toast.makeText(getActivity(),"您没有登录过，没有缓存文件！",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aCache = ACache.get(getActivity());
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private static final int SHARE = 0;
        private static final int LIKE = 1;
        private static final int DISLIKE = 2;
        private static final int COMMENT = 3;

        private List<Post> data;
        private Context context;

        public MyAdapter(List<Post> data,Context context,View view) {
            this.data = data;
            this.context = context;
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
                final String cacheBooleanKey = objectId + "footerBoolean";
                final ArrayList<Integer> numberFooter = post.getNumberFooter();
                final byte[] footerBoolean = aCache.getAsBinary(cacheBooleanKey);
                viewHolder.content.setText(post.getContent());
                viewHolder.userName.setText(post.getUser().getUsername());// 级联查询查找username
                viewHolder.merchantName.setText(post.getMerchantName());

                viewHolder.tvShared.setText("享受过" + (numberFooter.get(SHARE)));
                viewHolder.tvLike.setText("喜欢"+(numberFooter.get(LIKE)));
                viewHolder.tvDislike.setText("没有帮助" + (numberFooter.get(DISLIKE)));
                viewHolder.tvConment.setText("评论"+ numberFooter.get(COMMENT));


                /**
                 *定义菜单时间
                 */
                viewHolder.list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View menuView = View.inflate(context,R.layout.popupwindow,null);
                        Dialog menuDialog = new Dialog(context,R.style.popupDialog);
                        menuDialog.setContentView(menuView);

                        LinearLayout collect,unfollow,block,report;
                        collect = (LinearLayout) menuView.findViewById(R.id.collect);
                        unfollow = (LinearLayout) menuView.findViewById(R.id.unfollow);
                        block = (LinearLayout) menuView.findViewById(R.id.block);
                        report = (LinearLayout) menuView.findViewById(R.id.report);

                        /**
                         * 为弹出菜单写定义事件
                         */
                        collect.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //收藏
                            }
                        });
                        unfollow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //取消关注
                            }
                        });
                        block.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //屏蔽
                            }
                        });
                        report.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //举报
                            }
                        });
                        menuDialog.show();
                    }
                });


                /**
                 * 享受过
                 */
                viewHolder.shared.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (footerBoolean[SHARE]==0){
                            viewHolder.ivShare.setImageResource(R.drawable.thumbs_up);
                            //更新数据,和下面的有一些区别
                            footerBoolean[SHARE]=1;
                            if (aCache.remove(cacheBooleanKey)){
                                aCache.put(cacheBooleanKey, footerBoolean);
                                Log.e(TAG,"remove success");
                            }
                            numberFooter.set(SHARE,numberFooter.get(SHARE)-1);
                            post.setNumberFooter(numberFooter);

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
                            viewHolder.ivShare.setImageResource(R.drawable.thumbs_up_pressed);

                            footerBoolean[SHARE]=0;
                            numberFooter.set(SHARE, (numberFooter.get(SHARE) + 1));
                            post.setNumberFooter(numberFooter);
                            if (aCache.remove(cacheBooleanKey)){
                                aCache.put(cacheBooleanKey,footerBoolean);
                                Log.e(TAG,"remove success");
                            }
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

                /**
                 * 喜欢
                 */
                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (footerBoolean[LIKE]==0){
                            viewHolder.ivLike.setImageResource(R.drawable.icon_heart);

                            footerBoolean[LIKE]=1;
                            numberFooter.set(LIKE,numberFooter.get(LIKE)-1);
                            aCache.put(post.getObjectId()+"footerBoolean",footerBoolean);
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
                            viewHolder.ivLike.setImageResource(R.drawable.icon_heart_pressed);

                            footerBoolean[LIKE]=0;
                            numberFooter.set(LIKE,numberFooter.get(LIKE)+1);
                            post.setNumberFooter(numberFooter);
                            aCache.put(post.getObjectId()+"footerBoolean",footerBoolean);
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

                /**
                 * 没有帮助
                 */
                viewHolder.dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (footerBoolean[DISLIKE]==0){
                            viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike);

                            footerBoolean[DISLIKE]=1;
                            numberFooter.set(DISLIKE, numberFooter.get(DISLIKE) - 1);
                            post.setNumberFooter(numberFooter);
                            aCache.put(post.getObjectId()+"footerBoolean",footerBoolean);
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
                            viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike_pressed);

                            footerBoolean[DISLIKE]=0;
                            numberFooter.set(DISLIKE, numberFooter.get(DISLIKE) + 1);
                            post.setNumberFooter(numberFooter);
                            aCache.put(post.getObjectId()+"footerBoolean",footerBoolean);
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

                /**
                 * 评论
                 */
                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View menuView = View.inflate(context,R.layout.edittext_comment,null);
                        final AlertDialog menuDialog = new AlertDialog.Builder(context).create();
                        menuDialog.setView(menuView);
                        ImageView face = (ImageView) menuView.findViewById(R.id.team_singlechat_id_expression);
                        Button send = (Button) menuView.findViewById(R.id.team_singlechat_id_send);
                        final EditText comment = (EditText) menuView.findViewById(R.id.team_singlechat_id_edit);
                        comment.setFocusable(true);
                        /**
                         * 选择表情按钮
                         */
                        face.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //显示表情选择
                                final Dialog builder = new Dialog(context);

                                /**
                                 * 生成一个表情对话框中的gridview
                                 * @return
                                 */
                                    final GridView view = new GridView(context);
                                    List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
                                    //生成107个表情的id，封装
                                    for(int i = 0; i < 107; i++){
                                        try {
                                            if(i<10){
                                                Field field = R.drawable.class.getDeclaredField("f00" + i);
                                                int resourceId = Integer.parseInt(field.get(null).toString());
                                                imageIds[i] = resourceId;
                                            }else if(i<100){
                                                Field field = R.drawable.class.getDeclaredField("f0" + i);
                                                int resourceId = Integer.parseInt(field.get(null).toString());
                                                imageIds[i] = resourceId;
                                            }else{
                                                Field field = R.drawable.class.getDeclaredField("f" + i);
                                                int resourceId = Integer.parseInt(field.get(null).toString());
                                                imageIds[i] = resourceId;
                                            }
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        } catch (SecurityException e) {
                                            e.printStackTrace();
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchFieldException e) {
                                            e.printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                        Map<String,Object> listItem = new HashMap<String,Object>();
                                        listItem.put("image", imageIds[i]);
                                        listItems.add(listItem);
                                    }

                                    SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
                                    view.setAdapter(simpleAdapter);
                                    view.setNumColumns(6);
                                    view.setBackgroundColor(Color.rgb(214, 211, 214));
                                    view.setHorizontalSpacing(1);
                                    view.setVerticalSpacing(1);
                                    view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                                    view.setGravity(Gravity.CENTER);
                                GridView gridView = view;
                                builder.setContentView(gridView);
                                builder.setTitle("默认表情");
                                builder.show();
                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                            long arg3) {
                                        Bitmap bitmap = null;
                                        bitmap = BitmapFactory.decodeResource(context.getResources(), imageIds[arg2 % imageIds.length]);
                                        ImageSpan imageSpan = new ImageSpan(context, bitmap);
                                        String str = null;
                                        if(arg2<10){
                                            str = "f00"+arg2;
                                        }else if(arg2<100){
                                            str = "f0"+arg2;
                                        }else{
                                            str = "f"+arg2;
                                        }
                                        SpannableString spannableString = new SpannableString(str);
                                        spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        //添加成一个字符串
                                        comment.append(spannableString);
                                        builder.dismiss();
                                    }
                                });
                            }
                        });
                        /**
                         * 评论发布按钮
                         */
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //其实是定义了一个协议的，表情传送协议,在接受端也要进行处理
                                String zhengze = "f0[0-9]{2}|f10[0-7]";
                                SpannableString spannableString = ExpressionUtil.getExpressionString(context, comment.getText().toString(), zhengze);
                                if (post!=null){
                                    post.add("comments",comment.getText().toString());
                                    post.update(context,new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e(TAG,"成功评论");
                                            post.add("comments",BmobUser.getCurrentUser(context).getObjectId());
                                            post.update(context,new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Log.e(TAG,"成功添加评论人信息");
                                                    numberFooter.set(COMMENT, numberFooter.get(COMMENT) + 1);
                                                    post.setNumberFooter(numberFooter);
                                                    post.update(context,new UpdateListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            Log.e(TAG,"评论成功，加一");
                                                            Toast.makeText(context,"评论成功",Toast.LENGTH_LONG).show();
                                                        }

                                                        @Override
                                                        public void onFailure(int i, String s) {
                                                            Log.e(TAG,"评论失败"+s);
                                                        }
                                                    });
                                                    menuDialog.dismiss();
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    Log.e(TAG,"失败添加评论人信息"+s+i);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Log.e(TAG,"失败评论"+s+i);
                                        }
                                    } );
                                }else {
                                    Toast.makeText(context,"没有网络链接，请检查网络",Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        menuDialog.show();
                    }
                });

                /**
                 * 分享事件
                 */
                viewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "我有好东西与您分享"+post.getContent());
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                });

                //点击头像进入个人主页
                viewHolder.avata.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("user",post.getUser());
                        context.startActivity(intent);
                    }
                });
                //点击姓名进入个人主页
                viewHolder.userName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalActivity.class);
                        intent.putExtra("user",post.getUser());
                        context.startActivity(intent);
                    }
                });

                /**
                 * 设置展示的时候的图标颜色值，要正确的显示
                 */
            Log.e(TAG, Arrays.toString(footerBoolean));
                if (footerBoolean!=null){
                    if (footerBoolean[DISLIKE]==0){
                        viewHolder.ivDislike.setImageResource(R.drawable.icon_dislike_pressed);
                    }
                    if (footerBoolean[LIKE]==0){
                        viewHolder.ivLike.setImageResource(R.drawable.icon_heart_pressed);
                    }
                    if (footerBoolean[SHARE]==0){
                        viewHolder.ivShare.setImageResource(R.drawable.thumbs_up_pressed);
                    }
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


                ArrayList<String> arrayList = post.getThumnailsName();
                Log.e(TAG,arrayList.toString());

//                for (int i1 = 0 ;i1 <paths.length; i1++) {
//
//                    final ImageView imageView;
//                    imageView = new ImageView(context);
//                    picasso.load(paths[i1]).placeholder(R.drawable.ic_launcher).resize(200, 200).into(imageView);
//                    imageView.setPadding(2, 2, 2, 2);
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Dialog dialog = new Dialog(context);
//                            View photoView = LayoutInflater.from(context).inflate(R.layout.photo_view,null);
//                            ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
//                            photo.setImageDrawable(imageView.getDrawable());
//                            dialog.setContentView(photoView);
//                            dialog.show();
//                        }
//                    });
//                    viewHolder.gallery.addView(imageView);
//                }

                /**
                 * 想获取缩略图，但是没有成功，总是显示appkey is null
                 */
                //TODO
                for (int i1 = 0 ;i1 <arrayList.size(); i1++) {
                    Log.e(TAG,"kldsjf");
                    BmobProFile.getInstance(context).submitThumnailTask(arrayList.get(i1), 1, new ThumbnailListener() {

                        @Override
                        public void onSuccess(String thumbnailName, String thumbnailUrl) {
                            Log.e(TAG,thumbnailUrl+"djlksfjlksdjflksjdlkfjsd");
                            ImageView imageView =new ImageView(context);
                            picasso.load(thumbnailUrl).placeholder(R.drawable.ic_launcher).resize(200, 200).into(imageView);
                            imageView.setPadding(2,2,2,2);
                            viewHolder.gallery.addView(imageView);
                        }

                        @Override
                        public void onError(int statuscode, String errormsg) {
                            Log.e(TAG, String.valueOf(statuscode));
                            Log.e(TAG,errormsg+"kldsjfklsjdklfjsdkjfkjsdljfklsdjfjklsdjkfjsdklajfkljsadkfjkl");
                        }
                    });

                }
            }


        }
        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView merchantName,userName,content,time,tvShared,tvLike,tvDislike,tvConment;
            ImageView avata,share,list,ivShare,ivLike,ivDislike,ivComment;
            LinearLayout shared,like,dislike,comment,gallery;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                merchantName = (TextView) itemView.findViewById(R.id.tv_main_merchantName);
                userName = (TextView) itemView.findViewById(R.id.tv_main_postName);
                content = (TextView) itemView.findViewById(R.id.tv_main_content);
                time = (TextView) itemView.findViewById(R.id.tv_main_time);
                avata = (ImageView) itemView.findViewById(R.id.iv_main_logo);
                shared = (LinearLayout) itemView.findViewById(R.id.ll_main_shared);
                like = (LinearLayout) itemView.findViewById(R.id.ll_main_like);
                dislike = (LinearLayout) itemView.findViewById(R.id.ll_main_dislike);
                comment = (LinearLayout) itemView.findViewById(R.id.ll_main_conment);
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
