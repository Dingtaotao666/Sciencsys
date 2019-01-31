package cn.com.sciencsys.sciencsys.initsystem;

public class Laboratory {
    private String name;        //名称
    private int id;             //id
    private String text;           //端口

    public Laboratory(String name,String text,int id){
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
