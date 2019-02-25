package cn.com.sciencsys.sciencsys.dedicated;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;


import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;

import java.util.ArrayList;
import java.util.List;

import cn.com.sciencsys.sciencsys.R;

public class De0Activity extends Activity {
    private SmartTable<UserInfo> table;
    List<UserInfo> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);    //设置横屏模式
//普通列
        Column<String> title = new Column<>("次数", "num");
        Column<Float> num0 = new Column<>("1", "num0");
        Column<Float> num1 = new Column<>("2", "num1");
        Column<Float> num2 = new Column<>("3", "num2");
        Column<Float> num3 = new Column<>("4", "num3");
        Column<Float> num4 = new Column<>("5", "num4");
        Column<Float> num5 = new Column<>("6", "num5");
        Column<Float> num6 = new Column<>("7", "num6");
        Column<Float> num7 = new Column<>("8", "num7");
        Column<Float> num8 = new Column<>("9", "num8");
        Column<Float> num9 = new Column<>("10", "num9");
        //设置该列当字段相同时自动合并
        //city.setAutoMerge(true);

        table =  findViewById(R.id.table0);
        list.add(new UserInfo("U(V)",0,0,0,0,0,0,0,0,0,0));
        list.add(new UserInfo("I(A)",0,0,0,0,0,0,0,0,0,0));
        list.add(new UserInfo("R(Ω)",0,0,0,0,0,0,0,0,0,0));
        //table.setData(list);
//表格数据 datas 是需要填充的数据
        TableData<UserInfo> tableData = new TableData<UserInfo>("表格名", list, num0, num1, num2, num3, num4, num5, num6,num7,num8,num9);

//设置数据
        table.setTableData(tableData);
       // table.getConfig().setContentStyle(new FontStyle(50, Color.BLUE));

    }

    public class UserInfo {
        private String title;    //
        private float num0;
        private float num1;
        private float num2;
        private float num3;
        private float num4;
        private float num5;
        private float num6;
        private float num7;
        private float num8;
        private float num9;
        public UserInfo(String title, float num0, float num1, float num2, float num3, float num4, float num5, float num6, float num7, float num8, float num9){
            this.title = title;
            this.num0 = num0;
            this.num1 = num1;
            this.num2 = num2;
            this.num3 = num3;
            this.num4 = num4;
            this.num5 = num5;
            this.num6 = num6;
            this.num7 = num7;
            this.num8 = num8;
            this.num9 = num9;
        }

        public float getNum0() {
            return num0;
        }

        public float getNum1() {
            return num1;
        }

        public float getNum2() {
            return num2;
        }

        public float getNum3() {
            return num3;
        }

        public float getNum4() {
            return num4;
        }

        public float getNum5() {
            return num5;
        }

        public float getNum6() {
            return num6;
        }

        public float getNum7() {
            return num7;
        }

        public float getNum8() {
            return num8;
        }

        public float getNum9() {
            return num9;
        }

        public String getTitle() {
            return title;
        }
    }
}
