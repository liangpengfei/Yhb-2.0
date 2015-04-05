package com.example.fei.yhb_20.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.Post;
import com.example.fei.yhb_20.ui.DeatilActivity;
import com.example.fei.yhb_20.utils.ExpressionUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 这里封装了对一些可以抽象的视图
 * Email luckyliangfei@gmail.com
 * Created by fei on 4/4/15.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private static final String TAG = "ItemAdapter";
    private List<Post> data;
    private Context context;

    public ItemAdapter(List<Post> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (data != null) {
            //do something with the data
            final Post post = data.get(position);
            if (post == null) {
                Log.d(TAG, "posts传送失败");
            } else {

                String zhengze = "f0[0-9]{2}|f10[0-7]";
                SpannableString spannableString = ExpressionUtil.getExpressionString(context, post.getContent(), zhengze);

                holder.username.setText(post.getUser().getUsername());
                holder.content.setText(spannableString);
                holder.time.setText(post.getUpdatedAt().substring(5, 10));
                final String paths[] = post.getPaths().split("\\|");
                Picasso.with(context).load(post.getUser().getAvatarPaht()).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(45, 45).into(holder.avatar);
                Picasso.with(context).load(paths[0]).placeholder(R.drawable.pull_scroll_view_avatar_default).error(R.drawable.pull_scroll_view_avatar_default).resize(68, 68).into(holder.postPhoto);
                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DeatilActivity.class);
                        intent.putExtra("postId", post.getObjectId());
                        intent.putExtra("sourceActivity", "MyCollections");
                        context.startActivity(intent);
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
        TextView content, username, time;
        ImageView avatar, postPhoto;
        LinearLayout container;

        public ViewHolder(View itemView) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            username = (TextView) itemView.findViewById(R.id.tv_username);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            avatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            postPhoto = (ImageView) itemView.findViewById(R.id.iv_post_photo);
            container = (LinearLayout) itemView.findViewById(R.id.item_container);
        }
    }
}