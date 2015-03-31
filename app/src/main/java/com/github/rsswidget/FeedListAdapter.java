package com.github.rsswidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FeedListAdapter extends ArrayAdapter<EnumFeed>
{
    private final Context mContext;
    private final List<EnumFeed> values;

    public FeedListAdapter(Context context, List<EnumFeed> values)
    {
        super(context, R.layout.model_feedlist, values);
        this.mContext = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.model_feedlist, parent, false);

        ImageView mFeedListIcon = (ImageView)rowView.findViewById(R.id.model_feedList_icon);
        TextView mFeedListTitle = (TextView)rowView.findViewById(R.id.model_feedList_title);
        TextView mFeedListDescription = (TextView)rowView.findViewById(R.id.model_feedList_description);
        
        if(values.get(position).Icon != null)
        {
            mFeedListIcon.setImageDrawable(values.get(position).Icon);
        }
        
        mFeedListTitle.setText(values.get(position).Name);
        mFeedListDescription.setText(values.get(position).URL);

        rowView.setTag(values.get(position));

        return rowView;
    }
}