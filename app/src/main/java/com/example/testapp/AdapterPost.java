package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyViewHolder> {

    Context context;
    ArrayList<PostModel> postModelArrayList;
    RequestManager glide;
    public static AdapterPostListener onClickListener;

    public interface AdapterPostListener {
        void onLikeClicked(View v, int position);
        void onShareClicked(View v, int position);
    }

    public AdapterPost(Context context,ArrayList<PostModel> postModelArrayList, AdapterPostListener listener) {
        this.context = context;
        this.postModelArrayList = postModelArrayList;
        this.onClickListener = listener;
        glide = Glide.with(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_template, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final PostModel postModel = postModelArrayList.get(position);

        holder.post_creator_name.setText(postModel.getName());
        holder.post_create_time.setText(postModel.getTime());
        holder.post_like.setText(String.valueOf(postModel.getLikes()));
        holder.post_title.setText(postModel.getTitle());

        glide.load(postModel.getCreator_profile()).into(holder.post_creator_profile);

        if (postModel.getPost_image() == 0) {
            holder.post_image.setVisibility(View.GONE);
        }else{
            holder.post_image.setVisibility(View.VISIBLE);
            glide.load(postModel.getPost_image()).into(holder.post_image);
        }

    }

    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView post_creator_name, post_create_time, post_like, post_title,post_like_btn,post_share_btn;
        ImageView post_creator_profile, post_image;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            post_creator_profile = itemView.findViewById(R.id.post_creator_profile);
            post_image = itemView.findViewById(R.id.post_image);

            post_creator_name = itemView.findViewById(R.id.post_creator_name);
            post_create_time = itemView.findViewById(R.id.post_create_time);
            post_like = itemView.findViewById(R.id.post_like);
            post_title = itemView.findViewById(R.id.post_title);

            post_like_btn = itemView.findViewById(R.id.post_like_btn);
            post_share_btn = itemView.findViewById(R.id.post_share_btn);

            post_like_btn.setOnClickListener(v -> onClickListener.onLikeClicked(v, getAbsoluteAdapterPosition()));
            post_share_btn.setOnClickListener(v -> onClickListener.onShareClicked(v, getAbsoluteAdapterPosition()));

        }
    }
}
