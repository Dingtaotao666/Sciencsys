package cn.com.sciencsys.sciencsys.initsystem;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

//活动集合处理
public class ActivityCollector {
    private List<Activity> activityList = new ArrayList<>();
    private static ActivityCollector instance;

    // 单例模式中获取唯一的ExitApplication实例
    //* getInstance这个方法在单例模式用的甚多，为了避免对内存造成浪费，直到需要实例化该类的时候才将其实例化，所以用getInstance来获取该对象，
    //* 至于其他时候，也就是为了简便而已，为了不让程序在实例化对象的时候，不用每次都用new，索性提供一个instance方法，不必一执行这个类就
    //* 初始化，这样做到不浪费系统资源！单例模式 可以防止 数据的冲突，节省内存空间
    public static synchronized ActivityCollector getInstance() {
        if (null == instance) {
            instance = new ActivityCollector();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activityList == null)
            activityList = new ArrayList<>();
        activityList.add(activity);
    }

    // 移除Activity
    public void removeActivity(Activity activity) {
        if (activityList != null)
            activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public void exitSystem() {
        for (Activity activity : activityList) {
            if (activity != null)
                activity.finish();
        }
        // 退出进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
