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

//adapter基本上大部分都算是对UI的操作，因此算是View层
public class LeftAdapter extends RecyclerView.Adapter<LeftAdapter.LeftViewHolder> {

    private Context context;
    private RecyclerView rv;
    private List<String> list = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(RecyclerView parent,View view, int p_id);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.mOnItemClickListener = clickListener;
    }

    public LeftAdapter(Context context, RecyclerView rv, List<String> list) {
        this.context = context;
        this.rv = rv;
        this.list = list;
    }

    @NonNull
    @Override
    public LeftAdapter.LeftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.left_item,parent,false);

        return new LeftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeftAdapter.LeftViewHolder holder, int position) {
        holder.mTv.setText(list.get(position));
        String id_text = "#"+(position+1);
        holder.mId.setText(id_text);
        bindImageView(holder,position);

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemClickListener!=null){
                    int p_id = holder.getAdapterPosition()+1;
                    mOnItemClickListener.onItemClick(rv,view,p_id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void bindImageView(LeftAdapter.LeftViewHolder holder,int position){
        int id = position+1;
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"+id+".png";

        RequestOptions options = new RequestOptions()
                .skipMemoryCache(false)  //用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有图片(原图,转换图)
                .fitCenter();

        Glide.with(holder.mContainer).load(url).apply(options).
                thumbnail(Glide.with(holder.mImageView).load(R.drawable.loading)).into(holder.mImageView);
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {
        CardView mContainer;
        ImageView mImageView;
        TextView mTv;
        TextView mId;
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.left_item_container);
            mImageView = itemView.findViewById(R.id.left_item_image);
            mTv = itemView.findViewById(R.id.left_item_tv);
            mId = itemView.findViewById(R.id.left_item_id);
        }
    }
}
