package com.example.photomanagementapp;

import android.content.Context;
import android.telephony.BarringInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
/*
앨범 사진을 올려주는 액티비티
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private ArrayList<User> arrayList;
    private Context mContext;
    private static final String Tag = "RecyclerView";


    public RecyclerAdapter(Context mContext, ArrayList<User> arrayList){
        this.mContext = mContext;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .load(arrayList.get(position).getPhotoUri())
                .centerCrop()
                .into(holder.imageview);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends  RecyclerView .ViewHolder{

        ImageView imageview;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imageview = itemView.findViewById(R.id.imageView);
        }
    }




}