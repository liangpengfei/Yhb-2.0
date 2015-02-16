package com.example.fei.yhb_20.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fei on 2/15/15.
 */
public class MyUtils {
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
}
