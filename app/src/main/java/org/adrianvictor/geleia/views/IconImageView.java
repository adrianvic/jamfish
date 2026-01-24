package org.adrianvictor.geleia.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import org.adrianvictor.geleia.util.ThemeUtil;
import org.adrianvictor.geleia.R;

public class IconImageView extends AppCompatImageView {

    public IconImageView(Context context) {
        super(context);
    }

    public IconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        if (getContext() == null) return;
        setColorFilter(ThemeUtil.getColorResource(getContext(), R.attr.iconColor), PorterDuff.Mode.SRC_IN);
    }
}
