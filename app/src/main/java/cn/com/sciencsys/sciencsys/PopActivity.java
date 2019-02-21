package cn.com.sciencsys.sciencsys;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.com.sciencsys.sciencsys.MPChartUtils.DynamicLineChartManager;
import cn.com.sciencsys.sciencsys.SysService.TcpService;
import cn.com.sciencsys.sciencsys.UImaker.SensorAdapter;
import cn.com.sciencsys.sciencsys.UImaker.UploadCommandListener;
import cn.com.sciencsys.sciencsys.initsystem.MessageSource;
import cn.com.sciencsys.sciencsys.initsystem.PublicMethod;
import cn.com.sciencsys.sciencsys.initsystem.Sensor;

import static java.lang.String.format;

//调试时候AppCom可以屏蔽broad
public class PopActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private List<Sensor> sensorList = new ArrayList<>();
    private SensorAdapter sensorAdapter;
    private Sensor itemSensor;
    //private int itemNumber = 0;         //获取的总的端口数量，为添加item的position做指标
    //private int sensorPort = 0;           //获取的端口号
    //private int sensorId = 0;         //获取的ID号
    //private int itemId = 0;         //Item的对应端口
    //private int shiftIndex = 0; //选择的档位（默认0为1档）
    //private int freqIndex = 5;  //选择的频率（默认5为10hz）
    private int maxTime = 10; //定时时间，默认10s
    private LineChart lineChart;
    private DynamicLineChartManager dynamicLineChartManager;
    private List<Float> list = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合
    private int k =0;
    private EditText timeText;
//--------------------------------------------------------------------------------------------
    private Messenger rMessenger = null;//activity发送消息的message（Server端的信使对象）
    private Messenger mMessenger = null;//activity接受消息的message

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MessageSource.MSG_TASK_TIME_TICK:               //显示倒计时（携带arg1：需要显示的时间）
                    int timeTick = msg.arg1;
                    timeText.setText(String.valueOf(timeTick));
                    break;
                case MessageSource.MSG_TASK_UPDATA_COMMAND_SUCCESS:               //显示倒计时（携带arg1：需要显示的时间）
                    int i = msg.arg1;
                    if (i == 1){
                        Toast.makeText(getApplicationContext(),"修改成功！",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"修改失败！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MessageSource.MSG_TASK_SOCKET_SUCCESS:

                    if (msg.arg1 == 1){
                        Toast.makeText(getApplicationContext(),"采集器连接成功！",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case MessageSource.MSG_TASK_UPDATA_ITEM:            //显示Item//读取端口传感器完成的回调返回参数：argg1：id号，arg2.port号，ojb：当前绑定的item号
                    final int sensorId = msg.arg1;
                    final int sensorPort = msg.arg2;
                    final int itemPort = (int) msg.obj;
                    final int type = PublicMethod.IdToType(sensorId);
                    itemSensor = new Sensor(sensorPort, sensorId, itemPort,0,type);
                    for (int position=0;position<sensorAdapter.getItemCount();position++){
                        if (sensorList.get(position).getPort() == sensorPort){
                            sensorAdapter.removeItem(position);
                        }
                    }
                    sensorAdapter.addItem(itemPort,itemSensor);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //-------------------------------------------------
                            //
                            //-------------------------------------------------

                            String sensorName = sensorList.get(itemPort).IdToName(sensorId);
                            names.add(sensorName);      //在集合中添加名称数据
                            colour.add(PublicMethod.randomColor());  //随机颜色
                            PopActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dynamicLineChartManager.initLineDataSet(names.get(itemPort),colour.get(itemPort));
                                }
                            });
                        }
                    }).start();

                    break;
                case MessageSource.MSG_TASK_READ_DATA:               //显示数据（携带arg1：更新的数据的端口号，// ，arg2：更新的item号，只关心，对应的item更新数据，不关心id）
                    int newSensorPort = msg.arg1;
                    int newItemPort = msg.arg2;
                    float yData = (float)msg.obj;      //保留3位小数
                    yData = PublicMethod.floatRemain(yData,3);

                    k++;
                    //System.out.println(" k "+System.currentTimeMillis()+" thread id "+Thread.currentThread().getId() + "Byte " + k);
                    sensorList.get(newItemPort).setData(yData);
                    sensorAdapter.notifyItemChanged(newItemPort,"payload");//更新对应item的数据

                    if (dynamicLineChartManager != null){

                        list.add(yData);
                        try {
                            dynamicLineChartManager.addMyEntry(yData,newItemPort);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        list.clear();
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
    });
//--------------------------------------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        initView();

        WifiManager wifiManager =(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = (ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff);
        //initSensor();
        //Toast.makeText(this,"dd",Toast.LENGTH_SHORT).show();
        /**
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        sensorAdapter = new SensorAdapter(this,sensorList);
        recyclerView.setAdapter(sensorAdapter);
        */

        setViews();             //
        initData();             //初始化rec
        /**
         * Item点击选择频率和档位
         */
        sensorAdapter.setUploadCommandListener(new UploadCommandListener() {
            @Override
            public void onUploadCommand(int dw, int pl, int port) {
                sendMessage(MessageSource.MSG_TASK_UPDATE_COMMAND,dw,pl,port);    //参数：上传设置命令，携带：arg1:档位，arg2：端口号（设置更改的对应端口号），obj：port
            }
        });
        //initMPChart();




    }

    /**
     * 初始化mpchart
     */
    private void initMPChart(){

        dynamicLineChartManager = new DynamicLineChartManager(lineChart);
        dynamicLineChartManager.initLineDataSet(names, colour); //边框初始化，需要在打开界面时打开图表
        dynamicLineChartManager.setYAxis(100,-100,10);
    }
    /*
    初始化按键
     */
    private void initView(){
        Button startButton = (Button) findViewById(R.id.start_button);
        Button stopButton = (Button) findViewById(R.id.stop_button);
        Button clearButton = (Button) findViewById(R.id.clear_button);
        Button deleteButton = (Button) findViewById(R.id.delete_button);

        timeText = (EditText) findViewById(R.id.timeText);
        //timeText.setText("id"+10);
        /**
         * 键入的定时时间，只有在开始以后才会上传定时时间
         *需要键入判断，不然会导致读取显示的值
         */

        timeText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event!= null && event.getAction() != KeyEvent.ACTION_DOWN){
                    return false;
                } else if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO
                        || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    //隐藏软键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputMethodManager.isActive()){
                        inputMethodManager.hideSoftInputFromWindow(PopActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    maxTime = Integer.parseInt(timeText.getText().toString()) ;
                    intent.putExtra("maxTime",maxTime);
                    startService(intent);
                    return true;
                }else return false;
            }
        });

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    /*
    初始化rec
     */
    private void setViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        lineChart = (LineChart) findViewById(R.id.chart);
    }
    private void initData() {
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(recyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //添加分隔线
        recyclerView.setAdapter(sensorAdapter = new SensorAdapter(this,sensorList));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            /**
                             * 删除所有存在的Item，接收到数据后在回调中创建item
                             * 删除的position都是0，是否是addItem的时候加的position都是0的原因
                             */
                            PopActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    long i =Thread.currentThread().getId();
                                    int size =sensorList.size();
                                    for (int position=0;position<size;position++){
                                        sensorAdapter.removeItem(0);
                                    }
                                }
                            });
                            long i =Thread.currentThread().getId();
                            /**  刷新Item数据
                             * 0,先清除数据
                             * 1、发送重置命令
                             * 2、发送查询端口命令
                             * 3、查询每个端口对应的传感器加入到item
                             */
                            clearMpchartData();
                            if (dynamicLineChartManager != null){
                                dynamicLineChartManager.clear();        //清除dataSets
                            }
                            sendMessage(MessageSource.MSG_TASK_RESET,0,0,0);    //参数：开始命令，携带：设置的定时时间
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
                new Handler().postDelayed(new Runnable() {          //n秒后执行run
                    @Override
                    public void run() {
                        //Sensor cgq = new Sensor(0,12,0.88f);
                        //sensorAdapter.addItem(0,cgq);

                        //tcpBinder.initServiceTaskStart();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });
    }



    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.start_button:
                if (sensorList != null) {
                    sendMessage(MessageSource.MSG_TASK_START, maxTime, 0, sensorList);    //参数：开始命令，携带：设置的定时时间obj :sensorlist
                }
                break;
            case R.id.stop_button:
                sendMessage(MessageSource.MSG_TASK_STOP,0,0,0);    //参数：开始命令

                break;
            case R.id.clear_button:
                sendMessage(MessageSource.MSG_TASK_CLEAR,0,0,0);    //参数:清屏，携带：设置的定时时间
                if (list != null) {
                    list.clear();
                }
                if (sensorList != null && sensorAdapter !=null) {
                    sensorList.get(0).setData(0);
                    sensorAdapter.notifyItemChanged(0, "payload");//更新对应item的数据
                    sensorList.get(1).setData(0);
                    sensorAdapter.notifyItemChanged(1, "payload");//更新对应item的数据
                }
                if (dynamicLineChartManager != null) {
                    dynamicLineChartManager.clearData();
                }

                break;
            case R.id.delete_button:
                sendMessage(MessageSource.MSG_TASK_DELETED,0,0,0);    //参数：开始命令，携带：设置的定时时间
                if (dynamicLineChartManager != null) {
                    dynamicLineChartManager.deleteMyEntry();
                }
                break;

            default:
                break;
        }
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
        intent = new Intent(this,TcpService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

        clearMpchartData();
        initMPChart();

    }
    void clearMpchartData(){
        if (names != null){
            names.clear();
        }
        if (colour != null){
            colour.clear();
        }
        if (list != null){
            list.clear();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println(" pause pop "+System.currentTimeMillis());
        //sendMessage(MessageSource.MSG_TASK_CONNEECT,0,0,0);   //发送暂停activity指令，由于退出activity不会马上销毁
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println(" destroy pop "+System.currentTimeMillis());
        unbindService(connection);

        clearMpchartData();
        if (dynamicLineChartManager != null){
            dynamicLineChartManager.clearAll();        //清除dataSets
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){

        }
        return super.onKeyDown(keyCode, event);
    }
}
