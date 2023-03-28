package com.example.ArtShare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ArtShare.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final Context context;
    private final List<String> contentPaths;
    private final IContentListener contentListener;

    public GalleryAdapter(Fragment fragment, List<String> contentPaths, IContentListener contentListener) {
        this.context = fragment.getContext();
        this.contentPaths = contentPaths;
        this.contentListener = contentListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.galery_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contentPath = contentPaths.get(position);
        if (contentPath.endsWith("mp3")) {
            holder.galleryImage.setImageResource(R.drawable.sound_waves);
        } else {
            Glide.with(context).load(contentPath).into(holder.galleryImage);
        }

        holder.itemView.setOnClickListener(v -> contentListener.onPhotoClick(contentPath));
    }

    @Override
    public int getItemCount() {
        return contentPaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView galleryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            galleryImage = itemView.findViewById(R.id.gallery_image);
        }
    }

    public interface IContentListener {
        void onPhotoClick(String path);
    }
}
