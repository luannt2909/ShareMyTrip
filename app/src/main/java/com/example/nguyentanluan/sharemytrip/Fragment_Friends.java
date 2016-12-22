package com.example.nguyentanluan.sharemytrip;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.FriendAdapter;
import Model.Post;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Friends extends Fragment {
    FriendAdapter adapter;
    List<Post> listuser;
    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    Map<String, Post> listpost;

    public Fragment_Friends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerviewFriend);
        listuser = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listpost = new HashMap<>();
        mDatabase.child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dtn : dataSnapshot.getChildren()) {
                    listpost.put(dtn.getKey().toString(), dtn.getValue(Post.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new FriendAdapter(listuser, this.getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration decoration = new DividerItemDecoration(this.getActivity(), layoutManager.getOrientation());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("friend","vao");
    }
}
