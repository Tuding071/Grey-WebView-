package com.grey.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// ── Colours ──────────────────────────────────────────────────────────────────
private val BG        = Color(0xFF121212)
private val SURFACE   = Color(0xFF1C1C1E)
private val FIELD_BG  = Color(0xFF2C2C2E)
private val BORDER    = Color(0xFF3A3A3C)
private val WHITE     = Color(0xFFFFFFFF)
private val MUTED     = Color(0xFF8E8E93)
private val ACCENT    = Color(0xFF636366)
private val PROGRESS  = Color(0xFF0A84FF)

// ── Entry point ───────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GreyApp() }
    }
}

// ── Root composable ───────────────────────────────────────────────────────────
@Composable
fun GreyApp() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        GreyBrowser()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GreyBrowser() {
    val HOME = "https://www.google.com"

    var webView        by remember { mutableStateOf<WebView?>(null) }
    var urlField       by remember { mutableStateOf(TextFieldValue(HOME)) }
    var displayUrl     by remember { mutableStateOf(HOME) }
    var progress       by remember { mutableStateOf(0) }
    var loading        by remember { mutableStateOf(false) }
    var canGoBack      by remember { mutableStateOf(false) }
    var canGoForward   by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    fun navigate(input: String) {
        val url = normalizeUrl(input)
        displayUrl = url
        urlField = TextFieldValue(url)
        webView?.loadUrl(url)
        focusManager.clearFocus()
    }

    BackHandler(enabled = canGoBack) { webView?.goBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG)
            .systemBarsPadding()
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Surface(
            color = SURFACE,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Back
                    IconButton(
                        onClick = { webView?.goBack() },
                        enabled = canGoBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack) WHITE else ACCENT
                        )
                    }

                    // Forward
                    IconButton(
                        onClick = { webView?.goForward() },
                        enabled = canGoForward
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) WHITE else ACCENT
                        )
                    }

                    // URL field
                    TextField(
                        value = urlField,
                        onValueChange = { urlField = it },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { state ->
                                if (state.isFocused) {
                                    // Select all text when focused
                                    urlField = urlField.copy(
                                        selection = TextRange(0, urlField.text.length)
                                    )
                                }
                            },
                        singleLine = true,
                        placeholder = {
                            Text(
                                "Search or enter URL",
                                color = MUTED,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { navigate(urlField.text) }
                        ),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = WHITE,
                            unfocusedTextColor = WHITE,
                            focusedContainerColor = FIELD_BG,
                            unfocusedContainerColor = FIELD_BG,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = PROGRESS,
                            focusedPlaceholderColor = MUTED,
                            unfocusedPlaceholderColor = MUTED,
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                    )

                    // Refresh / Stop
                    IconButton(onClick = {
                        if (loading) webView?.stopLoading() else webView?.reload()
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = if (loading) "Stop" else "Refresh",
                            tint = WHITE
                        )
                    }
                }

                // ── Progress bar ──────────────────────────────────────────────
                AnimatedVisibility(visible = loading) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        color = PROGRESS,
                        trackColor = BORDER,
                    )
                }

                HorizontalDivider(color = BORDER, thickness = 0.5.dp)
            }
        }

        // ── WebView ───────────────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    with(settings) {
                        javaScriptEnabled      = true
                        domStorageEnabled      = true
                        loadWithOverviewMode   = true
                        useWideViewPort        = true
                        builtInZoomControls    = true
                        displayZoomControls    = false
                        setSupportZoom(true)
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView, newProgress: Int) {
                            progress = newProgress
                            loading  = newProgress < 100
                        }
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                            loading      = true
                            displayUrl   = url
                            urlField     = TextFieldValue(url)
                            canGoBack    = view.canGoBack()
                            canGoForward = view.canGoForward()
                        }
                        override fun onPageFinished(view: WebView, url: String) {
                            loading      = false
                            displayUrl   = url
                            urlField     = TextFieldValue(url)
                            canGoBack    = view.canGoBack()
                            canGoForward = view.canGoForward()
                        }
                    }
                    loadUrl(HOME)
                    webView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ── URL helper ────────────────────────────────────────────────────────────────
private fun normalizeUrl(input: String): String {
    val t = input.trim()
    return when {
        t.startsWith("http://") || t.startsWith("https://") -> t
        t.contains(" ") || !t.contains(".") ->
            "https://www.google.com/search?q=${t.replace(" ", "+")}"
        else -> "https://$t"
    }
}
