package com.example.fei.yhb_20.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingActivity extends ActionBarActivity implements View.OnClickListener {

    @InjectView(R.id.rl_setting_gender)RelativeLayout rlGender;
    @InjectView(R.id.rl_setting_hometown)RelativeLayout rlHometown;
    @InjectView(R.id.rl_setting_certification)RelativeLayout rlCertification;

    @InjectView(R.id.tv_setting_gender)TextView mGender;
    @InjectView(R.id.tv_setting_hometown)TextView mHometown;
    @InjectView(R.id.tv_setting_certification)TextView mCertification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initEvents();
    }

    private void initEvents() {
        rlGender.setOnClickListener(this);
        rlCertification.setOnClickListener(this);
        rlHometown.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_setting_gender:
                new AlertDialog.Builder(this)
                        .setTitle("性别")
                        .setSingleChoiceItems(new String[]{"男士","女士"},0,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(SettingActivity.this,which,Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                break;
            case R.id.rl_setting_hometown:
                break;
            case R.id.rl_setting_certification:
                break;
            default:
                break;


        }
    }
}
