package cn.com.sciencsys.sciencsys.initsystem;

import android.net.wifi.WifiManager;

public class Constants {
    public static class WifiValue{
        public static final String SSID = "Elab_AP";
        public static final String Password = "20100813";
    }

    public static class BroadcaseUtilsValue {
        public static final String BroadcastUtils[] = {
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION,                              //列表扫描完成通知
                WifiManager.WIFI_STATE_CHANGED_ACTION,                                  //WIFI状态变化广播
                WifiManager.NETWORK_STATE_CHANGED_ACTION,                               //是否连接了WIFI的广播
                WifiManager.NETWORK_IDS_CHANGED_ACTION};                                //网络ID改变的广播

    }
    public static class TcpValue{
        public static final String HOST_ADDRESS = "192.168.15.84";
        public static final int HOST_PORT = 1234;
    }

    public static class TaskCommand{
        //public static final byte[] SSTART_TASK = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x7e,(byte)0x03,(byte)0x00,(byte)0x82};//此命令是发送开始命令

        public static final byte[] READ_PORT_TASK = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x7e,(byte)0x03,(byte)0x00,(byte)0x7f};//此命令是查询端口设备指令
        public static final byte[] STOP_TASK_0 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};
        public static final byte[] STOP_TASK_1 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};
        public static final byte[] STOP_TASK_2 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};
        public static final byte[] STOP_TASK_3 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x03,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};
        public static final byte[] STOP_TASK_4 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x04,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};
        public static final byte[] STOP_TASK_5 = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x05,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x81};

        /**
         *
         */
        public static final byte[] SETDATATYPE_TASK = new byte[]{(byte)0x00,(byte)0x00,(byte)0x00} ;

        /**
         * USB采集器复位
         * 命令APP发送到采集器
         * 0x55 0xaa 0x01 0x7e 0x03 0x00 0xF0
         */
        public static final byte[] RESET_TASK = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x7e,(byte)0x03,(byte)0x00,(byte)0xf0};
        /**
         * USB采集器端口设备查询
         * 命令APP发送到采集器
         * 0x55 0xaa 0x01 0x7e 0x03 0x00 0x7f
         * 命令回复采集器到APP
         * 0x55 0xaa 0x00 0x48 0x07 0x00 0x7f 0x2f 0x01 0x01 0x02
         */
        public static final byte[] HEARTBEAT_TASK = new byte[]{(byte)0x55,(byte)0xaa,(byte)0x01,(byte)0x7e,(byte)0x03,(byte)0x00,(byte)0x7f};
    }


}
