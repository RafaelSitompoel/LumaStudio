package com.example.lumastudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumastudio.utils.MediaItem;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MediaItem item);
    }

    private final Context context;
    private final List<MediaItem> mediaItems;
    private final OnItemClickListener listener;

    public GalleryAdapter(Context context, List<MediaItem> mediaItems, OnItemClickListener listener) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem item = mediaItems.get(position);

        holder.iconVideoBadge.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Bitmap thumbnail = context.getContentResolver().loadThumbnail(item.getUri(), new Size(200, 200), null);
                holder.imgThumbnail.setImageBitmap(thumbnail);
            } else {
                holder.imgThumbnail.setImageURI(item.getUri());
            }
        } catch (Exception e) {
            holder.imgThumbnail.setImageResource(R.drawable.ic_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgThumbnail;
        final ImageView iconVideoBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            iconVideoBadge = itemView.findViewById(R.id.icon_video_badge);
        }
    }
}