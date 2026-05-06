package com.example.tosswidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.tosswidget.R
import com.example.tosswidget.logic.TossLogic
import com.example.tosswidget.logic.TossResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TossWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOSS = "com.example.tosswidget.ACTION_TOSS"
        private val widgetStates = mutableMapOf<Int, Boolean>() // true = expanded, false = small
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOSS) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Toggle state or just perform toss if already expanded
                if (widgetStates[appWidgetId] != true) {
                    widgetStates[appWidgetId] = true
                }
                performAnimatedToss(context, appWidgetId)
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val isExpanded = widgetStates[appWidgetId] ?: false
        val layoutId = if (isExpanded) R.layout.widget_expanded else R.layout.widget_small
        val views = RemoteViews(context.packageName, layoutId)

        // Intent to trigger toss/expand
        val intent = Intent(context, TossWidgetProvider::class.java).apply {
            action = ACTION_TOSS
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (isExpanded) {
            views.setOnClickPendingIntent(R.id.btn_retry, pendingIntent)
            views.setOnClickPendingIntent(R.id.coin_image_expanded, pendingIntent)
        } else {
            views.setOnClickPendingIntent(R.id.widget_root_small, pendingIntent)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun performAnimatedToss(context: Context, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val flipFrames = listOf(
            R.drawable.ic_coin_flip_1,
            R.drawable.ic_coin_flip_2,
            R.drawable.ic_coin_flip_1,
            R.drawable.ic_coin_heads,
            R.drawable.ic_coin_flip_1,
            R.drawable.ic_coin_flip_2,
            R.drawable.ic_coin_flip_1,
            R.drawable.ic_coin_tails
        )

        CoroutineScope(Dispatchers.Main).launch {
            val views = RemoteViews(context.packageName, R.layout.widget_expanded)
            
            // Animation frames
            for (frame in flipFrames) {
                views.setImageViewResource(R.id.coin_image_expanded, frame)
                views.setTextViewText(R.id.result_text, "Tossing...")
                appWidgetManager.updateAppWidget(appWidgetId, views)
                delay(100)
            }

            // Final Result
            val result = TossLogic.performToss(context)
            val finalDrawable = if (result == TossResult.HEADS) R.drawable.ic_coin_heads else R.drawable.ic_coin_tails
            val finalText = if (result == TossResult.HEADS) context.getString(R.string.heads) else context.getString(R.string.tails)

            views.setImageViewResource(R.id.coin_image_expanded, finalDrawable)
            views.setTextViewText(R.id.result_text, finalText)
            
            // Re-bind click listener for retry
            val intent = Intent(context, TossWidgetProvider::class.java).apply {
                action = ACTION_TOSS
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_retry, pendingIntent)
            views.setOnClickPendingIntent(R.id.coin_image_expanded, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
