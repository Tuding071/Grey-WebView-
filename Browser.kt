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
// === PART 1/10 — Package, Imports, MainActivity [UPDATED v12] ===
// ═══════════════════════════════════════════════════════════════════

package com.grey.browser

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import java.security.MessageDigest
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setContent { GreyBrowser() }
    }
    override fun onPause() { super.onPause(); saveTabsData(this) }
    override fun onDestroy() { super.onDestroy(); saveTabsData(this) }
}

// END OF PART 1/10




// ═══════════════════════════════════════════════════════════════════
// === PART 2/10 — Constants, FaviconCache [UPDATED v4] ===
// ═══════════════════════════════════════════════════════════════════

private const val PREFS_NAME = "browser_tabs"
private const val KEY_TABS = "saved_tabs"
private const val KEY_PINNED = "pinned_domains"
private const val KEY_LAST_ACTIVE_URL = "last_active_url"
private const val KEY_BOOKMARKS = "saved_bookmarks"
private const val KEY_HISTORY = "saved_history"
private const val KEY_SCRIPTS = "saved_scripts"
private const val KEY_FILTERS = "saved_filters"
private const val KEY_FILTERS_ENABLED = "filters_enabled"

const val MAX_WARM_WEBVIEWS = 3
const val UNDO_DELAY_MS = 3000L
const val MAX_HISTORY_ITEMS = 500

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
// === PART 3/10 — Data Classes, Save/Load Functions [UPDATED v6] ===
// ═══════════════════════════════════════════════════════════════════

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val url: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Script(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val code: String,
    val enabled: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class Filter(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rawText: String,
    val networkRules: List<String>,
    val cosmeticRules: List<String>,
    val enabled: Boolean = true,
    val networkRuleCount: Int,
    val cosmeticRuleCount: Int,
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
    var parentTabIndex by mutableIntStateOf(-1)  // -1 = created from homepage
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

fun saveHistory(context: Context, history: List<HistoryItem>) {
    val arr = JSONArray()
    for (h in history) {
        val obj = JSONObject()
        obj.put("url", h.url); obj.put("title", h.title); obj.put("timestamp", h.timestamp)
        arr.put(obj)
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_HISTORY, arr.toString()).apply()
}

fun loadHistory(context: Context): List<HistoryItem> {
    val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_HISTORY, null) ?: return emptyList()
    return try {
        val arr = JSONArray(json)
        mutableListOf<HistoryItem>().apply {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(HistoryItem(o.getString("url"), o.getString("title"), o.getLong("timestamp")))
            }
        }
    } catch (e: Exception) { emptyList() }
}

fun saveScripts(context: Context, scripts: List<Script>) {
    val arr = JSONArray()
    for (s in scripts) {
        val obj = JSONObject()
        obj.put("id", s.id); obj.put("title", s.title)
        obj.put("code", s.code); obj.put("enabled", s.enabled)
        obj.put("timestamp", s.timestamp)
        arr.put(obj)
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_SCRIPTS, arr.toString()).apply()
}

fun loadScripts(context: Context): List<Script> {
    val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_SCRIPTS, null) ?: return emptyList()
    return try {
        val arr = JSONArray(json)
        mutableListOf<Script>().apply {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(Script(
                    o.getString("id"),
                    o.getString("title"),
                    o.getString("code"),
                    o.optBoolean("enabled", true),
                    o.getLong("timestamp")
                ))
            }
        }
    } catch (e: Exception) { emptyList() }
}

fun saveFilters(context: Context, filters: List<Filter>) {
    val arr = JSONArray()
    for (f in filters) {
        val obj = JSONObject()
        obj.put("id", f.id)
        obj.put("name", f.name)
        obj.put("rawText", f.rawText)
        obj.put("networkRuleCount", f.networkRuleCount)
        obj.put("cosmeticRuleCount", f.cosmeticRuleCount)
        obj.put("enabled", f.enabled)
        obj.put("timestamp", f.timestamp)
        val networkArr = JSONArray()
        for (r in f.networkRules) networkArr.put(r)
        obj.put("networkRules", networkArr)
        val cosmeticArr = JSONArray()
        for (r in f.cosmeticRules) cosmeticArr.put(r)
        obj.put("cosmeticRules", cosmeticArr)
        arr.put(obj)
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(KEY_FILTERS, arr.toString()).apply()
}

fun loadFilters(context: Context): List<Filter> {
    val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_FILTERS, null) ?: return emptyList()
    return try {
        val arr = JSONArray(json)
        mutableListOf<Filter>().apply {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val networkArr = o.getJSONArray("networkRules")
                val networkList = mutableListOf<String>()
                for (j in 0 until networkArr.length()) networkList.add(networkArr.getString(j))
                val cosmeticArr = o.getJSONArray("cosmeticRules")
                val cosmeticList = mutableListOf<String>()
                for (j in 0 until cosmeticArr.length()) cosmeticList.add(cosmeticArr.getString(j))
                add(Filter(
                    o.getString("id"),
                    o.getString("name"),
                    o.getString("rawText"),
                    networkList,
                    cosmeticList,
                    o.optBoolean("enabled", true),
                    o.getInt("networkRuleCount"),
                    o.getInt("cosmeticRuleCount"),
                    o.getLong("timestamp")
                ))
            }
        }
    } catch (e: Exception) { emptyList() }
}

// END OF PART 3/10



// ═══════════════════════════════════════════════════════════════════
// === PART 4/10 — Utility Functions [UPDATED v4] ===
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

// ── Script Header Parser ─────────────────────────────────────────────
fun parseScriptHeader(code: String): Map<String, String> {
    val meta = mutableMapOf<String, String>()
    val headerRegex = Regex("""/\*\s*==UserScript==\s*(.*?)\s*==/UserScript==\s*\*/""", RegexOption.DOT_MATCHES_ALL)
    val headerMatch = headerRegex.find(code) ?: return meta

    val header = headerMatch.groupValues[1]
    val fieldRegex = Regex("""@(\w+)\s+(.+)""")
    for (line in header.lines()) {
        val fieldMatch = fieldRegex.find(line.trim()) ?: continue
        val key = fieldMatch.groupValues[1]
        val value = fieldMatch.groupValues[2].trim()
        if (key in listOf("match", "exclude")) {
            val existing = meta[key] ?: ""
            meta[key] = if (existing.isEmpty()) value else "$existing||$value"
        } else {
            meta[key] = value
        }
    }
    return meta
}

fun getScriptBody(code: String): String {
    val headerRegex = Regex("""/\*\s*==UserScript==\s*.*?\s*==/UserScript==\s*\*/\s*""", RegexOption.DOT_MATCHES_ALL)
    return headerRegex.replaceFirst(code, "")
}

fun urlMatchesPattern(url: String, pattern: String): Boolean {
    if (pattern == "*" || pattern == "*://*/*") return true
    var regexStr = Regex.escape(pattern)
    regexStr = regexStr.replace("\\*", ".*")
    regexStr = regexStr.replace("""\.\*://\.\*""", ".*://.*")
    regexStr = regexStr.replace("""\.\*://""", ".*://")
    return try {
        Regex(regexStr, RegexOption.IGNORE_CASE).containsMatchIn(url)
    } catch (e: Exception) {
        url.contains(pattern.replace("*", ""))
    }
}

fun shouldInjectScript(script: Script, url: String): Boolean {
    if (!script.enabled) return false
    val meta = parseScriptHeader(script.code)
    val matchPatterns = meta["match"]?.split("||") ?: listOf("*://*/*")
    val excludePatterns = meta["exclude"]?.split("||") ?: emptyList()

    for (pattern in excludePatterns) {
        if (urlMatchesPattern(url, pattern)) return false
    }
    for (pattern in matchPatterns) {
        if (urlMatchesPattern(url, pattern)) return true
    }
    return false
}

// ── Filter Rule Parser ───────────────────────────────────────────────
fun parseFilterRules(rawText: String): Pair<List<String>, List<String>> {
    val networkRules = mutableListOf<String>()
    val cosmeticRules = mutableListOf<String>()

    for (line in rawText.lines()) {
        val trimmed = line.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("!") || trimmed.startsWith("[")) continue
        if (trimmed.startsWith("##") || trimmed.startsWith("#@#") || trimmed.startsWith("##+js")) {
            cosmeticRules.add(trimmed)
        } else {
            networkRules.add(trimmed)
        }
    }
    return Pair(networkRules, cosmeticRules)
}

// ── Ad Block Rule Matching ───────────────────────────────────────────
fun matchesAdBlockRule(url: String, host: String, rule: String): Boolean {
    val trimmed = rule.trim()
    if (trimmed.isEmpty()) return false

    // Host-anchored rule: ||domain.com^
    if (trimmed.startsWith("||") && trimmed.endsWith("^")) {
        val domain = trimmed.removePrefix("||").removeSuffix("^")
        val cleanDomain = domain.substringBefore('$')
        if (host == cleanDomain || host.endsWith(".$cleanDomain")) return true
        return false
    }

    // Host-anchored without ^: ||domain.com
    if (trimmed.startsWith("||")) {
        val domain = trimmed.removePrefix("||")
        val cleanDomain = domain.substringBefore('$')
        if (host == cleanDomain || host.endsWith(".$cleanDomain")) return true
        return false
    }

    // Exact match: |url|
    if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
        val exact = trimmed.removePrefix("|").removeSuffix("|")
        return url == exact
    }

    // Contains match for /path/ patterns
    if (trimmed.startsWith("/") && trimmed.endsWith("/")) {
        return url.contains(trimmed.removePrefix("/").removeSuffix("/"))
    }

    // Simple contains
    if (url.contains(trimmed)) return true

    return false
}

// END OF PART 4/10




// ═══════════════════════════════════════════════════════════════════
// === PART 5/10 — GreyBrowser() State Declarations [UPDATED v14] ===
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

    // ── History State ────────────────────────────────────────────────
    val history = remember { mutableStateListOf<HistoryItem>().apply { addAll(loadHistory(context)) } }
    var showHistory by remember { mutableStateOf(false) }

    // ── Scripts State ────────────────────────────────────────────────
    val scripts = remember { mutableStateListOf<Script>().apply { addAll(loadScripts(context)) } }
    var showScripts by remember { mutableStateOf(false) }
    var showScriptEditor by remember { mutableStateOf(false) }
    var editingScript by remember { mutableStateOf<Script?>(null) }

    // ── Filters State ────────────────────────────────────────────────
    val filters = remember { mutableStateListOf<Filter>().apply { addAll(loadFilters(context)) } }
    var showFilters by remember { mutableStateOf(false) }
    var filtersEnabled by remember {
        mutableStateOf(
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_FILTERS_ENABLED, true)
        )
    }
    var totalBlocked by remember { mutableIntStateOf(0) }

    // ── Toast State ──────────────────────────────────────────────────
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    fun showToast(msg: String) {
        toastMessage = msg
        showToast = true
    }

    // ── Base WebView — always exists, never destroyed ───────────────
    val baseWebView = remember {
        WebView(context).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#121212"))
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
            loadUrl("about:blank")
        }
    }

    // ── Stable container — AndroidView always renders this ──────────
    val webViewContainer = remember {
        android.widget.FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.parseColor("#121212"))
        }
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

    // currentTabIndex = -1 means homepage (neutral ground)
    var currentTabIndex by remember { mutableIntStateOf(-1) }
    var highlightedTabIndex by remember {
        mutableIntStateOf(
            tabs.indexOfFirst { it.url.substringBefore("#") == savedLastActiveUrl.substringBefore("#") }
                .let { if (it >= 0) it else -1 }
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

    // ── Link context menu state ─────────────────────────────────────
    var showLinkMenu by remember { mutableStateOf(false) }
    var linkMenuUrl by remember { mutableStateOf<String?>(null) }

    // ── URL field focus state (used by ContentLayer overlay) ────────
    var isUrlFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(tabs.toList(), pinnedDomains.toList(), lastActiveUrl) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
    }
    LaunchedEffect(tabs.map { "${it.url}|${it.title}" }.joinToString()) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
    }
    LaunchedEffect(bookmarks.toList()) { saveBookmarks(context, bookmarks) }
    LaunchedEffect(history.toList()) { saveHistory(context, history) }
    LaunchedEffect(scripts.toList()) { saveScripts(context, scripts) }
    LaunchedEffect(filters.toList()) { saveFilters(context, filters) }

    LaunchedEffect(filtersEnabled) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_FILTERS_ENABLED, filtersEnabled).apply()
    }

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
// === PART 6/10 — Tab Functions (Create, Delete, Lifecycle, Delegates) [UPDATED v20] ===
// ═══════════════════════════════════════════════════════════════════

    // ── WebView creation helper ──────────────────────────────────────
    fun createWebView(url: String): WebView {
        return WebView(context).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#121212"))
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
                // ── Inject document-start scripts ──────────────────
                for (script in scripts) {
                    if (!shouldInjectScript(script, url)) continue
                    val meta = parseScriptHeader(script.code)
                    val runAt = meta["run-at"] ?: "document-end"
                    if (runAt == "document-start") {
                        val body = getScriptBody(script.code)
                        val wrapped = "try { (function() { $body })(); } catch(e) { }"
                        wv.evaluateJavascript(wrapped, null)
                    }
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
                    // ── Log to history ──────────────────────────────
                    val cleanUrl = url.substringBefore("#")
                    history.removeAll { it.url.substringBefore("#") == cleanUrl }
                    history.add(HistoryItem(url = url, title = tabState.title.ifBlank { url }))
                    if (history.size > MAX_HISTORY_ITEMS) {
                        history.removeAt(0)
                    }
                }
                // ── Inject document-end scripts (default) ───────────
                for (script in scripts) {
                    if (!shouldInjectScript(script, url)) continue
                    val meta = parseScriptHeader(script.code)
                    val runAt = meta["run-at"] ?: "document-end"
                    if (runAt == "document-end" || runAt == "document-idle") {
                        val body = getScriptBody(script.code)
                        val wrapped = "try { (function() { $body })(); } catch(e) { }"
                        wv.evaluateJavascript(wrapped, null)
                    }
                }
            }
            // ── Ad/Filter blocking ───────────────────────────────────
            override fun shouldInterceptRequest(
                view: WebView,
                request: android.webkit.WebResourceRequest
            ): android.webkit.WebResourceResponse? {
                if (!filtersEnabled) return null
                val requestUrl = request.url.toString()
                val requestHost = request.url.host ?: return null

                for (filter in filters) {
                    if (!filter.enabled) continue
                    // Check exception rules first (@@)
                    for (rule in filter.networkRules) {
                        if (rule.startsWith("@@")) {
                            val exceptionPattern = rule.removePrefix("@@")
                            if (matchesAdBlockRule(requestUrl, requestHost, exceptionPattern)) {
                                return null // Exception — allow
                            }
                        }
                    }
                    // Check blocking rules
                    for (rule in filter.networkRules) {
                        if (rule.startsWith("@@")) continue
                        if (matchesAdBlockRule(requestUrl, requestHost, rule)) {
                            totalBlocked++
                            return android.webkit.WebResourceResponse(
                                "text/plain", "UTF-8", java.io.ByteArrayInputStream(ByteArray(0))
                            )
                        }
                    }
                }
                return null // Allow
            }
        }

        // ── Track touch coordinates (scaled for page content) ────────
        var lastTouchX = 0f
        var lastTouchY = 0f
        wv.setOnTouchListener { _, event ->
            val scale = if (wv.scale > 0) wv.scale else 1f
            lastTouchX = event.x / scale
            lastTouchY = event.y / scale
            false
        }

        // ── Long-press: use JS to find the nearest link ──────────────
        wv.setOnLongClickListener {
            wv.evaluateJavascript(
                "(function(){" +
                "var el=document.elementFromPoint($lastTouchX,$lastTouchY);" +
                "while(el&&el.tagName!=='A'&&el.tagName!=='AREA'){" +
                "el=el.parentElement;" +
                "}" +
                "return el?el.href:'';" +
                "})()"
            ) { href ->
                val clean = href.trim('"').trim()
                if (clean.isNotEmpty() && clean != "null" && clean != "undefined" && clean != "") {
                    linkMenuUrl = clean
                    showLinkMenu = true
                }
            }
            true
        }
    }

    // ── Helper: match URL against an ad block rule ────────────────
    fun matchesAdBlockRule(url: String, host: String, rule: String): Boolean {
        val trimmed = rule.trim()
        if (trimmed.isEmpty()) return false

        // Host-anchored rule: ||domain.com^
        if (trimmed.startsWith("||") && trimmed.endsWith("^")) {
            val domain = trimmed.removePrefix("||").removeSuffix("^")
            if (host == domain || host.endsWith(".$domain")) return true
            // Check for $option after ^
            val optionIndex = domain.indexOf('$')
            if (optionIndex >= 0) {
                val cleanDomain = domain.substring(0, optionIndex)
                return host == cleanDomain || host.endsWith(".$cleanDomain")
            }
            return false
        }

        // Host-anchored without ^: ||domain.com
        if (trimmed.startsWith("||")) {
            val domain = trimmed.removePrefix("||")
            val cleanDomain = if (domain.contains('$')) domain.substringBefore('$') else domain
            return host == cleanDomain || host.endsWith(".$cleanDomain")
        }

        // Exact match: |url|
        if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
            val exact = trimmed.removePrefix("|").removeSuffix("|")
            return url == exact
        }

        // Contains match for /path/ patterns
        if (trimmed.startsWith("/") && trimmed.endsWith("/")) {
            return url.contains(trimmed.removePrefix("/").removeSuffix("/"))
        }

        // Simple contains
        if (url.contains(trimmed)) return true

        return false
    }

    // ── Remove duplicate tab if exists, adjust indices ─────────────
    fun removeDuplicateTab(url: String) {
        val cleanUrl = url.substringBefore("#")
        val oldIndex = tabs.indexOfFirst {
            it.url.substringBefore("#") == cleanUrl && !it.isBlankTab
        }
        if (oldIndex >= 0) {
            tabs[oldIndex].webView?.destroy()
            tabs.removeAt(oldIndex)
            // Fix parent references
            for (t in tabs) {
                if (t.parentTabIndex == oldIndex) t.parentTabIndex = -1
                else if (t.parentTabIndex > oldIndex) t.parentTabIndex--
            }
            // Adjust indices
            if (currentTabIndex >= oldIndex && currentTabIndex >= 0) currentTabIndex--
            if (highlightedTabIndex >= oldIndex && highlightedTabIndex >= 0) highlightedTabIndex--
            val updated = mutableMapOf<Int, Long>()
            for ((idx, time) in pendingDeletions) {
                if (idx > oldIndex) updated[idx - 1] = time
                else if (idx < oldIndex) updated[idx] = time
            }
            pendingDeletions.clear()
            pendingDeletions.putAll(updated)
        }
    }

    // ── Tab lifecycle management ────────────────────────────────────
    fun manageTabLifecycle(activeIndex: Int) {
        if (activeIndex < 0 || activeIndex >= tabs.size) return
        val activeTab = tabs[activeIndex]
        if (activeTab.webView == null && activeTab.isDiscarded) {
            activeTab.webView = createWebView(activeTab.url)
            activeTab.isDiscarded = false
            setupDelegates(activeTab)
            activeTab.lastUpdated = System.currentTimeMillis()
        }

        val warmTabs = tabs.filterIndexed { i, t ->
            i != activeIndex && !t.isDiscarded && t.webView != null
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
    // insertAfterIndex = -1 means insert at top (from homepage)
    // insertAfterIndex >= 0 means insert right below that tab (from long-press)
    fun createForegroundTab(url: String, insertAfterIndex: Int = -1) {
        // Remove old duplicate if exists
        removeDuplicateTab(url)
        // Calculate insertion point
        val insertIdx = if (insertAfterIndex >= 0) insertAfterIndex + 1 else 0
        // Capture parent before switching
        val parentIdx = if (insertAfterIndex >= 0) insertAfterIndex else -1
        val wv = createWebView(url)
        tabs.add(insertIdx, TabState().apply {
            webView = wv
            this.url = url
            isBlankTab = false
            isDiscarded = false
            lastUpdated = System.currentTimeMillis()
            parentTabIndex = parentIdx
            setupDelegates(this)
        })
        currentTabIndex = insertIdx
        highlightedTabIndex = currentTabIndex
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
                // Fix parent references
                for (t in tabs) {
                    if (t.parentTabIndex == index) t.parentTabIndex = -1
                    else if (t.parentTabIndex > index) t.parentTabIndex--
                }

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
            baseWebView.onPause()
        } else {
            baseWebView.onResume()
            if (currentTabIndex >= 0) {
                tabs.getOrNull(currentTabIndex)?.webView?.onResume()
                manageTabLifecycle(currentTabIndex)
            }
        }
    }

// END OF PART 6/10



    // ═══════════════════════════════════════════════════════════════════
    // === PART 7/10 — BackHandler, ContentLayer Composable [UPDATED v21] ===
    // ═══════════════════════════════════════════════════════════════════

    // ── Helper: close a tab immediately and fix parent references ───
    fun closeTabAndFixParents(index: Int) {
        if (index < 0 || index >= tabs.size) return
        tabs[index].webView?.destroy()
        tabs.removeAt(index)
        for (t in tabs) {
            if (t.parentTabIndex == index) t.parentTabIndex = -1
            else if (t.parentTabIndex > index) t.parentTabIndex--
        }
    }

    BackHandler {
        when {
            // Close overlays first
            showTabManager -> showTabManager = false
            showBookmarks -> showBookmarks = false
            showHistory -> showHistory = false
            showMenu -> showMenu = false
            showConfirmDialog -> { showConfirmDialog = false; confirmAction = null }
            showLinkMenu -> { showLinkMenu = false; linkMenuUrl = null }
            // On homepage (neutral ground)
            currentTabIndex == -1 -> {
                activity?.finish()
            }
            // On a real tab
            currentTabIndex >= 0 -> {
                val tab = tabs.getOrNull(currentTabIndex)
                if (tab?.webView?.canGoBack() == true) {
                    // WebView has history → go back in page
                    tab.webView?.goBack()
                } else {
                    // No WebView history → close tab and go to parent
                    val parentIdx = tab?.parentTabIndex ?: -1
                    val closingIdx = currentTabIndex
                    // Adjust indices
                    if (highlightedTabIndex == closingIdx) highlightedTabIndex = -1
                    else if (highlightedTabIndex > closingIdx) highlightedTabIndex--
                    // Remove from pending deletions
                    pendingDeletions.remove(closingIdx)
                    val updated = mutableMapOf<Int, Long>()
                    for ((idx, time) in pendingDeletions) {
                        updated[if (idx > closingIdx) idx - 1 else idx] = time
                    }
                    pendingDeletions.clear()
                    pendingDeletions.putAll(updated)
                    // Close the tab
                    closeTabAndFixParents(closingIdx)
                    // Navigate to parent or homepage
                    if (parentIdx >= 0 && parentIdx < tabs.size) {
                        currentTabIndex = parentIdx
                    } else {
                        currentTabIndex = -1
                    }
                    if (tabs.isEmpty()) {
                        currentTabIndex = -1
                        selectedDomain = ""
                    }
                }
            }
        }
    }

    @Composable
    fun ContentLayer() {
        Box(Modifier.fillMaxSize().background(BG)) {
            // ONE AndroidView — factory never changes, container always exists
            AndroidView(
                factory = { webViewContainer },
                update = { container ->
                    val target = if (currentTabIndex == -1) {
                        baseWebView
                    } else {
                        tabs.getOrNull(currentTabIndex)?.webView ?: baseWebView
                    }
                    if (container.childCount == 0 || container.getChildAt(0) != target) {
                        // Pause the old WebView if it exists
                        val old = if (container.childCount > 0) container.getChildAt(0) as? WebView else null
                        old?.onPause()
                        container.removeAllViews()
                        container.addView(target)
                        target.onResume()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Grey overlay only on homepage
            if (currentTabIndex == -1) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Grey",
                        color = WHITE.copy(alpha = 0.15f),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Transparent overlay to dismiss keyboard when tapping WebView area
            if (isUrlFocused) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            focusManager.clearFocus()
                        }
                )
            }
        }
    }

    // END OF PART 7/10
    
    
    
    // ═══════════════════════════════════════════════════════════════════
// === PART 8/10 — Top Bar, Tab Manager UI, Menu, Toast, Link Menu [UPDATED v36] ===
// ═══════════════════════════════════════════════════════════════════

    var urlInput by remember {
        mutableStateOf(
            TextFieldValue(
                if (currentTabIndex == -1) ""
                else currentTab?.url?.let { if (it == "about:blank") "" else it } ?: ""
            )
        )
    }

    // ── URL sync — SideEffect runs on every recomposition ──────────
    SideEffect {
        if (!isUrlFocused && currentTabIndex >= 0) {
            val tabUrl = currentTab?.url ?: ""
            if (tabUrl != "about:blank" && tabUrl != urlInput.text) {
                urlInput = TextFieldValue(tabUrl, selection = TextRange(0))
            }
        }
        if (!isUrlFocused && currentTabIndex == -1 && urlInput.text.isNotEmpty()) {
            urlInput = TextFieldValue("", selection = TextRange(0))
        }
    }

    // ── Pattern Lock state ──────────────────────────────────────────
    var showAppLockSettings by remember { mutableStateOf(false) }
    var patternDrawMode by remember { mutableStateOf("") }

    // ── Launch check: if lock enabled, show unlock screen ───────────
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
        val lockEnabled = prefs.getBoolean("lock_enabled", false)
        val hasPattern = prefs.getString("pattern_hash", null) != null
        if (lockEnabled && hasPattern) {
            patternDrawMode = "unlock"
        }
    }

    // ── Everything wrapped in a Box so overlays layer correctly ─────
    Box(Modifier.fillMaxSize()) {

        // ── Pattern Unlock Screen (app launch) ──────────────────────
        if (patternDrawMode == "unlock") {
            val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
            val savedHash = prefs.getString("pattern_hash", null)
            PatternDrawScreen(
                mode = "unlock",
                savedHash = savedHash,
                onDismiss = { activity?.finish() },
                onPatternVerified = { patternDrawMode = "" },
                onPatternSet = {},
                onPatternRemoved = {}
            )
        }

        // ── Confirm dialog ──────────────────────────────────────────
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

        // ── App Lock Settings Screen ────────────────────────────────
        if (showAppLockSettings) {
            val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
            val lockEnabled = prefs.getBoolean("lock_enabled", false)
            val hasPattern = prefs.getString("pattern_hash", null) != null

            AppLockSettingsScreen(
                lockEnabled = lockEnabled,
                hasPattern = hasPattern,
                onDismiss = { showAppLockSettings = false },
                onToggleChange = { newValue ->
                    if (newValue) {
                        if (!hasPattern) {
                            showAppLockSettings = false
                            patternDrawMode = "set"
                        } else {
                            prefs.edit().putBoolean("lock_enabled", true).apply()
                        }
                    } else {
                        showAppLockSettings = false
                        patternDrawMode = "toggle_off"
                    }
                },
                onChangePattern = {
                    showAppLockSettings = false
                    patternDrawMode = "change_verify"
                }
            )
        }

        // ── Pattern Draw Screen (set / change / toggle_off) ─────────
        if (patternDrawMode in listOf("set", "change_verify", "change_set", "toggle_off")) {
            val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
            val savedHash = prefs.getString("pattern_hash", null)

            PatternDrawScreen(
                mode = patternDrawMode,
                savedHash = savedHash,
                onDismiss = {
                    patternDrawMode = ""
                },
                onPatternVerified = {
                    when (patternDrawMode) {
                        "change_verify" -> patternDrawMode = "change_set"
                        "toggle_off" -> {
                            prefs.edit().putBoolean("lock_enabled", false).apply()
                            patternDrawMode = ""
                            showToast("App lock disabled")
                        }
                    }
                },
                onPatternSet = { hash ->
                    prefs.edit().putString("pattern_hash", hash).apply()
                    prefs.edit().putBoolean("lock_enabled", true).apply()
                    patternDrawMode = ""
                    showToast("Pattern saved")
                },
                onPatternRemoved = {}
            )
        }

        // ── Scripts Manager ─────────────────────────────────────────
        if (showScripts) {
            ScriptsManagerScreen(
                scripts = scripts,
                onDismiss = { showScripts = false },
                onAddScript = {
                    editingScript = null
                    showScriptEditor = true
                },
                onEditScript = { script ->
                    editingScript = script
                    showScriptEditor = true
                },
                onDeleteScript = { id ->
                    scripts.removeAll { it.id == id }
                    showToast("Script deleted")
                },
                onToggleScript = { id ->
                    val index = scripts.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        scripts[index] = scripts[index].copy(enabled = !scripts[index].enabled)
                    }
                }
            )
        }

        // ── Script Editor ───────────────────────────────────────────
        if (showScriptEditor) {
            ScriptEditorScreen(
                script = editingScript,
                onDismiss = { showScriptEditor = false },
                onSave = { title, code ->
                    var finalTitle = title
                    if (finalTitle.isBlank()) {
                        val nameMatch = Regex("""@name\s+(.+)""").find(code)
                        finalTitle = nameMatch?.groupValues?.get(1)?.trim() ?: ""
                    }
                    if (finalTitle.isBlank()) {
                        showToast("Enter a script name")
                        return@ScriptEditorScreen
                    }
                    if (editingScript != null) {
                        val index = scripts.indexOfFirst { it.id == editingScript!!.id }
                        if (index >= 0) {
                            scripts[index] = scripts[index].copy(
                                title = finalTitle,
                                code = code,
                                timestamp = System.currentTimeMillis()
                            )
                        }
                    } else {
                        scripts.add(Script(title = finalTitle, code = code))
                    }
                    showScriptEditor = false
                    showToast(if (editingScript != null) "Script updated" else "Script added")
                }
            )
        }

        // ── Filters Manager ─────────────────────────────────────────
        if (showFilters) {
            FiltersManagerScreen(
                filters = filters,
                filtersEnabled = filtersEnabled,
                totalBlocked = totalBlocked,
                onDismiss = { showFilters = false },
                onToggleMaster = { filtersEnabled = it },
                onToggleFilter = { id ->
                    val index = filters.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        filters[index] = filters[index].copy(enabled = !filters[index].enabled)
                    }
                },
                onDeleteFilter = { id ->
                    filters.removeAll { it.id == id }
                    showToast("Filter deleted")
                },
                onImportFilter = { name, rawText ->
                    val (network, cosmetic) = parseFilterRules(rawText)
                    filters.add(Filter(
                        name = name,
                        rawText = rawText,
                        networkRules = network,
                        cosmeticRules = cosmetic,
                        networkRuleCount = network.size,
                        cosmeticRuleCount = cosmetic.size
                    ))
                    showToast("Filter imported: ${network.size} rules")
                }
            )
        }

        // ── Bookmarks UI ────────────────────────────────────────────
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

        // ── History UI ──────────────────────────────────────────────
        if (showHistory) {
            HistoryUI(
                history = history,
                onDismiss = { showHistory = false },
                onOpenUrl = { url -> createForegroundTab(url) },
                faviconBitmaps = faviconBitmaps,
                loadFavicon = { loadFavicon(it) }
            )
        }

        // ── Link Context Menu ───────────────────────────────────────
        if (showLinkMenu && linkMenuUrl != null) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showLinkMenu = false; linkMenuUrl = null },
                properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Surface(
                    modifier = Modifier
                        .width(240.dp)
                        .border(1.dp, WHITE, RectangleShape),
                    color = SURFACE,
                    shape = RectangleShape,
                    tonalElevation = 0.dp
                ) {
                    Column {
                        DropdownMenuItem(
                            text = { Text("New Tab", color = WHITE) },
                            onClick = {
                                createForegroundTab(linkMenuUrl!!, currentTabIndex)
                                showLinkMenu = false
                                linkMenuUrl = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Link", color = WHITE) },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(linkMenuUrl!!))
                                showToast("Link copied")
                                showLinkMenu = false
                                linkMenuUrl = null
                            }
                        )
                    }
                }
            }
        }

        // ── Tab Manager (real tabs only) ────────────────────────────
        if (showTabManager) {
            val realTabs = tabs.toList()
            val domainGroups = realTabs.groupBy { getDomainName(it.url) }.filter { it.key.isNotBlank() }
            val sortedDomains = domainGroups.keys.sortedWith(
                compareByDescending<String> { pinnedDomains.contains(it) }
                    .thenBy { d: String -> domainGroups[d]?.firstOrNull()?.let { t -> tabs.indexOf(t) } ?: Int.MAX_VALUE }
            )
            val pinnedSorted = sortedDomains.filter { pinnedDomains.contains(it) }
            val unpinnedSorted = sortedDomains.filter { !pinnedDomains.contains(it) }
            val allSidebarItems = listOf("__ALL__") + pinnedSorted + unpinnedSorted
            val highlightDomain = if (highlightedTabIndex >= 0 && highlightedTabIndex < tabs.size) {
                getDomainName(tabs[highlightedTabIndex].url)
            } else ""

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
                        // ── Header ───────────────────────────────────
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
                            if (realTabs.isNotEmpty()) {
                                Spacer(Modifier.width(8.dp))
                                Text("(${realTabs.size})", color = MUTED, fontSize = 14.sp)
                            }
                        }

                        // ── Body: Sidebar + Tab list ─────────────────
                        Row(Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp)) {
                            val groupListState = rememberLazyListState()

                            LaunchedEffect(Unit) {
                                selectedDomain = ""
                                if (highlightDomain.isNotBlank()) {
                                    val blinkIdx = allSidebarItems.indexOf(highlightDomain)
                                    if (blinkIdx >= 0) {
                                        groupListState.scrollToItem(blinkIdx)
                                        blinkTargetDomain.value = highlightDomain
                                        showBlink = true
                                        delay(1200)
                                        showBlink = false
                                        blinkTargetDomain.value = ""
                                        groupListState.scrollToItem(0)
                                    }
                                }
                            }

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
                                        tabCount = realTabs.size,
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

                            val tabsToShow = if (selectedDomain.isBlank()) realTabs
                            else domainGroups[selectedDomain] ?: emptyList()
                            val tabListState = rememberLazyListState()
                            val scrollTarget = if (highlightedTabIndex >= 0) tabs.getOrNull(highlightedTabIndex) else null
                            LaunchedEffect(selectedDomain) {
                                val idx = tabsToShow.indexOf(scrollTarget)
                                if (idx >= 0) tabListState.scrollToItem(idx)
                            }

                            if (realTabs.isEmpty()) {
                                Box(
                                    Modifier.weight(1f).fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("No open tabs", color = MUTED, fontSize = 16.sp)
                                        Spacer(Modifier.height(8.dp))
                                        Text("Tap the + button to browse", color = MUTED.copy(alpha = 0.7f), fontSize = 14.sp)
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
                                                Text("No tabs in this group", color = MUTED, fontSize = 14.sp)
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
                                                    Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (tabFav != null) {
                                                        Image(
                                                            tabFav.asImageBitmap(),
                                                            tabDomain,
                                                            Modifier.size(16.dp).clip(CircleShape),
                                                            contentScale = ContentScale.Fit
                                                        )
                                                    } else {
                                                        Box(
                                                            Modifier.size(16.dp).clip(CircleShape).background(Color.DarkGray),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = tabDomain.take(1).uppercase(),
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
                                                            Icon(Icons.Default.Undo, "Undo", tint = WHITE, modifier = Modifier.size(18.dp))
                                                        }
                                                    } else {
                                                        IconButton({ requestDeleteTab(tabIndex) }) {
                                                            Icon(Icons.Default.Close, "Close", tint = if (isHighlighted) Color.Black else WHITE, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ── Footer ───────────────────────────────────
                        Column(
                            Modifier.fillMaxWidth().padding(12.dp).navigationBarsPadding()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    currentTabIndex = -1
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
                                    Text(if (isPinned) "Unpin Group" else "Pin Group", fontSize = 13.sp, color = tint)
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
                                                tabs.removeAll(toRemove.toSet())
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

        // ── Main layout ─────────────────────────────────────────────
        Column(
            Modifier.fillMaxSize().systemBarsPadding().background(BG)
        ) {
            Surface(
                color = SURFACE,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)) {
                        IconButton({ showTabManager = true }) {
                            Icon(Icons.Default.Tab, "Tabs", tint = WHITE)
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    val canGoForward = currentTab?.webView?.canGoForward() == true
                    Box(Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)) {
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

                    val isLoading = currentTabIndex >= 0 && (currentTab?.progress ?: 100) in 1..99
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                if (currentTabIndex == -1) "Search or enter URL"
                                else currentTab?.url?.take(50) ?: "",
                                color = WHITE.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(SURFACE)
                            .focusRequester(focusRequester)
                            .onFocusChanged { isUrlFocused = it.isFocused }
                            .drawBehind {
                                if (isLoading) {
                                    drawRect(
                                        color = WHITE,
                                        size = size.copy(width = size.width * (currentTab?.progress ?: 100) / 100f)
                                    )
                                }
                            },
                        textStyle = TextStyle(color = if (isLoading) Color.Gray else WHITE, fontSize = 14.sp),
                        shape = RectangleShape,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                val input = urlInput.text
                                if (input.isNotBlank()) {
                                    focusManager.clearFocus()
                                    urlInput = urlInput.copy(selection = TextRange(0))
                                    val uri = resolveUrl(input)
                                    if (currentTabIndex == -1) {
                                        createForegroundTab(uri)
                                    } else {
                                        val cleanUri = uri.substringBefore("#")
                                        val existingIndex = tabs.indexOfFirst {
                                            it.url.substringBefore("#") == cleanUri && !it.isBlankTab
                                        }
                                        if (existingIndex >= 0 && existingIndex != currentTabIndex) {
                                            removeDuplicateTab(uri)
                                            createForegroundTab(uri)
                                        } else {
                                            currentTab?.webView?.loadUrl(uri)
                                        }
                                    }
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
                                    urlInput = urlInput.copy(selection = TextRange(0, urlInput.text.length))
                                    focusRequester.requestFocus()
                                }) {
                                    Icon(Icons.Default.SelectAll, "Select all", tint = WHITE)
                                }
                            }
                        }
                    )

                    Spacer(Modifier.width(4.dp))

                    Box(Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)) {
                        IconButton({ currentTabIndex = -1 }) {
                            Icon(Icons.Default.Add, "New Tab", tint = WHITE)
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    Box(Modifier.border(0.5.dp, BORDER_SUBTLE, RectangleShape)) {
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
                                    text = { Text("Refresh", color = WHITE) },
                                    onClick = {
                                        showMenu = false
                                        currentTab?.webView?.reload()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add to Bookmark", color = WHITE) },
                                    onClick = {
                                        showMenu = false
                                        val url = currentTab?.url ?: ""
                                        if (url != "about:blank" && url.isNotBlank()) {
                                            bookmarks.removeAll { it.url == url }
                                            bookmarks.add(Bookmark(url = url, title = currentTab?.title?.ifBlank { url } ?: url))
                                            showToast("Added to bookmarks")
                                        }
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Bookmarks", color = WHITE) },
                                onClick = { showMenu = false; showBookmarks = true }
                            )
                            DropdownMenuItem(
                                text = { Text("History", color = WHITE) },
                                onClick = { showMenu = false; showHistory = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Scripts", color = WHITE) },
                                onClick = {
                                    showMenu = false
                                    showScripts = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Filters", color = WHITE) },
                                onClick = {
                                    showMenu = false
                                    showFilters = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("App Lock", color = WHITE) },
                                onClick = {
                                    showMenu = false
                                    showAppLockSettings = true
                                }
                            )
                        }
                    }
                }
            }

            Box(Modifier.weight(1f).fillMaxWidth()) {
                ContentLayer()
            }
        }
    }

    // ── Toast ────────────────────────────────────────────────────
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
// === PART 10/10 — Helper Composables (Chips, HistoryUI) [UPDATED v3] ===
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

    // ── Blink animation ──────────────────────────────────────────
    val blinkAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        )
    )

    val borderColor = when {
        isBlinking -> WHITE.copy(alpha = blinkAlpha)
        isSelected -> WHITE
        else -> BORDER_SUBTLE
    }

    val borderThickness = if (isBlinking) 1.dp else 0.5.dp

    val bg = if (isSelected) WHITE else Color.Transparent
    Surface(
        Modifier.padding(vertical = 4.dp).width(52.dp)
            .clickable { onClick() }
            .border(borderThickness, borderColor, RectangleShape),
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

@Composable
fun HistoryUI(
    history: List<HistoryItem>,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit,
    faviconBitmaps: Map<String, Bitmap?>,
    loadFavicon: (String) -> Unit
) {
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
                    Text("History", color = WHITE, fontSize = 18.sp)
                    if (history.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text("(${history.size})", color = MUTED, fontSize = 14.sp)
                    }
                }
                if (history.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No history", color = MUTED, fontSize = 16.sp)
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        // Most recent first (history is stored most-recent-last, so we reverse)
                        items(history.reversed()) { item ->
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// END OF PART 10/10




// ═══════════════════════════════════════════════════════════════════
// === PART 11/11 — App Lock Settings + Pattern Draw Screen ===
// ═══════════════════════════════════════════════════════════════════

fun hashPattern(pattern: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(pattern.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}

// ── App Lock Settings Screen ─────────────────────────────────────────
@Composable
fun AppLockSettingsScreen(
    lockEnabled: Boolean,
    hasPattern: Boolean,
    onDismiss: () -> Unit,
    onToggleChange: (Boolean) -> Unit,
    onChangePattern: () -> Unit
) {
    var toggleChecked by remember { mutableStateOf(lockEnabled) }

    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            Modifier.fillMaxSize().statusBarsPadding().background(SURFACE),
            color = SURFACE
        ) {
            Column(Modifier.fillMaxSize().navigationBarsPadding()) {
                // ── Header ─────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("App Lock", color = WHITE, fontSize = 18.sp)
                }

                Divider(color = Color.DarkGray, thickness = 0.5.dp)

                // ── Enable/Disable Toggle ──────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (toggleChecked) "Enabled" else "Disabled",
                        color = WHITE,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = toggleChecked,
                        onCheckedChange = { newVal ->
                            toggleChecked = newVal
                            onToggleChange(newVal)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WHITE,
                            checkedTrackColor = WHITE.copy(alpha = 0.3f),
                            uncheckedThumbColor = WHITE.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF444444)
                        )
                    )
                }

                Divider(color = Color.DarkGray, thickness = 0.5.dp)

                Spacer(Modifier.height(24.dp))

                // ── Set / Change Pattern Button ────────────────────
                if (toggleChecked) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (hasPattern) {
                                    onChangePattern()
                                } else {
                                    onToggleChange(true)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                            border = BorderStroke(1.dp, WHITE)
                        ) {
                            Text(
                                if (hasPattern) "Change Pattern" else "Set Pattern",
                                color = WHITE,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Box(
                        Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Enable app lock to set a pattern",
                            color = MUTED,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Pattern Draw Screen (shared by unlock, set, change, toggle_off) ──
@Composable
fun PatternDrawScreen(
    mode: String,
    savedHash: String?,
    onDismiss: () -> Unit,
    onPatternVerified: () -> Unit,
    onPatternSet: (String) -> Unit,
    onPatternRemoved: () -> Unit
) {
    val dotSpacing = 80.dp
    val dotSize = 24.dp
    val gridColumns = 3
    val gridRows = 3
    val density = LocalDensity.current

    val selectedDots = remember { mutableStateListOf<Int>() }
    var firstPattern by remember { mutableStateOf("") }
    var errorState by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var promptText by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(0) }

    // Pre-compute pixel values once
    val spacingPx = remember { with(density) { dotSpacing.toPx() } }
    val sizePx = remember { with(density) { dotSize.toPx() } }

    // ── Initialize prompt ─────────────────────────────────────────
    LaunchedEffect(mode) {
        selectedDots.clear()
        errorState = false
        showError = false
        firstPattern = ""
        step = 0
        promptText = when (mode) {
            "unlock" -> "Draw pattern to unlock"
            "set" -> "Connect at least 4 dots to make pattern"
            "change_verify" -> "Draw the last pattern to change"
            "change_set" -> "Connect at least 4 dots to make new pattern"
            "toggle_off" -> "Draw pattern to disable lock"
            else -> ""
        }
    }

    // ── Error shake animation ─────────────────────────────────────
    val shakeOffset by animateFloatAsState(
        targetValue = if (showError) 10f else 0f,
        animationSpec = if (showError) {
            repeatable(iterations = 3, animation = tween(50), repeatMode = RepeatMode.Reverse)
        } else {
            tween(0)
        }
    )

    // ── Error auto-clear ──────────────────────────────────────────
    LaunchedEffect(showError) {
        if (showError) {
            delay(600)
            showError = false
            selectedDots.clear()
        }
    }

    // ── Helper: check if a pixel position hits a dot ──────────────
    fun hitDotAt(px: Float, py: Float): Int? {
        val hitRadius = spacingPx * 0.6f

        for (row in 0 until gridRows) {
            for (col in 0 until gridColumns) {
                val cx = col * spacingPx + sizePx / 2
                val cy = row * spacingPx + sizePx / 2
                val dist = kotlin.math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy))
                if (dist <= hitRadius) {
                    return row * gridColumns + col + 1
                }
            }
        }
        return null
    }

    // ── Helper: handle pattern completion ─────────────────────────
    fun handleComplete() {
        val patternStr = selectedDots.joinToString(",")
        val dotCount = selectedDots.size
        val hash = hashPattern(patternStr)

        // Dot 9 master key (tiny drag from dot 9)
        if (dotCount == 1 && patternStr == "9") {
            when (mode) {
                "unlock", "change_verify", "toggle_off" -> {
                    onPatternVerified()
                    return
                }
            }
        }

        when (mode) {
            "unlock" -> {
                if (hash == savedHash) {
                    onPatternVerified()
                } else {
                    showError = true
                    errorState = true
                    promptText = "Incorrect pattern"
                }
            }
            "set" -> {
                if (dotCount < 4) {
                    showError = true
                    errorState = true
                    promptText = "Connect at least 4 dots"
                } else if (step == 0) {
                    firstPattern = patternStr
                    step = 1
                    selectedDots.clear()
                    promptText = "Do it again to confirm"
                } else {
                    if (hashPattern(patternStr) == hashPattern(firstPattern)) {
                        onPatternSet(hash)
                    } else {
                        showError = true
                        errorState = true
                        promptText = "Patterns don't match. Try again."
                        firstPattern = ""
                        step = 0
                    }
                }
            }
            "change_verify" -> {
                if (hash == savedHash) {
                    onPatternVerified()
                } else {
                    showError = true
                    errorState = true
                    promptText = "Incorrect pattern"
                }
            }
            "change_set" -> {
                if (dotCount < 4) {
                    showError = true
                    errorState = true
                    promptText = "Connect at least 4 dots"
                } else if (step == 0) {
                    firstPattern = patternStr
                    step = 1
                    selectedDots.clear()
                    promptText = "Do it again to confirm"
                } else {
                    if (hashPattern(patternStr) == hashPattern(firstPattern)) {
                        onPatternSet(hash)
                    } else {
                        showError = true
                        errorState = true
                        promptText = "Patterns don't match. Try again."
                        firstPattern = ""
                        step = 0
                    }
                }
            }
            "toggle_off" -> {
                if (hash == savedHash) {
                    onPatternVerified()
                } else {
                    showError = true
                    errorState = true
                    promptText = "Incorrect pattern"
                }
            }
        }
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
            Column(
                Modifier.fillMaxSize().navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Header ─────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        when (mode) {
                            "unlock" -> "Unlock"
                            "set" -> "Set Pattern"
                            "change_verify" -> "Change Pattern"
                            "change_set" -> "Set New Pattern"
                            "toggle_off" -> "Disable Lock"
                            else -> "Pattern"
                        },
                        color = WHITE,
                        fontSize = 18.sp
                    )
                }

                Spacer(Modifier.weight(0.3f))

                // ── Pattern grid (centered) ────────────────────────
                Box(
                    Modifier
                        .size(dotSpacing * 2 + dotSize)
                        .offset { IntOffset(shakeOffset.toInt(), 0) }
                        .pointerInput(mode, step) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    hitDotAt(offset.x, offset.y)?.let { dot ->
                                        if (!selectedDots.contains(dot)) {
                                            selectedDots.add(dot)
                                        }
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    hitDotAt(change.position.x, change.position.y)?.let { dot ->
                                        if (!selectedDots.contains(dot)) {
                                            selectedDots.add(dot)
                                        }
                                    }
                                },
                                onDragEnd = {
                                    handleComplete()
                                },
                                onDragCancel = {
                                    selectedDots.clear()
                                }
                            )
                        }
                ) {
                    // ── Draw lines between connected dots ─────────
                    Canvas(Modifier.fillMaxSize()) {
                        if (selectedDots.size >= 2) {
                            val path = Path()
                            for (i in 0 until selectedDots.size - 1) {
                                val from = selectedDots[i]
                                val to = selectedDots[i + 1]
                                val fromCol = (from - 1) % gridColumns
                                val fromRow = (from - 1) / gridColumns
                                val toCol = (to - 1) % gridColumns
                                val toRow = (to - 1) / gridColumns

                                path.moveTo(
                                    fromCol * spacingPx + sizePx / 2,
                                    fromRow * spacingPx + sizePx / 2
                                )
                                path.lineTo(
                                    toCol * spacingPx + sizePx / 2,
                                    toRow * spacingPx + sizePx / 2
                                )
                            }
                            drawPath(
                                path,
                                color = if (errorState) DELETE_BG else WHITE,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }

                    // ── Draw dots (all solid white, always) ───────
                    for (row in 0 until gridRows) {
                        for (col in 0 until gridColumns) {
                            Box(
                                Modifier
                                    .offset(x = dotSpacing * col, y = dotSpacing * row)
                                    .size(dotSize)
                                    .background(WHITE, RectangleShape)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Prompt text ─────────────────────────────────────
                Text(
                    promptText,
                    color = if (errorState) DELETE_BG else MUTED,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(24.dp))

                // ── Reset button ────────────────────────────────────
                if (mode != "unlock") {
                    OutlinedButton(
                        onClick = {
                            selectedDots.clear()
                            firstPattern = ""
                            errorState = false
                            showError = false
                            step = 0
                            promptText = when (mode) {
                                "set" -> "Connect at least 4 dots to make pattern"
                                "change_verify" -> "Draw the last pattern to change"
                                "change_set" -> "Connect at least 4 dots to make new pattern"
                                "toggle_off" -> "Draw pattern to disable lock"
                                else -> ""
                            }
                        },
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                        border = BorderStroke(1.dp, WHITE)
                    ) {
                        Text("Reset")
                    }
                }

                Spacer(Modifier.weight(0.3f))
            }
        }
    }
}

// END OF PART 11/11




// ═══════════════════════════════════════════════════════════════════
// === PART 12/12 — Scripts Manager + Script Editor + Script Guide ===
// ═══════════════════════════════════════════════════════════════════

@Composable
fun ScriptsManagerScreen(
    scripts: List<Script>,
    onDismiss: () -> Unit,
    onAddScript: () -> Unit,
    onEditScript: (Script) -> Unit,
    onDeleteScript: (String) -> Unit,
    onToggleScript: (String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var scriptToDelete by remember { mutableStateOf<String?>(null) }
    var showGuide by remember { mutableStateOf(false) }

    if (showDeleteConfirm && scriptToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; scriptToDelete = null },
            title = { Text("Delete Script?", color = WHITE, fontSize = 18.sp) },
            text = { Text("This cannot be undone.", color = MUTED, fontSize = 14.sp) },
            confirmButton = {
                TextButton({
                    onDeleteScript(scriptToDelete!!)
                    showDeleteConfirm = false
                    scriptToDelete = null
                }) { Text("Delete", color = WHITE) }
            },
            dismissButton = {
                TextButton({
                    showDeleteConfirm = false
                    scriptToDelete = null
                }) { Text("Cancel", color = WHITE) }
            },
            containerColor = SURFACE,
            titleContentColor = WHITE,
            textContentColor = WHITE,
            shape = RectangleShape,
            tonalElevation = 0.dp
        )
    }

    if (showGuide) {
        ScriptGuideScreen(onDismiss = { showGuide = false })
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
                // ── Header ─────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Scripts", color = WHITE, fontSize = 18.sp)
                    if (scripts.isNotEmpty()) {
                        Spacer(Modifier.width(4.dp))
                        Text("(${scripts.size})", color = MUTED, fontSize = 14.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        onClick = { showGuide = true },
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                        border = BorderStroke(1.dp, WHITE),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("?", fontSize = 14.sp, color = WHITE)
                    }
                }

                // ── Script List ────────────────────────────────────
                if (scripts.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No scripts", color = MUTED, fontSize = 16.sp)
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        items(scripts) { script ->
                            Surface(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    .border(0.5.dp, Color.DarkGray, RectangleShape),
                                color = Color.Transparent
                            ) {
                                Row(
                                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable { onEditScript(script) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(
                                        checked = script.enabled,
                                        onCheckedChange = { onToggleScript(script.id) },
                                        modifier = Modifier.padding(end = 4.dp),
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = WHITE,
                                            checkedTrackColor = WHITE.copy(alpha = 0.3f),
                                            uncheckedThumbColor = WHITE.copy(alpha = 0.5f),
                                            uncheckedTrackColor = Color(0xFF444444)
                                        )
                                    )
                                    Text(
                                        script.title.ifBlank { "Untitled" },
                                        color = if (script.enabled) WHITE else MUTED,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton({
                                        scriptToDelete = script.id
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

                // ── Footer ─────────────────────────────────────────
                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    OutlinedButton(
                        onClick = onAddScript,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                        border = BorderStroke(1.dp, WHITE)
                    ) {
                        Icon(Icons.Default.Add, null, tint = WHITE)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Script", color = WHITE)
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptEditorScreen(
    script: Script?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(script?.title ?: "") }
    var code by remember { mutableStateOf(script?.code ?: "") }

    // AlertDialog — same style as Filter Import, but larger for code editing
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (script != null) "Edit Script" else "Add Script",
                    color = WHITE,
                    fontSize = 18.sp
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, "Close", tint = WHITE, modifier = Modifier.size(20.dp))
                }
            }
        },
        text = {
            Column {
                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    placeholder = { Text("Script name", color = WHITE.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = WHITE, fontSize = 14.sp),
                    shape = RectangleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = WHITE,
                        unfocusedBorderColor = WHITE,
                        cursorColor = WHITE
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Code label
                Text("Code", color = MUTED, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))

                // Code field — fixed height, internally scrollable
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = {
                        Column {
                            Text(
                                "JavaScript code...",
                                color = WHITE.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Note: Paste your code here. For editing, use an\nexternal code editor for a better experience.",
                                color = MUTED.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 350.dp),
                    textStyle = TextStyle(
                        color = WHITE,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    ),
                    shape = RectangleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = WHITE,
                        unfocusedBorderColor = WHITE,
                        cursorColor = WHITE
                    )
                )
            }
        },
        confirmButton = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                    border = BorderStroke(1.dp, WHITE)
                ) {
                    Text("Cancel", color = WHITE)
                }
                OutlinedButton(
                    onClick = { onSave(title, code) },
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                    border = BorderStroke(1.dp, WHITE)
                ) {
                    Text("Save", color = WHITE)
                }
            }
        },
        containerColor = SURFACE,
        titleContentColor = WHITE,
        textContentColor = WHITE,
        shape = RectangleShape,
        tonalElevation = 0.dp
    )
}

@Composable
fun ScriptGuideScreen(onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current

    val guideText = """
WebView Script Guide

Scripts run in page context via Android WebView.
Full DOM access and standard JS APIs available.

Available:
• DOM manipulation (querySelector, etc.)
• XHR/fetch interception
• Media element detection (video, audio, source)
• URL.createObjectURL hooking
• navigator.clipboard.writeText
• window.open for new tabs
• @match / @exclude URL patterns
• @run-at document-start / document-end
• @name for script identification
• try/catch error wrapping

Not available:
• GM_getValue / GM_setValue (no storage bridge)
• GM_xmlhttpRequest (no CORS bypass)
• GM_download (no download manager)
• Cross-origin iframe access
• Browser tab management
• Native Android integration

Scripts use userscript header format:
/* ==UserScript==
@name My Script
@match *://*.example.com/*
@run-at document-end
==/UserScript== */

Errors are silently caught. Use console.log
for debugging via remote DevTools.
""".trimIndent()

    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            Modifier.fillMaxSize().statusBarsPadding().background(SURFACE),
            color = SURFACE
        ) {
            Column(Modifier.fillMaxSize().navigationBarsPadding()) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Script Guide", color = WHITE, fontSize = 18.sp)
                }

                Divider(color = Color.DarkGray, thickness = 0.5.dp)

                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth().padding(16.dp)
                ) {
                    item {
                        Text(
                            guideText,
                            color = WHITE.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    Box(
                        Modifier.fillMaxWidth().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(guideText))
                            },
                            shape = RectangleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                            border = BorderStroke(1.dp, WHITE)
                        ) {
                            Text("Copy Guide", color = WHITE)
                        }
                    }
                }
            }
        }
    }
}

// END OF PART 12/12





// ═══════════════════════════════════════════════════════════════════
// === PART 13/13 — Filters Manager + Import Dialog ===
// ═══════════════════════════════════════════════════════════════════

@Composable
fun FiltersManagerScreen(
    filters: List<Filter>,
    filtersEnabled: Boolean,
    totalBlocked: Int,
    onDismiss: () -> Unit,
    onToggleMaster: (Boolean) -> Unit,
    onToggleFilter: (String) -> Unit,
    onDeleteFilter: (String) -> Unit,
    onImportFilter: (String, String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var filterToDelete by remember { mutableStateOf<Filter?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirm && filterToDelete != null) {
        val f = filterToDelete!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; filterToDelete = null },
            title = { Text("Delete Filter?", color = WHITE, fontSize = 18.sp) },
            text = {
                Column {
                    Text(f.name, color = WHITE, fontSize = 14.sp)
                    Text("${f.networkRuleCount} network rules", color = MUTED, fontSize = 12.sp)
                    Text("${f.cosmeticRuleCount} cosmetic (skipped)", color = MUTED, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("This cannot be undone.", color = MUTED, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton({
                    onDeleteFilter(f.id)
                    showDeleteConfirm = false
                    filterToDelete = null
                }) { Text("Delete", color = WHITE) }
            },
            dismissButton = {
                TextButton({
                    showDeleteConfirm = false
                    filterToDelete = null
                }) { Text("Cancel", color = WHITE) }
            },
            containerColor = SURFACE,
            titleContentColor = WHITE,
            textContentColor = WHITE,
            shape = RectangleShape,
            tonalElevation = 0.dp
        )
    }

    if (showImportDialog) {
        FilterImportDialog(
            onDismiss = { showImportDialog = false },
            onImport = { name, rawText ->
                onImportFilter(name, rawText)
                showImportDialog = false
            }
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
                // ── Header ─────────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton({ onDismiss() }, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Close, "Close", tint = WHITE)
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("Filters", color = WHITE, fontSize = 18.sp)
                }

                Divider(color = Color.DarkGray, thickness = 0.5.dp)

                // ── Master Toggle ──────────────────────────────────
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            if (filtersEnabled) "Enabled" else "Disabled",
                            color = WHITE,
                            fontSize = 14.sp
                        )
                        if (totalBlocked > 0) {
                            Text(
                                "$totalBlocked blocked on this page",
                                color = MUTED,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = filtersEnabled,
                        onCheckedChange = onToggleMaster,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WHITE,
                            checkedTrackColor = WHITE.copy(alpha = 0.3f),
                            uncheckedThumbColor = WHITE.copy(alpha = 0.5f),
                            uncheckedTrackColor = Color(0xFF444444)
                        )
                    )
                }

                Divider(color = Color.DarkGray, thickness = 0.5.dp)

                // ── Filter List ────────────────────────────────────
                if (filters.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No filters", color = MUTED, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Tap Import to add a filter list", color = MUTED.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        items(filters) { filter ->
                            Surface(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                    .border(0.5.dp, Color.DarkGray, RectangleShape),
                                color = Color.Transparent
                            ) {
                                Column(
                                    Modifier.fillMaxWidth().padding(12.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                filter.name,
                                                color = if (filter.enabled) WHITE else MUTED,
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                "${filter.networkRuleCount} network rules",
                                                color = MUTED,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                "${filter.cosmeticRuleCount} cosmetic (skipped)",
                                                color = MUTED.copy(alpha = 0.7f),
                                                fontSize = 11.sp
                                            )
                                        }
                                        Switch(
                                            checked = filter.enabled,
                                            onCheckedChange = { onToggleFilter(filter.id) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = WHITE,
                                                checkedTrackColor = WHITE.copy(alpha = 0.3f),
                                                uncheckedThumbColor = WHITE.copy(alpha = 0.5f),
                                                uncheckedTrackColor = Color(0xFF444444)
                                            )
                                        )
                                        IconButton({
                                            filterToDelete = filter
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

                // ── Footer ─────────────────────────────────────────
                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                        border = BorderStroke(1.dp, WHITE)
                    ) {
                        Icon(Icons.Default.Add, null, tint = WHITE)
                        Spacer(Modifier.width(8.dp))
                        Text("Import Filter", color = WHITE)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterImportDialog(
    onDismiss: () -> Unit,
    onImport: (String, String) -> Unit
) {
    var filterName by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }
    var fileContent by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader()?.readText() ?: ""
                inputStream?.close()
                fileContent = content
                // Extract filename from URI
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) {
                            selectedFileName = it.getString(nameIndex)
                        }
                    }
                }
                if (selectedFileName.isEmpty()) selectedFileName = "filter.txt"
                if (filterName.isEmpty()) {
                    filterName = selectedFileName.removeSuffix(".txt")
                }
            } catch (e: Exception) {
                // File read failed
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Filter", color = WHITE, fontSize = 18.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = filterName,
                    onValueChange = { filterName = it },
                    singleLine = true,
                    placeholder = { Text("Filter name", color = WHITE.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = WHITE, fontSize = 14.sp),
                    shape = RectangleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = WHITE,
                        unfocusedBorderColor = WHITE,
                        cursorColor = WHITE
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { filePickerLauncher.launch("text/plain") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WHITE),
                    border = BorderStroke(1.dp, WHITE)
                ) {
                    Text(
                        if (selectedFileName.isEmpty()) "Select File"
                        else selectedFileName,
                        color = WHITE,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (filterName.isNotBlank() && fileContent.isNotBlank()) {
                        onImport(filterName, fileContent)
                    }
                },
                enabled = filterName.isNotBlank() && fileContent.isNotBlank()
            ) {
                Text("Import", color = if (filterName.isNotBlank() && fileContent.isNotBlank()) WHITE else WHITE.copy(alpha = 0.3f))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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

// END OF PART 13/13
