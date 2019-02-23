package cn.com.sciencsys.sciencsys.initsystem;

public class Laboratory {
    private String name;        //实验名称
    private int imageId;        //实验图片
    private int sensorId[];     //实验所需的传感器ID
    private String key;         //关键词查找所用的关键字（首字母）
    private int num;         //列表序號，從0開始，每個實驗一個序號

    public Laboratory(String name,int imageId,int sensorId[],String key,int num){
        this.name = name;
        this.imageId = imageId;
        this.sensorId = sensorId;
        this.key = key;
        this.num = num;
    }

    public String getName() {
        return name;
    }
    public int getImageId(){
        return imageId;
    }

    public int[] getSensorId() {
        return sensorId;
    }

    public String getKey() {
        return key;
    }

    public int getNum() {
        return num;
    }
}
