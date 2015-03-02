package com.example.fei.yhb_20.utils;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/15/15.
 */
public class MyUtils {
    private static DBManager dbm;
    private static SQLiteDatabase db;
    public static boolean isEmail(String email){
        return match("\\w+@(\\w+.)+[a-z]{2,3}",email);
    }
    public static boolean passwordNumberLength(String value){

        return match("\\w{6,18}",value);
    }
    /**
     * @param regex 正则表达式字符串
     * @param str 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否有SD卡
     *
     * @return 有SD卡返回true，否则false
     */
    public static boolean hasSDCard() {
        // 获取外部存储的状态
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // 有SD卡
            return true;
        }
        return false;
    }

    /**
     * 初始化存储图片的文件
     *
     * @return 初始化成功返回true，否则false
     */
    public static boolean initImageFile(File imageFile) {
        // 有SD卡时才初始化文件
        if (hasSDCard()) {
            // 构造存储图片的文件的路径，文件名为当前时间
            String filePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/"
                    + System.currentTimeMillis()
                    + ".png";
            imageFile = new File(filePath);
            if (!imageFile.exists()) {// 如果文件不存在，就创建文件
                try {
                    imageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    //TODO 下面是初始化定位的操作，不知道怎么实现了，暂时先这样

//    public static void initSpinner(Context context,String province,String city,String district,Spinner position1 ){
//        DBManager dbm = new DBManager(context);
//        dbm.openDatabase();
//        SQLiteDatabase db = dbm.getDatabase();
//        List<MyListItem> list = new ArrayList<MyListItem>();
//        int p = 0;
//        try {
//
//            String sql = "select * from province";
//            Cursor cursor = db.rawQuery(sql,null);
//            cursor.moveToFirst();
//            while (!cursor.isLast()){
//                String code=cursor.getString(cursor.getColumnIndex("code"));
//                byte bytes[]=cursor.getBlob(2);
//                String name=new String(bytes,"gbk");
//                MyListItem myListItem=new MyListItem();
//                myListItem.setName(name);
//                myListItem.setPcode(code);
//                list.add(myListItem);
//                cursor.moveToNext();
//            }
//            String code=cursor.getString(cursor.getColumnIndex("code"));
//            byte bytes[]=cursor.getBlob(2);
//            String name=new String(bytes,"gbk");
//            MyListItem myListItem=new MyListItem();
//            myListItem.setName(name);
//            myListItem.setPcode(code);
//            list.add(myListItem);
//
//        } catch (Exception e) {
//        }
//        dbm.closeDatabase();
//        db.close();
//
//        MyAdapter myAdapter = new MyAdapter(context,list);
//        position1.setAdapter(myAdapter);
//        position1.setSelection();
//        position1.setOnItemSelectedListener(new SpinnerOnSelectedListener1());
//    }

}
