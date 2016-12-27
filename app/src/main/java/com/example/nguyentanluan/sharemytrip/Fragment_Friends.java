package com.example.nguyentanluan.sharemytrip;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import Modules.OnPassData;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Friends extends Fragment implements OnPassData {
    FriendAdapter adapter;
    List<Post> listTrip;
    List<Post> listAll;
    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    public static boolean isPass = false;
    public static Map<String, List<Post>> listpost;

    public Fragment_Friends() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerviewFriend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration decoration = new DividerItemDecoration(this.getActivity(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(layoutManager);
        listAll = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listpost = new HashMap<>();
        mDatabase.child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addPost(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void addPost(DataSnapshot dataSnapshot) {
        listAll.clear();
        for (DataSnapshot dtn : dataSnapshot.getChildren()) {
            if (dtn.getChildren() != null) {
                listTrip = new ArrayList();
                for (DataSnapshot dtn1 : dtn.getChildren()) {
                    listTrip.add(dtn1.getValue(Post.class));
                }
                listpost.put(dtn.getKey().toString(), listTrip);
                listAll.addAll(listTrip);
            }
            if (getActivity() != null) {
                adapter = new FriendAdapter(listAll, dtn.getKey(), getActivity(), this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPassData(final String data) {
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(getActivity());
        dialogbuilder.setTitle("QUESTION?");
        dialogbuilder.setMessage("Bạn muốn xem hành trình?");
        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isPass = true;
                Fragment f = new Fragment_SetupTrip();
                Bundle bundle = new Bundle();
                bundle.putString("url", data);
                f.setArguments(bundle);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container_body, f);

//            Bundle bundle = new Bundle();
//            bundle.putString("key", key);
//            fragment.setArguments(bundle);
                transaction.commit();
            }
        });
        dialogbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*if (dialog != null)
                    dialog.dismiss();*/
                return;
            }
        });
        AlertDialog dialog = dialogbuilder.create();
        dialog.show();

    }
}
