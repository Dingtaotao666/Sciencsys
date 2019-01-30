package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SendCmdStopTask extends AsyncTask<Void,Void,Integer> {
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
            byte buff[] = new byte[46];

            try {
                outputStream = socket.getOutputStream();
                outputStream.write(Constants.TaskCommand.STOP_TASK_0);   //发送开始指令
                outputStream.flush();
                outputStream.write(Constants.TaskCommand.STOP_TASK_1);   //发送开始指令
                outputStream.flush();
                outputStream.write(Constants.TaskCommand.STOP_TASK_2);   //发送开始指令
                outputStream.flush();
                outputStream.write(Constants.TaskCommand.STOP_TASK_3);   //发送开始指令
                outputStream.flush();
                outputStream.write(Constants.TaskCommand.STOP_TASK_4);   //发送开始指令
                outputStream.flush();
                outputStream.write(Constants.TaskCommand.STOP_TASK_5);   //发送开始指令
                outputStream.flush();
                inputStream = socket.getInputStream();
                socket.setSoTimeout(1000);

                bufferedInputStream = new BufferedInputStream(inputStream);
                bufferedInputStream.read(buff);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    /**
     * 获取连接的数据和此asyn通信
     *
     * @param socket
     */
    public SendCmdStopTask(Socket socket) {
        this.socket = socket;
    }
}