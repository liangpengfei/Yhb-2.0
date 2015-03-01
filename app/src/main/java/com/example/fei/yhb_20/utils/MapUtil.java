package com.example.fei.yhb_20.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.marshalchen.common.uimodule.cropimage.util.Log;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 3/1/15.
 */
public class MapUtil {

    private static final String TAG = "MapUtil";

    public static void getLocation(Context context){
        Log.e(TAG,"2");

        LocationClient mLocationClient= new LocationClient(context.getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
//                String [] array1 = new String[1];
//                array1 [0] = location.getProvince();
//                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,array1);
//                spinner1.setAdapter(adapter1);
//
//                String [] array2 = new String[1];
//                array2 [0] = location.getCity();
//                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,array2);
//                spinner1.setAdapter(adapter2);
//
//                String [] array3 = new String[1];
//                array3 [0] = location.getDistrict();
//                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,array3);
//                spinner1.setAdapter(adapter3);
                Log.e(TAG,location.getProvince()+location.getCity()+location.getDistrict());
            }
        });

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        option.setCoorType("gcj02");
        int span=1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

        mLocationClient.start();

        Log.e(TAG,"1");

    }

}
