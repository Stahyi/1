package breakbadhabits.android.app.appwidget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViewsService

class HabitsAppWidgetRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent) = HabitsAppWidgetRemoteViewsFactory(
        applicationContext,
        intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    )
}
