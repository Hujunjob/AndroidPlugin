package com.hiscene.drone;

import android.util.Log;

/**
 * Created by hujun on 2019-06-18.
 */

public class Log4aUtil {
    public static void d(String tag,String format,Object...args){
        Log.d(tag,String.format(format,args));
    }

    public static void e(String tag,String format,Object...args){
        Log.e(tag,String.format(format,args));
    }
}
