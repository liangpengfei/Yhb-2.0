package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.adapter.ItemAdapter;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MyCollections extends ActionBarActivity {

    private static final String TAG = "MyCollections";
    @InjectView(R.id.rv_collections)
    RecyclerView recyclerView;
    private Intent intent;
    private BaseUser currentUser;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collections);
        ButterKnife.inject(this);
        intent = getIntent();
        if (intent != null) {
            currentUser = (BaseUser) intent.getBundleExtra("bundle").getSerializable("currentUser");
            if (currentUser.getMyInfo() != null) {
                if (currentUser.getMyInfo().getMycollections() != null) {
                    ArrayList<String> collections = currentUser.getMyInfo().getMycollections();
                    BmobQuery<Post> query = new BmobQuery<Post>();
                    query.include("user");
                    query.addQueryKeys("content,updatedAt,paths,avatarPaht,username,user,objectId");
                    query.addWhereContainedIn("objectId", collections);
                    query.findObjects(this, new FindListener<Post>() {
                        @Override
                        public void onSuccess(List<Post> posts) {
                            recyclerView.setAdapter(new ItemAdapter(posts, MyCollections.this));
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.d(TAG, s + i);
                        }
                    });
                } else {
                    Toast.makeText(this, "您还没有收藏任何惠报", Toast.LENGTH_LONG).show();
                }
            }
        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_collections, menu);
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
}
