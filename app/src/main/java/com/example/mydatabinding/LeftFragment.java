package com.example.mydatabinding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


//最左侧精灵图鉴的fragment，由recyclerView组成
public class LeftFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LeftAdapter mLeftAdapter;
    private List<String> p_name_list;

    public LeftFragment(List<String> list) {
        p_name_list = list;
        // Required empty public constructor
    }

    public static LeftFragment newInstance(List<String> list) {
        LeftFragment fragment = new LeftFragment(list);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mLeftAdapter = new LeftAdapter(getActivity(),mRecyclerView,p_name_list);

        mRecyclerView.setAdapter(mLeftAdapter);

        return view;
    }
}