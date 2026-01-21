package org.adrianvictor.geleia.model;

import androidx.annotation.StyleRes;

import org.adrianvictor.geleia.R;

public enum Theme {
    LIGHT(R.style.Theme_Phonograph_Light),
    DARK(R.style.Theme_Phonograph),
    BLACK(R.style.Theme_Phonograph_Black);

    @StyleRes
    public final int style;

    Theme(int style) {
        this.style = style;
    }
}
