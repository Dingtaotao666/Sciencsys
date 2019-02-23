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
    private OnClickViewListener onClickViewListener;
    private Context mContext;
    private List<Laboratory> mLaboratory;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView laboratoryImage;
        TextView laboratoryName;

        View labView;
        public ViewHolder(View view){
            super(view);
            labView = view;
            cardView = (CardView) view;
            laboratoryImage = (ImageView) view.findViewById(R.id.lab_image);
            laboratoryName = (TextView) view.findViewById(R.id.lab_name);
        }
    }
    public LabAdapter(Context context, List<Laboratory> mLaboratory){
        this.mLaboratory = mLaboratory;
        this.mContext = context;
    }
    public interface OnClickViewListener {
        void onViewClick(View view,int num);
    }
    public void setOnClickViewListener(OnClickViewListener onClickViewListener) {
        this.onClickViewListener = onClickViewListener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Laboratory laboratory = mLaboratory.get(position);
        holder.laboratoryName.setText(laboratory.getName());    //setText传入其他类型数据会出错
        Glide.with(mContext).load(laboratory.getImageId()).into(holder.laboratoryImage);

        holder.labView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewListener.onViewClick(v,laboratory.getNum());
            }
        });
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
