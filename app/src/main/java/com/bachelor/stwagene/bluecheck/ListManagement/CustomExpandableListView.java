package com.bachelor.stwagene.bluecheck.ListManagement;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.widget.ExpandableListView;

import com.bachelor.stwagene.bluecheck.R;

/**
 * Created by stwagene on 30.05.2016.
 */
public class CustomExpandableListView extends ExpandableListView
{
    public CustomExpandableListView(Context context)
    {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(5000, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Nullable
    @Override
    public Drawable getDivider()
    {
        return new ColorDrawable(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public int getDividerHeight()
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
    }
}
