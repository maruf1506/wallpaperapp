package com.digiclack.wallpapers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.digiclack.unsplash.models.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {

    private List<Photo> photos;
    private Context context;

    public PhotoRecyclerAdapter(Context context) {
        photos = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Photo photo = photos.get(position);

        String a = photo.getUrls().getFull();

        Picasso.get().load(photo.getUrls().getSmall()).into(holder.imageView);


    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //showing interstitial ad
                    int itemPosition = getLayoutPosition();
                    Log.e("position:", String.valueOf(photos.get(itemPosition).getId()));
                    Intent i = new Intent(context, FullScreenDetailActivity.class);
                    i.putExtra("PHOTO_ID",photos.get(itemPosition).getId());
                    context.startActivity(i);
                }
            });
        }

    }
}
