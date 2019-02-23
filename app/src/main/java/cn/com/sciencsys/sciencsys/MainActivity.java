package cn.com.sciencsys.sciencsys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.com.sciencsys.sciencsys.dedicated.DedicatedMainActivity;
import cn.com.sciencsys.sciencsys.initsystem.LogUtil;

//调试时候AppCom可以屏蔽broad
public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private static final String PermissionTAG = "PERMISSION";

    private boolean permissionIsGet = false;            //获取权限参数true为已获取
    private IntentFilter intentFilter;
    public static final int WIFI_SCAN_PERMISSION_CODE = 1;
    public static final int GPS_PERMISSION_CODE = 2;

    //ConnectAsyncTask mConnectAsyncTask = null;


    //WifiListAdapter mWifiListAdapter;                 //不需要获取wifi列表到适配器，只需要获取列表保存在List中

    /**
     * 处理信号量改变或者扫描结果改变的广播
     */
    //WifiAutoConnectManager mWifiAutoConnectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d("Activity","MainCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);    //设置竖屏模式
        //获取权限
        getLocationAndGPSPermission();
        //按键初始化UI
        initView();
        //初始化wifi工具
        //WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //mWifiAutoConnectManager = WifiAutoConnectManager.newInstance(wifiManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.common_button:
                Intent intent = new Intent(MainActivity.this, PopActivity.class);
                startActivity(intent);
                break;
            case R.id.dedicated_button:
                Intent intent2 = new Intent(MainActivity.this, DedicatedMainActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
    /*
    初始化按键
     */
    private void initView(){
        Button comButton = (Button) findViewById(R.id.common_button);
        Button dedButton = (Button) findViewById(R.id.dedicated_button);

        comButton.setOnClickListener(this);
        dedButton.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("Activity","MainResume");

    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("Activity","MainStart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("Activity","MainDestroy");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case WIFI_SCAN_PERMISSION_CODE:
                if (grantResults.length>0 && grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限需要手动连接采集器WIFI并关闭WIFI+自动连接功能！",Toast.LENGTH_LONG).show();
                    permissionIsGet = false;
                }
                else {
                    permissionIsGet = true;                    //权限已获取
                    LogUtil.d(PermissionTAG,"get permission");
                }
                break;
            default:break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != GPS_PERMISSION_CODE){
            Toast.makeText(this,"未能打开GPS，请手动打开后重试，或者手动连接采集器WIFI并关闭WIFI+自动连接功能！",Toast.LENGTH_LONG).show();
        }
    }

    /*
     * 检测GPS、位置权限
     */
    private boolean GPSIsOpen() {                                                              //检查GPS是否已经打开
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }
    private void getLocationAndGPSPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION)    //获取定位权限组
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE},
                        WIFI_SCAN_PERMISSION_CODE);
            }
            if (!GPSIsOpen()) {                                                                                   //没有打开GPS
                Toast.makeText(this, "连接采集器需要打开GPS", Toast.LENGTH_LONG).show();  //android6.0以上获取wifi列表需要获取定位信息，打开GPS
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_PERMISSION_CODE);
            }
        }
    }
}
