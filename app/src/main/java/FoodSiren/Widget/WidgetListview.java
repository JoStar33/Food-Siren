package FoodSiren.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.eml_listview_test3.R;

import FoodSiren.Activity.ManageActivity;
import FoodSiren.Activity.TipActivity;

public class WidgetListview extends AppWidgetProvider {
    /**
     *
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_listview);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     *
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }

        Intent serviceIntent = new Intent(context, MyRemoteViewsService.class);
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_listview);
        widget.setRemoteAdapter(R.id.widget_listview, serviceIntent);


        Intent intent = new Intent(context, ManageActivity.class);
        Intent listIntent= new Intent(context, TipActivity.class);
//        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent widgetListIntent=PendingIntent.getActivity(context,0,listIntent,0);


        widget.setOnClickPendingIntent(R.id.widget_imageButton, pendingIntent);
        widget.setPendingIntentTemplate(R.id.widget_listview,widgetListIntent);


        appWidgetManager.updateAppWidget(appWidgetIds, widget);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

//        if (intent.getAction().equals(Intent.ACTION_MAIN)) {
//            intent = new Intent(context, ManageActivity.class);
//            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//
//        }

    }
}
