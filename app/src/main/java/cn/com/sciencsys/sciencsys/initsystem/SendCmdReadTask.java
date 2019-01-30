package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//查询传感器设备命令，由于传感器查询只能通过对应的端口号来查询
public class SendCmdReadTask extends AsyncTask<Byte,Integer,Integer> {
    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private OutputStream outputStream;
    private Socket socket;
    private SendCmdReadListener mListener;

    public interface SendCmdReadListener {
        void onTextViewIdShow(int cmd,int id,int port);    //在传感器列表中显示数据
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Byte... bytes) {
        if (socket != null) {
            byte buff[] = new byte[46];
            int buffInt[] = new int[46];
            int sum = 0;
            int recLen = -1;

            try {
                byte b[] = new byte[7];

                for (int i = 0;i<7;i++){
                    b[i]=bytes[i];
                }

                outputStream = socket.getOutputStream();
                outputStream.write(b);   //发送开始指令
                outputStream.flush();
                LogUtil.d("message","send " + b[0] + b[1] +b[2]+ b[3]+ b[4]+ b[5] + b[6]);
                inputStream = socket.getInputStream();
                socket.setSoTimeout(2000);

                bufferedInputStream = new BufferedInputStream(inputStream);
                recLen = bufferedInputStream.read(buff);

                for (int i = 0;i<recLen;i=i+1){
                    buffInt[i] = buff[i]&0xff;
                    sum =sum + (buffInt[i]%0xff);
                }
                sum =(sum%0xff)>>8;
                LogUtil.d("message","get " + buffInt[0] + buffInt[1] + buffInt[6]);
                if ((buffInt[0] == 0x55) && ((buffInt[1]) == 0xaa)){
                                                              //0x80为传感器查询命令：查询命令必须依赖于端口设备查询，查到哪个端口才能查传感器，不然没有回复
                        publishProgress(buffInt[6],buffInt[7],buffInt[2]);              //第一个参数：命令类型，第二个参数id号，第三个参数port号
                        LogUtil.d("message","get ");


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
        mListener.onTextViewIdShow(values[0],values[1],values[2]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    /**
     * 获取连接的数据和此asyn通信
     * @param socket
     */
    public SendCmdReadTask(SendCmdReadListener mListener,Socket socket) {
        this.socket = socket;
        this.mListener = mListener;
    }

}

