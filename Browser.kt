// ═══════════════════════════════════════════════════════════════════
// Grey Browser - V4.0 (WebView Multi-Tab)
// ═══════════════════════════════════════════════════════════════════
// === PART 0/10 — Theme Specification ===
// ═══════════════════════════════════════════════════════════════════
//
// THEME: Dark Grey + White Accents — No Round Corners
//
// COLOURS:
//   Background:    #121212  (deep dark grey)
//   Surface:       #1E1E1E  (elevated dark grey)
//   Field BG:      #1E1E1E  (same as surface)
//   Border:        White at 20% alpha  (subtle)
//   Border Active: White solid
//   Text Primary:  #FFFFFF  (white)
//   Text Muted:    #808080  (grey)
//   Accent:        #FFFFFF  (white — used for icons, buttons, progress)
//   Progress Bar:  #FFFFFF  (white fill behind URL text)
//   Delete:        #FF0000 at 30% alpha  (pending delete tab)
//   Toast BG:      White at 90% alpha
//   Toast Text:    #000000  (black on white toast)
//
// SHAPES:
//   Everything:    RectangleShape  (0dp corner radius)
//   Favicons:      CircleShape  (only exception)
//
// TYPOGRAPHY:
//   URL field:     14sp
//   Tab title:     14sp
//   Tab domain:    12sp
//   Headings:      18sp
//   Toast:         14sp
//   Menu items:    16sp
//   Group chips:   12sp (domain), 9sp (count)
//
// COMPONENT STYLES:
//   Buttons:       OutlinedButton, RectangleShape, white border, white text
//   TextFields:    RectangleShape, transparent bg, white border
//   Dividers:      0.5dp or 1dp, Color.DarkGray
//   Icons:         White when active, White at 30% when disabled
//   Switches:      White thumb, #444444 track
//   Dialogs:       #1E1E1E background, RectangleShape, white text
//   Dropdowns:     #1E1E1E background, 1dp white border, RectangleShape
//   Elevation:     0dp throughout (flat design)
//
// SPACING:
//   Top bar padding:    8dp horizontal, 8dp vertical
//   Tab list padding:   10dp horizontal, 10dp vertical
//   Icon button size:   48dp (in top bar)
//   Small icons:        18dp (close, undo in tab list)
//   Favicon size:       16dp (tab list), 20dp (bookmarks/history), 24dp (sidebar)
// ═══════════════════════════════════════════════════════════════════




// ═══════════════════════════════════════════════════════════════════
// === PART 1/10 — Package, Imports, MainActivity ===
// ═══════════════════════════════════════════════════════════════════

package com.grey.browser

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GreyBrowser() }
    }
    override fun onPause() { super.onPause(); saveTabsData(this) }
    override fun onDestroy() { super.onDestroy(); saveTabsData(this) }
}

// END OF PART 1/10







// ═══════════════════════════════════════════════════════════════════
// === PART 2/10 — Constants, FaviconCache ===
// ═══════════════════════════════════════════════════════════════════

private const val PREFS_NAME = "browser_tabs"
private const val KEY_TABS = "saved_tabs"
private const val KEY_PINNED = "pinned_domains"
private const val KEY_LAST_ACTIVE_URL = "last_active_url"
private const val KEY_BOOKMARKS = "saved_bookmarks"

const val MAX_WARM_WEBVIEWS = 3
const val UNDO_DELAY_MS = 3000L

// ── Theme Colours ──────────────────────────────────────────────────
private val BG            = Color(0xFF121212)
private val SURFACE       = Color(0xFF1E1E1E)
private val WHITE         = Color(0xFFFFFFFF)
private val MUTED         = Color(0xFF808080)
private val ACCENT_DIM    = Color.White.copy(alpha = 0.3f)
private val BORDER_SUBTLE = Color.White.copy(alpha = 0.2f)
private val DELETE_BG     = Color.Red.copy(alpha = 0.3f)
private val TOAST_BG      = Color.White.copy(alpha = 0.9f)
private val TOAST_TEXT    = Color.Black

// ── FaviconCache ────────────────────────────────────────────────────
object FaviconCache {
    private const val MAX_FAVICONS = 50
    private const val FAVICON_DIR = "favicons"
    private const val META_FILE = "favicon_meta.json"

    data class FaviconMeta(val domain: String, val lastAccessed: Long = System.currentTimeMillis())

    private fun getFaviconDir(context: Context): File {
        val dir = File(context.filesDir, FAVICON_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getMetaFile(context: Context) = File(context.filesDir, META_FILE)

    private fun loadMeta(context: Context): MutableList<FaviconMeta> {
        val file = getMetaFile(context)
        if (!file.exists()) return mutableListOf()
        return try {
            val array = JSONArray(file.readText())
            mutableListOf<FaviconMeta>().apply {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(FaviconMeta(obj.getString("domain"), obj.getLong("lastAccessed")))
                }
            }
        } catch (e: Exception) { mutableListOf() }
    }

    private fun saveMeta(context: Context, meta: List<FaviconMeta>) {
        val array = JSONArray()
        for (item in meta) {
            val obj = JSONObject()
            obj.put("domain", item.domain)
            obj.put("lastAccessed", item.lastAccessed)
            array.put(obj)
        }
        getMetaFile(context).writeText(array.toString())
    }

    fun getFaviconFile(context: Context, domain: String) =
        File(getFaviconDir(context), domain.replace(".", "_").replace("/", "_") + ".png")

    fun getFaviconBitmap(context: Context, domain: String): Bitmap? {
        val file = getFaviconFile(context, domain)
        if (!file.exists()) return null
        val meta = loadMeta(context)
        val existing = meta.find { it.domain == domain }
        if (existing != null) {
            meta.remove(existing)
            meta.add(existing.copy(lastAccessed = System.currentTimeMillis()))
        } else {
            meta.add(FaviconMeta(domain, System.currentTimeMillis()))
        }
        saveMeta(context, meta)
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun tryDownload(urlStr: String): Bitmap? {
        return try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 4000; conn.readTimeout = 4000; conn.connect()
            if (conn.responseCode == 200) {
                val b = BitmapFactory.decodeStream(conn.inputStream)
                conn.inputStream.close(); conn.disconnect()
                b
            } else { conn.disconnect(); null }
        } catch (e: Exception) { null }
    }

    suspend fun downloadAndCacheFavicon(context: Context, domain: String): Bitmap? = withContext(Dispatchers.IO) {
        val sources = listOf(
            "https://www.google.com/s2/favicons?domain=$domain&sz=64",
            "https://$domain/favicon.ico",
            "https://$domain/favicon.png"
        )
        var bitmap: Bitmap? = null
        for (src in sources) { bitmap = tryDownload(src); if (bitmap != null) break }
        if (bitmap != null) {
            FileOutputStream(getFaviconFile(context, domain)).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            val meta = loadMeta(context)
            meta.removeAll { it.domain == domain }
            meta.add(FaviconMeta(domain, System.currentTimeMillis()))
            if (meta.size > MAX_FAVICONS) {
                val oldest = meta.minByOrNull { it.lastAccessed }
                if (oldest != null) { meta.remove(oldest); getFaviconFile(context, oldest.domain).delete() }
            }
            saveMeta(context, meta)
        }
        bitmap
    }
}

// END OF PART 2/10







// ═══════════════════════════════════════════════════════════════════
// === PART 3/10 — Data Classes, Save/Load Functions ===
// ═══════════════════════════════════════════════════════════════════

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

class TabState {
    var webView by mutableStateOf<WebView?>(null)
    var title by mutableStateOf("New Tab")
    var url by mutableStateOf("about:blank")
    var progress by mutableIntStateOf(100)
    var lastUpdated by mutableLongStateOf(System.currentTimeMillis())
    var isBlankTab by mutableStateOf(true)
    var isDiscarded by mutableStateOf(false)
}

fun saveTabsData(context: Context) {
    // Called from onPause/onDestroy — actual save is done reactively via LaunchedEffect
}

fun saveTabsDataNow(context: Context, tabs: List<TabState>, pinnedDomains: List<String>, lastActiveUrl: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val tabsArray = JSONArray()
    for (tab in tabs) {
        if (!tab.isBlankTab) {
            val obj = JSONObject()
            obj.put("url", tab.url)
            obj.put("title", tab.title)
            tabsArray.put(obj)
        }
    }
    prefs.edit()
        .putString(KEY_TABS, tabsArray.toString())
        .putString(KEY_LAST_ACTIVE_URL, lastActiveUrl)
        .apply()
    val pinnedArray = JSONArray()
    for (d in pinnedDomains) pinnedArray.put(d)
    prefs.edit().putString(KEY_PINNED, pinnedArray.toString()).apply()
}

fun loadTabsData(context: Context): Triple<List<Pair<String, String>>, List<String>, String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val tabsList = mutableListOf<Pair<String, String>>()
    prefs.getString(KEY_TABS, null)?.let { json ->
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                tabsList.add(Pair(o.getString("url"), o.optString("title", o.getString("url"))))
            }
        } catch (e: Exception) { }
    }
    val pinnedList = mutableListOf<String>()
    prefs.getString(KEY_PINNED, null)?.let { json ->
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) pinnedList.add(arr.getString(i))
        } catch (e: Exception) { }
    }
    val lastActiveUrl = prefs.getString(KEY_LAST_ACTIVE_URL, "") ?: ""
    return Triple(tabsList, pinnedList, lastActiveUrl)
}

fun saveBookmarks(context: Context, bookmarks: List<Bookmark>) {
    val arr = JSONArray()
    for (b in bookmarks) {
        val obj = JSONObject()
        obj.put("id", b.id); obj.put("url", b.url)
        obj.put("title", b.title); obj.put("timestamp", b.timestamp)
        arr.put(obj)
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_BOOKMARKS, arr.toString()).apply()
}

fun loadBookmarks(context: Context): List<Bookmark> {
    val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_BOOKMARKS, null) ?: return emptyList()
    return try {
        val arr = JSONArray(json)
        mutableListOf<Bookmark>().apply {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(Bookmark(o.getString("id"), o.getString("url"), o.getString("title"), o.getLong("timestamp")))
            }
        }
    } catch (e: Exception) { emptyList() }
}

// END OF PART 3/10







// ═══════════════════════════════════════════════════════════════════
// === PART 4/10 — Utility Functions ===
// ═══════════════════════════════════════════════════════════════════

fun getDomainName(url: String): String {
    if (url == "about:blank" || url.isBlank()) return ""
    return try {
        val host = Uri.parse(url).host?.removePrefix("www.") ?: return ""
        val parts = host.split(".")
        if (parts.size >= 2) "${parts[parts.size - 2]}.${parts[parts.size - 1]}" else host
    } catch (e: Exception) { "Unknown" }
}

fun resolveUrl(input: String): String {
    if (input.isBlank()) return "about:blank"
    if (input.contains("://") || (input.contains(".") && !input.contains(" "))) {
        return if (input.contains("://")) input else "https://$input"
    }
    return "https://www.google.com/search?q=${Uri.encode(input)}"
}

// END OF PART 4/10







// ═══════════════════════════════════════════════════════════════════
// === PART 5/10 — GreyBrowser() State Declarations ===
// ═══════════════════════════════════════════════════════════════════

@Composable
fun GreyBrowser() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activity = context as? ComponentActivity
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    // ── Bookmarks State ──────────────────────────────────────────────
    val bookmarks = remember { mutableStateListOf<Bookmark>().apply { addAll(loadBookmarks(context)) } }
    var showBookmarks by remember { mutableStateOf(false) }

    // ── Toast State ──────────────────────────────────────────────────
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    fun showToast(msg: String) {
        toastMessage = msg
        showToast = true
    }

    // ── Load saved tabs ──────────────────────────────────────────────
    val (savedTabs, savedPinned, savedLastActiveUrl) = remember { loadTabsData(context) }
    val tabs = remember {
        mutableStateListOf<TabState>().apply {
            for ((url, title) in savedTabs) {
                add(TabState().apply {
                    this.url = url; this.title = title; isBlankTab = false
                    isDiscarded = true; webView = null
                })
            }
        }
    }

    // currentTabIndex = -1 means homepage overlay is showing
    var currentTabIndex by remember { mutableIntStateOf(-1) }
    var highlightedTabIndex by remember {
        mutableIntStateOf(
            tabs.indexOfFirst { it.url.substringBefore("#") == savedLastActiveUrl.substringBefore("#") }
                .let { if (it >= 0) it else if (tabs.isNotEmpty()) 0 else -1 }
        )
    }
    var lastActiveUrl by remember { mutableStateOf(savedLastActiveUrl) }

    var showTabManager by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val pinnedDomains = remember { mutableStateListOf<String>().apply { addAll(savedPinned) } }
    var selectedDomain by remember { mutableStateOf("") }
    val faviconBitmaps = remember { mutableStateMapOf<String, Bitmap?>() }
    val faviconLoading = remember { mutableStateMapOf<String, Boolean>() }
    val tabFavicons = remember { mutableStateMapOf<String, Bitmap?>() }
    val tabFaviconLoading = remember { mutableStateMapOf<String, Boolean>() }
    val currentTab = tabs.getOrNull(currentTabIndex)

    val pendingDeletions = remember { mutableStateMapOf<Int, Long>() }
    var showBlink by remember { mutableStateOf(false) }
    val blinkTargetDomain = remember { mutableStateOf("") }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var confirmTitle by remember { mutableStateOf("") }
    var confirmMessage by remember { mutableStateOf("") }

    LaunchedEffect(tabs.toList(), pinnedDomains.toList(), lastActiveUrl) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
    }
    LaunchedEffect(tabs.map { "${it.url}|${it.title}" }.joinToString()) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
    }
    LaunchedEffect(bookmarks.toList()) { saveBookmarks(context, bookmarks) }

    LaunchedEffect(currentTabIndex) {
        if (currentTabIndex >= 0 && currentTabIndex < tabs.size) {
            lastActiveUrl = tabs[currentTabIndex].url
            highlightedTabIndex = currentTabIndex
        }
    }

    if (showToast) {
        LaunchedEffect(showToast) {
            delay(2000)
            showToast = false
        }
    }

    // END OF PART 5/10
    
    
    
    // ═══════════════════════════════════════════════════════════════════
// === PART 6/10 — Tab Functions (Create, Delete, Lifecycle, Delegates) ===
// ═══════════════════════════════════════════════════════════════════

    // ── WebView creation helper ──────────────────────────────────────
    fun createWebView(url: String): WebView {
        return WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
            }
            loadUrl(url)
        }
    }

    // ── Attach delegates to a tab's WebView ─────────────────────────
    fun setupDelegates(tabState: TabState) {
        val wv = tabState.webView ?: return
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                tabState.progress = newProgress
                tabState.lastUpdated = System.currentTimeMillis()
            }
            override fun onReceivedTitle(view: WebView, title: String?) {
                if (!tabState.isBlankTab && title != null && title.isNotBlank()) {
                    tabState.title = title
                }
            }
        }
        wv.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                tabState.url = url
                tabState.progress = 5
                tabState.lastUpdated = System.currentTimeMillis()
                if (url != "about:blank") {
                    tabState.isBlankTab = false
                }
            }
            override fun onPageFinished(view: WebView, url: String) {
                tabState.progress = 100
                tabState.url = url
                tabState.lastUpdated = System.currentTimeMillis()
                if (url != "about:blank") {
                    tabState.isBlankTab = false
                    lastActiveUrl = url
                    if (currentTabIndex >= 0 && currentTabIndex < tabs.size) {
                        highlightedTabIndex = currentTabIndex
                    }
                }
            }
        }
    }

    // ── Tab lifecycle management ────────────────────────────────────
    fun manageTabLifecycle(activeIndex: Int) {
        // Ensure active tab has a live WebView
        val activeTab = tabs.getOrNull(activeIndex) ?: return
        if (activeTab.webView == null && activeTab.isDiscarded) {
            activeTab.webView = createWebView(activeTab.url)
            activeTab.isDiscarded = false
            setupDelegates(activeTab)
            activeTab.lastUpdated = System.currentTimeMillis()
        }

        // Keep only MAX_WARM_WEBVIEWS WebViews alive
        val warmTabs = tabs.filterIndexed { i, t ->
            i != activeIndex && !t.isDiscarded && !t.isBlankTab && t.webView != null
        }
        if (warmTabs.size >= MAX_WARM_WEBVIEWS) {
            val toDiscard = warmTabs.sortedBy { it.lastUpdated }.take(warmTabs.size - (MAX_WARM_WEBVIEWS - 1))
            for (tab in toDiscard) {
                tab.webView?.destroy()
                tab.webView = null
                tab.isDiscarded = true
                tab.progress = 100
            }
        }
    }

    // ── Create tabs ─────────────────────────────────────────────────
    fun createForegroundTab(url: String) {
        val wv = createWebView(url)
        tabs.add(TabState().apply {
            webView = wv
            isBlankTab = false
            isDiscarded = false
            lastUpdated = System.currentTimeMillis()
            setupDelegates(this)
        })
        currentTabIndex = tabs.lastIndex
        manageTabLifecycle(currentTabIndex)
    }

    fun createBackgroundTab(url: String) {
        val wv = createWebView(url)
        tabs.add(TabState().apply {
            webView = wv
            isBlankTab = false
            isDiscarded = false
            lastUpdated = System.currentTimeMillis()
            setupDelegates(this)
        })
        manageTabLifecycle(currentTabIndex)
    }

    // ── Delete with undo ────────────────────────────────────────────
    fun requestDeleteTab(index: Int) {
        if (index >= 0 && index < tabs.size) {
            pendingDeletions[index] = System.currentTimeMillis()
        }
    }

    fun undoDeleteTab(index: Int) {
        pendingDeletions.remove(index)
    }

    // ── Favicon loading helpers ─────────────────────────────────────
    fun loadFavicon(domain: String) {
        if (domain.isNotBlank() && !faviconBitmaps.containsKey(domain) && faviconLoading[domain] != true) {
            faviconLoading[domain] = true
            scope.launch {
                faviconBitmaps[domain] = FaviconCache.getFaviconBitmap(context, domain)
                    ?: FaviconCache.downloadAndCacheFavicon(context, domain)
                faviconLoading[domain] = false
            }
        }
    }

    fun loadTabFavicon(domain: String) {
        if (domain.isNotBlank() && !tabFavicons.containsKey(domain) && tabFaviconLoading[domain] != true) {
            tabFaviconLoading[domain] = true
            scope.launch {
                tabFavicons[domain] = FaviconCache.getFaviconBitmap(context, domain)
                    ?: FaviconCache.downloadAndCacheFavicon(context, domain)
                tabFaviconLoading[domain] = false
            }
        }
    }

    // ── Process pending deletions (undo timer) ──────────────────────
    LaunchedEffect(pendingDeletions.toMap()) {
        while (pendingDeletions.isNotEmpty()) {
            delay(1000)
            val now = System.currentTimeMillis()
            val toRemove = pendingDeletions.filter { now - it.value >= UNDO_DELAY_MS }.keys.toList()
            for (index in toRemove.sortedDescending()) {
                pendingDeletions.remove(index)
                val tab = tabs.getOrNull(index) ?: continue
                tab.webView?.destroy()
                tabs.removeAt(index)

                // Remap remaining deletion indices
                val updated = mutableMapOf<Int, Long>()
                for ((oldIdx, time) in pendingDeletions) {
                    updated[if (oldIdx > index) oldIdx - 1 else oldIdx] = time
                }
                pendingDeletions.clear()
                pendingDeletions.putAll(updated)

                if (tabs.isEmpty()) {
                    currentTabIndex = -1
                    selectedDomain = ""
                } else if (currentTabIndex > index) {
                    currentTabIndex--
                } else if (currentTabIndex == index && tabs.isNotEmpty()) {
                    currentTabIndex = minOf(currentTabIndex, tabs.lastIndex)
                }
                if (highlightedTabIndex == index) highlightedTabIndex = -1
                else if (highlightedTabIndex > index) highlightedTabIndex--
                if (selectedDomain.isNotBlank()) {
                    val dg = tabs.groupBy { getDomainName(it.url) }.filter { it.key.isNotBlank() }
                    if (!dg.containsKey(selectedDomain)) selectedDomain = ""
                }
            }
        }
    }

    // ── Pause WebViews when Tab Manager is open ─────────────────────
    LaunchedEffect(showTabManager, currentTabIndex) {
        if (showTabManager) {
            tabs.forEach { it.webView?.onPause() }
        } else {
            if (currentTabIndex >= 0) {
                tabs.getOrNull(currentTabIndex)?.webView?.onResume()
                manageTabLifecycle(currentTabIndex)
            }
        }
    }

    // END OF PART 6/10







// ═══════════════════════════════════════════════════════════════════
// === PART 7/10 — BackHandler, WebViewBox Composable ===
// ═══════════════════════════════════════════════════════════════════

    BackHandler {
        when {
            // Close overlays first
            showTabManager -> showTabManager = false
            showBookmarks -> showBookmarks = false
            showMenu -> showMenu = false
            showConfirmDialog -> { showConfirmDialog = false; confirmAction = null }
            // If on a real tab and WebView can go back
            currentTabIndex >= 0 -> {
                val tab = tabs.getOrNull(currentTabIndex)
                if (tab?.webView?.canGoBack() == true) {
                    tab.webView?.goBack()
                } else {
                    // Go to homepage overlay
                    currentTabIndex = -1
                }
            }
            // On homepage overlay
            currentTabIndex == -1 -> {
                if (highlightedTabIndex >= 0 && highlightedTabIndex < tabs.size) {
                    currentTabIndex = highlightedTabIndex
                } else if (tabs.isNotEmpty()) {
                    currentTabIndex = tabs.lastIndex
                } else {
                    activity?.finish()
                }
            }
        }
    }

    @Composable
    fun WebViewBox() {
        if (currentTabIndex == -1) {
            // ── Homepage overlay ─────────────────────────────────────
            Box(
                Modifier.fillMaxSize().background(BG),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Grey",
                    color = WHITE.copy(alpha = 0.15f),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            val tab = tabs.getOrNull(currentTabIndex)
            val wv = tab?.webView
            if (wv != null) {
                AndroidView(
                    factory = { wv },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    Modifier.fillMaxSize().background(BG),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WHITE)
                }
            }
        }
    }

    // END OF PART 7/10







// ═══════════════════════════════════════════════════════════════════
// === PART 8/10 — Top Bar, Tab Manager UI, Menu, Toast ===
// ═══════════════════════════════════════════════════════════════════

    var urlInput by remember {
        mutableStateOf(
            TextFieldValue(
                if (currentTabIndex == -1) ""
                else currentTab?.url?.let { if (it == "about:blank") "" else it } ?: ""
            )
        )
    }
    var isUrlFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(currentTabIndex, currentTab?.url) {
        if (!isUrlFocused) {
            urlInput = TextFieldValue(
                if (currentTabIndex == -1) ""
                else currentTab?.url?.let { if (it == "about:blank") "" else it } ?: ""
            )
        }
    }

    // ── Confirm dialog ──────────────────────────────────────────────
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false; confirmAction = null },
            title = { Text(confirmTitle, color = WHITE, fontSize = 18.sp) },
            text = { Text(confirmMessage, color = MUTED, fontSize = 14.sp) },
            confirmButton = {
                TextButton({
                    val a = confirmAction; showConfirmDialog = false; confirmAction = null; a?.invoke()
                }) { Text("Confirm", color = WHITE) }
            },
            dismissButton = {
                TextButton({ showConfirmDialog = false; confirmAction = null }) {
                    Text("Cancel", color = WHITE)
                }
            },
            containerColor = SURFACE,
            titleContentColor = WHITE,
            textContentColor = WHITE,
            shape = RectangleShape,
            tonalElevation = 0.dp
        )
    }

    // ── Bookmarks UI ────────────────────────────────────────────────
    if (showBookmarks) {
        BookmarksUI(
            bookmarks = bookmarks,
            onDismiss = { showBookmarks = false },
            onOpenUrl = { url -> createForegroundTab(url) },
            onDelete = { id ->
                bookmarks.removeAll { it.id == id }
                showToast("Bookmark deleted")
            },
            faviconBitmaps = faviconBitmaps,
            loadFavicon = { loadFavicon(it) }
        )
    }

    // ── Tab Manager ─────────────────────────────────────────────────
    if (showTabManager) {
        val domainGroups = tabs.groupBy { getDomainName(it.url) }.filter { it.key.isNotBlank() }
        val sortedDomains = domainGroups.keys.sortedWith(
            compareByDescending<String> { pinnedDomains.contains(it) }
                .thenBy { d: String -> domainGroups[d]?.firstOrNull()?.let { t -> tabs.indexOf(t) } ?: Int.MAX_VALUE }
        )
        val pinnedSorted = sortedDomains.filter { pinnedDomains.contains(it) }
        val unpinnedSorted = sortedDomains.filter { !pinnedDomains.contains(it) }
        val highlightDomain = if (highlightedTabIndex >= 0 && highlightedTabIndex < tabs.size) {
            getDomainName(tabs[highlightedTabIndex].url)
        } else ""

        LaunchedEffect(Unit) {
            selectedDomain = ""
            if (highlightDomain.isNotBlank()) {
                blinkTargetDomain.value = highlightDomain
                showBlink = true
                delay(1500)
                showBlink = false
                blinkTargetDomain.value = ""
            }
        }

        Popup(
            alignment = Alignment.TopStart,
            onDismissRequest = { showTabManager = false },
            properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Surface(
                Modifier.fillMaxSize().statusBarsPadding().background(SURFACE),
                color = SURFACE
            ) {
                Column(Modifier.fillMaxSize()) {
                    // ── Header ───────────────────────────────────────
                    Row(
                        Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            { showTabManager = false },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Close, "Close", tint = WHITE)
                        }
                        Spacer(Modifier.width(4.dp))
                        Text("Tabs", color = WHITE, fontSize = 18.sp)
                        if (tabs.isNotEmpty()) {
                            Spacer(Modifier.width(8.dp))
                            Text("(${tabs.size})", color = MUTED, fontSize = 14.sp)
                        }
                    }

                    // ── Body: Sidebar + Tab list ─────────────────────
                    Row(Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp)) {
                        // ── Sidebar ──────────────────────────────────
                        val groupListState = rememberLazyListState()
                        val allSidebarItems = listOf("__ALL__") + pinnedSorted + unpinnedSorted
                        LaunchedEffect(allSidebarItems, selectedDomain) {
                            val idx = if (selectedDomain.isBlank()) 0
                            else allSidebarItems.indexOf(selectedDomain)
                            if (idx >= 0) groupListState.scrollToItem(idx)
                        }

                        LazyColumn(
                            state = groupListState,
                            modifier = Modifier.width(56.dp).fillMaxHeight().padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                AllGroupChip(
                                    isSelected = selectedDomain.isBlank(),
                                    tabCount = tabs.size,
                                    onClick = { selectedDomain = "" }
                                )
                            }
                            items(pinnedSorted) { domain: String ->
                                SidebarGroupChip(
                                    domain, domain == selectedDomain,
                                    domainGroups[domain]?.size ?: 0,
                                    { selectedDomain = domain },
                                    faviconBitmaps[domain],
                                    { loadFavicon(domain) },
                                    showBlink && blinkTargetDomain.value == domain,
                                    true
                                )
                            }
                            items(unpinnedSorted) { domain: String ->
                                SidebarGroupChip(
                                    domain, domain == selectedDomain,
                                    domainGroups[domain]?.size ?: 0,
                                    { selectedDomain = domain },
                                    faviconBitmaps[domain],
                                    { loadFavicon(domain) },
                                    showBlink && blinkTargetDomain.value == domain,
                                    false
                                )
                            }
                        }

                        VerticalDivider(
                            color = Color.DarkGray,
                            modifier = Modifier.fillMaxHeight().width(1.dp)
                        )

                        // ── Tab list ─────────────────────────────────
                        val tabsToShow = if (selectedDomain.isBlank()) tabs.toList()
                        else domainGroups[selectedDomain] ?: emptyList()
                        val tabListState = rememberLazyListState()
                        val scrollTarget = if (highlightedTabIndex >= 0) tabs.getOrNull(highlightedTabIndex) else null
                        LaunchedEffect(selectedDomain) {
                            val idx = tabsToShow.indexOf(scrollTarget)
                            if (idx >= 0) tabListState.scrollToItem(idx)
                        }

                        if (tabs.isEmpty()) {
                            Box(
                                Modifier.weight(1f).fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No open tabs", color = MUTED, fontSize = 16.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Tap 'New Tab' to start browsing",
                                        color = MUTED.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                state = tabListState,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                if (tabsToShow.isEmpty()) {
                                    item {
                                        Box(
                                            Modifier.fillMaxWidth().padding(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No tabs in this group",
                                                color = MUTED,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                } else {
                                    items(tabsToShow) { tab: TabState ->
                                        val tabIndex = tabs.indexOf(tab)
                                        val isHighlighted = tabIndex == highlightedTabIndex
                                        val isPending = pendingDeletions.containsKey(tabIndex)
                                        val tabDomain = getDomainName(tab.url)
                                        LaunchedEffect(tab.url) { loadTabFavicon(tabDomain) }
                                        val tabFav = tabFavicons[tabDomain]

                                        Surface(
                                            Modifier.fillMaxWidth()
                                                .clickable(
                                                    enabled = !isPending,
                                                    onClick = {
                                                        currentTabIndex = tabIndex
                                                        showTabManager = false
                                                    }
                                                )
                                                .border(0.5.dp, Color.DarkGray, RectangleShape),
                                            color = when {
                                                isPending -> DELETE_BG
                                                isHighlighted -> WHITE
                                                else -> Color.Transparent
                                            }
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Favicon
                                                if (tabFav != null) {
                                                    Image(
                                                        tabFav.asImageBitmap(),
                                                        tabDomain,
                                                        Modifier.size(16.dp).clip(CircleShape),
                                                        contentScale = ContentScale.Fit
                                                    )
                                                } else {
                                                    Box(
                                                        Modifier.size(16.dp).clip(CircleShape)
                                                            .background(Color.DarkGray),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            tabDomain.take(1).uppercase(),
                                                            color = WHITE,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    if (tab.title == "New Tab" || tab.title.isBlank()) tab.url
                                                    else tab.title,
                                                    color = when {
                                                        isPending -> WHITE
                                                        isHighlighted -> Color.Black
                                                        else -> WHITE
                                                    },
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = 14.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (isPending) {
                                                    IconButton({ undoDeleteTab(tabIndex) }) {
                                                        Icon(
                                                            Icons.Default.Undo,
                                                            "Undo",
                                                            tint = WHITE,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                } else {
                                                    IconButton({ requestDeleteTab(tabIndex) }) {
                                                        Icon(
                                                            Icons.Default.Close,
                                                            "Close",
                                                            tint = if (isHighlighted) Color.Black else WHITE,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Footer ───────────────────────────────────────
                    Column(
                        Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding()
                    ) {
                        OutlinedButton(
                            onClick = {
                                createForegroundTab("about:blank")
                                showTabManager = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                            border = BorderStroke(1.dp, WHITE)
                        ) {
                            Icon(Icons.Default.Add, null, tint = WHITE)
                            Spacer(Modifier.width(8.dp))
                            Text("New Tab", color = WHITE)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth()) {
                            val hasSelection = selectedDomain.isNotBlank()
                            val isPinned = pinnedDomains.contains(selectedDomain)
                            val tint = if (hasSelection) WHITE else ACCENT_DIM

                            OutlinedButton(
                                onClick = {
                                    if (hasSelection) {
                                        confirmTitle = if (isPinned) "Unpin Group?" else "Pin Group?"
                                        confirmMessage = "Are you sure?"
                                        confirmAction = {
                                            if (isPinned) pinnedDomains.remove(selectedDomain)
                                            else pinnedDomains.add(selectedDomain)
                                        }
                                        showConfirmDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = tint),
                                border = BorderStroke(1.dp, tint)
                            ) {
                                Text(
                                    if (isPinned) "Unpin Group" else "Pin Group",
                                    fontSize = 13.sp,
                                    color = tint
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    if (hasSelection) {
                                        confirmTitle = "Delete Group?"
                                        confirmMessage = "All tabs in this group will be lost."
                                        confirmAction = {
                                            val toRemove = (domainGroups[selectedDomain] ?: emptyList()).toList()
                                            toRemove.forEach { tab ->
                                                val idx = tabs.indexOf(tab)
                                                if (idx >= 0) pendingDeletions.remove(idx)
                                                tab.webView?.destroy()
                                            }
                                            tabs.removeAll(toRemove)
                                            if (tabs.isEmpty()) {
                                                currentTabIndex = -1
                                                highlightedTabIndex = -1
                                                selectedDomain = ""
                                            } else {
                                                currentTabIndex = currentTabIndex.coerceIn(0, tabs.lastIndex)
                                                highlightedTabIndex = currentTabIndex
                                                selectedDomain = ""
                                            }
                                        }
                                        showConfirmDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = tint),
                                border = BorderStroke(1.dp, tint)
                            ) {
                                Text("Delete Group", fontSize = 13.sp, color = tint)
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Main layout ─────────────────────────────────────────────────
    Surface(Modifier.fillMaxSize(), color = BG) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                // ── Top Bar ──────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tabs button
                    Box(
                        Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)
                    ) {
                        IconButton({ showTabManager = true }) {
                            Icon(Icons.Default.Tab, "Tabs", tint = WHITE)
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    // Forward button
                    val canGoForward = currentTab?.webView?.canGoForward() == true
                    Box(
                        Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)
                    ) {
                        IconButton(
                            onClick = { currentTab?.webView?.goForward() },
                            enabled = canGoForward
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                "Forward",
                                tint = if (canGoForward) WHITE else ACCENT_DIM
                            )
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    // URL field
                    val isLoading = (currentTab?.progress ?: 100) in 1..99
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                if (currentTabIndex == -1 || currentTab?.isBlankTab == true)
                                    "Search or enter URL"
                                else currentTab?.url?.take(50) ?: "",
                                color = WHITE.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp)
                            .background(SURFACE)
                            .focusRequester(focusRequester)
                            .onFocusChanged { isUrlFocused = it.isFocused }
                            .drawBehind {
                                if (isLoading) {
                                    drawRect(
                                        color = WHITE,
                                        size = size.copy(
                                            width = size.width * (currentTab?.progress ?: 100) / 100f
                                        )
                                    )
                                }
                            },
                        textStyle = TextStyle(
                            color = if (isLoading) Color.Gray else WHITE,
                            fontSize = 14.sp
                        ),
                        shape = RectangleShape,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                val input = urlInput.text
                                if (input.isNotBlank()) {
                                    focusManager.clearFocus()
                                    val uri = resolveUrl(input)
                                    createForegroundTab(uri)
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = WHITE,
                            unfocusedBorderColor = WHITE,
                            cursorColor = if (isLoading) Color.Gray else WHITE
                        ),
                        trailingIcon = {
                            if (isLoading) {
                                IconButton({ currentTab?.webView?.stopLoading() }) {
                                    Icon(Icons.Default.Close, "Stop", tint = WHITE)
                                }
                            } else {
                                IconButton({
                                    urlInput = urlInput.copy(
                                        selection = TextRange(0, urlInput.text.length)
                                    )
                                    focusRequester.requestFocus()
                                }) {
                                    Icon(Icons.Default.SelectAll, "Select all", tint = WHITE)
                                }
                            }
                        }
                    )

                    Spacer(Modifier.width(4.dp))

                    // New Tab button
                    Box(
                        Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)
                    ) {
                        IconButton({
                            createForegroundTab("about:blank")
                        }) {
                            Icon(Icons.Default.Add, "New Tab", tint = WHITE)
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    // Menu button
                    Box(
                        Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)
                    ) {
                        IconButton({ showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Menu", tint = WHITE)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.border(1.dp, WHITE, RectangleShape),
                            containerColor = SURFACE,
                            shape = RectangleShape
                        ) {
                            if (currentTabIndex >= 0) {
                                DropdownMenuItem(
                                    text = { Text("Add to Bookmark", color = WHITE) },
                                    onClick = {
                                        showMenu = false
                                        val url = currentTab?.url ?: ""
                                        if (url != "about:blank" && url.isNotBlank()) {
                                            bookmarks.removeAll { it.url == url }
                                            bookmarks.add(
                                                Bookmark(
                                                    url = url,
                                                    title = currentTab?.title?.ifBlank { url } ?: url
                                                )
                                            )
                                            showToast("Added to bookmarks")
                                        }
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Bookmarks", color = WHITE) },
                                onClick = {
                                    showMenu = false
                                    showBookmarks = true
                                }
                            )
                        }
                    }
                }

                // ── WebView area ─────────────────────────────────────
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    WebViewBox()
                }
            }

            // ── Toast ────────────────────────────────────────────────
            if (showToast) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        modifier = Modifier.padding(bottom = 80.dp),
                        color = TOAST_BG,
                        shape = RectangleShape
                    ) {
                        Text(
                            toastMessage,
                            color = TOAST_TEXT,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

// END OF PART 8/10







// ═══════════════════════════════════════════════════════════════════
// === PART 9/10 — BookmarksUI Composable ===
// ═══════════════════════════════════════════════════════════════════

@Composable
fun BookmarksUI(
    bookmarks: List<Bookmark>,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit,
    onDelete: (String) -> Unit,
    faviconBitmaps: Map<String, Bitmap?>,
    loadFavicon: (String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var bookmarkToDelete by remember { mutableStateOf<String?>(null) }

    if (showDeleteConfirm && bookmarkToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; bookmarkToDelete = null },
            title = { Text("Delete Bookmark?", color = WHITE, fontSize = 18.sp) },
            text = { Text("This cannot be undone.", color = MUTED, fontSize = 14.sp) },
            confirmButton = {
                TextButton({
                    onDelete(bookmarkToDelete!!)
                    showDeleteConfirm = false
                    bookmarkToDelete = null
                }) { Text("Delete", color = WHITE) }
            },
            dismissButton = {
                TextButton({
                    showDeleteConfirm = false
                    bookmarkToDelete = null
                }) { Text("Cancel", color = WHITE) }
            },
            containerColor = SURFACE,
            titleContentColor = WHITE,
            textContentColor = WHITE,
            shape = RectangleShape,
            tonalElevation = 0.dp
        )
    }

    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            Modifier.fillMaxSize().statusBarsPadding().background(SURFACE),
            color = SURFACE
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Bookmarks", color = WHITE, fontSize = 18.sp)
                    if (bookmarks.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text("(${bookmarks.size})", color = MUTED, fontSize = 14.sp)
                    }
                }
                if (bookmarks.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No bookmarks", color = MUTED, fontSize = 16.sp)
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        items(bookmarks.reversed()) { item ->
                            val domain = getDomainName(item.url)
                            LaunchedEffect(item.url) { loadFavicon(domain) }
                            val fav = faviconBitmaps[domain]
                            Surface(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    .border(0.5.dp, Color.DarkGray, RectangleShape),
                                color = Color.Transparent
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(12.dp)
                                        .clickable {
                                            onOpenUrl(item.url)
                                            onDismiss()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (fav != null) {
                                        Image(
                                            fav.asImageBitmap(),
                                            domain,
                                            Modifier.size(20.dp).clip(CircleShape),
                                            contentScale = ContentScale.Fit
                                        )
                                    } else {
                                        Box(
                                            Modifier.size(20.dp).clip(CircleShape).background(Color.DarkGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                domain.take(1).uppercase(),
                                                color = WHITE,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            item.title.ifBlank { item.url },
                                            color = WHITE,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            item.url,
                                            color = MUTED.copy(alpha = 0.7f),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton({
                                        bookmarkToDelete = item.id
                                        showDeleteConfirm = true
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            "Delete",
                                            tint = WHITE.copy(alpha = 0.5f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// END OF PART 9/10







// ═══════════════════════════════════════════════════════════════════
// === PART 10/10 — Helper Composables (Chips, ContextMenuItem) ===
// ═══════════════════════════════════════════════════════════════════

@Composable
fun AllGroupChip(isSelected: Boolean, tabCount: Int, onClick: () -> Unit) {
    Surface(
        Modifier.padding(vertical = 4.dp).width(52.dp)
            .clickable { onClick() }
            .border(
                0.5.dp,
                if (isSelected) WHITE else BORDER_SUBTLE,
                RectangleShape
            ),
        color = if (isSelected) WHITE else Color.Transparent
    ) {
        Column(
            Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "All",
                color = if (isSelected) Color.Black else WHITE,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier
                    .background(if (isSelected) Color.LightGray else Color.DarkGray)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    tabCount.toString(),
                    color = if (isSelected) Color.Black else WHITE,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun SidebarGroupChip(
    domain: String,
    isSelected: Boolean,
    tabCount: Int,
    onClick: () -> Unit,
    favicon: Bitmap?,
    onAppear: () -> Unit,
    isBlinking: Boolean,
    isPinned: Boolean
) {
    LaunchedEffect(domain) { onAppear() }
    val bg = if (isSelected) WHITE else Color.Transparent
    Surface(
        Modifier.padding(vertical = 4.dp).width(52.dp)
            .clickable { onClick() }
            .border(
                0.5.dp,
                if (isSelected) WHITE else BORDER_SUBTLE,
                RectangleShape
            ),
        color = bg
    ) {
        Box(Modifier.padding(6.dp)) {
            if (isPinned) {
                Icon(
                    Icons.Default.PushPin,
                    "Pinned",
                    tint = if (isSelected) Color.Black else WHITE,
                    modifier = Modifier.size(12.dp).align(Alignment.TopStart)
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(4.dp))
                if (favicon != null) {
                    Image(
                        favicon.asImageBitmap(),
                        domain,
                        Modifier.size(24.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        Modifier.size(24.dp).clip(CircleShape)
                            .background(if (isSelected) Color.LightGray else Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            domain.take(1).uppercase(),
                            color = if (isSelected) Color.Black else WHITE,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Box(
                Modifier.align(Alignment.BottomEnd)
                    .background(if (isSelected) Color.LightGray else Color.DarkGray)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    tabCount.toString(),
                    color = if (isSelected) Color.Black else WHITE,
                    fontSize = 9.sp
                )
            }
        }
    }
}

// END OF PART 10/10
// END OF FILE - Grey Browser V4.0
// ═══════════════════════════════════════════════════════════════════
