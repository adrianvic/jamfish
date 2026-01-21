package org.adrianvictor.geleia.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.transition.Transition;
import org.adrianvictor.geleia.R;
import org.adrianvictor.geleia.glide.palette.BitmapPaletteTarget;
import org.adrianvictor.geleia.glide.palette.BitmapPaletteWrapper;
import org.adrianvictor.geleia.util.ThemeUtil;

public abstract class CustomPaletteTarget extends BitmapPaletteTarget {
    public CustomPaletteTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        onColorReady(getDefaultFooterColor());
    }

    @Override
    public void onResourceReady(@NonNull BitmapPaletteWrapper resource, Transition<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(ThemeUtil.getColor(resource.getPalette(), getDefaultFooterColor()));
    }

    protected int getDefaultFooterColor() {
        return ThemeUtil.getColorResource(getView().getContext(), R.attr.defaultFooterColor);
    }

    protected int getAlbumArtistFooterColor() {
        return ThemeUtil.getColorResource(getView().getContext(), R.attr.cardBackgroundColor);
    }

    public abstract void onColorReady(int color);
}
