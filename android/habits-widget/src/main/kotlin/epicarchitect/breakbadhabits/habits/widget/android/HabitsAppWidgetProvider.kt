package epicarchitect.breakbadhabits.habits.widget.android

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import epicarchitect.breakbadhabits.android.habits.widget.R
import epicarchitect.breakbadhabits.database.AppData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HabitsAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, widgetSystemIds: IntArray) {
        super.onUpdate(context, manager, widgetSystemIds)
        widgetSystemIds.forEach { widgetSystemId ->
            updateAppWidget(context, manager, widgetSystemId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        widgetSystemId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, widgetSystemId, newOptions)
        sendUpdateIntent(context)
    }

    override fun onDeleted(context: Context, widgetSystemIds: IntArray) {
        super.onDeleted(context, widgetSystemIds)
        widgetSystemIds.forEach {
            AppData.mainDatabase.habitWidgetQueries.deleteBySystemId(it)
        }
    }

    private fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetSystemId: Int) = runBlocking {
        val isDarkModeEnabled = context.resources.configuration.let {
            it.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        val widget = AppData.mainDatabase.habitWidgetQueries.selectBySystemId(widgetSystemId)
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .first()
        if (widget == null) {
            manager.updateAppWidget(
                widgetSystemId,
                RemoteViews(
                    context.packageName,
                    R.layout.habits_app_widget_light
                ).apply {
                    setTextViewText(R.id.title_textView, "Not found")
                    setViewVisibility(R.id.title_textView, View.VISIBLE)
                }
            )
            manager.notifyAppWidgetViewDataChanged(
                widgetSystemId,
                R.id.habits_listView
            )
            return@runBlocking
        }

        val views = RemoteViews(
            context.packageName,
            if (isDarkModeEnabled) {
                R.layout.habits_app_widget_dark
            } else {
                R.layout.habits_app_widget_light
            }
        ).apply {
            setTextViewText(R.id.title_textView, widget.title)
            setViewVisibility(
                R.id.title_textView,
                if (widget.title.isEmpty()) View.GONE else View.VISIBLE
            )
            setRemoteAdapter(
                R.id.habits_listView,
                Intent(context, HabitsAppWidgetRemoteViewsService::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetSystemId)
                    data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                }
            )
        }

        manager.updateAppWidget(widgetSystemId, views)
        manager.notifyAppWidgetViewDataChanged(widgetSystemId, R.id.habits_listView)
    }

    companion object {
        fun sendUpdateIntent(context: Context) = context.sendBroadcast(
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                component = ComponentName(context, HabitsAppWidgetProvider::class.java)
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    AppWidgetManager.getInstance(context).getAppWidgetIds(
                        ComponentName(
                            context,
                            HabitsAppWidgetProvider::class.java
                        )
                    )
                )
            }
        )
    }
}