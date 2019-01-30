package cn.com.sciencsys.sciencsys.initsystem;

import android.graphics.Color;

import java.math.BigDecimal;
import java.util.Random;

public class PublicMethod {
    /**
     * 频率选择
     * @param
     * @return
     */
    public static int rateToRate(int witch){
        //1hz
        if (witch == 0){
            return 3;
        }
        //10hz
        if (witch == 1){
            return 5;
        }
        //50hz
        if (witch == 2){
            return 10;
        }
        //100hz
        if (witch == 3){
            return 3;
        }
        //1000hz
        if (witch == 4){
            return 13;
        }
        else return 0;

    }
    /**
     * 频率对应毫秒数
     * @param
     * @return
     */
    public static int rateToSecond(int witch){
        //1hz
        if (witch == 3){
            return 1000;
        }
        //10hz
        if (witch == 5){
            return 100;
        }
        //50hz
        if (witch == 10){
            return 20;
        }
        //100hz
        if (witch == 11){
            return 10;
        }
        //1000hz
        if (witch == 13){
            return 1;
        }
        else return 0;

    }

    /**
     * id转换成传感器类型
     */
    public static int IdToType(int id){
        if (id == 30){
            return 2;
        }
        if (id == 34){
            return  0;
        }else return 1;
    }
    //随机颜色
    public static int randomColor(){
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.rgb(r,g,b);
    }

    /**
     * 保留几位小数
     * @param f
     * @param i
     * @return
     */
    public static float floatRemain(float f,int i){
        BigDecimal b = new BigDecimal(f);
        float f1 = b.setScale(i, BigDecimal.ROUND_HALF_UP).floatValue();
//   b.setScale(2,  BigDecimal.ROUND_HALF_UP)  表明四舍五入，保留两位小数  
        return f1;
    }
}
