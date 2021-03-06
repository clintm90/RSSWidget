package com.github.rsswidget;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class EnumFeed implements Serializable
{
    public Context mContext;
    public int ID;
    public String Name;
    public String URL;

    public EnumFeed(Context context, int id, String name, String url)
    {
        mContext = context;
        ID = id;
        Name = name;
        URL = url;
    }
}
