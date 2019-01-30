package cn.com.sciencsys.sciencsys.initsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtils {
    public static IntentUtils instance;

    public static IntentUtils getInstence() {
        if (null == instance) {
            instance = new IntentUtils();
        }
        return instance;
    }

    private IntentUtils() {

    }

    /**
     * 不带参数的跳转
     *
     * @param fromContext
     * @param cls
     *            泛型
     */
    public void intent(Context fromContext, Class<?> cls) {
        Intent intent = new Intent(fromContext, cls);
        fromContext.startActivity(intent);
    }

    /**
     * 带参数的跳转
     *
     * @param fromContext
     * @param cls
     *            泛型
     */
    public void intent(Context fromContext, Class<?> cls,Bundle bb) {
        Intent intent = new Intent(fromContext, cls);
        intent.putExtras(bb);
        fromContext.startActivity(intent);
    }
    /**
     * 封装 startActivityForResult 无带参数传�? </br>
     * Same as calling
     * {@link #startActivityForResult(Activity, Class, int, Bundle)}
     *
     * @param fromClass
     * @param toClass
     * @param requestCode
     */
    public void startActivityForResult(Activity fromClass, Class<?> toClass, int requestCode) {
        startActivityForResult(fromClass, toClass, requestCode, null);
    }

    /**
     * 封装 startActivityForResult 带参数传�?
     *
     * @param fromClass
     * @param toClass
     * @param requestCode
     * @param data
     *            参数传�?
     */
    public void startActivityForResult(Activity fromClass, Class<?> toClass, int requestCode, Bundle data) {
        Intent intent = new Intent();
        intent.setClass(fromClass, toClass);
        if (null != data) {
            intent.putExtras(data);
        }
        fromClass.startActivityForResult(intent, requestCode);
    }

    /**
     * 接收参数然后在返回数值
     * @param fromContext 当前的activity
     * @param bb
     * @param RESULT_OK
     */
    public void setResult(Activity fromContext,Bundle bb,int RESULT_OK){
        Intent intent=new Intent();
        intent.putExtras(bb);
        fromContext.setResult(RESULT_OK, intent);
        fromContext.finish();
    }
}
//Intent intent = new Intent();  
//      //执行动作  
//     intent.setAction(Intent.ACTION_VIEW);  
//     //执行的数据类型  
//     intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive"); //安裝打開apk
//	    intent.setDataAndType(uri,"*/*");  //打开所有文件
//	    intent.setDataAndType(uri, "video/*");  //打开video
//	    intent.setDataAndType(uri, "audio/*"); //打开audio
//	    intent.setDataAndType(uri2, "text/html");//打开h5界面
//	    intent.setDataAndType(uri, "image/*"); //打开image文件
//	    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");  //打开ppt文件
//	    intent.setDataAndType(uri, "application/vnd.ms-excel");  //打开excel
//	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);intent.setDataAndType(uri, "application/msword");  //打開word
//	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);intent.setDataAndType(uri, "application/x-chm"); //打開chm
//	    intent.setDataAndType(uri1, "text/plain");  //打开文本文件
//	    intent.setDataAndType(uri, "application/pdf");  //打开pdf
//           startActivity(intent );
