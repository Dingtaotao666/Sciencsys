package cn.com.sciencsys.sciencsys.initsystem;

public class Laboratory {
    private String name;
    private int imageId;

    public Laboratory(String name,int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
