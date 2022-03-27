package com.example.mydatabinding.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mydatabinding.R;

import java.util.ArrayList;
import java.util.List;

public class RightAdapter extends RecyclerView.Adapter<RightAdapter.RightViewHolder>{

    private Context context;
    private RecyclerView rv;
    private List<Integer> pid_list = new ArrayList<>();
    private LeftAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(RecyclerView parent,View view, int p_id);
    }

    public void setOnItemClickListener(LeftAdapter.OnItemClickListener clickListener){
        this.mOnItemClickListener = clickListener;
    }

    public RightAdapter(Context context, RecyclerView rv, List<Integer> pid_list) {
        this.context = context;
        this.rv = rv;
        this.pid_list = pid_list;
    }

    @NonNull
    @Override
    public RightAdapter.RightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.right_item,parent,false);
        return new RightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RightAdapter.RightViewHolder holder, int position) {
        String id_text = "#"+pid_list.get(position);
        holder.mId.setText(id_text);

        bindImageView(holder,position);

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener!=null){
                    int position1 = holder.getAdapterPosition();
                    int p_id = pid_list.get(position1);
                    mOnItemClickListener.onItemClick(rv,view,p_id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pid_list.size();
    }


    public void bindImageView(RightAdapter.RightViewHolder holder,int position){
        int id = pid_list.get(position);
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+id+".png";

        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)  //用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有图片(原图,转换图)
                .fitCenter();

        Glide.with(holder.mContainer).load(url).apply(options).
                thumbnail(Glide.with(holder.mImageView).load(R.drawable.loading)).into(holder.mImageView);
    }


    public class RightViewHolder extends RecyclerView.ViewHolder {
        CardView mContainer;
        ImageView mImageView;
        TextView mId;
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.right_item_container);
            mImageView = itemView.findViewById(R.id.right_item_image);
            mId = itemView.findViewById(R.id.right_item_id);
        }
    }
}
