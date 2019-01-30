package cn.com.sciencsys.sciencsys.initsystem;

import java.util.HashMap;
import java.util.Map;

public class Sensor {
    private String name;        //名称
    private int id;             //id
    private int port;           //端口
    private int itemPort;       //显示的列表端口
    private float data;         //数据
    private int type;           //传感器类型 0：数字型 1：模拟型 2: 5字节光电门型

    public Sensor(int port,int id,int itemPort,float data,int type){
        this.id = id;
        this.data = data;
        this.port = port;
        this.itemPort = itemPort;
        this.type = type;
    }

    public int getId(){
        return id;
    }
    public float getData(){
        return data;
    }
    public int getPort(){
        return port;
    }
    public void setData(float data){
        this.data = data;
    }

    public void setPortToItemPort(int port){
        this.itemPort = port;
    }

    public int getItemPort() {
        return itemPort;
    }

    public int getType(){
        return type;
    }

    /**
     * id转换成传感器名称
     * @param id
     * @return
     */
    public String IdToName(int id){
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 4){
            return "静电计传感器";
        }
        if (id == 6){
            return "电导率传感器";
        }
        if (id == 12){
            return "电流传感器";
        }
        if (id == 88){
            return "磁场强度传感器";
        }
        if (id == 231){
            return "静电计传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }
        if (id == 3){
            return "磁场强度传感器";
        }else {
            return "未命名传感器";
        }

    }

    /**
     * 根据id选择不同档位
     * @param id
     * @return
     */
    public String[] IdToGear(int id){
        String[] mString  = new String[3];
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;
        }
        if (id == 4){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 6){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 12){
            mString[0] = "-0.02A～0.02A";
            mString[1] = "-0.2A～0.2A";
            mString[2] = "-2A～2A";
            return mString;

        }
        if (id == 88){
            mString[0] = "档位1";
            mString[1] = "档位2";
            mString[2] = "档位3";
            return mString;

        }
        if (id == 231){
            mString[0] = "0～5v";
            mString[1] = "0～200v";
            mString[2] = "0～2000v";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }
        if (id == 3){
            mString[0] = "";
            mString[1] = "";
            mString[2] = "";
            return mString;

        }else {
            mString[0] = "无档位";
            mString[1] = "无档位";
            mString[2] = "无档位";
            return mString;

        }

    }

    /**
     * 传感器类型选择
     * @param id
     * @return
     */
    public int IdToDataType(int id){
        //光电门传感器协议，对于光电门类型的传感器，采样数据按五字节unsigned long格式上传 低字节在前
        if (id == 30){
            return 2;
        }
        //对于数字量类型的传感器，采样数据按四字节unsigned long格式上传 低字节在前
        if (id == 3){
            return 1;
        }else {     //对于模拟量类型的传感器，采样数据按四字节float格式上传
            return 0;
        }
    }

}
