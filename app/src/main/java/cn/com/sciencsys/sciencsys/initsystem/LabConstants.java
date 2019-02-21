package cn.com.sciencsys.sciencsys.initsystem;

import java.util.ArrayList;

import cn.com.sciencsys.sciencsys.R;

public class LabConstants {
    //物理实验列表
    public static Laboratory [] physicsLaboratoriesList = {
            new Laboratory("1",R.drawable.timg,new int[]{1,2}),
            new Laboratory("2",R.drawable.timg,new int[]{1,2}),
            new Laboratory("3",R.drawable.timg,new int[]{1,2}),
            new Laboratory("4",R.drawable.timg,new int[]{1,2}),
            new Laboratory("5",R.drawable.timg,new int[]{1,2}),
            new Laboratory("6",R.drawable.timg,new int[]{1,2}),
            new Laboratory("7",R.drawable.timg,new int[]{1,2}),
            new Laboratory("8",R.drawable.timg,new int[]{1,2}),
            new Laboratory("9",R.drawable.timg,new int[]{1,2}),
    };
    //化学实验列表
    public static Laboratory [] chemistryLaboratoriesList = {
            new Laboratory("11", R.drawable.timg,new int[]{1,2}),
            new Laboratory("12",R.drawable.timg,new int[]{1,2}),
            new Laboratory("13",R.drawable.timg,new int[]{1,2}),
            new Laboratory("14",R.drawable.timg,new int[]{1,2}),
            new Laboratory("15",R.drawable.timg,new int[]{1,2}),
            new Laboratory("16",R.drawable.timg,new int[]{1,2}),
            new Laboratory("17",R.drawable.timg,new int[]{1,2}),
            new Laboratory("18",R.drawable.timg,new int[]{1,2}),
            new Laboratory("19",R.drawable.timg,new int[]{1,2}),
    };
    //生物实验列表
    public static Laboratory [] biologyLaboratoriesList = {
            new Laboratory("21", R.drawable.timg,new int[]{1,2}),
            new Laboratory("22",R.drawable.timg,new int[]{1,2}),
            new Laboratory("23",R.drawable.timg,new int[]{1,2}),
            new Laboratory("24",R.drawable.timg,new int[]{1,2}),
            new Laboratory("25",R.drawable.timg,new int[]{1,2}),
            new Laboratory("26",R.drawable.timg,new int[]{1,2}),
            new Laboratory("27",R.drawable.timg,new int[]{1,2}),
            new Laboratory("28",R.drawable.timg,new int[]{1,2}),
            new Laboratory("29",R.drawable.timg,new int[]{1,2}),
    };
}
