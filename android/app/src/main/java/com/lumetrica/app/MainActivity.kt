package com.lumetrica.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                if (uri.scheme == "https" && uri.host == "nexus-alone-1912.github.io") return false
                return true
            }
        }
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(NativeWidgetBridge(this), "LumetricaNative")
        setContentView(webView)
        if (savedInstanceState == null) {
            webView.loadUrl("https://nexus-alone-1912.github.io/LumetricaMovil-BETA-1.4/")
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}

class NativeWidgetBridge(private val activity: Activity) {
    @JavascriptInterface
    fun updateWidgetData(json: String) {
        try {
            val data = JSONObject(json)
            activity.getSharedPreferences(FinanceWidgetProvider.PREFS, Activity.MODE_PRIVATE)
                .edit().putString(FinanceWidgetProvider.DATA_KEY, data.toString()).apply()
            FinanceWidgetProvider.refresh(activity)
        } catch (_: Exception) {
            // An invalid widget snapshot must never interrupt the web app.
        }
    }
}
