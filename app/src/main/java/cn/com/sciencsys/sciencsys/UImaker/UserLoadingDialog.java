package cn.com.sciencsys.sciencsys.UImaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.com.sciencsys.sciencsys.R;

public class UserLoadingDialog extends AlertDialog {


    public UserLoadingDialog(Context context,int theme){
        super(context,theme);
    }
    public UserLoadingDialog(Context context){
        super(context);
    }

    public void CreateLoadingDialog(UserLoadingDialog userLoadingDialog,String mString){
        if (userLoadingDialog.isShowing()){                                                             //已经显示了dialog
            TextView textView = (TextView) userLoadingDialog.findViewById(R.id.tipTextView);            //获取dialog的textView对象（此对象必须在Dialog。show之后才有效）
            textView.setText(mString);
        }else {
            userLoadingDialog.setCancelable(false);
            userLoadingDialog.create();
            userLoadingDialog.show();
            TextView textView = (TextView) userLoadingDialog.findViewById(R.id.tipTextView);
            textView.setText(mString);
        }

    }

    public void Dismiss(UserLoadingDialog userLoadingDialog) {
        if (userLoadingDialog.isShowing()) {                                                    //已经显示了dialog
            userLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userprogressdialog);
    }
}
