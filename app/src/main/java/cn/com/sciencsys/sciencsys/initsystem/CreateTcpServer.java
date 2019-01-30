package cn.com.sciencsys.sciencsys.initsystem;

import android.os.AsyncTask;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class CreateTcpServer extends AsyncTask<Void,Void,Integer> {
    private ServerSocket serverSocket;
    private Socket socket;
    private  CreateListener mListener;
    public interface CreateListener {
        void onFinish(int data);     //完成任务标志
    }
    public CreateTcpServer(CreateListener listener) {
        this.mListener =listener;
    }
    @Override
    protected Integer doInBackground(Void... voids) {
        if (serverSocket == null) {
            try {
                serverSocket = new ServerSocket();
                serverSocket.setSoTimeout(5000);
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(Constants.TcpValue.HOST_PORT));
                socket = serverSocket.accept();
                if (socket.isConnected()){
                    return 1;
                }else {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            return 1;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.onFinish(integer);
    }

    /**
     * 获取当前的socket数据
     * @return socket的对象
     */

    public Socket getSocket() {
        if (socket != null) {
            return socket;
        }
        else return null;
    }

}
