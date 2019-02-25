package cn.com.sciencsys.sciencsys.dedicated;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;

import cn.com.sciencsys.sciencsys.R;

public class De1Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);    //设置横屏模式
    }

}
