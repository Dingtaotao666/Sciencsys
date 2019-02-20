package cn.com.sciencsys.sciencsys.UImaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import cn.com.sciencsys.sciencsys.R;
import cn.com.sciencsys.sciencsys.initsystem.Laboratory;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.ViewHolder> {
    private Context mContext;
    private List<Laboratory> mLaboratory;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView laboratoryImage;
        TextView laboratoryName;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            laboratoryImage = (ImageView) view.findViewById(R.id.lab_image);
            laboratoryName = (TextView) view.findViewById(R.id.lab_name);
        }
    }
    public LabAdapter(Context context, List<Laboratory> mLaboratory){
        this.mLaboratory = mLaboratory;
        this.mContext = context;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laboratory laboratory = mLaboratory.get(position);
        holder.laboratoryName.setText(laboratory.getName());    //setText传入其他类型数据会出错
        Glide.with(mContext).load(laboratory.getImageId()).into(holder.laboratoryImage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.laboratory_item,parent,false);
        ViewHolder holder =new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mLaboratory.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
