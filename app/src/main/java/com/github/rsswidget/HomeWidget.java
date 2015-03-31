package com.github.rsswidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class HomeWidget extends AppWidgetProvider
{
    private static final String TAG = HomeWidget.class.getSimpleName();
    private static final String PREVIOUS_ACTION = "PREVIOUS";
    private static final String NEXT_ACTION = "NEXT";
    private static final String CLICK_ACTION = "CLICK";
    private RemoteViews mRemoteViews;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int i = 0; i < appWidgetIds.length; i++)
        {
            int widgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.home_widget);
            InitPager(context, remoteViews, widgetId);
            mRemoteViews = remoteViews;
            remoteViews.setTextViewText(R.id.textView, String.valueOf(appWidgetIds[i]));
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void InitPager(Context context, RemoteViews remoteViews, int appWidgetId)
    {
        final Intent ClickIntent = new Intent(context, HomeWidget.class);
        ClickIntent.setAction(HomeWidget.CLICK_ACTION);
        ClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        final Intent PreviousIntent = new Intent(context, HomeWidget.class);
        PreviousIntent.setAction(HomeWidget.PREVIOUS_ACTION);
        PreviousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        final Intent NextIntent = new Intent(context, HomeWidget.class);
        NextIntent.setAction(HomeWidget.NEXT_ACTION);
        NextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        final PendingIntent ClickPendingIntent = PendingIntent.getBroadcast(context, 0, ClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent PreviousPendingIntent = PendingIntent.getBroadcast(context, 0, PreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent NextPendingIntent = PendingIntent.getBroadcast(context, 0, NextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.Root, NextPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.action_previous, PreviousPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.action_next, NextPendingIntent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
        {
            Toast.makeText(context, "Widget deleted" + String.valueOf(appWidgetIds[i]), Toast.LENGTH_LONG).show();
            //HomeWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context)
    {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context)
    {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(PREVIOUS_ACTION) || intent.getAction().equals(NEXT_ACTION))
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.home_widget);
            final ComponentName mComponentName = new ComponentName(context, HomeWidget.class);
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
            int id = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            //Toast.makeText(context.getApplicationContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();

            if(intent.getAction().equals(PREVIOUS_ACTION))
            {
                remoteViews.showPrevious(R.id.MainContainer);
            }
            else
            {
                remoteViews.showNext(R.id.MainContainer);
            }

            mAppWidgetManager.updateAppWidget(id, remoteViews);
            //mAppWidgetManager.updateAppWidget(cn, remoteViews);
        }
        else if(intent.getAction().equals(CLICK_ACTION))
        {
            //Toast.makeText(context.getApplicationContext(), "SALUT", Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    /*static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId)
    {
        final CharSequence widgetText = HomeWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);

        AsyncTask<Object, Void, Void> LoadRemoteImage = new AsyncTask<Object, Void, Void>()
        {
            @Override
            protected Void doInBackground(Object... params)
            {
                return null;
            }
        };

        AsyncTask<String, Void, Void> RSSTask = new AsyncTask<String, Void, Void>()
        {
            @Override
            protected Void doInBackground(String... params)
            {
                RSSReader reader = new RSSReader();
                try
                {
                    RSSFeed feed = reader.load(params[0]);
                    List<RSSItem> items = feed.getItems();

                    views.setTextViewText(R.id.SourceTitle, feed.getTitle());

                    RSSItem item = items.get(0);
                    views.setTextViewText(R.id.FeedTitle, item.getTitle().trim());
                    views.setTextViewText(R.id.FeedDescription, item.getDescription().replace(System.getProperty("line.separator"), "").trim());
                    views.setImageViewResource(R.id.FeedPhoto, R.drawable.helicopter);
                    //views.setImageViewUri(R.id.FeedPhoto, Uri.parse("http://rt.com/static/img/static/logo.png"));
                    //views.setImageViewBitmap(R.id.FeedPhoto, BitmapFactory.decodeResource(context.getResources(), R.drawable.helicopter));
                    //setBitmap(views, R.id.FeedPhoto, BitmapFactory.decodeResource(context.getResources(), R.drawable.helicopter));
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }
                catch (RSSReaderException e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        };
        RSSTask.execute("http://francais.rt.com/rss/");

        //PkRSS.with(context.getApplicationContext()).load("http://francais.rt.com/rss/").async();
        //List<Article> articleList = PkRSS.with(context.getApplicationContext()).get("http://francais.rt.com/rss/");
        //Log.i("result", articleList.get(0).getTitle());

        //views.setTextViewText(R.id.textView2, "salut ca va");


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }*/

    /*public static void setBitmap(RemoteViews views, int resId, Bitmap bitmap)
    {
        Bitmap proxy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(proxy);
        c.drawBitmap(bitmap, new Matrix(), null);
        views.setImageViewBitmap(resId, proxy);
    }

    public static void LoadingImage(final Context context, final ImageView imageView, final String url)
    {
        final AsyncTask<String, Void, Bitmap> LoadingImageTask = new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                HttpURLConnection httpURLConnection = null;

                try
                {
                    httpURLConnection = (HttpURLConnection) (new URL(params[0])).openConnection();
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

            protected void onPostExecute(Bitmap bitmap)
            {
                imageView.setImageBitmap(bitmap);
            }
        };
        LoadingImageTask.execute(url);
    }*/
}
