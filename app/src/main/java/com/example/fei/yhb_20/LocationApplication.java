package com.example.fei.yhb_20;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;


public class LocationApplication extends Application {
    private static final String TAG = "LocationApplication";
    public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	
	public TextView trigger;
    public Spinner position1,position2,position3;

    boolean flag = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
            if (!flag){
                mLocationClient.stop();
            }
			//Receive Location 
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
            }
            logMsg(location.getProvince(),location.getCity(),location.getDistrict());
            flag = true;
			Log.i("BaiduLocationApiDem", sb.toString());
		}


	}
	

	public void logMsg(String province,String city,String district) {

        // 在这里得到城市的信息,然后进行查询
        String [] array1 = new String[1];
        array1 [0] = province;
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,array1);
        position1.setAdapter(adapter1);
        position1.setSelection(0);
        Log.e(TAG,province);

        String [] array2 = new String[1];
        array2 [0] = city;
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,array2);
        position2.setAdapter(adapter2);
        position2.setSelection(0);
        Log.e(TAG,city);

        String [] array3 = new String[1];
        array3 [0] = district;
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,array3);
        position3.setAdapter(adapter3);
        position3.setSelection(0);
        Log.e(TAG,district);
    }

	
}
