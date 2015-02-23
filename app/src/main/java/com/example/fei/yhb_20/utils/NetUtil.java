package com.example.fei.yhb_20.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by fei on 1/24/15.
 */
public class NetUtil {

    private static final String TAG = "NetUtil";

    public static String getVersionName(Context context) throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        return packInfo.versionName;
    }


    //检查更新
    public static void checkForUpdate(final Context context) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://192.168.1.103:8080/yhb/release.json", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("version")== getVersionName(context)){
                                Toast.makeText(context, "noupdate", Toast.LENGTH_LONG).show();
                            }else {
                                update(response.getString("description"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    private void update(String msg) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(msg)
                                .setCancelable(false)
                                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //下载
                                        Toast.makeText(context,"下载",Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"更新失败",Toast.LENGTH_LONG).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }
}
