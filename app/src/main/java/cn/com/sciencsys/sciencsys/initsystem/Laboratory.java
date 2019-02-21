package cn.com.sciencsys.sciencsys.initsystem;

public class Laboratory {
    private String name;
    private int imageId;
    private int sensorId[];

    public Laboratory(String name,int imageId,int sensorId[]){
        this.name = name;
        this.imageId = imageId;
        this.sensorId = sensorId;
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
}
