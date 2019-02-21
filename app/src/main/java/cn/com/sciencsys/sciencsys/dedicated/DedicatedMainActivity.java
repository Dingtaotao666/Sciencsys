package cn.com.sciencsys.sciencsys.dedicated;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.com.sciencsys.sciencsys.PopActivity;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.SysService.TcpService;
import cn.com.sciencsys.sciencsys.UImaker.LabAdapter;
import cn.com.sciencsys.sciencsys.initsystem.BaseActivity;
import cn.com.sciencsys.sciencsys.initsystem.LabConstants;
import cn.com.sciencsys.sciencsys.initsystem.Laboratory;
import cn.com.sciencsys.sciencsys.initsystem.MessageSource;
import cn.com.sciencsys.sciencsys.initsystem.PublicMethod;
import cn.com.sciencsys.sciencsys.initsystem.Sensor;

public class DedicatedMainActivity extends AppCompatActivity {
    private boolean filtration = false;     //是否过滤

    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private List<Laboratory> laboratoryList = new ArrayList<>();
    private List<Integer> labSensorList = new ArrayList<>();
    private LabAdapter adapter;
    private int labStytle = 0;   //0:物理 1：化学 2：生物
    private Messenger rMessenger = null;//activity发送消息的message（Server端的信使对象）
    private Messenger mMessenger = null;//activity接受消息的message

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MessageSource.MSG_TASK_SOCKET_SUCCESS:

                    if (msg.arg1 == 1){
                        Toast.makeText(getApplicationContext(),"采集器连接成功！",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MessageSource.MSG_TASK_UPDATA_ITEM:            //显示Item//读取端口传感器完成的回调返回参数：argg1：id号，arg2.port号，ojb：当前绑定的item号
                    int sensorId = msg.arg1;
                    int sensorPort = msg.arg2;
                    labSensorList.add(sensorId);           //把获取到的传感器ID放进数组，在过滤时进行比较
                    break;
                default:
                    break;
            }

            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dedicated_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_physics);        //默认选项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_physics:
                        labStytle = 0;
                        if (filtration){        //如果是过滤选项
                            initFiltration();
                        }else {
                            laboratoryList.clear();
                            Collections.addAll(laboratoryList, LabConstants.physicsLaboratoriesList);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.nav_chemistry:
                        labStytle = 1;
                        if (filtration){        //如果是过滤选项
                            initFiltration();
                        }else {
                            laboratoryList.clear();
                            Collections.addAll(laboratoryList, LabConstants.chemistryLaboratoriesList);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.nav_biology:
                        labStytle = 2;
                        if (filtration){        //如果是过滤选项
                            initFiltration();
                        }else {
                            laboratoryList.clear();
                            Collections.addAll(laboratoryList, LabConstants.biologyLaboratoriesList);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    default:labStytle = 0;laboratoryList.clear();break;
                }

                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        initLaboratory();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lab_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new LabAdapter(this,laboratoryList);
        recyclerView.setAdapter(adapter);
        /**
         * 下拉刷新
         */
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.lab_recyclerView_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSwipeRefresh();
            }
        });
        /**
         * 单选选中
         * 选中过滤则只显示能做实验的实验
         */
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radioButton:          //过滤选项
                        filtration = true;
                        initFiltration();
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.radioButton2:         //不过滤选项
                        filtration = false;
                        initLaboratory();
                        adapter.notifyDataSetChanged();
                        break;
                        default:break;
                }
            }
        });
    }
    private void initFiltration(){
        laboratoryList.clear();
        //没有传感器时默认清空列表
        if (labStytle == 0){
            int num =0;
            for (int i = 0;i< LabConstants.physicsLaboratoriesList.length;i++){
                int labList[] = LabConstants.physicsLaboratoriesList[i].getSensorId();
                for (int j = 0;j<labList.length;j++){
                    if (labSensorList.contains(labList[j])){
                        num ++;
                    }
                }
                if (num == labList.length){
                    laboratoryList.add(LabConstants.physicsLaboratoriesList[i]);
                }
            }
        }else if (labStytle == 1){
            int num =0;
            for (int i = 0;i< LabConstants.chemistryLaboratoriesList.length;i++){
                int labList[] = LabConstants.chemistryLaboratoriesList[i].getSensorId();
                for (int j = 0;j<labList.length;j++){
                    if (labSensorList.contains(labList[j])){
                        num ++;
                    }
                }
                if (num == labList.length){
                    laboratoryList.add(LabConstants.chemistryLaboratoriesList[i]);
                }
            }
        }else if (labStytle == 2){
            int num =0;
            for (int i = 0;i< LabConstants.biologyLaboratoriesList.length;i++){
                int labList[] = LabConstants.biologyLaboratoriesList[i].getSensorId();
                for (int j = 0;j<labList.length;j++){
                    if (labSensorList.contains(labList[j])){
                        num ++;
                    }
                }
                if (num == labList.length){
                    laboratoryList.add(LabConstants.biologyLaboratoriesList[i]);
                }
            }
        }
    }
    private void initLaboratory(){
        laboratoryList.clear();
        /**用下面的替换不会出警告
        for (int i = 0 ;i< LabConstants.physicsLaboratoriesList.length;i++) {
            laboratoryList.add(LabConstants.physicsLaboratoriesList[i]);
        }
         */
        switch (labStytle){
            case 0:
                Collections.addAll(laboratoryList,LabConstants.physicsLaboratoriesList);
                break;
            case 1:
                Collections.addAll(laboratoryList,LabConstants.chemistryLaboratoriesList);
                break;
            case 2:;
                Collections.addAll(laboratoryList,LabConstants.biologyLaboratoriesList);
                break;
                default:break;
        }
    }
    /**
     * 刷新列表
     */
    private void setSwipeRefresh(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (filtration){
                            initFiltration();
                        }else {
                            initLaboratory();
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    /**
     arg1 int类型 用来存放整型数据  
     arg2 int类型 用来存放整型数据  
     what int类型 用来保存消息标示  
     obj Object类型 是Object类型的任意对象  
     replyTo Messager类型 用来指定此Message发送到何处的可选Message对象 
     */
    private void sendMessage(int what, int arg1, int arg2,Object obj ) {
        Message msg = Message.obtain(null, what, arg1, arg2, obj);
        msg.replyTo = mMessenger;
        try {
            rMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rMessenger = new Messenger(service);//get the object of remote service
            mMessenger = new Messenger(mHandler);//initial the object of local service
            sendMessage(MessageSource.MSG_TASK_CONNEECT,0,0,0);   //连接后需要发送一次连接指令不然无法在service中获取replay
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rMessenger = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, TcpService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
        labSensorList.clear();          //重启服务需要清空传感器列表
    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println(" pause de "+System.currentTimeMillis());
        //sendMessage(MessageSource.MSG_TASK_CONNEECT,0,0,0);   //发送暂停activity指令，由于退出activity不会马上销毁
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println(" destroy de "+System.currentTimeMillis());
        unbindService(connection);
        labSensorList.clear();
    }

}
