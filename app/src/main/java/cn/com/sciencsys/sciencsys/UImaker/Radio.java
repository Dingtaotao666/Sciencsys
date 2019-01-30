package cn.com.sciencsys.sciencsys.UImaker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.com.sciencsys.sciencsys.R;

public class Radio extends AlertDialog {
    public Radio(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void CreateChoseDialog(String dw[],View view){
        RadioGroup dwsz = (RadioGroup) view.findViewById(R.id.dwsz);            //获取dialog的textView对象（此对象必须在Dialog。show之后才有效）
        RadioGroup plxz = (RadioGroup) view.findViewById(R.id.plxz);            //获取dialog的textView对象（此对象必须在Dialog。show之后才有效）
        RadioButton r1hz = (RadioButton) view.findViewById(R.id.R1HZ);
        RadioButton r10hz = (RadioButton) view.findViewById(R.id.R10HZ);
        RadioButton r50hz = (RadioButton) view.findViewById(R.id.R50HZ);
        RadioButton r100hz = (RadioButton) view.findViewById(R.id.R100HZ);
        RadioButton r1khz = (RadioButton) view.findViewById(R.id.R1KHZ);
        RadioButton r10khz = (RadioButton) view.findViewById(R.id.R10KHZ);
        RadioButton dw1 = (RadioButton) view.findViewById(R.id.DW1);
        RadioButton dw2 = (RadioButton) view.findViewById(R.id.DW2);
        RadioButton dw3 = (RadioButton) view.findViewById(R.id.DW3);

        dw1.setText(dw[0]);
        dw2.setText(dw[1]);
        dw3.setText(dw[2]);


    }

    public void Dismiss(UserLoadingDialog userLoadingDialog) {
        if (userLoadingDialog.isShowing()) {                                                    //已经显示了dialog
            userLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chose_dialog);
    }

}
