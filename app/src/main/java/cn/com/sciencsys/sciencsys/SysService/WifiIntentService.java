package cn.com.sciencsys.sciencsys.SysService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;

import androidx.annotation.Nullable;
import cn.com.sciencsys.sciencsys.initsystem.Constants;
import cn.com.sciencsys.sciencsys.initsystem.WifiAutoConnectManager;

public class WifiIntentService extends IntentService {



    public WifiIntentService(){
        super("WifiIntentService");
    }

    //WifiAutoConnectManager wifiAutoConnectManager = (WifiAutoConnectManager) newInstance((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int wifiState = 0;
        boolean State = intent.getBooleanExtra("TodoState",false);
        //Toast.makeText(,State,Toast.LENGTH_LONG).show();                  //Toast只能在主线程的UI中运行，要在service中Toast需要Handler
        WifiManager mWifiManager = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiAutoConnectManager mWifiAutoConnectManager = new WifiAutoConnectManager(this);
        WifiConfiguration tempConfig;

        if (State == false){
            //1、先打开WIFI
            mWifiAutoConnectManager.openWifi();
            // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
            // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
            while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                try {
                    // 为了避免程序一直while循环，让它睡个100毫秒检测……
                    Thread.sleep(100);

                } catch (InterruptedException ie) {
                    Log.e("wifidemo", ie.toString());
                }
            }

            //2、扫描wifi列表
            mWifiAutoConnectManager.scanWifi();
            tempConfig = mWifiAutoConnectManager.getExitsWifiConfig(Constants.WifiValue.SSID);
            /**
             * 禁用配置列表的WIFI，不然会使自动连接最优信号
             */
            for (WifiConfiguration wifiConfiguration: mWifiManager.getConfiguredNetworks()) {
                if (wifiConfiguration.networkId != tempConfig.networkId){
                    mWifiManager.disableNetwork(wifiConfiguration.networkId);
                }else {
                    mWifiManager.enableNetwork(tempConfig.networkId,true);
                }
            }
            //3、连接目标wifi
            if (tempConfig != null){
                while (mWifiManager.enableNetwork(tempConfig.networkId,true) == true);          //使能目标wifi
                mWifiAutoConnectManager.addNetWork(Constants.WifiValue.SSID, Constants.WifiValue.Password, mWifiAutoConnectManager.WIFI_CIPHER_WAP);

            }
            else {
                mWifiAutoConnectManager.addNetWork(Constants.WifiValue.SSID, Constants.WifiValue.Password, mWifiAutoConnectManager.WIFI_CIPHER_WAP);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
