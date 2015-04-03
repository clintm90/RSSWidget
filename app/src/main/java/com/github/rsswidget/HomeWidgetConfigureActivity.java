package com.github.rsswidget;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The configuration screen for the {@link HomeWidget HomeWidget} AppWidget.
 */
public class HomeWidgetConfigureActivity extends ActionBarActivity
{
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    private static final String PREFS_NAME = "com.github.rsswidget.HomeWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private SharedPreferences mPrefsGlobal;
    private SharedPreferences.Editor mStorageGlobal;

    public HomeWidgetConfigureActivity()
    {
        super();
    }

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        mPrefsGlobal = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mStorageGlobal = mPrefsGlobal.edit();

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.home_widget_configure);

        final ListView FeedList = (ListView) findViewById(R.id.RSSList);

        final List FEEDLIST = new ArrayList<EnumFeed>();
        final FeedListAdapter mFeedListAdapter = new FeedListAdapter(this, FEEDLIST);
        /*mFeedListAdapter.add(new EnumFeed(this, -1, "RT", "http://francais.rt.com/rss/", getResources().getDrawable(R.drawable.rt)));
        mFeedListAdapter.add(new EnumFeed(this, -1, "Réseau Voltaire", "http://www.voltairenet.org/spip.php?page=backend&id_secteur=1110&lang=fr", getResources().getDrawable(R.drawable.voltairenet)));
        mFeedListAdapter.add(new EnumFeed(this, -1, "Sputnik News", "http://fr.sputniknews.com/rss/", getResources().getDrawable(R.drawable.sputnik)));*/

        for(Map.Entry<String, ?> t : mPrefsGlobal.getAll().entrySet())
        {
            mFeedListAdapter.add(new EnumFeed(this, -1, t.getKey(), t.getValue().toString()));
        }

        FeedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                mStorageGlobal.remove(((EnumFeed)view.getTag()).Name);
                mStorageGlobal.apply();
                mFeedListAdapter.notifyDataSetChanged();
                FeedList.setAdapter(mFeedListAdapter);
                mFeedListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        FeedList.setAdapter(mFeedListAdapter);

        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        //findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
        {
            finish();
        }

        //mAppWidgetText.setText(loadTitlePref(HomeWidgetConfigureActivity.this, mAppWidgetId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void Add(MenuItem item)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final View modelNewFeed = getLayoutInflater().inflate(R.layout.model_newfeed, null);

        final EditText mModelNewFeedUrl = (EditText) modelNewFeed.findViewById(R.id.model_newfeed_url);

        alertDialogBuilder.setView(modelNewFeed);

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        alertDialogBuilder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                AsyncTask<String, Void, Void> ParseRSSFeed = new AsyncTask<String, Void, Void>()
                {
                    @Override
                    protected Void doInBackground(String... params)
                    {
                        RSSReader reader = new RSSReader();
                        try
                        {
                            RSSFeed feed = reader.load(params[0]);
                            mStorageGlobal.putString(feed.getTitle(), params[0]);
                            mStorageGlobal.apply();
                            mStorageGlobal.commit();
                        }
                        catch (Exception e)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Toast.makeText(getApplicationContext(), getString(R.string.norssurl), Toast.LENGTH_SHORT).show();
                                }
                            });
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(mModelNewFeedUrl.getText().toString());

                AsyncTask<String, Void, Elements> ParseHTMLTask = new AsyncTask<String, Void, Elements>()
                {
                    @Override
                    protected Elements doInBackground(String... params)
                    {
                        try
                        {
                            final Document doc = Jsoup.connect(params[0]).get();
                            Elements links = doc.select("head > link" +
                                    "[rel=alternate]" +
                                    "[type~=(application/(rss|(x(\\.|\\-))?atom|rdf)\\+|text/)xml]" +
                                    "[href~=.+]");

                            return links;
                        }
                        catch (IOException e)
                        {
                            cancel(true);
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Elements elements)
                    {
                        if(elements != null)
                        {
                            if (elements.size() > 0)
                            {
                                for (Element link : elements)
                                {
                                    mStorageGlobal.putString(link.attr("title"), link.attr("abs:href"));
                                }
                                mStorageGlobal.apply();
                                mStorageGlobal.commit();
                            }
                        }
                    }
                };//.execute(mModelNewFeedUrl.getText().toString());
            }
        });

        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    public void Close(MenuItem item)
    {
        final Context context = HomeWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        //String widgetText = mAppWidgetText.getText().toString();
        //saveTitlePref(context, mAppWidgetId, widgetText);

        // It is the responsibility of the configuration activity to update the app widget
        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        //HomeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    /*View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            final Context context = HomeWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //HomeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };*/

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null)
        {
            return titleValue;
        }
        else
        {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}



