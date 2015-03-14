package com.example.fei.yhb_20.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.fei.yhb_20.R;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 杂七杂八的一些便捷的方法
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/15/15.
 */
public class MyUtils {
    private static final String TAG = "MyUtils";
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

    public static String timeLogic(Date past, final Context context){

        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.DAY_OF_MONTH);
        long now = calendar.getTimeInMillis();
        calendar.setTime(past);
        long lDate = calendar.getTimeInMillis();
        long time = (now - lDate)/1000;

        Log.e(TAG, String.valueOf(time) + "b");
        Log.e(TAG,String.valueOf(now)+"c");
        Log.e(TAG,String.valueOf(lDate)+"d");

        StringBuilder sb = new StringBuilder();
        if (time > 0 && time < 60) { // 1小时内
            return sb.append(time).append("秒前").toString();
        } else if (time > 60 && time < 3600) {
            return sb.append(time / 60).append("分钟前").toString();
        } else if (time >= 3600 && time < 3600 * 24) {
            return sb.append(time / 3600).append("小时前").toString();
        }else if (time >= 3600 * 24 && time < 3600 * 48) {
            return sb.append("昨天").toString();
        }else if (time >= 3600 * 48 && time < 3600 * 72) {
            return sb.append("前天").toString();
        }else if (time >= 3600 * 72) {
            return sb.append("3天前").toString();
        }else{
            return sb.append("多天之前").toString();
        }
    }

    /**
     * 上传dialog
     * @param context
     * @param text
     */
    public static void showDialog(Context context,String text){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        TextView tv_text = (TextView) view.findViewById(R.id.tv_custom_dialog);
        tv_text.setText(text);
        final Dialog dialog = new Dialog(context, R.style.popupDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
