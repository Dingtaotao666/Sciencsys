package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendCmdResetTask extends AsyncTask<Void,Void,Integer> {
    private OutputStream outputStream;
    private Socket socket;

    private InputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        if (socket != null) {
            try {
                byte buff[] = new byte[46];
                outputStream = socket.getOutputStream();
                outputStream.write(Constants.TaskCommand.RESET_TASK);   //发送开始指令
                outputStream.flush();

                inputStream = socket.getInputStream();
                socket.setSoTimeout(100);

                bufferedInputStream = new BufferedInputStream(inputStream);
                bufferedInputStream.read(buff);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
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
    public SendCmdResetTask(Socket socket) {
        this.socket = socket;
    }

}
