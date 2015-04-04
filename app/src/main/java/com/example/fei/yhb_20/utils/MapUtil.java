package com.example.fei.yhb_20.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.marshalchen.common.uimodule.cropimage.util.Log;

/**
 * 地图定位的工具类，以后再仔细完善，现在还是有一些问题
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/1/15.
 */
public class MapUtil {

    private static final String TAG = "MapUtil";
    static boolean flag = false;

    public static void getLocation(final Context context, final TextView position) {
        Log.e(TAG, "2");

        final LocationClient mLocationClient = new LocationClient(context.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setCoorType("gcj02");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                position.setText(location.getCity());

                //持久化写入地理数据，以后
                SharedPreferences settings = context.getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("province", location.getProvince());
                editor.putString("city", location.getCity());
                editor.putString("district", location.getDistrict());
                editor.putBoolean("ever", true);
                editor.apply();

                if (flag) {
                    mLocationClient.stop();
                }
            }
        });

        mLocationClient.start();
        flag = true;
    }


}
