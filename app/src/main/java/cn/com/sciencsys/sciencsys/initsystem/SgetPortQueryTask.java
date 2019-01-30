package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SgetPortQueryTask extends AsyncTask<Void,Integer,Integer> {

    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private OutputStream outputStream;
    private Socket socket;
    private SgetPortQueryListener mListener;

    public interface SgetPortQueryListener {
        void onPortNumberRead(int port,int portNumber);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        if (socket != null) {
            byte buff[] = new byte[46];
            int buffInt[] = new int[46];
            int sum = 0;
            int recLen = -1;
            int portNumber = 0;
            try {
                outputStream = socket.getOutputStream();
                outputStream.write(Constants.TaskCommand.READ_PORT_TASK);   //发送开始指令
                outputStream.flush();

                inputStream = socket.getInputStream();
                socket.setSoTimeout(2000);

                bufferedInputStream = new BufferedInputStream(inputStream);
                recLen = bufferedInputStream.read(buff);

                for (int i = 0;i<recLen;i=i+1){
                    buffInt[i] = buff[i]&0xff;
                    sum =sum + (buffInt[i]%0xff);
                }
                sum =(sum%0xff)>>8;

                if ((buffInt[0] == 0x55) && ((buffInt[1]) == 0xaa) && (sum == 0)){
                    if (buffInt[6] == 0x7f) {                         //0x设备端口查询档位设置
                        int number = buffInt[7];
                        for (int i = 0;i<6;i++) {
                            if ((number & 0x01) == 1) {
                                portNumber++;
                            }
                            number = number >> 1;
                        }
                        publishProgress(buffInt[7],portNumber);//第一个参数：命令类型，第二个设备端口00000000，有1就是有设备，第三个参数总的设备数量
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
            mListener.onPortNumberRead(values[0],values[1]);                 //有传感器的端口
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

    }

    /**
     * 获取连接的数据和此asyn通信
     * @param socket
     */
    public SgetPortQueryTask(SgetPortQueryListener listener, Socket socket) {
        this.socket = socket;
        this.mListener = listener;
    }
}

