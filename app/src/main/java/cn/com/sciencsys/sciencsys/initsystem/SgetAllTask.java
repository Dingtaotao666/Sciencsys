package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class SgetAllTask extends AsyncTask<Integer,Integer,Integer> {
    private SgetAllListener mListener;
    private Socket socket;
    private boolean isStop = false;
    private InputStream inputStream =null;
    private BufferedInputStream bufferedInputStream = null;
    private OutputStream outputStream;

    private List<Sensor> mSensorList;
    private Sensor sensor;

    public interface SgetAllListener {

        void onShow(int port, int readItem, float data);     //模拟类和数字类4字节传送的数据显示
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(Integer... integers)  {
        if (socket != null) {
            int p = integers[0];
            int l = 0; int d=0;
            for (int k = 0; k < 6; k++) {               //获取通道个数来计算延时时间
                if (((p & 0xff) & 0x01) == 1) {
                    l++;
                    if (l == 7){
                        l = 0;
                    }
                }
                p = p >> 1;
            }
            int pl = PublicMethod.rateToSecond(integers[1])/l;
            pl = 1000;
            while (!isStop) {
                try {
                    byte buff[] = new byte[11 * 1000];
                    int buffInt[] = new int[11];

                    outputStream = socket.getOutputStream();

                    inputStream = socket.getInputStream();
                    socket.setSoTimeout(pl);
                    bufferedInputStream = new BufferedInputStream(inputStream);

                    int channel = integers[0];
                    for (int nym = 0; nym < 6; nym++) {
                        if (((channel & 0xff) & 0x01) == 1) {
                            byte b[] = new byte[]{(byte) 0x55, (byte) 0xaa, (byte)nym, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x82, (byte) (integers[1] & 0xff)};
                            outputStream.write(b);   //发送开始指令
                            outputStream.flush();
                            //Thread.sleep(pl/2);

                                    d++;
                            LogUtil.d("Data","DataSend = " +d);
                            int recLen = bufferedInputStream.read(buff);
                            //Thread.sleep(pl/2);
                            if ((recLen % 11) == 0) {
                                for (int i=0;i<recLen/11 ;i++) {
                                    for (int s = 0; s < 11; s = s + 1) {
                                        buffInt[s] = buff[s+i*11] & 0xff;
                                    }
                                    LogUtil.d("Data","buffInt = " + buffInt);
                                    if ((buffInt[0] == 0x55) && ((buffInt[1]) == 0xaa)) {
                                        int recItemNumber = getItemNumber(buffInt[2]);
                                        if (recItemNumber != 8) {                               //不等于8表示端口存在item
                                            int type = getSensorType(buffInt[2]);
                                            if (type == 0) {                                    //如果是数字类传感器
                                                int len = (buffInt[4] - 3) / 4;
                                                for (int j = 0; j < len; j++) {
                                                    publishProgress(buffInt[2], buffInt[7 + 4 * j], buffInt[8 + 4 * j], buffInt[9 + 4 * j], buffInt[10 + 4 * j], 0, recItemNumber, 0);//凑足5位，以便光电门介入

                                                }
                                            } else if (type == 1) {                               //如果是模拟类传感器
                                                int len = (buffInt[4] - 3) / 4;
                                                for (int j = 0; j < len; j++) {
                                                    publishProgress(buffInt[2], buffInt[7 + 4 * j], buffInt[8 + 4 * j], buffInt[9 + 4 * j], buffInt[10 + 4 * j], 0, recItemNumber, 1);//凑足5位，以便光电门介入
                                                    LogUtil.d("Data","ok");
                                                }
                                            } else if (type == 2) {
                                                int len = (buffInt[4] - 3) / 5;
                                                for (int j = 0; j < len; j++) {
                                                    publishProgress(buffInt[2], buffInt[7 + 5 * j], buffInt[8 + 5 * j], buffInt[9 + 5 * j], buffInt[10 + 5 * j], buffInt[11 + 5 * j], recItemNumber, 2);//凑足5位，以便光电门介入
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        channel = channel >> 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 2;       //完成读取u
        }else return 0;     //未创建Socket
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        float data =0;
        if (values[7] == 0) {             //如果是数字类传感器

        }else if (values[7] == 1){
            byte b[] = new byte[4];
            b[0] = (byte)(values[1]&0xff);
            b[1] = (byte)(values[2]&0xff);
            b[2] = (byte)(values[3]&0xff);
            b[3] = (byte)(values[4]&0xff);
            data = Float.intBitsToFloat(getInt(b));

        }else if (values[7] == 2){

        }
        mListener.onShow(values[0],values[6],data);     //参数1：port号 参数2：port对应的item号，参数3,：数据
        LogUtil.d("readData","data = " + data);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }


    public SgetAllTask(SgetAllListener listener, Socket socket , List<Sensor> mSensorList) {
        this.mListener = listener;
        this.socket = socket;
        this.mSensorList  =mSensorList;
    }

    public void setStop(){
        isStop = true;
    }

    private int getItemNumber(int port){
        int s = 8;                                              //返回8表示未找到数据
        try {
            if (mSensorList !=null) {
                for (int i = 0; i < mSensorList.size(); i++) {                //遍历list，查找对应端口号的
                    sensor = mSensorList.get(i);
                    if (port == sensor.getPort()) {
                        s = sensor.getItemPort();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

    private int getSensorType(int port){
        int s = 3;                                              //返回3表示在列表中未找到对应的传感器
        for (int i =0;i<mSensorList.size();i++){                //遍历list，查找对应端口号的
            sensor = mSensorList.get(i);
            if (port == sensor.getPort()){
                s = sensor.getType();
            }
        }
        return s;
    }
    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr) {
        return 	(0xff000000 	& (arr[0] << 24))  |
                (0x00ff0000 	& (arr[1] << 16))  |
                (0x0000ff00 	& (arr[2] << 8))   |
                (0x000000ff 	&  arr[3]);
    }
}
