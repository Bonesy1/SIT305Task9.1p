package com.example.lostandfoundapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    private List<Advert> advertList;
    private OnItemClickListener listener;

    // Interface for handling clicks
    public interface OnItemClickListener {
        void onItemClick(Advert advert);
    }

    // Constructor
    public AdvertAdapter(List<Advert> advertList, OnItemClickListener listener) {
        this.advertList = advertList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item_advert.xml layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        Advert currentAdvert = advertList.get(position);

        // Bind the data to the views
        holder.tvStatus.setText(currentAdvert.getPostType());
        holder.tvItemName.setText(currentAdvert.getItemName());
        holder.tvTimestamp.setText(holder.itemView.getContext().getString(R.string.timestamp_format, currentAdvert.getTimestamp()));

        if (currentAdvert.getImageUri() != null && !currentAdvert.getImageUri().isEmpty()) {
            holder.ivImage.setImageURI(Uri.parse(currentAdvert.getImageUri()));
            holder.ivImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        // Handle the click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentAdvert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return advertList.size();
    }

    // ViewHolder class to hold references to the views in item_advert.xml
    public static class AdvertViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvItemName, tvTimestamp;
        ImageView ivImage;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            ivImage = itemView.findViewById(R.id.iv_item_image);
        }
    }
}
