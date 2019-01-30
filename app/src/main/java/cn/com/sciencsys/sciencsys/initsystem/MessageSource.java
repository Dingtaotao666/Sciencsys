package cn.com.sciencsys.sciencsys.initsystem;

public class MessageSource {


    public static final int MSG_TASK_START = 1;         //开始任务（携带定时时间）
    public static final int MSG_TASK_STOP = 2;          //停止任务
    public static final int MSG_TASK_RESET = 4;         //重置任务
    public static final int MSG_TASK_CLEAR = 5;         //清除任务
    public static final int MSG_TASK_DELETED = 6;       //删除任务
    public static final int MSG_TASK_UPDATE_COMMAND = 8;        //上传档位频率数据

    public static final int MSG_TASK_TIME_TICK = 7;
    public static final int MSG_TASK_CONNEECT= 0;
    public static final int MSG_TASK_READ_DATA= 3;
    public static final int MSG_TASK_UPDATA_ITEM= 9;


    public static final int MSG_TASK_UPDATA_COMMAND_SUCCESS= 10;
    public static final int MSG_TASK_SOCKET_SUCCESS= 11;
}
