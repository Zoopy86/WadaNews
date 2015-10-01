package com.zinc.zoopy.wadanews;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

/**
 * Created by Administrator on 30-09-15.
 */
public class MyFactory implements RemoteViewsFactory {

    ArrayList<WNews> data = new ArrayList<WNews>();
    Context mContext = null;
    int mWidgetID;

    MyFactory(Context context, Intent intent){
        this.mContext = context;
        mWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        loadListItem();
    }

    @Override
    public void onCreate() {
        data = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        if(RemoteFetchService.data !=null ){
            data = (ArrayList) RemoteFetchService.data.clone();
            Log.i("checking", data.get(0).getAuthor());
        }
    }

    @Override
    public void onDestroy() {

    }

    private void loadListItem(){
        if(RemoteFetchService.data != null){
            data = (ArrayList<WNews>) RemoteFetchService.data.clone();
        }
        else {
            data = new ArrayList<WNews>();
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        WNews item = data.get(position);
        views.setTextViewText(R.id.w_author, item.getAuthor());
        views.setTextViewText(R.id.w_title, item.getTitle());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
