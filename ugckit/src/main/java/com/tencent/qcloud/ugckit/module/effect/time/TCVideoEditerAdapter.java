package com.tencent.qcloud.ugckit.module.effect.time;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.hardware.camera2.params.OisSample;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.tencent.qcloud.ugckit.R;

import java.util.ArrayList;

public class TCVideoEditerAdapter extends RecyclerView.Adapter<TCVideoEditerAdapter.ViewHolder> {
    private final Context mContext;
    @NonNull
    private ArrayList<Bitmap> data = new ArrayList<Bitmap>();

    public TCVideoEditerAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ugc_video_cut_thumb,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.thumb).load(data.get(position)).centerCrop().into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(int position, Bitmap b) {
        data.add(b);
        notifyItemInserted(position);
    }

    public void clearAllBitmap() {
        data.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ImageView thumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.ivThumb);
        }
    }

}
