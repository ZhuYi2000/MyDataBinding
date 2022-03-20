package com.example.mydatabinding.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mydatabinding.R;

import java.util.ArrayList;
import java.util.List;


//最左侧精灵图鉴的fragment，由recyclerView组成，精灵图鉴有800只宝可梦组成
public class LeftFragment extends Fragment implements LeftAdapter.OnItemClickListener{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private LeftAdapter mLeftAdapter;
    private List<String> p_name_list;//总列表
    private List<String> current_list = new ArrayList<>();//当前列表
    private int pageSize = 50;//每页的大小
    private boolean isLoading = false;

    private final String TAG = "zhu";

    public LeftFragment(List<String> list) {
        p_name_list = list;
    }

    public static LeftFragment newInstance(List<String> list) {
        LeftFragment fragment = new LeftFragment(list);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_left, container, false);

        //fragment要在此生命周期中设置rv及其adapter
        mRecyclerView = view.findViewById(R.id.my_rv);
        //设置rv的布局
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        for(int i=0;i<pageSize;i++){
            current_list.add(p_name_list.get(i));
        }

        mLeftAdapter = new LeftAdapter(getActivity(),mRecyclerView,current_list);
        mLeftAdapter.setOnItemClickListener(this);//绑定点击事件

        mRecyclerView.setAdapter(mLeftAdapter);


        //设置监听器，在到达底部的时候加载更多数据,不涉及业务逻辑、仅仅是UI操作,所以放在View层即可
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //到达底部，且还有数据加载
                if(isBottom(recyclerView) && (current_list.size()!=p_name_list.size()) && !isLoading){
                    Log.d(TAG,"到达底部");
                    isLoading = true;
                    //获取新数据
                    int position = current_list.size();
                    for(int i=position;i<position+pageSize;i++){
                        current_list.add(p_name_list.get(i));
                    }
                    mLeftAdapter.notifyDataSetChanged();
                    Log.d(TAG,"新数据更新完成");
                    Log.d(TAG,"..."+current_list.get(position));
                    isLoading = false;
                }
                //到达底部，已经加载完成所有数据
                if(isBottom(recyclerView) && (current_list.size()==p_name_list.size())){
                    Toast.makeText(mContext, "数据已经全部加载完毕", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    public boolean isBottom(RecyclerView recyclerView){
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();

        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();


        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1
                && state == recyclerView.SCROLL_STATE_IDLE){
//            Log.d(TAG,"屏幕中最后一个可见子项的position"+lastVisibleItemPosition);
//            Log.d(TAG,"当前屏幕所看到的子项个数"+visibleItemCount);
//            Log.d(TAG,"当前RecyclerView的所有子项个数"+totalItemCount);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int p_id) {
        Log.d(TAG,"点击"+p_id);
        Intent info_intent = new Intent(mContext, PokemonInfoActivity.class);
        info_intent.putExtra("p_id",p_id);
        startActivity(info_intent);
    }
}