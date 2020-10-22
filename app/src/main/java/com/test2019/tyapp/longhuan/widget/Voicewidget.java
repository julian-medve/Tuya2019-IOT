package com.test2019.tyapp.longhuan.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.test2019.tyapp.longhuan.R;
import com.test2019.tyapp.longhuan.activity.MainActivity;


public class Voicewidget extends AppWidgetProvider {

    private static SharedPreferences sPrefs;
    private boolean isActivity;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i ++){
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("WidgetActivity", true);


            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.voicewidget);
            views.setOnClickPendingIntent(R.id.VoiceButton, pendingIntent);

//            Intent intent = new Intent(context, WidgetActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.voicewidget);
//            views.setOnClickPendingIntent(R.id.VoiceButton, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

//        if(sPrefs == null){
//            sPrefs = context.getSharedPreferences(WidgetActivity.PREFS_NAME, WidgetActivity.MODE_PRIVATE);
//        }
//
//        String result = sPrefs.getString(WidgetActivity.PREFS_VOICE_RECOGNITION_RESULT, null);
//
//        if(result == null){
//            result = "";
//        }
//
//        final int N = appWidgetIds.length;
//
//        for (int i = 0; i < N; i ++){
//            int appWidgetId = appWidgetIds[i];
//
//            Intent intent = new Intent(context, WidgetActivity.class);
//            intent.setData(Uri.parse(String.valueOf(appWidgetId)));
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.voicewidget);
//            views.setOnClickPendingIntent(R.id.VoiceButton, pendingIntent);
//            views.setTextViewText(R.id.message, result);
//
//            final PackageManager pm = context.getPackageManager();
//            final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
//
//            if(activities.size() != 0){
//                views.setOnClickPendingIntent(R.id.VoiceButton, pendingIntent);
//            }
//
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

