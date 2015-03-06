package com.example.fei.yhb_20.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Post;
import com.marshalchen.common.uimodule.cropimage.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    public static final List<String> data;

    private RecyclerView recyclerView;

    static {
        data = new ArrayList<String>();
        data.add("浮夸 - 陈奕迅");
        data.add("好久不见 - 陈奕迅");
        data.add("时间都去哪儿了 - 王铮亮");
        data.add("董小姐 - 宋冬野");
        data.add("爱如潮水 - 张信哲");
        data.add("给我一首歌的时间 - 周杰伦");
        data.add("天黑黑 - 孙燕姿");
        data.add("可惜不是你 - 梁静茹");
        data.add("太委屈 - 陶晶莹");
        data.add("用心良苦 - 张宇");
        data.add("说谎 - 林宥嘉");
        data.add("独家记忆 - 陈小春");
    }

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
        query.order("createdAt");
        query.findObjects(getActivity(),new FindListener<Post>() {
            @Override
            public void onSuccess(List<Post> posts) {
                recyclerView.setAdapter(new MyAdapter(posts));
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
        private List<Post> data;

        public MyAdapter(List<Post> data) {
            this.data = data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 加载Item的布局.布局中用到的真正的CardView.
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_item, viewGroup, false);
            // ViewHolder参数一定要是Item的Root节点.
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            //从data中获取数据，填充入视图中
            Post post = data.get(i);

            viewHolder.tvConment.setText("评论"+post.getComment());
            viewHolder.tvDislike.setText("没有帮助"+post.getDislike());
            viewHolder.tvLike.setText("喜欢"+post.getLike());
            viewHolder.tvShared.setText("享受过"+post.getShared());
            viewHolder.content.setText(post.getContent());
//            viewHolder.time.setText(post.getTime());
//            viewHolder.userName.setText(post.getTime); 联级查询查找username
            viewHolder.merchantName.setText(post.getMerchantName());
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView merchantName,userName,content,time,tvShared,tvLike,tvDislike,tvConment;
            ImageView icon,share,list;
            LinearLayout shared,like,dislike,conment;


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
            }
        }
    }

}
