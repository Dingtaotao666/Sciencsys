package cn.com.sciencsys.sciencsys.UImaker;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import cn.com.sciencsys.sciencsys.PopActivity;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.initsystem.Constants;
import cn.com.sciencsys.sciencsys.initsystem.Sensor;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {
    private UploadCommandListener uploadCommandListener;
    private Context context;
    private List<Sensor> mSensorList;
    private Sensor sensor;
    private int whichDw = 0;         //档位
    private int whichPl = 0;          //频率
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView sensorId;
        TextView sensorDate;
        TextView sensorPort;
        TextView sensorName;


        View sensorView;
        public ViewHolder(View view){
            super(view);

            sensorView = view;
            sensorId = (TextView) view.findViewById(R.id.idText);
            sensorDate = (TextView) view.findViewById(R.id.dataText);
            sensorPort = (TextView) view.findViewById(R.id.portText);
            sensorName = (TextView) view.findViewById(R.id.nameText);

        }
    }

    public SensorAdapter(Context context,List<Sensor> sensorList){
        this.mSensorList = sensorList;
        this.context = context;
    }
    public void setUploadCommandListener(UploadCommandListener uploadCommandListener) {
        this.uploadCommandListener = uploadCommandListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor,parent,false);
        final ViewHolder holder =new ViewHolder(view);


        return holder;
    }

    /**
     * 该回调可以实现Item的部分数据的更新而不更新所以数据
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()){
            onBindViewHolder(holder,position);
        }else {
            holder.sensorDate.setText(String.valueOf(mSensorList.get(position).getData()));
        }
    }

    //对recycleview子项赋值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        sensor = mSensorList.get(position);
        holder.sensorId.setText("ID:"+String.valueOf(sensor.getId()));    //setText传入其他类型数据会出错
        holder.sensorDate.setText(String.valueOf(sensor.getData()));
        holder.sensorPort.setText("Port:"+String.valueOf(sensor.getPort()));
        holder.sensorName.setText(sensor.IdToName(sensor.getId()));


        /**
         * 长按选择
         */
        holder.sensorView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        /**
         * 之前的按键监听放在onCreateViewHolder中，无法获取position
         * 先选择档位：档位根据传感器Id来选择
         * 确定后再选择频率，频率固定
         * 此dialog可以取消，只有确定后才会上传数据
         * 此dialog只有在通用软件处使用，所以不在activity上应用，集成在adapter中，方便之后有rec时候调用
         */
        holder.sensorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("选择档位：");
                //设置按键监听
                alertDialog.setSingleChoiceItems(sensor.IdToGear(mSensorList.get(position).getId()), position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            whichDw = which;
                    }
                });
                alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 频率选择
                         * @param dialog
                         * @param which
                         */
                        String[] mString = new String[]{"1Hz","10Hz","50Hz","100Hz","1KHz"};
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("选择频率：");
                        //设置按键监听
                        alertDialog.setSingleChoiceItems(mString, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                whichPl = which;
                            }
                        });
                        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            //确定后开线程上传档位数据
                            //用回调到PopActivity中处理
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadCommandListener.onUploadCommand(whichDw,whichPl,sensor.getPort());
                            }
                        });
                        alertDialog.create();
                        alertDialog.show();

                    }
                });
                alertDialog.create();
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }

    public void addItem(int position,Sensor sensor){
        if (sensor != null) {
            mSensorList.add(position, sensor);
            notifyItemChanged(position);
        }
    }

    public void removeItem(int position){
        if (getItemCount() > 0) {
            mSensorList.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removeAllItem(){
        if (getItemCount() > 0) {
            for (int i =0 ;i<getItemCount();i++) {
                mSensorList.remove(i);

            }
            notifyItemRangeRemoved(0,getItemCount());
        }
    }
    public void updataItemDataText(int position, float data){

    }

}
