package com.example.testapp;

import static android.widget.Toast.*;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupFragment extends Fragment {

    ArrayList<PostModel> postModelArrayList = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_group,container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        populateRecyclerView();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(new AdapterPost(view.getContext(), postModelArrayList, new AdapterPost.AdapterPostListener() {
            @Override
            public void onLikeClicked(View v, int position) {
                Toast.makeText(getContext(), "Clicked liked on post " + position + " With " + postModelArrayList.get(position).getLikes() + " likes.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShareClicked(View v, int position) {
                Toast.makeText(getContext(), "Clicked Share on post " + position + " With " + postModelArrayList.get(position).getLikes() + " likes.", Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void populateRecyclerView() {

        PostModel postModel = new PostModel(1, 5, R.drawable.pfp_2, 0, "Tommy Waltion1", "2 Hrs", "Yeetus Deletus!");
        postModelArrayList.add(postModel);
        postModel = new PostModel(2, 15, R.drawable.pfp_1, R.drawable.background, "Tommy Waltion2", "2 Hrs", "Yeetus Deletus!");
        postModelArrayList.add(postModel);
        postModel = new PostModel(3, 25, R.drawable.pfp_2, 0, "Tommy Waltion3", "2 Hrs", "Yeetus Deletus!");
        postModelArrayList.add(postModel);
        postModel = new PostModel(4, 69, R.drawable.pfp_1, R.drawable.background, "Tommy Waltion4", "2 Hrs", "Yeetus Deletus!");
        postModelArrayList.add(postModel);
        postModel = new PostModel(5, 99, R.drawable.pfp_2, 0, "Tommy Waltion5", "2 Hrs", "Yeetus Deletus!");
        postModelArrayList.add(postModel);
        //adapterPost.notifyDataSetChanged();
    }
}