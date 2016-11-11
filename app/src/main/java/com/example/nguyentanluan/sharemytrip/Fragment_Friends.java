package com.example.nguyentanluan.sharemytrip;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import Adapter.FriendAdapter;
import Model.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Friends extends Fragment {
    FriendAdapter adapter;
    List<User> listuser;
    RecyclerView recyclerView;

    public Fragment_Friends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView=(RecyclerView)view.findViewById(R.id.recylerviewFriend);
        listuser=new ArrayList<>();
        for(int i=0;i<5;i++){
            User user=new User("Username"+i);
            listuser.add(user);
        }

        adapter=new FriendAdapter(listuser,this.getActivity());
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.getActivity(),LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration decoration=new DividerItemDecoration(this.getActivity(),layoutManager.getOrientation());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

}
