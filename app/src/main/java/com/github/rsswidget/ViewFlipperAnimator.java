package com.github.rsswidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RemoteViews;
import android.widget.ViewFlipper;

@RemoteViews.RemoteView
public class ViewFlipperAnimator extends ViewFlipper
{
    public ViewFlipperAnimator(Context context)
    {
        super(context);
    }

    public ViewFlipperAnimator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
}
