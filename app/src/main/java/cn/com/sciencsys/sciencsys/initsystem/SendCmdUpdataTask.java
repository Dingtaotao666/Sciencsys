package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendCmdUpdataTask extends AsyncTask<Byte,Void,Integer> {
    private OutputStream outputStream;
    private Socket socket;
    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;
    private SendCmdUpdataListener mListener;

    public interface SendCmdUpdataListener {
        void onSucess(int i);    //回传是否成功修改
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
                byte b[] = new byte[8];

                for (int i = 0;i<8;i++){
                    b[i]=bytes[i];
                }

                outputStream = socket.getOutputStream();
                outputStream.write(b);   //发送开始指令
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
                    return 1;
                }else{
                    return 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.onSucess(integer);
    }

    /**
     * 获取连接的数据和此asyn通信
     * @param socket
     */
    public SendCmdUpdataTask(SendCmdUpdataListener mListener,Socket socket) {
        this.socket = socket;
        this.mListener = mListener;
    }

}
