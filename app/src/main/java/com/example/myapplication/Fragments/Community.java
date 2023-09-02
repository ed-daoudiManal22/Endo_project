package com.example.myapplication.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Community.Adapter;
import com.example.myapplication.Community.Model;
import com.example.myapplication.Community.PublishActivity;
import com.example.myapplication.databinding.FragmentCommunityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
public class Community extends Fragment{FragmentCommunityBinding binding;
    ArrayList<Model> list;
    Adapter adapter;
    Model model;
    private String currentUserId;

    public Community() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRv();
        setusearchview();
        setPublishButtonOnClick();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setusearchview() {
        binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String newText) {
        ArrayList<Model> filtered_list = new ArrayList<>();
        for(Model item:list){
            if (item.getTitle().toLowerCase().contains(newText)){
                filtered_list.add(item);
            }
        }
        if (filtered_list.isEmpty()){
            //
        }
        else{
            adapter.filter_list(filtered_list);
        }
    }

    private void setupRv() {
        list = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Blogs").orderBy("timestamp").addSnapshotListener((value, error) -> {
            list.clear();
            if (value != null) {
                for (DocumentSnapshot snapshot:value.getDocuments()){
                    model = snapshot.toObject(Model.class);
                    if (model != null) {
                        model.setId(snapshot.getId());
                    }
                    list.add(model);
                }
            }
            adapter.notifyDataSetChanged();
        });
        adapter = new Adapter(getContext(),list, currentUserId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        binding.rvBlogs.setLayoutManager(linearLayoutManager);
        binding.rvBlogs.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
    private void setPublishButtonOnClick() {
        binding.btnPublish.setOnClickListener(v -> {
            // Handle button click, start the PublishActivity here
            startActivity(new Intent(getActivity(), PublishActivity.class));
        });
    }
}
