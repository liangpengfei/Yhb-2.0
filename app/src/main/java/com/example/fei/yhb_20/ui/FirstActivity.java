package com.example.fei.yhb_20.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.utils.GV;


public class FirstActivity extends ActionBarActivity implements View.OnClickListener{

    private Button person_regist,merchant_regist;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        initViews();
        initEvents();
    }

    private void initEvents() {
        person_regist.setOnClickListener(this);
        merchant_regist.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    private void initViews() {
        person_regist = (Button) findViewById(R.id.bt_first_person);
        merchant_regist = (Button) findViewById(R.id.bt_first_merchant);
        login = (TextView) findViewById(R.id.tv_first_login);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
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
            case R.id.bt_first_merchant:
                Intent merchantintent = new Intent(this,RegistActivity.class);
                merchantintent.putExtra("role",GV.MERCHANT);
                startActivity(merchantintent);
//                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.bt_first_person:
                Intent person = new Intent(this,RegistActivity.class);
                person.putExtra("role", GV.PERSON);
                startActivity(person);
//                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
//            case R.id.tv_first_login:
//                Intent intent = new Intent(this,LoginActivity.class);
//                startActivity(intent);
//                break;
            default:
                break;
        }
    }
}
