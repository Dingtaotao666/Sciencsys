package cn.com.sciencsys.sciencsys.SysService;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.sciencsys.sciencsys.initsystem.CreateTcpServer;
import cn.com.sciencsys.sciencsys.initsystem.LogUtil;
import cn.com.sciencsys.sciencsys.initsystem.MessageSource;
import cn.com.sciencsys.sciencsys.initsystem.PublicMethod;
import cn.com.sciencsys.sciencsys.initsystem.SendCmdReadTask;
import cn.com.sciencsys.sciencsys.initsystem.SendCmdResetTask;
import cn.com.sciencsys.sciencsys.initsystem.SendCmdStartTask;
import cn.com.sciencsys.sciencsys.initsystem.SendCmdStopTask;
import cn.com.sciencsys.sciencsys.initsystem.SendCmdUpdataTask;
import cn.com.sciencsys.sciencsys.initsystem.Sensor;
import cn.com.sciencsys.sciencsys.initsystem.SgetAllTask;
import cn.com.sciencsys.sciencsys.initsystem.SgetPortQueryTask;
import cn.com.sciencsys.sciencsys.initsystem.SgetTask;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;


public class TcpService extends Service {

    private SgetPortQueryTask portQueryTask;
    private SendCmdStopTask sendCmdStopTask;
   // private SendCmdStartTask sendCmdStartTask;
    private SendCmdReadTask sendCmdReadTask;
    private SgetAllTask sgetAllTask;
    private SendCmdUpdataTask sendCmdUpdataTask;
    private CreateTcpServer createTcpServer;
    private SendCmdResetTask sendCmdResetTask;
    private List<Sensor> mSensorList;
    private int taskState = 0;
    //------------------------------定时器相关
    private Timer mTimer = null;
    private Timer mTimer2 = null;
    private TimerTask mTimerTask = null;
    private TimerTask mTimerTask2 = null;
    private int timeTick = 10;  //默认10s
    private static int count = 0;
    private boolean isStop = true;
    private boolean readChannel= false;
    private  int nowChannel = 0;
    private  int itemChannel=0;
    private  int DATANU=0;
    //-----------------------------
    private int pl = 5;                     //默认频率10hz
    //private int dw = 0;                   //默认档位0

    //-----------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------
    //private SgetTask sgetTask;
    private Socket socket;
    /*
    private SgetTask.SgetListener sgetListener = new SgetTask.SgetListener() {
        @Override
        public void onShow(int port, int readItem, float data) {
            sendMessage(MessageSource.MSG_TASK_READ_DATA, port, readItem,data);    //显示数据（携带arg1：更新的数据的端口号，
            DATANU ++;                                                                        // ，arg2：更新的item号，只关心，对应的item更新数据，不关心id）

        }
    };
    */
    private SgetAllTask.SgetAllListener sgetAllListener = new SgetAllTask.SgetAllListener() {
        @Override
        public void onShow(int port, int readItem, float data) {
            sendMessage(MessageSource.MSG_TASK_READ_DATA, port, readItem,data);    //显示数据（携带arg1：更新的数据的端口号，
            DATANU ++;                                                                        // ，arg2：更新的item号，只关心，对应的item更新数据，不关心id）

        }
    };

    private CreateTcpServer.CreateListener createListener = new CreateTcpServer.CreateListener() {
        @Override
        public void onFinish(int i) {
            //sendMessage(MessageSource.MSG_TASK_SOCKET_SUCCESS,i,0,0);           //回传是否创建成功SOCKET会报空handle
        }
    };
    private SgetPortQueryTask.SgetPortQueryListener sgetPortQueryListener = new SgetPortQueryTask.SgetPortQueryListener() {
        @Override
        public void onPortNumberRead(int port, int portNumber) {            //获取2个参数，第一个参数：端口号 11111111 第二个参数，总共有多少设备在线
            nowChannel = port;
            sendDemand();                           //接收到端口号后查询对应的端口设备
        }
    };
    private SendCmdReadTask.SendCmdReadListener sendCmdReadListener = new SendCmdReadTask.SendCmdReadListener() {
        @Override
        public void onTextViewIdShow(int cmd, int id, int port) {
            sendMessage(MessageSource.MSG_TASK_UPDATA_ITEM,id,port,itemChannel);//读取端口传感器完成的回调返回参数：argg1：id号，arg2.port号，ojb：当前绑定的item号
            LogUtil.d("message","itemChange l " + itemChannel);
            itemChannel = itemChannel+1;
        }
    };
    private SendCmdUpdataTask.SendCmdUpdataListener sendCmdUpdataListener = new SendCmdUpdataTask.SendCmdUpdataListener() {
        @Override
        public void onSucess(int i) {                           //回传是否成功修改1：修改成功0：失败
            sendMessage(MessageSource.MSG_TASK_UPDATA_COMMAND_SUCCESS,i,0,0);
        }
    };
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MessageSource.MSG_TASK_CONNEECT:           //测试连接
                    cMessenger = msg.replyTo;//get the messenger of client
                    break;
                case MessageSource.MSG_TASK_UPDATE_COMMAND:      //获取参数：上传档位命令，携带：arg1:档位，arg2：端口号（设置更改的对应端口号），obj：port
                    cMessenger = msg.replyTo;//get the messenger of client
                    int dw = msg.arg1;
                    dw = dw + 1;
                    pl = PublicMethod.rateToRate(msg.arg2);

                    int port = (int)msg.obj;
                    sendUpdateCommandTask(dw,port);
                    break;
                case MessageSource.MSG_TASK_RESET:
                    cMessenger = msg.replyTo;//get the messenger of client
                    resetAllTask();
                    break;
                case MessageSource.MSG_TASK_START:      //定时时间携带
                    timeTick = msg.arg1;                //开始命令中如果定时时间为0 表示时间无法确定，在专用软件中使用
                    mSensorList = ( List<Sensor>) msg.obj;
                    cMessenger = msg.replyTo;//get the messenger of client
                    //startCollectTask();
                    //startTimer();
                    startMyTask();
                    break;
                case MessageSource.MSG_TASK_STOP:
                    cMessenger = msg.replyTo;//get the messenger of client
                    stopAllTask();
                    stopTimer();
                    break;

                default:
                    break;
            }


            return true;
        }
    });
    private Messenger mMessenger = new Messenger(mHandler);//It's the messenger of server
    private Messenger cMessenger = null;//It's the messenger of client

    /**
     arg1 int类型 用来存放整型数据  
     arg2 int类型 用来存放整型数据  
     what int类型 用来保存消息标示  
     obj Object类型 是Object类型的任意对象  
     replyTo Messager类型 用来指定此Message发送到何处的可选Message对象 
     */
    private void sendMessage(int what, int arg1, int arg2, Object obj) {
        Message msg = Message.obtain(null, what, arg1, arg2,obj);
        //cMessenger = msg.replyTo;//get the messenger of client
        try {
            if (cMessenger != null) {
                cMessenger.send(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public TcpService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        itemChannel = 0;  //初始化通道号
        DATANU=0;
        return mMessenger.getBinder();
    }


    // 创建Service时调用该方法，只调用一次
    @Override
    public void onCreate() {
        super.onCreate();
        if (createTcpServer == null) {
            createTcpServer = new CreateTcpServer(createListener);
            createTcpServer.execute();
        }
        itemChannel = 0;  //初始化通道号
        DATANU=0;
    }
    // 每次启动Service时都会调用该方法

    /**
     * 每次调用键盘，每次屏幕旋转都会调用一次改回调函数
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        itemChannel = 0;  //初始化通道号
        DATANU=0;
        initTimer();
        initServiceTaskStart();
        return super.onStartCommand(intent, flags, startId);
    }
    // 退出或者销毁时调用该方法
    @Override
    public void onDestroy() {
        itemChannel = 0;  //初始化通道号
        DATANU=0;
        stopTimer();
        stopAllTask();         //上传结束采集命令
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {

        itemChannel = 0;  //初始化通道号
        DATANU=0;
        stopTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stopAllTask();
                    keilAllTask();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onUnbind(intent);
    }
    /**
     * 发送档位
     * cm2为档位数据(频率数据在开始任务的时需要使用)
     */
    private void sendUpdateCommandTask(final int cm2, final int port){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = createTcpServer.getSocket();
                    if (socket != null ) {
                        sendCmdUpdataTask = new SendCmdUpdataTask(sendCmdUpdataListener,socket);
                        sendCmdUpdataTask.execute((byte) 0x55, (byte) 0xaa, (byte) port, (byte) 0x7e, (byte) 0x04, (byte) 0x00, (byte) 0x84, (byte) cm2);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //普通结束
    private void stopAllTask(){
        DATANU=0;
        socket = createTcpServer.getSocket();
        if ((socket ) != null) {
            if (sgetAllTask != null) {         //停止接收数据
                sgetAllTask.setStop();
                //if (sendCmdStartTask != null) {
                   // sendCmdStartTask.stopTask();
                //}
            }
            for (int i = 0; i < 6; i++) {
                sendCmdStopTask = new SendCmdStopTask(socket);
                sendCmdStopTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
            }
        }

    }
    /**
     * 开始采集数据命令
     */
    /**
     * 开始定时上传
     */
    private void startMyTask(){
        if (isStop) {
            isStop = false;

            socket = createTcpServer.getSocket();
            if ((socket ) != null) {                //必须要获取通道号，不然无法获取等数量数据
                final int channel =nowChannel;
                if (channel != 0) {
                    sgetAllTask =  new SgetAllTask(sgetAllListener,socket,mSensorList);
                    sgetAllTask.execute(channel,3);

                            //sendCmdChangeTask = new SendCmdChangeTask(socket);
                            //sendCmdChangeTask.execute(channel);
                    //sgetTask = new SgetTask(sgetListener, socket, mSensorList);
                    //sgetTask.executeOnExecutor(THREAD_POOL_EXECUTOR);

                }
            }
            //timeTick = 0为专用实验，停止命令才能停止实验
            if (mTimer == null && timeTick != 0) {
                mTimer = new Timer();
            }

            count = 0;
            if (mTimerTask == null&& timeTick != 0) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        sendMessage(MessageSource.MSG_TASK_TIME_TICK, count, 0,0);
                        if (count == timeTick){
                            stopTimer();
                            stopAllTask();         //上传结束采集命令
                        }
                        count++;
                    }
                };
            }

            if (mTimer != null && mTimerTask != null)
                mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    private void keilAllTask(){
        if (portQueryTask != null){
            portQueryTask.cancel(true);
        }

        if (sgetAllTask != null){
            sgetAllTask.cancel(true);
        }
        if (sendCmdReadTask != null){
            sendCmdReadTask.cancel(true);
        }
        if (sendCmdUpdataTask != null){
            sendCmdUpdataTask.cancel(true);
        }
        if (sendCmdResetTask != null){
            sendCmdResetTask.cancel(true);
        }
        if (createTcpServer != null){
            createTcpServer.cancel(true);
        }
        //if (sgetTask != null){
            //sgetTask.cancel(true);
       // }

    }
    private void stopTimer(){
        if (!isStop) {
            isStop = true;
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            if (mTimer2 != null) {
                mTimer2.cancel();
                mTimer2 = null;
            }

            if (mTimerTask2 != null) {
                mTimerTask2.cancel();
                mTimerTask2 = null;
            }
            count = 0;
        }

    }

    private void initTimer(){

            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            if (mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }

            if (mTimer2 != null) {
                mTimer2.cancel();
                mTimer2 = null;
            }

            if (mTimerTask2 != null) {
                mTimerTask2.cancel();
                mTimerTask2 = null;
            }

            count = 0;
            isStop = true;

    }
    /**
     * 复位重置采集器，下来刷新重置采集器
     */
    private void resetAllTask(){
        itemChannel = 0;  //初始化通道号
        DATANU=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    resetTask();
                    Thread.sleep(5000);
                    getPortQueryTask();
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化传感器读取程序
     * 1、判断是否已经创建了socket，如果没有创建，再重新创建一次
     * 2、发送停止命令，停止所以采集器采集，共发送6次，不用接收数据任务
     */
    private void initServiceTaskStart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    if (createTcpServer == null){       //重新创建一次
                        createTcpServer = new CreateTcpServer(createListener);
                        createTcpServer.execute();
                    }
                    Thread.sleep(1000);          //等待Create创建完成
                    stopAllTask();                      //停止所以采集器采集
                    Thread.sleep(1000);           //等待完成
                    getPortQueryTask();                  //查询端口 ---- 端口查询完成在回调中查找对应端口的传感器
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 发送重置命令
     */
    private void resetTask(){
        socket = createTcpServer.getSocket();
        if (socket != null ) {
            sendCmdResetTask = new SendCmdResetTask(socket);
            sendCmdResetTask.execute();
        }

    }


    /**
     * 获取端口设备号命令
     */
    private void getPortQueryTask(){
        socket = createTcpServer.getSocket();
        if (socket != null ) {
            portQueryTask = new SgetPortQueryTask(sgetPortQueryListener,socket);
            portQueryTask.execute();
        }
    }

    //查询命令，查询需要循环差每个有设备的端口；
    private void sendDemand(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ((socket = createTcpServer.getSocket()) != null) {
                        socket = createTcpServer.getSocket();
                        int channel =nowChannel;
                        LogUtil.d("task","task2"+channel);
                        if ((channel) != 0) {
                            int j = 0;
                            for (int i = 0; i < 6; i++) {
                                if ((channel & 0x01) == 1) {
                                    sendCmdReadTask = new SendCmdReadTask(sendCmdReadListener,socket);
                                    sendCmdReadTask.execute((byte) 0x55, (byte) 0xaa, (byte) j, (byte) 0x7e, (byte) 0x03, (byte) 0x00, (byte) 0x80);
                                    Thread.sleep(500);
                                }
                                channel = channel >> 1;
                                j = j + 1;
                                if (j == 6) {
                                    j = 0;
                                }
                            }

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();



    }

/*
    private void startTimer(){
        if (mTimer == null){
            mTimer = new Timer();
        }
        if (mTimerTask == null){
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    sendMessageInt(UPDATE_TEXTVIEW,count);
                    do {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    } while (isStop || count == maxTime);

                    count++;
                }
            };
        }
        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, 1, 1);
    }

    public void sendMessageInt(int msg,int data){
        if (mHandler != null) {
            Message message = new Message();
            message.what = msg;
            Bundle bundle = new Bundle();
            bundle.putInt("data1",data);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }
    public void sendMessageflaot(int msg,float data){
        if (mHandler != null) {
            Message message = new Message();
            message.what = msg;
            Bundle bundle = new Bundle();
            bundle.putFloat("data1",data);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }
    private void stopTimer(){

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        count = 0;

    }
*/
}
