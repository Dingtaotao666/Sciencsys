package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 参数输入，
 * 第一个参数：查询端口返回的端口数据
 * 第二个参数：频率参数
 */
public class SendCmdStartTask extends AsyncTask<Integer,Integer,Integer> {
    private OutputStream outputStream;
    private Socket socket;
    private boolean isStop = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        if (socket != null) {
            int j = integers[0];
            int l = 0;
            for (int k = 0; k < 6; k++) {               //获取通道个数来计算延时时间
                if (((j & 0xff) & 0x01) == 1) {
                    l++;
                    if (l == 7){
                        l = 0;
                    }
                }
                j = j >> 1;
            }
            int pl = PublicMethod.rateToSecond(integers[1])/l;
            while (!isStop) {
                try {
                    outputStream = socket.getOutputStream();
                    int channel = integers[0];
                    for (int i = 0; i < 6; i++) {
                        if (((channel & 0xff) & 0x01) == 1) {
                            byte b[] = new byte[]{(byte) 0x55, (byte) 0xaa, (byte) i, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x82, (byte) (integers[1] & 0xff)};
                            outputStream.write(b);   //发送开始指令
                            outputStream.flush();
                            Thread.sleep(pl);
                            LogUtil.d("readData","startCom = " + i);
                        }
                        channel = channel >> 1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    /**
     * 获取连接的数据和此asyn通信
     * @param socket
     */
    public SendCmdStartTask( Socket socket) {
        this.socket = socket;
    }
    public void stopTask(){
        isStop = true;
    }
}

