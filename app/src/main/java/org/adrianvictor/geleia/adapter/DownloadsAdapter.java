package org.adrianvictor.geleia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.model.Song;

import java.util.ArrayList;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {
    private final List<Song> mSongs;
    private final int mLayoutId;

    public DownloadsAdapter(int layoutId) {
        mLayoutId = layoutId;
        this.mSongs = new ArrayList<>();
    }

    public void swapDataSet(List<Song> newSongs) {
        mSongs.clear();
        if (newSongs != null) {
            mSongs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DownloadsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsAdapter.ViewHolder holder, int position) {
        final Song song = mSongs.get(position);

        holder.title.setText(song.title);
        holder.artist.setText(song.artistName);

        // TODO: Load album cover into holder.cover using Glide
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView artist;
        public final ImageView cover;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            artist = itemView.findViewById(R.id.text);
            cover = itemView.findViewById(R.id.image);
        }
    }
}
