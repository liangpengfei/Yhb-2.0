package com.example.fei.yhb_20.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fei.yhb_20.bean.MyListItem;

public class MyAdapter extends BaseAdapter {
	
	private Context context;
	private List<MyListItem> myList;

	
	public MyAdapter(Context context, List<MyListItem> myList) { 
		this.context = context; 
		this.myList = myList;
	}
	
	public int getCount() {
		return myList.size(); 
	} 
	public Object getItem(int position) {
		return myList.get(position);
	} 
	public long getItemId(int position) {
		return position;
	} 
	
	public View getView(int position, View convertView, ViewGroup parent)
	{ 
		MyListItem myListItem = myList.get(position); 
		return new MyAdapterView(this.context, myListItem ); 
	}

	class MyAdapterView extends LinearLayout { 
		public static final String LOG_TAG = "MyAdapterView";
		
		public MyAdapterView(Context context, MyListItem myListItem ) { 
		super(context);
		this.setOrientation(HORIZONTAL); 
		
		LayoutParams params = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		params.setMargins(1, 1, 1, 1); 
		
		TextView name = new TextView( context ); 
		name.setText( myListItem.getName() ); 
		addView( name, params); 
		
		LayoutParams params2 = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
		params2.setMargins(1, 1, 1, 1); 
		
		TextView pcode = new TextView(context); 
		pcode.setText(myListItem.getPcode()); 
		addView( pcode, params2); 
		pcode.setVisibility(GONE);

		}		 

		}

}