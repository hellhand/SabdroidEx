package com.sabdroidex.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Marc on 9/06/13.
 */
public class AlphaImageView extends ImageView {

    public AlphaImageView(Context context) {
        super(context);
    }

    public AlphaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed) {
            setAlpha(127);
        }
        else {
            setAlpha(255);
        }
        super.setPressed(pressed);
    }
}
