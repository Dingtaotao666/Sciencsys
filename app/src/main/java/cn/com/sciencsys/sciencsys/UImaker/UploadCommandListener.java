package cn.com.sciencsys.sciencsys.UImaker;

/**
 * 此接口用于上传档位命令，在通用程序中使用
 * dw:档位，pl：频率，port：端口
 */
public interface UploadCommandListener {
    void onUploadCommand(int dw,int pl,int port);
}
