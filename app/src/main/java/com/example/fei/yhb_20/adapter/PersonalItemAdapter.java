package com.example.fei.yhb_20.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.fei.yhb_20.bean.Post;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/17/15.
 */
public class PersonalItemAdapter extends BaseAdapter {

    private Post post;

    public void PersonalLikeFragment(Post post){
        this.post = post;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
