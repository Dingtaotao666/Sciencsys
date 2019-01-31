package cn.com.sciencsys.sciencsys.UImaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.initsystem.Laboratory;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.ViewHolder> {
    private Context context;
    private List<Laboratory> mLaboratory;
    private Laboratory laboratory;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView laboratoryText;
        TextView laboratoryName;
        Button startBottom;


        View sensorView;
        public ViewHolder(View view){
            super(view);

            sensorView = view;
            laboratoryText = (TextView) view.findViewById(R.id.laboratoryText);
            laboratoryName = (TextView) view.findViewById(R.id.labName);
            startBottom = (Button) view.findViewById(R.id.startLab);

        }
    }
    public LabAdapter(Context context, List<Laboratory> mLaboratory){
        this.mLaboratory = mLaboratory;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        laboratory = mLaboratory.get(position);
        holder.laboratoryName.setText(laboratory.getName());    //setText传入其他类型数据会出错
        holder.laboratoryText.setText(laboratory.getText());
        holder.sensorPort.setText("Port:"+String.valueOf(sensor.getPort()));
        holder.sensorName.setText(sensor.IdToName(sensor.getId()));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor,parent,false);
        final ViewHolder holder =new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
