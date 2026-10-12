package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoryOfDayWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { intent ->
            intent.putExtra("ACTION_FROM_WIDGET", "MEMORY_OF_DAY")
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val memories = try {
                db.memoryDao().getAllMemoriesOnce()
            } catch (e: Exception) {
                emptyList()
            }

            val randomMemory = if (memories.isNotEmpty()) {
                memories.random()
            } else null

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_memory_of_the_day).apply {
                    if (randomMemory != null) {
                        setTextViewText(R.id.widget_memory_title, randomMemory.title)
                        setTextViewText(R.id.widget_memory_desc, randomMemory.description)
                        setTextViewText(R.id.widget_date, randomMemory.date.ifEmpty { "2023-07-26" })
                        
                        val imgRes = when {
                            randomMemory.photoResNames.contains("img_elder_portrait") -> R.drawable.img_elder_portrait_1785079069040
                            randomMemory.photoResNames.contains("img_anniversary_golden") -> R.drawable.img_anniversary_golden_1785079056294
                            else -> R.drawable.img_family_reunion_1785079042483
                        }
                        setImageViewResource(R.id.widget_image, imgRes)
                    } else {
                        setTextViewText(R.id.widget_memory_title, "金色记忆 · 那年今日")
                        setTextViewText(R.id.widget_memory_desc, "点击进入相册重温四代同堂温馨时刻")
                        setTextViewText(R.id.widget_date, "今天")
                        setImageViewResource(R.id.widget_image, R.drawable.img_family_reunion_1785079042483)
                    }

                    setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
