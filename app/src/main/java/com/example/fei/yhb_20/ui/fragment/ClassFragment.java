package com.example.fei.yhb_20.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.fei.yhb_20.R;

import java.util.ArrayList;
import java.util.HashMap;


public class ClassFragment extends Fragment {
    private static final String TAG = "ClassFragment";
    private ListView postItem;

    private GridView class0,class1,class2,class3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        class0 = (GridView) view.findViewById(R.id.gv_class_0);
        class1 = (GridView) view.findViewById(R.id.gv_class_1);
        class2 = (GridView) view.findViewById(R.id.gv_class_2);
        class3 = (GridView) view.findViewById(R.id.gv_class_3);

        String [] strings0 = {"IFS","王府井","火锅","万达影城","肯德基","日本料理","足疗按摩","等等"};
        String [] strings1 = {"全部","火锅","小吃快餐","川菜","自助","面餐","面包甜点"};
        String [] strings2 = {"全部","电影院","KTV","美容","摄影写真","酒吧","健身"};
        String [] strings3 = {"专卖店","商场","家电","家居","超市","生鲜","书店"};

        class0.setAdapter(new GridViewAdapter(strings0,getActivity()));
        class1.setAdapter(new GridViewAdapter(strings1, getActivity()));
        class2.setAdapter(new GridViewAdapter(strings2, getActivity()));
        class3.setAdapter(new GridViewAdapter(strings3, getActivity()));
        return view;

    }

    class GridViewAdapter extends BaseAdapter{

        private String [] stringItems;
        private Context context;

        public GridViewAdapter (String [] stringItems,Context context){
            this.stringItems = stringItems;
            this.context= context;
        }

        @Override
        public int getCount() {
            return stringItems.length;
        }

        @Override
        public Object getItem(int position) {
            return stringItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_class_item,null);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(stringItems[position]);
            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
