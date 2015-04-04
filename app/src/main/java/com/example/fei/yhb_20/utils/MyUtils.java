package com.example.fei.yhb_20.utils;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fei.yhb_20.R;
import com.example.fei.yhb_20.bean.BaseUser;
import com.example.fei.yhb_20.bean.CommentItem;
import com.example.fei.yhb_20.bean.MyInfo;
import com.example.fei.yhb_20.bean.Post;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 杂七杂八的一些便捷的方法
 * Email luckyliangfei@gmail.com
 * Created by fei on 2/15/15.
 */
public class MyUtils {
    private static final String TAG = "MyUtils";
    private static DBManager dbm;
    private static SQLiteDatabase db;
    private static int[] imageIds = new int[107];

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
     * 显示进度条dialog
     * @param context
     * @param text
     */
    public static void showProgressDialog(Context context,String text){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog, null);
        TextView tv_text = (TextView) view.findViewById(R.id.tv_custom_dialog);
        tv_text.setText(text);
        final Dialog dialog = new Dialog(context, R.style.popupDialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 显示表情选择dialog
     * @param context
     * @param editText
     */
    public static void showFaceDialog(final Context context, final EditText editText){

        //显示表情选择
        final Dialog builder = new Dialog(context);

        /**
         * 生成一个表情对话框中的gridview
         * @return
         */
        final GridView view = new GridView(context);
        List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
        //生成107个表情的id，封装
        for(int i = 0; i < 107; i++){
            try {
                if(i<10){
                    Field field = R.drawable.class.getDeclaredField("f00" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }else if(i<100){
                    Field field = R.drawable.class.getDeclaredField("f0" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }else{
                    Field field = R.drawable.class.getDeclaredField("f" + i);
                    int resourceId = Integer.parseInt(field.get(null).toString());
                    imageIds[i] = resourceId;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Map<String,Object> listItem = new HashMap<String,Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
        view.setAdapter(simpleAdapter);
        view.setNumColumns(6);
        view.setBackgroundColor(Color.rgb(214, 211, 214));
        view.setHorizontalSpacing(1);
        view.setVerticalSpacing(1);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER);
        GridView gridView = view;
        builder.setContentView(gridView);
        builder.setTitle("默认表情");
        builder.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bitmap bitmap = null;
                bitmap = BitmapFactory.decodeResource(context.getResources(), imageIds[arg2 % imageIds.length]);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                String str = null;
                if(arg2<10){
                    str = "f00"+arg2;
                }else if(arg2<100){
                    str = "f0"+arg2;
                }else{
                    str = "f"+arg2;
                }
                SpannableString spannableString = new SpannableString(str);
                spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //添加成一个字符串
                editText.append(spannableString);
                builder.dismiss();
            }
        });
    }

    /**
     * 主页的弹出菜单
     * @param context
     */
    public static void showPopupMenu(final Context context, final String objectId, final String userId) {
        View menuView = View.inflate(context,R.layout.popupwindow,null);
        final Dialog menuDialog = new Dialog(context,R.style.popupDialog);
        menuDialog.setContentView(menuView);

        final LinearLayout collect, unfollow, block, report;
        collect = (LinearLayout) menuView.findViewById(R.id.collect);
        unfollow = (LinearLayout) menuView.findViewById(R.id.unfollow);
        block = (LinearLayout) menuView.findViewById(R.id.block);
        report = (LinearLayout) menuView.findViewById(R.id.report);

        final BaseUser baseUser = BmobUser.getCurrentUser(context, BaseUser.class);


        /**
         * 为弹出菜单写定义事件
         */
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //收藏
                MyInfo myInfo;
                if (baseUser.getMyInfo() != null) {
                    myInfo = baseUser.getMyInfo();
                } else {
                    myInfo = new MyInfo();
                }
                ArrayList<String> myCollections;
                if (myInfo.getMycollections()!=null){
                    myCollections = myInfo.getMycollections();
                }else{
                    myCollections = new ArrayList<String>();
                }
                if (!myCollections.contains(objectId)){
                    myCollections.add(objectId);
                    myInfo.setMycollections(myCollections);
                    baseUser.setMyInfo(myInfo);
                    baseUser.update(context,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context,"收藏成功",Toast.LENGTH_LONG).show();
                            menuDialog.dismiss();
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            Log.e(TAG,s+i);
                        }
                    });
                }else{
                    Toast.makeText(context,"已经收藏过了",Toast.LENGTH_LONG).show();
                    menuDialog.dismiss();
                }

            }
        });
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消关注,建议去掉
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //屏蔽，去实现
                MyInfo myInfo;
                if (baseUser.getMyInfo() != null) {
                    myInfo = baseUser.getMyInfo();
                } else {
                    myInfo = new MyInfo();
                }

                ArrayList<String> blockers;
                if (myInfo.getBlockers() != null) {
                    blockers = myInfo.getBlockers();
                } else {
                    blockers = new ArrayList<String>();
                }
                if (blockers.contains(userId)) {
                    Toast.makeText(context, "您已经屏蔽此人", Toast.LENGTH_LONG).show();
                    menuDialog.dismiss();
                } else if (userId.equals(baseUser.getObjectId())) {
                    Toast.makeText(context, "您不能屏蔽您自己", Toast.LENGTH_LONG).show();
                    menuDialog.dismiss();
                } else {
                    blockers.add(userId);
                    myInfo.setBlockers(blockers);
                    baseUser.setMyInfo(myInfo);
                    baseUser.update(context, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "成功屏蔽此人", Toast.LENGTH_LONG).show();
                            menuDialog.dismiss();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.d(TAG, s + i);
                        }
                    });
                }


            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //举报
            }
        });
        menuDialog.show();
    }

    /**
     * 发送评论,已经更改问题
     * @param post
     * @param comment
     * @param context
     */

    public static void commentSend(final Post post,EditText comment, final Context context){
        if (comment.getText()==null || comment.getText().toString().equals("")){
            Toast.makeText(context,"没有输入内容",Toast.LENGTH_LONG).show();
        }else {
            if (post!=null){
                final ArrayList<Integer> numberFooter = post.getNumberFooter();

                final ArrayList<CommentItem> commentItems = post.getCommentItems();
                CommentItem commentItem = new CommentItem();

                commentItem.setComment(comment.getText().toString());
                commentItem.setObjectId(BmobUser.getCurrentUser(context).getObjectId());
                commentItem.setName(post.getUser().getUsername());
                commentItems.add(commentItem);
                post.setCommentItems(commentItems);

                post.update(context,new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG,commentItems.toString());

                        numberFooter.set(3, numberFooter.get(3) + 1);
                        post.setNumberFooter(numberFooter);
                        post.update(context,new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG,"评论成功，加一");
                                Toast.makeText(context, "评论成功", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.e(TAG,"评论失败"+s);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.e(TAG,"失败评论"+s+i);
                    }
                } );
            }else {
                Toast.makeText(context,"没有网络链接，请检查网络",Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * 处理footer上的相关操作，包括喜欢/享受过/没有帮助
     * @param footerBoolean
     * @param index
     * @param image
     * @param textView
     * @param numberFooter
     * @param aCache
     * @param post
     * @param context
     * @param objectId
     */
    public static void footerCommand(byte[] footerBoolean, final int index, final ImageView image, final TextView textView, final ArrayList<Integer> numberFooter,ACache aCache,Post post, final Context context,String objectId){
        Log.e(TAG, String.valueOf(index));
        int [] resources = {R.drawable.icon_heart,R.drawable.icon_dislike};
        int [] resourcesPressed = {R.drawable.icon_heart_pressed,R.drawable.icon_dislike_pressed};
        if (NetUtil.isNetConnected(context)){
            if (footerBoolean[index]==0){
                image.setImageResource(resources[index]);

                footerBoolean[index]=1;
                numberFooter.set(index, numberFooter.get(index) - 1);
                aCache.put(post.getObjectId() + "footerBoolean", footerBoolean);
                post.setNumberFooter(numberFooter);
                post.update(context, objectId, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "成功", Toast.LENGTH_LONG).show();
                        textView.setText(String.valueOf(numberFooter.get(index)));
                    }
                    @Override
                    public void onFailure(int code, String msg) {
                        Log.e(TAG,"失败");
                    }
                });
            }else{
                image.setImageResource(resourcesPressed[index]);

                footerBoolean[index]=0;
                numberFooter.set(index,numberFooter.get(index)+1);
                post.setNumberFooter(numberFooter);
                aCache.put(post.getObjectId()+"footerBoolean",footerBoolean);
                post.update(context, objectId, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"成功",Toast.LENGTH_LONG).show();
                        textView.setText(String.valueOf(numberFooter.get(index)));
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        Log.e(TAG,"失败");
                    }
                });
            }
        }
        else{
            Toast.makeText(context,"好像没网咯",Toast.LENGTH_LONG).show();
        }

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * 将从网上下载到的bean格式化为适合自定义的textView的settext方法
     * 如果bean中的某些属性是空的，则设置这些属性为“”，以防止空指针异常
     * @param c 是一个类
     * @param bean  想要格式化的对象
     * @return
     */
    public static Object formatObjectforTextView(Class c,Object bean){

        Object object = null;
        try {
            object = Class.forName(c.getName()).newInstance();
            Class<?> obj = object.getClass();
            Field[] fields = obj.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (fields[i].get(bean)==null){
                    fields[i].set(object,"去填写");
                }else{
                    fields[i].set(object,fields[i].get(bean));
                }
            }
            return object;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从byte对象中获取bitmap
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    /**
     * 从输入流中获取byte
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    public static Bitmap getImageFromWeb(String Url){
        try {
            URL url = new URL(Url);
            String responseCode = url.openConnection().getHeaderField(0);
            if (!responseCode.contains("200"))
                try {
                    throw new Exception("图片文件不存在或路径错误，错误代码：" + responseCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            try {
                throw new Exception(e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

}
