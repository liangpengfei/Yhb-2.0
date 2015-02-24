package com.example.fei.yhb_20.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fei.yhb_20.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";
    public static final List<String> data;

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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyAdapter(data));
        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> data;

        public MyAdapter(List<String> data) {
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
            viewHolder.text.setText(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            public ViewHolder(View itemView) {
                // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }

}
