package com.github.rsswidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
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

        LoadFavicon(mContext, mFeedListIcon, values.get(position).URL);
        
        mFeedListTitle.setText(values.get(position).Name);
        mFeedListDescription.setText(values.get(position).URL);

        rowView.setTag(values.get(position));

        return rowView;
    }


    public static void LoadFavicon(final Context context, final ImageView imageView, final String url)
    {
        final AsyncTask<String, Void, Bitmap> FaviconTask = new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                try
                {
                    //Document doc = Jsoup.connect("http://www.google.com/s2/favicons?domain=" + params[0]).timeout(30000).get();
                    //Element favicon = doc.head().select("link[href~=.*\\.ico]").first();
                    HttpURLConnection httpURLConnection = null;

                    try
                    {
                        httpURLConnection = (HttpURLConnection) (new URL("http://www.google.com/s2/favicons?domain=" + params[0])).openConnection();
                        return BitmapFactory.decodeStream(httpURLConnection.getInputStream(), null, new BitmapFactory.Options());
                    }
                    catch (UnknownHostException e)
                    {
                        cancel(true);
                        return null;
                    }
                    catch (Exception e)
                    {
                        cancel(true);
                        e.printStackTrace();
                        return null;
                    }
                    finally
                    {
                        if (httpURLConnection != null)
                        {
                            httpURLConnection.disconnect();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(Bitmap bitmap)
            {
                imageView.setImageBitmap(bitmap);
            }
        }.execute(url);
    }
}