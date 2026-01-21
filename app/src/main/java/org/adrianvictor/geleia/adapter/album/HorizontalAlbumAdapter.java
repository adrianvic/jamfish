package org.adrianvictor.geleia.adapter.album;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.adrianvictor.geleia.util.ThemeUtil;
import org.adrianvictor.geleia.glide.CustomGlideRequest;
import org.adrianvictor.geleia.glide.CustomPaletteTarget;
import org.adrianvictor.geleia.helper.HorizontalAdapterHelper;
import org.adrianvictor.geleia.interfaces.CabHolder;
import org.adrianvictor.geleia.model.Album;
import org.adrianvictor.geleia.util.MusicUtil;

import java.util.List;

public class HorizontalAlbumAdapter extends AlbumAdapter {
    public HorizontalAlbumAdapter(@NonNull AppCompatActivity activity, List<Album> dataSet, boolean usePalette, @Nullable CabHolder cabHolder) {
        super(activity, dataSet, HorizontalAdapterHelper.LAYOUT_RES, usePalette, cabHolder);
    }

    @Override
    protected ViewHolder createViewHolder(View view, int viewType) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        HorizontalAdapterHelper.applyMarginToLayoutParams(activity, params, viewType);

        return new ViewHolder(view);
    }

    @Override
    protected void setColors(int color, ViewHolder holder) {
        CardView card = (CardView) holder.itemView;
        card.setCardBackgroundColor(color);

        if (holder.title != null) {
            holder.title.setTextColor(ThemeUtil.getPrimaryTextColor(activity, color));
        }

        if (holder.text != null) {
            holder.text.setTextColor(ThemeUtil.getSecondaryTextColor(activity, color));
        }
    }

    @Override
    protected void loadAlbumCover(Album album, final ViewHolder holder) {
        if (holder.image == null) return;

        CustomGlideRequest.Builder
                .from(activity, album.primary, album.blurHash)
                .palette().build()
                .into(new CustomPaletteTarget(holder.image) {
                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                        setColors(getAlbumArtistFooterColor(), holder);
                    }

                    @Override
                    public void onColorReady(int color) {
                        if (usePalette) {
                            setColors(color, holder);
                        } else {
                            setColors(getAlbumArtistFooterColor(), holder);
                        }
                    }
                });
    }

    @Override
    protected String getAlbumText(Album album) {
        return MusicUtil.getYearString(album.year);
    }

    @Override
    public int getItemViewType(int position) {
        return HorizontalAdapterHelper.getItemViewType(position, getItemCount());
    }
}
