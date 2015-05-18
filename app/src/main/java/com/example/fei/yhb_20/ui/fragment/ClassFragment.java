package com.example.fei.yhb_20.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.ui.ImageFile;
import com.example.fei.yhb_20.ui.MerchantSearchPage;


public class ClassFragment extends Fragment {
    private static final String TAG = "ClassFragment";
    private GridView class0, class1, class2, class3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        class0 = (GridView) view.findViewById(R.id.gv_class_0);
        class1 = (GridView) view.findViewById(R.id.gv_class_1);
        class2 = (GridView) view.findViewById(R.id.gv_class_2);
        class3 = (GridView) view.findViewById(R.id.gv_class_3);

        String[] strings0 = {"IFS", "王府井", "火锅", "万达影城", "肯德基", "更多"};
        String[] strings1 = {"全部", "火锅", "小吃快餐", "川菜", "自助", "更多"};
        String[] strings2 = {"全部", "电影院", "KTV", "美容", "摄影写真", "更多"};
        String[] strings3 = {"专卖店", "商场", "家电", "家居", "超市", "更多"};

        class0.setAdapter(new GridViewAdapter(strings0, getActivity()));
        class1.setAdapter(new GridViewAdapter(strings1, getActivity()));
        class2.setAdapter(new GridViewAdapter(strings2, getActivity()));
        class3.setAdapter(new GridViewAdapter(strings3, getActivity()));
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        class0.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), MerchantSearchPage.class);
//                startActivity(intent);
            }
        });
        class1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), MerchantSearchPage.class);
//                startActivity(intent);
            }
        });
        class2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), MerchantSearchPage.class);
//                startActivity(intent);
            }
        });
        class3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Intent intent = new Intent(getActivity(), MerchantSearchPage.class);
                    startActivity(intent);
                }
            }
        });

    }


    class GridViewAdapter extends BaseAdapter {

        private String[] stringItems;
        private Context context;

        public GridViewAdapter(String[] stringItems, Context context) {
            this.stringItems = stringItems;
            this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_class_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            ImageView down_arrow = (ImageView) convertView.findViewById(R.id.class_down_arrow);
            if (position == 5){
                down_arrow.setVisibility(View.VISIBLE);
            }
            textView.setText(stringItems[position]);
            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
