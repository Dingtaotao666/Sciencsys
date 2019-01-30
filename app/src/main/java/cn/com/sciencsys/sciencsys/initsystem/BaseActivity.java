package cn.com.sciencsys.sciencsys.initsystem;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.SysService.WifiIntentService;
import cn.com.sciencsys.sciencsys.UImaker.UserLoadingDialog;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import static cn.com.sciencsys.sciencsys.initsystem.Constants.BroadcaseUtilsValue.BroadcastUtils;

public class BaseActivity extends AppCompatActivity {

    public static boolean todoState = false;                        //0:采集器断开状态，需要重新连接（已打开WIFI）步骤：::1：打开wifi，2：跳出Dialog连接网络提示，3、获取wifi列表，
                                                            //1：已获取wifi列表  --> 1、删除已保存密码，2、连接目标wifi

                                                            //2：已正确连接采集器  -->   intent 当前Activity

    public List<ScanResult> mScanResultList = new ArrayList<>();

    public UserLoadingDialog userLoadingDialog;

    WifiManager mWifiManager;
    WifiAutoConnectManager mWifiAutoConnectManager;             //定义在外，获取对象在应用处
    Intent intentService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.getInstance().addActivity(this);
        LogUtil.d("Activity","add" + getClass().getSimpleName());
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        */
        //设置屏幕方向
        if (getClass().getSimpleName().equals("MainActivity")){                    //获取当前的Activity
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);    //设置竖屏模式
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);    //设置横屏模式
        }
        userLoadingDialog = new UserLoadingDialog(this, R.style.MyDialogStyle);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiAutoConnectManager = new WifiAutoConnectManager(this);
        /**在broad中可以获取
        if (mWifiAutoConnectManager.getWifiInfo().getSSID().equals(Constants.WifiValue.SSID) == true) {             //开机已经连接正确的wifi
            //userLoadingDialog.CreateLoadingDialog(userLoadingDialog, "正在连接采集器...");
            Toast.makeText(this,"已连接采集器，可以开始试验!",Toast.LENGTH_LONG).show();
            todoState = true;
            userLoadingDialog.Dismiss(userLoadingDialog);
            intentService = new Intent(this,WifiIntentService.class);
            intentService.putExtra("TodoState",todoState);
            startService(intentService);

        }else {
            userLoadingDialog.CreateLoadingDialog(userLoadingDialog, "正在连接采集器...");         //开启软件没有连接任何wifi的情况
            todoState = false;
            userLoadingDialog.Dismiss(userLoadingDialog);
            intentService = new Intent(this,WifiIntentService.class);
            intentService.putExtra("TodoState",todoState);
            startService(intentService);
        }
        **/

    }

    private void initWifiSateBroadcastReceiver(){
        BroadcastManager.getInstance(this).addAction(Constants.BroadcaseUtilsValue.BroadcastUtils, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){                              // 扫描结果改表
                    if (mScanResultList != null) {
                        //Toast.makeText(context, "已完成扫描wifi列表...", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "找不到wifi信号,请检查采集器网络状态...", Toast.LENGTH_SHORT).show();
                    }
                }else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,0);         //第二个参数为得不到时的默认值
                    if (wifiState == WifiManager.WIFI_STATE_DISABLED){                                       //WIFI_STATE:disable不可用disabling正在关闭，enable可以使用wifi，enabling正在开启wifi
                        todoState = false;
                        userLoadingDialog.CreateLoadingDialog(userLoadingDialog,"WIFI已关闭,正在重试...");
                        //Toast.makeText(context,"WIFI已关闭,正在重试...",Toast.LENGTH_SHORT).show();

                        intentService = new Intent(context,WifiIntentService.class);
                        intentService.putExtra("TodoState",todoState);
                        startService(intentService);

                    }else if (wifiState == WifiManager.WIFI_STATE_ENABLING){
                        Toast.makeText(context,"WIFI正在打开...",Toast.LENGTH_SHORT).show();
                    }else if (wifiState == WifiManager.WIFI_STATE_ENABLED){
                        if (mWifiAutoConnectManager.getWifiInfo().getNetworkId() == -1){                            //开机未连接任何网络
                            todoState = false;
                            userLoadingDialog.CreateLoadingDialog(userLoadingDialog,"正在连接传感器...");
                            intentService = new Intent(context,WifiIntentService.class);
                            intentService.putExtra("TodoState",todoState);
                            startService(intentService);
                        }
                        //Toast.makeText(context,"WIFI已打开...",Toast.LENGTH_SHORT).show();
                    }
                }else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    NetworkInfo.DetailedState state = ((NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).getDetailedState();
                    setWifiState(state);
                }
            }
        });
    }
    /**
     * 显示wifi状态
     *
     * @param state
     */
    private void setWifiState(final NetworkInfo.DetailedState state) {
        if (state == NetworkInfo.DetailedState.AUTHENTICATING) {

        } else if (state == NetworkInfo.DetailedState.BLOCKED) {

        } else if (state == NetworkInfo.DetailedState.CONNECTED) {                                                  //这里是因为在android2.之后wifissid获取的是带双引号的
            if (mWifiAutoConnectManager.getWifiInfo().getSSID()!=null &&
                    (mWifiAutoConnectManager.getWifiInfo().getSSID().
                    equalsIgnoreCase("\"" + Constants.WifiValue.SSID + "\"")||
                    mWifiAutoConnectManager.getWifiInfo().getSSID().equalsIgnoreCase(Constants.WifiValue.SSID))) {
                todoState = true;
                userLoadingDialog.Dismiss(userLoadingDialog);
                //intentService = new Intent(this,WifiIntentService.class);
                //intentService.putExtra("TodoState",todoState);
                //startService(intentService);

            }
            else {
                userLoadingDialog.CreateLoadingDialog(userLoadingDialog,"采集器连接错误,正在重试...");
                todoState =false;
                intentService = new Intent(this,WifiIntentService.class);
                intentService.putExtra("TodoState",todoState);
                startService(intentService);

            }
        } else if (state == NetworkInfo.DetailedState.CONNECTING) {
            //mBuilder.title("正在连接采集器...");// 标题
            //Toast.makeText(this, "正在连接采集器...", Toast.LENGTH_SHORT).show();
        } else if (state == NetworkInfo.DetailedState.DISCONNECTED) {
            userLoadingDialog.CreateLoadingDialog(userLoadingDialog,"正在连接采集器，请稍后...");
            todoState =false;
            intentService = new Intent(this,WifiIntentService.class);
            intentService.putExtra("TodoState",todoState);
            startService(intentService);
        } else if (state == NetworkInfo.DetailedState.DISCONNECTING) {
        } else if (state == NetworkInfo.DetailedState.FAILED) {
            userLoadingDialog.CreateLoadingDialog(userLoadingDialog,"采集器连接失败，正在重试请稍后...");
            todoState =false;
            intentService = new Intent(this,WifiIntentService.class);
            intentService.putExtra("TodoState",todoState);
            startService(intentService);

            //Toast.makeText(this, "采集器连接失败，请检查采集器后重试...", Toast.LENGTH_SHORT).show();
        } else if (state == NetworkInfo.DetailedState.IDLE) {

        } else if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {

        } else if (state == NetworkInfo.DetailedState.SCANNING) {

        } else if (state == NetworkInfo.DetailedState.SUSPENDED) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化广播
        initWifiSateBroadcastReceiver();                                                 //初始化广播
        LogUtil.d("Activity","registerReceiver" + getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initWifiSateBroadcastReceiver();                                                 //初始化广播

    }

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastManager.getInstance(this).destroy(BroadcastUtils);             //注销广播
        LogUtil.d("Activity","unregisterReceiver" + getClass().getSimpleName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.getInstance().removeActivity(this);
        LogUtil.d("Activity","remove" + getClass().getSimpleName());
    }

}
