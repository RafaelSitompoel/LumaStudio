package com.example.lumastudio;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lumastudio.adapters.FrameGalleryAdapter;
import com.example.lumastudio.models.FrameModel;
import com.example.lumastudio.utils.FrameCatalogManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class FrameGalleryActivity extends BaseActivity {

    private RecyclerView rvFrames;
    private TextView tvEmptyFrames;
    private EditText etSearch;
    private ChipGroup chipGroupCategories;

    private List<FrameModel> allFrames;
    private final List<FrameModel> filteredFrames = new ArrayList<>();
    private FrameGalleryAdapter adapter;
    private String selectedCategory = "Semua";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_gallery);

        allFrames = FrameCatalogManager.getAllFrames();
        filteredFrames.addAll(allFrames);

        initViews();
        setupCategories();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        rvFrames = findViewById(R.id.rv_frames);
        tvEmptyFrames = findViewById(R.id.tv_empty_frames);
        etSearch = findViewById(R.id.et_search_frame);
        chipGroupCategories = findViewById(R.id.chip_group_categories);

        btnBack.setOnClickListener(v -> finish());

        rvFrames.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FrameGalleryAdapter(this, filteredFrames, new FrameGalleryAdapter.OnFrameActionListener() {
            @Override
            public void onPreview(FrameModel frame) {
                showPreviewDialog(frame);
            }

            @Override
            public void onUse(FrameModel frame) {
                Toast.makeText(FrameGalleryActivity.this, "Menggunakan bingkai: " + frame.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FrameGalleryActivity.this, MainActivity.class);
                intent.putExtra("selected_frame_id", frame.getId());
                intent.putExtra("slot_count", frame.getSlotCount());
                startActivity(intent);
            }
        });
        rvFrames.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                filterFrames();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategories() {
        List<String> categories = FrameCatalogManager.getCategories();
        chipGroupCategories.removeAllViews();

        for (String cat : categories) {
            Chip chip = new Chip(this);
            chip.setText(cat);
            chip.setCheckable(true);
            if (cat.equals(selectedCategory)) {
                chip.setChecked(true);
            }
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategory = cat;
                    filterFrames();
                }
            });
            chipGroupCategories.addView(chip);
        }
    }

    private void filterFrames() {
        filteredFrames.clear();
        for (FrameModel frame : allFrames) {
            boolean matchesCat = selectedCategory.equals("Semua") || frame.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = searchQuery.isEmpty() || frame.getName().toLowerCase().contains(searchQuery);

            if (matchesCat && matchesQuery) {
                filteredFrames.add(frame);
            }
        }

        adapter.notifyDataSetChanged();
        if (filteredFrames.isEmpty()) {
            tvEmptyFrames.setVisibility(View.VISIBLE);
            rvFrames.setVisibility(View.GONE);
        } else {
            tvEmptyFrames.setVisibility(View.GONE);
            rvFrames.setVisibility(View.VISIBLE);
        }
    }

    private void showPreviewDialog(FrameModel frame) {
        new AlertDialog.Builder(this)
                .setTitle(frame.getName())
                .setMessage("Kategori: " + frame.getCategory() + "\nJumlah Slot: " + frame.getSlotCount() + " Foto\nRasio: " + frame.getAspectRatio() + "\n\nDeskripsi: " + frame.getDescription())
                .setPositiveButton("Gunakan Bingkai", (dialog, which) -> {
                    Intent intent = new Intent(FrameGalleryActivity.this, MainActivity.class);
                    intent.putExtra("selected_frame_id", frame.getId());
                    intent.putExtra("slot_count", frame.getSlotCount());
                    startActivity(intent);
                })
                .setNegativeButton("Tutup", null)
                .show();
    }
}