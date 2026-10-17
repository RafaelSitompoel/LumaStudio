package com.example.lumastudio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumastudio.R;
import com.example.lumastudio.models.FrameModel;

import java.util.List;

public class FrameGalleryAdapter extends RecyclerView.Adapter<FrameGalleryAdapter.ViewHolder> {

    public interface OnFrameActionListener {
        void onPreview(FrameModel frame);
        void onUse(FrameModel frame);
    }

    private final Context context;
    private final List<FrameModel> frames;
    private final OnFrameActionListener listener;

    public FrameGalleryAdapter(Context context, List<FrameModel> frames, OnFrameActionListener listener) {
        this.context = context;
        this.frames = frames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_frame_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FrameModel frame = frames.get(position);

        holder.tvName.setText(frame.getName());
        holder.tvCategory.setText(frame.getCategory() + " • " + frame.getSlotCount() + " Slot • " + frame.getAspectRatio());

        // Render Real Visual Frame Preview
        holder.framePreviewBox.setBackgroundColor(frame.getBackgroundColor());
        holder.slotsContainer.removeAllViews();

        int slotsToDraw = Math.min(6, frame.getSlotCount());
        for (int i = 0; i < slotsToDraw; i++) {
            View slot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(6, 6, 6, 6);
            slot.setLayoutParams(params);
            slot.setBackgroundColor(frame.getFrameColor());
            holder.slotsContainer.addView(slot);
        }

        holder.btnPreview.setOnClickListener(v -> {
            if (listener != null) listener.onPreview(frame);
        });

        holder.btnUse.setOnClickListener(v -> {
            if (listener != null) listener.onUse(frame);
        });
    }

    @Override
    public int getItemCount() {
        return frames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvCategory;
        final FrameLayout framePreviewBox;
        final LinearLayout slotsContainer;
        final Button btnPreview, btnUse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_frame_name);
            tvCategory = itemView.findViewById(R.id.tv_frame_category);
            framePreviewBox = itemView.findViewById(R.id.frame_preview_box);
            slotsContainer = itemView.findViewById(R.id.slots_preview_container);
            btnPreview = itemView.findViewById(R.id.btn_preview);
            btnUse = itemView.findViewById(R.id.btn_use);
        }
    }
}