package com.example.fei.yhb_20.utils.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.fei.yhb_20.R;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/9/15.
 */
public class Comment_view extends RelativeLayout {
    public Comment_view(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.ui_edittext,this);
    }
}
