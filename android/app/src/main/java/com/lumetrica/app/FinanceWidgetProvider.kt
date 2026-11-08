package com.lumetrica.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class FinanceWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it) }
    }
    companion object {
        const val PREFS = "lumetrica_widget"
        const val DATA_KEY = "finance_data"
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, FinanceWidgetProvider::class.java))
            ids.forEach { render(context, manager, it) }
        }
        private fun money(value: Double): String =
            NumberFormat.getCurrencyInstance(Locale("es", "CR")).format(value)
        private fun render(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.finance_widget)
            val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(DATA_KEY, "{}")
            val data = try { JSONObject(raw ?: "{}") } catch (_: Exception) { JSONObject() }
            views.setTextViewText(R.id.w_available, "Disponible: " + money(data.optDouble("available")))
            views.setTextViewText(R.id.w_expenses, "Gastos del mes: " + money(data.optDouble("expenses")))
            views.setTextViewText(R.id.w_payments, "Próximos pagos: " + list(data.optJSONArray("upcoming"), "name", "amount"))
            views.setTextViewText(R.id.w_accounts, "Cuentas: " + list(data.optJSONArray("accounts"), "name", "balance"))
            views.setTextViewText(R.id.w_income, "Ingresos recientes: " + list(data.optJSONArray("recentIncome"), "name", "amount"))
            views.setTextViewText(R.id.w_chart, "Gastos por categoría: " + list(data.optJSONArray("chart"), "name", "value"))
            val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            launch?.let {
                val pending = PendingIntent.getActivity(context, 0, it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                views.setOnClickPendingIntent(R.id.widget_root, pending)
            }
            manager.updateAppWidget(id, views)
        }
        private fun list(array: org.json.JSONArray?, nameKey: String, valueKey: String): String {
            if (array == null || array.length() == 0) return "sin datos"
            return (0 until minOf(array.length(), 2)).joinToString(" · ") { i ->
                val item = array.optJSONObject(i) ?: JSONObject()
                val name = item.optString(nameKey, "Dato")
                val value = item.optDouble(valueKey, 0.0)
                name + " " + money(value)
            }
        }
    }
}
