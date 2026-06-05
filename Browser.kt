//PART 0 START
// Grey Browser - V4.0 — Theme Specification
//
// THEME: DeepSeek-Inspired Dark — No Borders, Layer-Based Separation
//
// LAYER SYSTEM (darkest to lightest):
//   Base:          #121212  (main app background, homepage)
//   Inset:         #1A1A1A  (list items, chips, input fields)
//   Elevated:      #1E1E1E  (top bar, overlays, dialogs, buttons)
//   Highlight:     #2B2B2B  (selected chip, hover states)
//
// COLOURS:
//   Background:    #121212  (deep dark grey - base layer)
//   Surface:       #1E1E1E  (elevated dark grey - panels, overlays)
//   Item BG:       #1A1A1A  (inset layer - list items, fields, chips)
//   Field BG:      #1A1A1A  (same as item - input fields)
//   Button BG:     #1E1E1E  (elevated - buttons on dark backgrounds)
//   Text Primary:  #ECECEC  (near-white, softened)
//   Text Muted:    #8B8B8B  (warm grey)
//   Accent:        #ECECEC  (near-white - icons, progress)
//   Progress Bar:  #ECECEC  (near-white fill behind URL text)
//   Delete:        #FF0000 at 30% alpha  (pending delete tab)
//   Toast BG:      #2B2B2B  (dark toast to match theme)
//   Toast Text:    #ECECEC  (near-white on dark toast)
//   Divider:       #2B2B2B  (subtle section dividers)
//
// BORDERS:
//   Active Tab Indicator:   2dp white bar, left side only
//   Active Chip Indicator:  2dp white bar, bottom side only
//   All Other Borders:      Removed entirely
//   Separation:             Achieved through background color layers
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
//   Buttons:       Button with Elevated BG (#1E1E1E), white text, no border
//   TextFields:    RectangleShape, Item BG (#1A1A1A), no border
//   Dividers:      1dp, #2B2B2B
//   Icons:         White when active, White at 30% when disabled
//   Switches:      White thumb, #444444 track
//   Dialogs:       #1E1E1E background, RectangleShape, no border
//   Dropdowns:     #1E1E1E background, no border, RectangleShape
//   Elevation:     0dp throughout (flat design)
//   List Items:    #1A1A1A background, no border, separated by 2dp gaps
//
// SPACING:
//   Top bar padding:    8dp horizontal, 8dp vertical
//   Tab list padding:   10dp horizontal, 10dp vertical
//   Icon button size:   48dp (in top bar)
//   Small icons:        18dp (close, undo in tab list)
//   Favicon size:       16dp (tab list), 20dp (bookmarks/history), 24dp (sidebar)
//PART 0 END

//PART 1 START
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
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.platform.LocalConfiguration
import android.util.Base64
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
    override fun onPause() { super.onPause() }
    override fun onStop() {
        super.onStop()
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
    override fun onDestroy() { super.onDestroy() }
}
//PART 1 END

//PART 2 START
private const val PREFS_NAME = "browser_tabs"
private const val KEY_TABS = "saved_tabs"
private const val KEY_PINNED = "pinned_domains"
private const val KEY_LAST_ACTIVE_URL = "last_active_url"
private const val KEY_BOOKMARKS = "saved_bookmarks"
private const val KEY_HISTORY = "saved_history"
private const val KEY_SCRIPTS = "saved_scripts"
private const val KEY_FILTERS = "saved_filters"
private const val KEY_FILTERS_ENABLED = "filters_enabled"

const val MAX_WARM_WEBVIEWS = 20
const val UNDO_DELAY_MS = 2000L
const val MAX_HISTORY_ITEMS = 500

const val BACKUP_DIR = "Grey"
const val BACKUP_FILE = "Grey-backup.json"
const val FILTERS_DIR = "filters"

private val BG            = Color(0xFF121212)
private val SURFACE       = Color(0xFF1E1E1E)
private val ITEM_BG       = Color(0xFF1A1A1A)
private val FIELD_BG      = Color(0xFF1A1A1A)
private val ELEVATED_BG   = Color(0xFF1E1E1E)
private val WHITE         = Color(0xFFECECEC)
private val MUTED         = Color(0xFF8B8B8B)
private val ACCENT_DIM    = Color.White.copy(alpha = 0.3f)
private val BORDER_SUBTLE = Color.Transparent
private val DELETE_BG     = Color.Red.copy(alpha = 0.3f)
private val TOAST_BG      = Color(0xFF2B2B2B)
private val TOAST_TEXT    = Color(0xFFECECEC)
private val DIVIDER_COLOR = Color(0xFF2B2B2B)

object FaviconMemoryCache {
    private const val MAX_MEMORY_FAVICONS = 100
    private val cache = object : LinkedHashMap<String, Bitmap>(
        MAX_MEMORY_FAVICONS, 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>): Boolean {
            return size > MAX_MEMORY_FAVICONS
        }
    }

    fun get(domain: String): Bitmap? = cache[domain]
    fun put(domain: String, bitmap: Bitmap) { cache[domain] = bitmap }
    fun clear() = cache.clear()
}

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
//PART 2 END

//PART 3 START
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

data class SavedTab(
    val url: String,
    val title: String,
    val thumbnailBytes: ByteArray? = null
)

data class CustomHideRule(
    val id: String = UUID.randomUUID().toString(),
    val domain: String,
    val selector: String,
    val enabled: Boolean = true,
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
    var parentTabIndex by mutableIntStateOf(-1)
    var thumbnailBytes by mutableStateOf<ByteArray?>(null)
}

fun saveTabsData(context: Context) {}

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
//PART 3 END

//PART 4 START
const val THUMBNAIL_CAPTURE_PROGRESS = 65

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

fun matchesAdBlockRule(url: String, host: String, rule: String): Boolean {
    val trimmed = rule.trim()
    if (trimmed.isEmpty()) return false
    if (trimmed.startsWith("||") && trimmed.endsWith("^")) {
        val domain = trimmed.removePrefix("||").removeSuffix("^")
        val cleanDomain = domain.substringBefore('$')
        if (host == cleanDomain || host.endsWith(".$cleanDomain")) return true
        return false
    }
    if (trimmed.startsWith("||")) {
        val domain = trimmed.removePrefix("||")
        val cleanDomain = domain.substringBefore('$')
        if (host == cleanDomain || host.endsWith(".$cleanDomain")) return true
        return false
    }
    if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
        val exact = trimmed.removePrefix("|").removeSuffix("|")
        return url == exact
    }
    if (trimmed.startsWith("/") && trimmed.endsWith("/")) {
        return url.contains(trimmed.removePrefix("/").removeSuffix("/"))
    }
    if (url.contains(trimmed)) return true
    return false
}

fun getFiltersDir(): File {
    val dir = File(getBackupDir(), FILTERS_DIR)
    if (!dir.exists()) dir.mkdirs()
    return dir
}

fun saveFilterToFile(name: String, rawText: String) {
    try {
        val file = File(getFiltersDir(), "$name.txt")
        file.writeText(rawText)
    } catch (e: Exception) { }
}

fun loadFiltersFromDirectory(): List<Filter> {
    return try {
        val dir = getFiltersDir()
        if (!dir.exists()) return emptyList()
        val filters = mutableListOf<Filter>()
        dir.listFiles()?.filter { it.extension == "txt" }?.forEach { file ->
            val rawText = file.readText()
            val (network, cosmetic) = parseFilterRules(rawText)
            filters.add(Filter(
                name = file.nameWithoutExtension,
                rawText = rawText,
                networkRules = network,
                cosmeticRules = cosmetic,
                networkRuleCount = network.size,
                cosmeticRuleCount = cosmetic.size
            ))
        }
        filters
    } catch (e: Exception) { emptyList() }
}

fun captureThumbnail(webView: WebView): ByteArray? {
    return try {
        val viewportWidth  = webView.width
        val viewportHeight = webView.height
        if (viewportWidth <= 0 || viewportHeight <= 0) return null
        val fullBitmap = Bitmap.createBitmap(viewportWidth, viewportHeight, Bitmap.Config.ARGB_8888)
        val fullCanvas = android.graphics.Canvas(fullBitmap)
        webView.draw(fullCanvas)
        val keepHeight = (viewportHeight * 0.90f).toInt()
        val croppedBitmap = Bitmap.createBitmap(fullBitmap, 0, 0, viewportWidth, keepHeight)
        fullBitmap.recycle()
        val outputSize = 480
        val scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, outputSize, outputSize, true)
        croppedBitmap.recycle()
        val baos = java.io.ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        scaledBitmap.recycle()
        baos.toByteArray()
    } catch (e: Exception) { null }
}

fun getBackupDir(): File {
    val dir = File(android.os.Environment.getExternalStorageDirectory(), BACKUP_DIR)
    if (!dir.exists()) dir.mkdirs()
    return dir
}

fun getBackupFile(): File = File(getBackupDir(), BACKUP_FILE)

fun collectAllDomains(tabs: List<TabState>, history: List<HistoryItem>, bookmarks: List<Bookmark>): Set<String> {
    val domains = mutableSetOf<String>()
    for (tab in tabs) {
        if (!tab.isBlankTab) {
            val host = try { Uri.parse(tab.url).host } catch (e: Exception) { null }
            if (host != null) domains.add(host)
        }
    }
    for (item in history) {
        val host = try { Uri.parse(item.url).host } catch (e: Exception) { null }
        if (host != null) domains.add(host)
    }
    for (bookmark in bookmarks) {
        val host = try { Uri.parse(bookmark.url).host } catch (e: Exception) { null }
        if (host != null) domains.add(host)
    }
    return domains
}

fun exportCookies(tabs: List<TabState>, history: List<HistoryItem>, bookmarks: List<Bookmark>): JSONArray {
    val cookieJson = JSONArray()
    try {
        val cookieManager = android.webkit.CookieManager.getInstance()
        val domains = collectAllDomains(tabs, history, bookmarks)
        for (domain in domains) {
            val cookies = cookieManager.getCookie("https://$domain")
            if (cookies != null && cookies.isNotEmpty()) {
                val obj = JSONObject()
                obj.put("domain", domain)
                obj.put("cookies", cookies)
                cookieJson.put(obj)
            }
        }
    } catch (e: Exception) { }
    return cookieJson
}

fun importCookies(cookieJson: JSONArray) {
    try {
        val cookieManager = android.webkit.CookieManager.getInstance()
        for (i in 0 until cookieJson.length()) {
            val obj = cookieJson.getJSONObject(i)
            val domain = obj.getString("domain")
            val cookieStr = obj.getString("cookies")
            cookieStr.split(";").forEach { cookie ->
                val trimmed = cookie.trim()
                if (trimmed.isNotEmpty()) {
                    cookieManager.setCookie("https://$domain", trimmed)
                }
            }
        }
        cookieManager.flush()
    } catch (e: Exception) { }
}

fun exportBackup(
    context: Context,
    tabs: List<TabState>,
    history: List<HistoryItem>,
    bookmarks: List<Bookmark>,
    customFilters: List<CustomHideRule>,
    scripts: List<Script>,
    lastActiveUrl: String
) {
    try {
        val root = JSONObject()
        root.put("lastActiveUrl", lastActiveUrl)
        val tabsArray = JSONArray()
        for (tab in tabs) {
            if (!tab.isBlankTab) {
                val obj = JSONObject()
                obj.put("url", tab.url)
                obj.put("title", tab.title)
                obj.put("parentTabIndex", tab.parentTabIndex)
                tab.thumbnailBytes?.let { bytes ->
                    obj.put("thumbnail", android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP))
                }
                tabsArray.put(obj)
            }
        }
        root.put("tabs", tabsArray)
        val historyArray = JSONArray()
        for (h in history) {
            val obj = JSONObject()
            obj.put("url", h.url)
            obj.put("title", h.title)
            obj.put("timestamp", h.timestamp)
            historyArray.put(obj)
        }
        root.put("history", historyArray)
        val bookmarksArray = JSONArray()
        for (b in bookmarks) {
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("url", b.url)
            obj.put("title", b.title)
            obj.put("timestamp", b.timestamp)
            bookmarksArray.put(obj)
        }
        root.put("bookmarks", bookmarksArray)
        val customFiltersArray = JSONArray()
        for (cf in customFilters) {
            val obj = JSONObject()
            obj.put("id", cf.id)
            obj.put("domain", cf.domain)
            obj.put("selector", cf.selector)
            obj.put("enabled", cf.enabled)
            obj.put("timestamp", cf.timestamp)
            customFiltersArray.put(obj)
        }
        root.put("customFilters", customFiltersArray)
        val scriptsArray = JSONArray()
        for (s in scripts) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("title", s.title)
            obj.put("code", s.code)
            obj.put("enabled", s.enabled)
            obj.put("timestamp", s.timestamp)
            scriptsArray.put(obj)
        }
        root.put("scripts", scriptsArray)
        val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
        val patternHash = prefs.getString("pattern_hash", null)
        val lockEnabled = prefs.getBoolean("lock_enabled", false)
        if (patternHash != null || lockEnabled) {
            val patternObj = JSONObject()
            patternObj.put("hash", patternHash ?: "")
            patternObj.put("enabled", lockEnabled)
            root.put("patternLock", patternObj)
        }
        root.put("cookies", exportCookies(tabs, history, bookmarks))
        getBackupFile().writeText(root.toString(1))
    } catch (e: Exception) { }
}

data class BackupData(
    val tabs: List<SavedTab>,
    val history: List<HistoryItem>,
    val bookmarks: List<Bookmark>,
    val customFilters: List<CustomHideRule>,
    val scripts: List<Script>,
    val lastActiveUrl: String,
    val patternHash: String?,
    val lockEnabled: Boolean
)

fun importBackup(context: Context): BackupData? {
    return try {
        val file = getBackupFile()
        if (!file.exists()) return null
        val root = JSONObject(file.readText())
        val lastActiveUrl = root.optString("lastActiveUrl", "")
        val tabsList = mutableListOf<SavedTab>()
        val tabsArray = root.optJSONArray("tabs")
        if (tabsArray != null) {
            for (i in 0 until tabsArray.length()) {
                val obj = tabsArray.getJSONObject(i)
                val url = obj.getString("url")
                val title = obj.optString("title", url)
                val thumbnailBytes: ByteArray? = if (obj.has("thumbnail") && obj.getString("thumbnail").isNotEmpty()) {
                    try { android.util.Base64.decode(obj.getString("thumbnail"), android.util.Base64.NO_WRAP) }
                    catch (e: Exception) { null }
                } else null
                tabsList.add(SavedTab(url = url, title = title, thumbnailBytes = thumbnailBytes))
            }
        }
        val historyList = mutableListOf<HistoryItem>()
        val historyArray = root.optJSONArray("history")
        if (historyArray != null) {
            for (i in 0 until historyArray.length()) {
                val obj = historyArray.getJSONObject(i)
                historyList.add(HistoryItem(obj.getString("url"), obj.getString("title"), obj.getLong("timestamp")))
            }
        }
        val bookmarksList = mutableListOf<Bookmark>()
        val bookmarksArray = root.optJSONArray("bookmarks")
        if (bookmarksArray != null) {
            for (i in 0 until bookmarksArray.length()) {
                val obj = bookmarksArray.getJSONObject(i)
                bookmarksList.add(Bookmark(obj.getString("id"), obj.getString("url"), obj.getString("title"), obj.getLong("timestamp")))
            }
        }
        val customFiltersList = mutableListOf<CustomHideRule>()
        val customFiltersArray = root.optJSONArray("customFilters")
        if (customFiltersArray != null) {
            for (i in 0 until customFiltersArray.length()) {
                val obj = customFiltersArray.getJSONObject(i)
                customFiltersList.add(CustomHideRule(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    domain = obj.getString("domain"),
                    selector = obj.getString("selector"),
                    enabled = obj.optBoolean("enabled", true),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                ))
            }
        }
        val scriptsList = mutableListOf<Script>()
        val scriptsArray = root.optJSONArray("scripts")
        if (scriptsArray != null) {
            for (i in 0 until scriptsArray.length()) {
                val obj = scriptsArray.getJSONObject(i)
                scriptsList.add(Script(
                    obj.getString("id"),
                    obj.getString("title"),
                    obj.getString("code"),
                    obj.optBoolean("enabled", true),
                    obj.getLong("timestamp")
                ))
            }
        }
        val patternObj = root.optJSONObject("patternLock")
        val patternHash = patternObj?.optString("hash", null)?.ifEmpty { null }
        val lockEnabled = patternObj?.optBoolean("enabled", false) ?: false
        val cookieJson = root.optJSONArray("cookies")
        if (cookieJson != null) {
            importCookies(cookieJson)
        }
        BackupData(tabsList, historyList, bookmarksList, customFiltersList, scriptsList, lastActiveUrl, patternHash, lockEnabled)
    } catch (e: Exception) { null }
}
//PART 4 END

//PART 5 START
@Composable
fun GreyBrowser() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activity = context as? ComponentActivity
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var backupLoaded by remember { mutableStateOf(false) }
    var showLoadingScreen by remember { mutableStateOf(true) }
    var loadingLetterIndex by remember { mutableIntStateOf(0) }

    val bookmarks = remember { mutableStateListOf<Bookmark>().apply { addAll(loadBookmarks(context)) } }
    var showBookmarks by remember { mutableStateOf(false) }

    val history = remember { mutableStateListOf<HistoryItem>().apply { addAll(loadHistory(context)) } }
    var showHistory by remember { mutableStateOf(false) }

    val scripts = remember { mutableStateListOf<Script>().apply { addAll(loadScripts(context)) } }
    var showScripts by remember { mutableStateOf(false) }
    var showScriptEditor by remember { mutableStateOf(false) }
    var editingScript by remember { mutableStateOf<Script?>(null) }

    val filters = remember { mutableStateListOf<Filter>().apply { addAll(loadFilters(context)) } }
    var showFilters by remember { mutableStateOf(false) }
    var filtersEnabled by remember {
        mutableStateOf(
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_FILTERS_ENABLED, true)
        )
    }
    var totalBlocked by remember { mutableIntStateOf(0) }

    val customHideRules = remember { mutableStateListOf<CustomHideRule>() }
    var showElementHider by remember { mutableStateOf(false) }
    var showElementRules by remember { mutableStateOf(false) }

    val thumbnailBitmapCache = remember { mutableStateMapOf<Int, Bitmap?>() }

    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    fun showToast(msg: String) {
        toastMessage = msg
        showToast = true
    }

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

    val webViewContainer = remember {
        android.widget.FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.parseColor("#121212"))
        }
    }

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

    var showLinkMenu by remember { mutableStateOf(false) }
    var linkMenuUrl by remember { mutableStateOf<String?>(null) }

    var isUrlFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(showLoadingScreen) {
        while (showLoadingScreen) {
            delay(200)
            loadingLetterIndex = (loadingLetterIndex + 1) % 4
        }
    }

    LaunchedEffect(backupLoaded) {
        if (backupLoaded) {
            delay(300)
            showLoadingScreen = false
        }
    }

    LaunchedEffect(Unit) {
        var permissionRequested = false
        while (!backupLoaded) {
            val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                android.os.Environment.isExternalStorageManager()
            } else { true }
            if (!hasPermission) {
                if (!permissionRequested) {
                    val intent = android.content.Intent(
                        android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                    permissionRequested = true
                }
                delay(500)
                continue
            }
            val backup = importBackup(context)
            if (backup != null) {
                tabs.clear()
                for (savedTab in backup.tabs) {
                    tabs.add(TabState().apply {
                        this.url = savedTab.url
                        this.title = savedTab.title
                        this.thumbnailBytes = savedTab.thumbnailBytes
                        isBlankTab = false
                        isDiscarded = true
                        webView = null
                    })
                }
                history.clear()
                history.addAll(backup.history)
                bookmarks.clear()
                bookmarks.addAll(backup.bookmarks)
                scripts.clear()
                scripts.addAll(backup.scripts)
                customHideRules.clear()
                customHideRules.addAll(backup.customFilters)
                val dirFilters = loadFiltersFromDirectory()
                if (dirFilters.isNotEmpty()) {
                    filters.clear()
                    filters.addAll(dirFilters)
                }
                currentTabIndex = -1
                val backupLastUrl = backup.lastActiveUrl
                highlightedTabIndex = tabs.indexOfFirst {
                    it.url.substringBefore("#") == backupLastUrl.substringBefore("#")
                }.let { if (it >= 0) it else -1 }
                if (backupLastUrl.isNotEmpty()) lastActiveUrl = backupLastUrl
                val patternPrefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
                if (backup.patternHash != null) {
                    patternPrefs.edit().putString("pattern_hash", backup.patternHash).apply()
                    patternPrefs.edit().putBoolean("lock_enabled", backup.lockEnabled).apply()
                }
                saveBookmarks(context, bookmarks)
                saveHistory(context, history)
                saveScripts(context, scripts)
                saveFilters(context, filters)
                saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
            } else {
                val dirFilters = loadFiltersFromDirectory()
                if (dirFilters.isNotEmpty()) {
                    filters.clear()
                    filters.addAll(dirFilters)
                }
                withContext(Dispatchers.IO) {
                    exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
                }
            }
            backupLoaded = true
        }
    }

    LaunchedEffect(tabs.toList(), pinnedDomains.toList(), lastActiveUrl) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(tabs.map { "${it.url}|${it.title}" }.joinToString()) {
        saveTabsDataNow(context, tabs, pinnedDomains, lastActiveUrl)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(bookmarks.toList()) {
        saveBookmarks(context, bookmarks)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(history.toList()) {
        saveHistory(context, history)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(scripts.toList()) {
        saveScripts(context, scripts)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(filters.toList()) {
        saveFilters(context, filters)
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }
    LaunchedEffect(customHideRules.toList()) {
        if (backupLoaded) {
            withContext(Dispatchers.IO) {
                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
            }
        }
    }

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
//PART 5 END

//PART 6 START
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
                setSupportMultipleWindows(true)
                javaScriptCanOpenWindowsAutomatically = false
            }
            loadUrl(url)
        }
    }

    fun setupDelegates(tabState: TabState) {
        val wv = tabState.webView ?: return
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                tabState.progress = newProgress
                tabState.lastUpdated = System.currentTimeMillis()
                if (newProgress >= THUMBNAIL_CAPTURE_PROGRESS) {
                    val bytes = captureThumbnail(view)
                    if (bytes != null && bytes.isNotEmpty()) {
                        tabState.thumbnailBytes = bytes
                        val idx = tabs.indexOf(tabState)
                        if (idx >= 0) thumbnailBitmapCache.remove(idx)
                    }
                }
            }
            override fun onReceivedTitle(view: WebView, title: String?) {
                if (!tabState.isBlankTab && title != null && title.isNotBlank()) {
                    tabState.title = title
                }
            }
            override fun onPermissionRequest(request: android.webkit.PermissionRequest) {
                request.deny()
            }
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                if (!isUserGesture) return false

                val tempWebView = WebView(view.context)
                tempWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        wv.loadUrl(url)
                        tempWebView.destroy()
                        return true
                    }
                }
                val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                transport.webView = tempWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean {
                if (!filtersEnabled) return false
                if (!request.isForMainFrame) return false
                if (request.hasGesture()) return false

                val url = request.url.toString()
                val host = request.url.host ?: return false

                val currentHost = Uri.parse(view.url ?: "").host ?: ""
                if (host == currentHost) return false

                for (filter in filters) {
                    if (!filter.enabled) continue
                    for (rule in filter.networkRules) {
                        if (rule.startsWith("@@")) {
                            val exceptionPattern = rule.removePrefix("@@")
                            if (matchesAdBlockRule(url, host, exceptionPattern)) return false
                        }
                    }
                    for (rule in filter.networkRules) {
                        if (rule.startsWith("@@")) continue
                        if (matchesAdBlockRule(url, host, rule)) {
                            totalBlocked++
                            return true
                        }
                    }
                }
                return false
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                tabState.url = url
                tabState.progress = 5
                tabState.lastUpdated = System.currentTimeMillis()
                if (url != "about:blank") tabState.isBlankTab = false
                if (showElementHider) showElementHider = false
                wv.evaluateJavascript("""
                    (function() {
                        var originalMatchMedia = window.matchMedia;
                        window.matchMedia = function(query) {
                            var result = originalMatchMedia(query);
                            if (query.includes('prefers-color-scheme')) {
                                return {
                                    matches: true,
                                    media: query,
                                    onchange: null,
                                    addListener: function(cb) { cb(this); },
                                    removeListener: function() {},
                                    addEventListener: function(type, cb) { if (type === 'change') cb(this); },
                                    removeEventListener: function() {},
                                    dispatchEvent: function() { return true; }
                                };
                            }
                            return result;
                        };
                    })();
                """.trimIndent(), null)
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
                if (url != "about:blank") {
                    val pageHost = Uri.parse(url).host?.removePrefix("www.") ?: ""
                    val selectors = mutableListOf<String>()
                    for (rule in customHideRules) {
                        if (!rule.enabled) continue
                        if (rule.domain == "*" || pageHost == rule.domain || pageHost.endsWith(".${rule.domain}")) {
                            selectors.add(rule.selector)
                        }
                    }
                    if (selectors.isNotEmpty()) {
                        val selectorsJson = JSONArray()
                        selectors.forEach { selectorsJson.put(it) }
                        val selectorsJs = selectorsJson.toString()
                        wv.evaluateJavascript("""
                            (function() {
                                var selectors = $selectorsJs;
                                function hideElements() {
                                    selectors.forEach(function(sel) {
                                        try {
                                            document.querySelectorAll(sel).forEach(function(el) {
                                                el.style.setProperty('display', 'none', 'important');
                                            });
                                        } catch(e) {}
                                    });
                                }
                                hideElements();
                                var timer = null;
                                new MutationObserver(function() {
                                    if (timer) clearTimeout(timer);
                                    timer = setTimeout(hideElements, 500);
                                }).observe(document.documentElement, { childList: true, subtree: true });
                            })();
                        """.trimIndent(), null)
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
                    val cleanUrl = url.substringBefore("#")
                    history.removeAll { it.url.substringBefore("#") == cleanUrl }
                    history.add(HistoryItem(url = url, title = tabState.title.ifBlank { url }))
                    if (history.size > MAX_HISTORY_ITEMS) history.removeAt(0)
                }
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
            override fun shouldInterceptRequest(
                view: WebView,
                request: android.webkit.WebResourceRequest
            ): android.webkit.WebResourceResponse? {
                if (!filtersEnabled) return null
                val requestUrl = request.url.toString()
                val requestHost = request.url.host ?: return null
                for (filter in filters) {
                    if (!filter.enabled) continue
                    for (rule in filter.networkRules) {
                        if (rule.startsWith("@@")) {
                            val exceptionPattern = rule.removePrefix("@@")
                            if (matchesAdBlockRule(requestUrl, requestHost, exceptionPattern)) return null
                        }
                    }
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
                return null
            }
        }

        var lastTouchX = 0f
        var lastTouchY = 0f
        wv.setOnTouchListener { _, event ->
            val scale = if (wv.scale > 0) wv.scale else 1f
            lastTouchX = event.x / scale
            lastTouchY = event.y / scale
            false
        }

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

    fun removeDuplicateTab(url: String) {
        val cleanUrl = url.substringBefore("#")
        val oldIndex = tabs.indexOfFirst {
            it.url.substringBefore("#") == cleanUrl && !it.isBlankTab
        }
        if (oldIndex >= 0) {
            tabs[oldIndex].webView?.destroy()
            tabs.removeAt(oldIndex)
            for (t in tabs) {
                if (t.parentTabIndex == oldIndex) t.parentTabIndex = -1
                else if (t.parentTabIndex > oldIndex) t.parentTabIndex--
            }
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

    fun createForegroundTab(url: String, insertAfterIndex: Int = -1) {
        removeDuplicateTab(url)
        val insertIdx = if (insertAfterIndex >= 0) insertAfterIndex + 1 else 0
        val parentIdx = if (insertAfterIndex >= 0) insertAfterIndex else -1
        val wv = createWebView(url)
        val newTab = TabState().apply {
            webView = wv
            this.url = url
            isBlankTab = false
            isDiscarded = false
            lastUpdated = System.currentTimeMillis()
            parentTabIndex = parentIdx
        }
        tabs.add(insertIdx, newTab)
        setupDelegates(newTab)
        currentTabIndex = insertIdx
        highlightedTabIndex = currentTabIndex
        manageTabLifecycle(currentTabIndex)
    }

    fun requestDeleteTab(index: Int) {
        if (index >= 0 && index < tabs.size) {
            pendingDeletions[index] = System.currentTimeMillis()
        }
    }

    fun undoDeleteTab(index: Int) {
        pendingDeletions.remove(index)
    }

    fun loadFavicon(domain: String) {
        if (domain.isBlank()) return
        val cached = FaviconMemoryCache.get(domain)
        if (cached != null) { faviconBitmaps[domain] = cached; return }
        if (!faviconBitmaps.containsKey(domain) && faviconLoading[domain] != true) {
            faviconLoading[domain] = true
            scope.launch {
                val bitmap = FaviconCache.getFaviconBitmap(context, domain)
                    ?: FaviconCache.downloadAndCacheFavicon(context, domain)
                if (bitmap != null) FaviconMemoryCache.put(domain, bitmap)
                faviconBitmaps[domain] = bitmap
                faviconLoading[domain] = false
            }
        }
    }

    fun loadTabFavicon(domain: String) {
        if (domain.isBlank()) return
        val cached = FaviconMemoryCache.get(domain)
        if (cached != null) { tabFavicons[domain] = cached; return }
        if (!tabFavicons.containsKey(domain) && tabFaviconLoading[domain] != true) {
            tabFaviconLoading[domain] = true
            scope.launch {
                val bitmap = FaviconCache.getFaviconBitmap(context, domain)
                    ?: FaviconCache.downloadAndCacheFavicon(context, domain)
                if (bitmap != null) FaviconMemoryCache.put(domain, bitmap)
                tabFavicons[domain] = bitmap
                tabFaviconLoading[domain] = false
            }
        }
    }

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
//PART 6 END

//PART 7 START
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
        showTabManager -> showTabManager = false
        showBookmarks -> showBookmarks = false
        showHistory -> showHistory = false
        showMenu -> showMenu = false
        showConfirmDialog -> { showConfirmDialog = false; confirmAction = null }
        showLinkMenu -> { showLinkMenu = false; linkMenuUrl = null }
        currentTabIndex == -1 -> { }
        currentTabIndex >= 0 -> {
            val tab = tabs.getOrNull(currentTabIndex)
            if (tab?.webView?.canGoBack() == true) {
                tab.webView?.goBack()
            } else {
                val parentIdx = tab?.parentTabIndex ?: -1
                val closingIdx = currentTabIndex
                if (highlightedTabIndex == closingIdx) highlightedTabIndex = -1
                else if (highlightedTabIndex > closingIdx) highlightedTabIndex--
                pendingDeletions.remove(closingIdx)
                val updated = mutableMapOf<Int, Long>()
                for ((idx, time) in pendingDeletions) {
                    updated[if (idx > closingIdx) idx - 1 else idx] = time
                }
                pendingDeletions.clear()
                pendingDeletions.putAll(updated)
                closeTabAndFixParents(closingIdx)
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
        AndroidView(
            factory = { webViewContainer },
            update = { container ->
                val target = if (currentTabIndex == -1) {
                    baseWebView
                } else {
                    tabs.getOrNull(currentTabIndex)?.webView ?: baseWebView
                }
                if (container.childCount == 0 || container.getChildAt(0) != target) {
                    val old = if (container.childCount > 0) container.getChildAt(0) as? WebView else null
                    old?.onPause()
                    container.removeAllViews()
                    container.addView(target)
                    target.onResume()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
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
//PART 7 END

//PART 8.1 START
    var urlInput by remember {
        mutableStateOf(
            TextFieldValue(
                if (currentTabIndex == -1) ""
                else currentTab?.url?.let { if (it == "about:blank") "" else it } ?: ""
            )
        )
    }

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

    var showAppLockSettings by remember { mutableStateOf(false) }
    var patternDrawMode by remember { mutableStateOf("") }

    LaunchedEffect(backupLoaded) {
        if (backupLoaded) {
            val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
            val lockEnabled = prefs.getBoolean("lock_enabled", false)
            val hasPattern = prefs.getString("pattern_hash", null) != null
            if (lockEnabled && hasPattern) {
                patternDrawMode = "unlock"
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
//PART 8.1 END

//PART 8.2 START
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
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
                                }
                            }
                        }
                    } else {
                        showAppLockSettings = false
                        patternDrawMode = "toggle_off"
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
                            }
                        }
                    }
                },
                onChangePattern = {
                    showAppLockSettings = false
                    patternDrawMode = "change_verify"
                }
            )
        }

        if (patternDrawMode in listOf("set", "change_verify", "change_set", "toggle_off")) {
            val prefs = context.getSharedPreferences("pattern_lock", Context.MODE_PRIVATE)
            val savedHash = prefs.getString("pattern_hash", null)

            PatternDrawScreen(
                mode = patternDrawMode,
                savedHash = savedHash,
                onDismiss = { patternDrawMode = "" },
                onPatternVerified = {
                    when (patternDrawMode) {
                        "change_verify" -> patternDrawMode = "change_set"
                        "toggle_off" -> {
                            prefs.edit().putBoolean("lock_enabled", false).apply()
                            patternDrawMode = ""
                            showToast("App lock disabled")
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
                                }
                            }
                        }
                    }
                },
                onPatternSet = { hash ->
                    prefs.edit().putString("pattern_hash", hash).apply()
                    prefs.edit().putBoolean("lock_enabled", true).apply()
                    patternDrawMode = ""
                    showToast("Pattern saved")
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            exportBackup(context, tabs.toList(), history.toList(), bookmarks.toList(), customHideRules.toList(), scripts.toList(), lastActiveUrl)
                        }
                    }
                },
                onPatternRemoved = {}
            )
        }
//PART 8.2 END

//PART 8.3 START
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
//PART 8.3 END

//PART 8.4 START
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
                    saveFilterToFile(name, rawText)
                    showToast("Filter imported: ${network.size} rules")
                }
            )
        }
//PART 8.4 END

//PART 8.5 START
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

        if (showHistory) {
            HistoryUI(
                history = history,
                onDismiss = { showHistory = false },
                onOpenUrl = { url -> createForegroundTab(url) },
                faviconBitmaps = faviconBitmaps,
                loadFavicon = { loadFavicon(it) }
            )
        }

        if (showLinkMenu && linkMenuUrl != null) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showLinkMenu = false; linkMenuUrl = null },
                properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true)
            ) {
                Surface(
                    modifier = Modifier
                        .width(240.dp),
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
//PART 8.5 END

//PART 8.6 START
        if (showTabManager) {
            val realTabs = tabs.toList()
            val domainGroups = realTabs.groupBy { getDomainName(it.url) }.filter { it.key.isNotBlank() }
            val sortedDomains = domainGroups.keys.sortedWith(
                compareByDescending<String> { pinnedDomains.contains(it) }
                    .thenBy { d: String -> domainGroups[d]?.firstOrNull()?.let { t -> tabs.indexOf(t) } ?: Int.MAX_VALUE }
            )
            val allSidebarItems = sortedDomains
            val highlightDomain = if (highlightedTabIndex >= 0 && highlightedTabIndex < tabs.size) {
                getDomainName(tabs[highlightedTabIndex].url)
            } else ""

            val groupedTabs = buildList {
                for (domain in sortedDomains) { addAll(domainGroups[domain] ?: emptyList()) }
            }

            LaunchedEffect(Unit) {
                sortedDomains.forEach { domain -> loadFavicon(domain) }
            }

            LaunchedEffect(showTabManager) {
                realTabs.forEachIndexed { index, tab ->
                    tab.thumbnailBytes?.let { bytes ->
                        withContext(Dispatchers.IO) {
                            try {
                                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                withContext(Dispatchers.Main) {
                                    thumbnailBitmapCache[index] = bmp
                                }
                            } catch (e: Exception) {
                                thumbnailBitmapCache[index] = null
                            }
                        }
                    }
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
                        Row(
                            Modifier.fillMaxWidth().padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton({ showTabManager = false }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Default.Close, "Close", tint = WHITE)
                            }
                            Spacer(Modifier.width(4.dp))
                            Text("Tabs", color = WHITE, fontSize = 18.sp)
                            if (realTabs.isNotEmpty()) {
                                Spacer(Modifier.width(8.dp))
                                Text("(${realTabs.size})", color = MUTED, fontSize = 14.sp)
                            }
                        }

                        val tabListState = rememberLazyListState()
                        val groupedForDisplay = groupedTabs.groupBy { getDomainName(it.url) }
                        val displayOrder = sortedDomains.filter { it in groupedForDisplay.keys }
                        val domainCount = displayOrder.size

                        val chipScrollState = rememberScrollState()
                        val coroutineScope = rememberCoroutineScope()
                        val density = LocalDensity.current
                        val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp

                        var selectedChipDomain by remember { mutableStateOf(highlightDomain) }

                        LaunchedEffect(selectedChipDomain) {
                            val domainIdx = displayOrder.indexOf(selectedChipDomain)
                            if (domainIdx >= 0 && domainCount > 1) {
                                val progress = domainIdx.toFloat() / (domainCount - 1).toFloat()
                                chipScrollState.animateScrollTo((progress * chipScrollState.maxValue).toInt())
                            }
                        }

                        LaunchedEffect(Unit) {
                            if (highlightedTabIndex < 0 || highlightedTabIndex >= tabs.size) return@LaunchedEffect
                            delay(250)
                            val targetUrl    = tabs[highlightedTabIndex].url
                            val targetDomain = getDomainName(targetUrl)
                            val domainIdx    = displayOrder.indexOf(targetDomain)
                            if (domainIdx < 0) return@LaunchedEffect

                            val tabsInGroup   = groupedForDisplay[targetDomain] ?: emptyList()
                            val tabIdxInGroup = tabsInGroup.indexOfFirst { it.url == targetUrl }.coerceAtLeast(0)

                            tabListState.scrollToItem(domainIdx)
                            delay(100)

                            val viewportPx      = tabListState.layoutInfo.viewportSize.height.toFloat()
                            val tabHeightPx     = with(density) { 92.dp.toPx() }
                            val contentItemInfo = tabListState.layoutInfo.visibleItemsInfo
                                .firstOrNull { it.index == domainIdx }

                            if (contentItemInfo == null) {
                                val fallbackOffset = (tabIdxInGroup * tabHeightPx - viewportPx / 2f + tabHeightPx / 2f)
                                    .toInt().coerceAtLeast(0)
                                tabListState.animateScrollToItem(domainIdx, fallbackOffset)
                            } else {
                                val tabCenterInViewport =
                                    contentItemInfo.offset.toFloat() +
                                    tabIdxInGroup * tabHeightPx +
                                    tabHeightPx / 2f
                                tabListState.animateScrollBy(tabCenterInViewport - viewportPx / 2f)
                            }

                            delay(150)
                            if (domainCount > 1) {
                                val progress = domainIdx.toFloat() / (domainCount - 1).toFloat()
                                chipScrollState.animateScrollTo((progress * chipScrollState.maxValue).toInt())
                            }
                        }

                        if (realTabs.isEmpty()) {
                            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No open tabs", color = MUTED, fontSize = 16.sp)
                            }
                        } else {
                            LazyColumn(
                                state = tabListState,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .pointerInput(displayOrder.toList()) {
                                        fun domainAtY(y: Float): String {
                                            val item = tabListState.layoutInfo.visibleItemsInfo
                                                .lastOrNull { it.offset <= y.toInt() } ?: return ""
                                            val idx = item.index
                                                .coerceIn(0, (domainCount - 1).coerceAtLeast(0))
                                            return displayOrder.getOrElse(idx) { "" }
                                        }
                                        awaitEachGesture {
                                            val down = awaitFirstDown(requireUnconsumed = false)
                                            domainAtY(down.position.y)
                                                .takeIf { it.isNotBlank() }
                                                ?.let { selectedChipDomain = it }
                                            do {
                                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                                if (!change.pressed) break
                                                domainAtY(change.position.y)
                                                    .takeIf { it.isNotBlank() }
                                                    ?.let { selectedChipDomain = it }
                                            } while (true)
                                        }
                                    },
                                contentPadding = PaddingValues(bottom = screenHeightDp)
                            ) {
                                for (domain in displayOrder) {
                                    val groupTabs = groupedForDisplay[domain] ?: continue

                                    item(key = domain) {
                                        Column(Modifier.padding(bottom = 48.dp)) {
                                            groupTabs.forEach { tab ->
                                                val tabIndex      = tabs.indexOf(tab)
                                                val isHighlighted = tabIndex == highlightedTabIndex
                                                val isPending     = pendingDeletions.containsKey(tabIndex)
                                                val tabDomain     = getDomainName(tab.url)
                                                LaunchedEffect(tab.url) { loadTabFavicon(tabDomain) }
                                                val tabFav = tabFavicons[tabDomain]
                                                val thumbBmp = thumbnailBitmapCache[tabIndex]

                                                Box(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 2.dp)
                                                        .padding(horizontal = 8.dp)
                                                        .drawWithContent {
                                                            drawRect(if (isPending) DELETE_BG else ITEM_BG)
                                                            if (isHighlighted) {
                                                                drawRect(
                                                                    color = Color.White,
                                                                    size = Size(4.dp.toPx(), size.height)
                                                                )
                                                            }
                                                            drawContent()
                                                        }
                                                ) {
                                                    Row(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .padding(10.dp)
                                                            .clickable(enabled = !isPending) {
                                                                currentTabIndex = tabIndex
                                                                showTabManager = false
                                                            },
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (thumbBmp != null) {
                                                            Image(
                                                                thumbBmp.asImageBitmap(),
                                                                "Thumbnail",
                                                                Modifier
                                                                    .size(80.dp, 72.dp)
                                                                    .border(0.8.dp, Color.DarkGray, RectangleShape)
                                                                    .clip(RectangleShape),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                        } else {
                                                            Box(
                                                                Modifier
                                                                    .size(80.dp, 72.dp)
                                                                    .background(Color(0xFF121212), RectangleShape)
                                                            )
                                                        }

                                                        Spacer(Modifier.width(10.dp))

                                                        if (tabFav != null) {
                                                            Image(
                                                                tabFav.asImageBitmap(), tabDomain,
                                                                Modifier.size(32.dp).clip(CircleShape),
                                                                contentScale = ContentScale.Fit
                                                            )
                                                        } else {
                                                            Box(
                                                                Modifier.size(32.dp).clip(CircleShape)
                                                                    .background(Color.DarkGray),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    tabDomain.take(1).uppercase(),
                                                                    color = WHITE,
                                                                    fontSize = 14.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }

                                                        Spacer(Modifier.width(12.dp))

                                                        Column(Modifier.weight(1f)) {
                                                            Text(
                                                                if (tab.title == "New Tab" || tab.title.isBlank()) tab.url else tab.title,
                                                                color = WHITE,
                                                                fontSize = 14.sp,
                                                                maxLines = 2,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                            Spacer(Modifier.height(2.dp))
                                                            Text(
                                                                tabDomain,
                                                                color = MUTED.copy(alpha = 0.7f),
                                                                fontSize = 11.sp,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }

                                                        if (isPending) {
                                                            IconButton({ undoDeleteTab(tabIndex) }) {
                                                                Icon(Icons.Default.Undo, "Undo", tint = WHITE, modifier = Modifier.size(18.dp))
                                                            }
                                                        } else {
                                                            IconButton({ requestDeleteTab(tabIndex) }) {
                                                                Icon(Icons.Default.Close, "Close", tint = WHITE.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
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

                        if (realTabs.isNotEmpty()) {
                            Row(
                                Modifier.fillMaxWidth().horizontalScroll(chipScrollState).padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                allSidebarItems.forEach { domain ->
                                    val isActiveTabDomain = domain == highlightDomain
                                    val isSelectedDomain  = domain == selectedChipDomain
                                    val isPinned  = pinnedDomains.contains(domain)
                                    val tabCount  = domainGroups[domain]?.size ?: 0
                                    val fav       = faviconBitmaps[domain]

                                    Box(
                                        Modifier
                                            .padding(horizontal = 4.dp)
                                            .drawWithContent {
                                                drawRect(if (isSelectedDomain) Color.DarkGray else ITEM_BG)
                                                if (isActiveTabDomain) {
                                                    drawRect(
                                                        color = Color.White,
                                                        topLeft = Offset(0f, size.height - 4.dp.toPx()),
                                                        size = Size(size.width, 4.dp.toPx())
                                                    )
                                                }
                                                drawContent()
                                            }
                                            .clickable {
                                                selectedChipDomain = domain
                                                val domainIdx = displayOrder.indexOf(domain)
                                                if (domainIdx >= 0) {
                                                    coroutineScope.launch {
                                                        tabListState.animateScrollToItem(domainIdx)
                                                    }
                                                }
                                            }
                                    ) {
                                        Box(Modifier.padding(6.dp).width(52.dp), contentAlignment = Alignment.Center) {
                                            if (isPinned) Icon(Icons.Default.PushPin, "Pinned", tint = WHITE, modifier = Modifier.size(10.dp).align(Alignment.TopStart))
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                if (fav != null) Image(fav.asImageBitmap(), domain, Modifier.size(20.dp).clip(CircleShape), contentScale = ContentScale.Fit)
                                                else Box(Modifier.size(20.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                                                    Text(domain.take(1).uppercase(), color = WHITE, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(Modifier.height(2.dp))
                                                Box(Modifier.background(Color.DarkGray).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                                    Text(tabCount.toString(), color = WHITE, fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { currentTabIndex = -1; showTabManager = false },
                                modifier = Modifier.weight(1f), shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, null, tint = WHITE, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("New Tab", fontSize = 11.sp, color = WHITE)
                            }
                        }
                    }
                }
            }
        }
//PART 8.6 END

//PART 8.7 START
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
                    IconButton({
                        focusManager.clearFocus()
                        scope.launch {
                            delay(50)
                            showTabManager = true
                        }
                    }) {
                        Icon(Icons.Default.Tab, "Tabs", tint = WHITE)
                    }

                    Spacer(Modifier.width(4.dp))

                    val isLoading = currentTabIndex >= 0 && (currentTab?.progress ?: 100) in 1..99
                    Box(
                        Modifier
                            .weight(1f)
                            .background(FIELD_BG)
                    ) {
                        if (isLoading) {
                            Box(
                                Modifier
                                    .matchParentSize()
                                    .drawBehind {
                                        drawRect(
                                            color = Color.White,
                                            size = Size(size.width * (currentTab?.progress ?: 100) / 100f, size.height)
                                        )
                                    }
                            )
                        }
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            singleLine = true,
                            placeholder = {
                                Text(
                                    if (currentTabIndex == -1) "Search or enter URL"
                                    else currentTab?.url?.removePrefix("https://")?.take(50) ?: "",
                                    color = WHITE.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { isUrlFocused = it.isFocused },
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
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
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
                    }

                    Spacer(Modifier.width(4.dp))

                    IconButton({ currentTabIndex = -1 }) {
                        Icon(Icons.Default.Add, "New Tab", tint = WHITE)
                    }

                    Spacer(Modifier.width(4.dp))

                    Box {
                        IconButton({ showMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Menu", tint = WHITE)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            offset = DpOffset((-500).dp, 0.dp),
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
                                DropdownMenuItem(
                                    text = { Text(if (showElementHider) "Stop Hiding" else "Hide Element", color = WHITE) },
                                    onClick = {
                                        showMenu = false
                                        showElementHider = !showElementHider
                                        val wv = currentTab?.webView
                                        if (showElementHider && wv != null) {
                                            wv.evaluateJavascript("""
                                                (function() {
                                                    if (window.__GREY_PICKER__) return;
                                                    window.__GREY_PICKER__ = true;
                                                    
                                                    var current = document.body;
                                                    var highlight = null;
                                                    var currentView = 'picker';
                                                    
                                                    function createHighlight() {
                                                        var h = document.createElement('div');
                                                        h.id = 'gp-highlight';
                                                        Object.assign(h.style, {
                                                            position: 'absolute', pointerEvents: 'none',
                                                            zIndex: '2147483646', border: '2px solid #FF4444',
                                                            background: 'rgba(255,68,68,0.25)',
                                                            borderRadius: '0px', transition: 'all 0.12s ease',
                                                            boxSizing: 'border-box', top: '0', left: '0'
                                                        });
                                                        document.body.appendChild(h);
                                                        return h;
                                                    }
                                                    
                                                    function moveHighlight(el) {
                                                        if (!el || el === document.documentElement) return;
                                                        if (!highlight) highlight = createHighlight();
                                                        var r = el.getBoundingClientRect();
                                                        Object.assign(highlight.style, {
                                                            top: (r.top + window.scrollY) + 'px',
                                                            left: (r.left + window.scrollX) + 'px',
                                                            width: r.width + 'px',
                                                            height: r.height + 'px',
                                                            display: 'block'
                                                        });
                                                    }
                                                    
                                                    function buildSelector(el) {
                                                        if (!el || el.nodeType !== 1) return '';
                                                        var sel = el.tagName.toLowerCase();
                                                        if (el.id) sel += '#' + el.id;
                                                        else if (el.className) {
                                                            var classes = Array.from(el.classList)
                                                                .filter(function(c) { return c && c.indexOf(':') === -1; })
                                                                .slice(0, 3).join('.');
                                                            if (classes) sel += '.' + classes;
                                                        }
                                                        return sel;
                                                    }
                                                    
                                                    function buildRule(el) {
                                                        var host = location.hostname.replace(/^www\./, '');
                                                        var sel = buildSelector(el);
                                                        return sel ? host + '##' + sel : '';
                                                    }
                                                    
                                                    function validEl(el) {
                                                        var panel = document.getElementById('gp-panel');
                                                        return el && el.nodeType === 1 && el !== panel && !(panel && panel.contains(el)) && el !== highlight;
                                                    }
                                                    
                                                    function update(el) {
                                                        if (!el || el === document.documentElement || el.id === 'gp-panel' || el.id === 'gp-highlight') return;
                                                        if (document.getElementById('gp-panel') && document.getElementById('gp-panel').contains(el)) return;
                                                        current = el;
                                                        moveHighlight(el);
                                                        var tagEl = document.getElementById('gp-tag');
                                                        var selEl = document.getElementById('gp-sel');
                                                        var ruleEl = document.getElementById('gp-rule');
                                                        if (tagEl) tagEl.textContent = '<' + el.tagName.toLowerCase() + (el.id ? ' id="' + el.id + '"' : '') + (el.className ? ' class="' + el.className.slice(0,60) + '"' : '') + '>';
                                                        if (selEl) selEl.textContent = buildSelector(el);
                                                        if (ruleEl) ruleEl.textContent = buildRule(el);
                                                    }
                                                    
                                                    function showPickerView() {
                                                        currentView = 'picker';
                                                        document.getElementById('gp-panel-body').innerHTML = 
                                                            '<div style="display:grid;grid-template-columns:1fr 1fr;gap:5px;margin-bottom:8px">' +
                                                            '<button id="gp-parent" class="gp-btn">◀ Parent</button>' +
                                                            '<button id="gp-child" class="gp-btn">Child ▶</button>' +
                                                            '<button id="gp-prev" class="gp-btn">◀ Prev</button>' +
                                                            '<button id="gp-next" class="gp-btn">Next ▶</button></div>' +
                                                            '<div style="background:#121212;padding:8px;margin-bottom:8px">' +
                                                            '<div style="color:#888;font-size:10px;margin-bottom:3px">ELEMENT</div>' +
                                                            '<div id="gp-tag" style="color:#7DD3FC;font-size:12px;word-break:break-all;margin-bottom:2px"></div>' +
                                                            '<div id="gp-sel" style="color:#86EFAC;font-size:11px;word-break:break-all"></div></div>' +
                                                            '<div style="background:#121212;padding:8px;margin-bottom:10px">' +
                                                            '<div style="color:#888;font-size:10px;margin-bottom:3px">RULE</div>' +
                                                            '<div id="gp-rule" style="color:#FBBF24;font-size:11px;word-break:break-all"></div></div>' +
                                                            '<div style="display:grid;grid-template-columns:1fr 1fr;gap:6px;margin-bottom:6px">' +
                                                            '<button id="gp-save" style="background:#FF4444;color:white;border:none;padding:8px;cursor:pointer;font-size:12px;font-family:monospace;font-weight:bold">✓ Hide & Save</button>' +
                                                            '<button id="gp-rules-btn" style="background:#1E1E2E;color:#CCC;border:1px solid #333;padding:8px;cursor:pointer;font-size:12px;font-family:monospace">☰ Rules</button></div>' +
                                                            '<div id="gp-msg" style="text-align:center;font-size:11px;color:#4ADE80;height:14px"></div>';
                                                        bindPickerEvents();
                                                        update(current);
                                                    }
                                                    
                                                    function showRulesView(rules) {
                                                        currentView = 'rules';
                                                        var html = '<div style="max-height:350px;overflow-y:auto;margin-bottom:8px">';
                                                        var domains = {};
                                                        rules.forEach(function(r) {
                                                            if (!domains[r.domain]) domains[r.domain] = [];
                                                            domains[r.domain].push(r);
                                                        });
                                                        var sortedDomains = Object.keys(domains).sort(function(a, b) {
                                                            var aMax = Math.max.apply(null, domains[a].map(function(r) { return r.timestamp || 0; }));
                                                            var bMax = Math.max.apply(null, domains[b].map(function(r) { return r.timestamp || 0; }));
                                                            return bMax - aMax;
                                                        });
                                                        if (sortedDomains.length === 0) {
                                                            html += '<div style="color:#888;text-align:center;padding:20px">No rules saved yet</div>';
                                                        } else {
                                                            sortedDomains.forEach(function(domain) {
                                                                html += '<div style="color:#888;font-size:10px;padding:6px 0 3px 0;border-top:1px solid #333">' + domain + '</div>';
                                                                var domainRules = domains[domain].sort(function(a, b) { return (b.timestamp || 0) - (a.timestamp || 0); });
                                                                domainRules.forEach(function(r) {
                                                                    html += '<div style="padding:4px 0;font-size:11px">' +
                                                                        '<span style="color:#FFF;font-family:monospace">' + r.selector + '</span>' +
                                                                        '</div>';
                                                                });
                                                            });
                                                        }
                                                        html += '</div>' +
                                                            '<button id="gp-back" style="background:#1E1E2E;color:#CCC;border:1px solid #333;padding:8px;cursor:pointer;font-size:12px;font-family:monospace;width:100%">← Back to Picker</button>';
                                                        document.getElementById('gp-panel-body').innerHTML = html;
                                                        document.getElementById('gp-back').addEventListener('click', function() {
                                                            showPickerView();
                                                        });
                                                    }
                                                    
                                                    function bindPickerEvents() {
                                                        document.getElementById('gp-parent').addEventListener('click', function() {
                                                            var p = current.parentElement;
                                                            if (validEl(p) && p !== document.documentElement) update(p);
                                                        });
                                                        document.getElementById('gp-child').addEventListener('click', function() {
                                                            var c = current.firstElementChild;
                                                            if (validEl(c)) update(c);
                                                        });
                                                        document.getElementById('gp-prev').addEventListener('click', function() {
                                                            var s = current.previousElementSibling;
                                                            while (s && !validEl(s)) s = s.previousElementSibling;
                                                            if (s) update(s);
                                                        });
                                                        document.getElementById('gp-next').addEventListener('click', function() {
                                                            var s = current.nextElementSibling;
                                                            while (s && !validEl(s)) s = s.nextElementSibling;
                                                            if (s) update(s);
                                                        });
                                                        document.getElementById('gp-save').addEventListener('click', function() {
                                                            var rule = buildRule(current);
                                                            if (rule) {
                                                                GreyPicker.onRuleGenerated(rule);
                                                                document.getElementById('gp-msg').textContent = '✓ Saved!';
                                                            }
                                                        });
                                                        document.getElementById('gp-rules-btn').addEventListener('click', function() {
                                                            GreyPicker.onShowRules();
                                                        });
                                                    }
                                                    
                                                    var panel = document.createElement('div');
                                                    panel.id = 'gp-panel';
                                                    Object.assign(panel.style, {
                                                        position: 'fixed', bottom: '16px', right: '12px',
                                                        background: '#1E1E1E', color: '#FFFFFF',
                                                        borderRadius: '0px', zIndex: '2147483647',
                                                        fontSize: '11px', fontFamily: 'monospace',
                                                        width: '290px', boxShadow: '0 4px 24px rgba(0,0,0,0.8)',
                                                        userSelect: 'none', border: '1px solid #333333'
                                                    });
                                                    
                                                    panel.innerHTML = 
                                                        '<div id="gp-header" style="display:flex;justify-content:space-between;align-items:center;padding:12px 12px 10px 12px;cursor:grab;border-bottom:1px solid #333">' +
                                                        '<span id="gp-title" style="font-weight:bold;color:#FF4444;font-size:13px;pointer-events:none">⬡ Element Picker</span></div>' +
                                                        '<div id="gp-panel-body" style="padding:12px"></div>';
                                                    document.body.appendChild(panel);
                                                    
                                                    document.head.insertAdjacentHTML('beforeend', '<style>.gp-btn{background:#1E1E2E;color:#CCC;border:1px solid #333;padding:6px 4px;cursor:pointer;font-size:10px;font-family:monospace}</style>');
                                                    
                                                    var header = document.getElementById('gp-header');
                                                    var isDragging = false;
                                                    var startX, startY, panelLeft, panelTop;
                                                    
                                                    header.addEventListener('touchstart', function(e) {
                                                        isDragging = true;
                                                        startX = e.touches[0].clientX;
                                                        startY = e.touches[0].clientY;
                                                        panelLeft = panel.offsetLeft;
                                                        panelTop = panel.offsetTop;
                                                        header.style.cursor = 'grabbing';
                                                        e.preventDefault();
                                                    });
                                                    
                                                    header.addEventListener('mousedown', function(e) {
                                                        isDragging = true;
                                                        startX = e.clientX;
                                                        startY = e.clientY;
                                                        panelLeft = panel.offsetLeft;
                                                        panelTop = panel.offsetTop;
                                                        header.style.cursor = 'grabbing';
                                                        e.preventDefault();
                                                    });
                                                    
                                                    window.addEventListener('touchmove', function(e) {
                                                        if (!isDragging) return;
                                                        var dx = e.touches[0].clientX - startX;
                                                        var dy = e.touches[0].clientY - startY;
                                                        panel.style.right = 'auto';
                                                        panel.style.bottom = 'auto';
                                                        panel.style.left = (panelLeft + dx) + 'px';
                                                        panel.style.top = (panelTop + dy) + 'px';
                                                    });
                                                    
                                                    window.addEventListener('mousemove', function(e) {
                                                        if (!isDragging) return;
                                                        var dx = e.clientX - startX;
                                                        var dy = e.clientY - startY;
                                                        panel.style.right = 'auto';
                                                        panel.style.bottom = 'auto';
                                                        panel.style.left = (panelLeft + dx) + 'px';
                                                        panel.style.top = (panelTop + dy) + 'px';
                                                    });
                                                    
                                                    window.addEventListener('touchend', function() {
                                                        isDragging = false;
                                                        header.style.cursor = 'grab';
                                                    });
                                                    
                                                    window.addEventListener('mouseup', function() {
                                                        isDragging = false;
                                                        header.style.cursor = 'grab';
                                                    });
                                                    
                                                    window.addEventListener('scroll', function() {
                                                        if (current && currentView === 'picker') moveHighlight(current);
                                                    }, true);
                                                    window.addEventListener('resize', function() {
                                                        if (current && currentView === 'picker') moveHighlight(current);
                                                    });
                                                    
                                                    showPickerView();
                                                    update(document.body.firstElementChild || document.body);
                                                    
                                                    document.addEventListener('click', function(e) {
                                                        if (currentView !== 'picker') return;
                                                        if (e.target.id === 'gp-panel' || (e.target.closest && e.target.closest('#gp-panel')) || e.target === highlight) return;
                                                        e.preventDefault();
                                                        e.stopPropagation();
                                                        update(e.target);
                                                    }, true);
                                                    
                                                    window.__GREY_SHOW_RULES__ = function(rules) {
                                                        document.getElementById('gp-title').textContent = '⬡ My Rules';
                                                        showRulesView(rules);
                                                    };
                                                })();
                                            """.trimIndent(), null)
                                        } else if (!showElementHider && wv != null) {
                                            wv.evaluateJavascript("""
                                                (function() {
                                                    var panel = document.getElementById('gp-panel');
                                                    var hl = document.getElementById('gp-highlight');
                                                    if (panel) panel.remove();
                                                    if (hl) hl.remove();
                                                    delete window.__GREY_PICKER__;
                                                    delete window.__GREY_SHOW_RULES__;
                                                })();
                                            """.trimIndent(), null)
                                        }
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Element Rules", color = WHITE) },
                                onClick = { showMenu = false; showElementRules = true }
                            )
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
                                onClick = { showMenu = false; showScripts = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Filters", color = WHITE) },
                                onClick = { showMenu = false; showFilters = true }
                            )
                            DropdownMenuItem(
                                text = { Text("App Lock", color = WHITE) },
                                onClick = { showMenu = false; showAppLockSettings = true }
                            )
                        }
                    }
                }
            }

            Box(Modifier.fillMaxWidth().height(0.5.dp).background(MUTED))

            Box(Modifier.weight(1f).fillMaxWidth()) {
                ContentLayer()
            }
        }
    }

    val currentWebView = currentTab?.webView
    LaunchedEffect(currentWebView) {
        currentWebView?.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun onRuleGenerated(rule: String) {
                scope.launch(Dispatchers.Main) {
                    val domain = rule.substringBefore("##").trim()
                    val selector = rule.substringAfter("##").trim()
                    if (domain.isNotBlank() && selector.isNotBlank()) {
                        customHideRules.removeAll { it.domain == domain && it.selector == selector }
                        customHideRules.add(0, CustomHideRule(domain = domain, selector = selector))
                        showToast("Saved: $rule")
                        currentWebView?.evaluateJavascript("""
                            try {
                                document.querySelectorAll('$selector').forEach(function(el) {
                                    el.style.setProperty('display', 'none', 'important');
                                });
                            } catch(e) {}
                        """.trimIndent(), null)
                    }
                }
            }

            @android.webkit.JavascriptInterface
            fun onPickerClosed() {
                scope.launch(Dispatchers.Main) {
                    showElementHider = false
                }
            }

            @android.webkit.JavascriptInterface
            fun onShowRules() {
                scope.launch(Dispatchers.Main) {
                    val wv = currentTab?.webView ?: return@launch
                    val rulesJson = JSONArray()
                    for (rule in customHideRules) {
                        val obj = JSONObject()
                        obj.put("id", rule.id)
                        obj.put("domain", rule.domain)
                        obj.put("selector", rule.selector)
                        obj.put("enabled", rule.enabled)
                        obj.put("timestamp", rule.timestamp)
                        rulesJson.put(obj)
                    }
                    wv.evaluateJavascript(
                        "if (window.__GREY_SHOW_RULES__) window.__GREY_SHOW_RULES__(${rulesJson});",
                        null
                    )
                }
            }
        }, "GreyPicker")
    }

    if (showElementRules) {
        ElementRulesScreen(
            rules = customHideRules,
            onDismiss = { showElementRules = false },
            onToggleRule = { id ->
                val index = customHideRules.indexOfFirst { it.id == id }
                if (index >= 0) {
                    customHideRules[index] = customHideRules[index].copy(enabled = !customHideRules[index].enabled)
                }
            },
            onDeleteRule = { id ->
                customHideRules.removeAll { it.id == id }
                showToast("Rule deleted")
            },
            onAddRule = { domain, selector ->
                customHideRules.removeAll { it.domain == domain && it.selector == selector }
                customHideRules.add(0, CustomHideRule(domain = domain, selector = selector))
                showToast("Rule added")
            }
        )
    }

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

    if (showLoadingScreen) {
        Box(
            Modifier.fillMaxSize().background(BG),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    val letters = listOf("G", "R", "E", "Y")
                    letters.forEachIndexed { index, letter ->
                        Text(
                            letter,
                            color = if (index <= loadingLetterIndex) WHITE else MUTED,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("browser", color = MUTED, fontSize = 16.sp)
            }
        }
    }
}
//PART 8.7 END

//PART 9 START
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
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                color = ITEM_BG
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
//PART 9 END

//PART 10 START
@Composable
fun AllGroupChip(isSelected: Boolean, tabCount: Int, onClick: () -> Unit) {
    Surface(
        Modifier.padding(vertical = 4.dp).width(52.dp)
            .clickable { onClick() },
        color = if (isSelected) WHITE else ITEM_BG
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

    val blinkAlpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        )
    )

    val bg = if (isSelected) WHITE else ITEM_BG
    Surface(
        Modifier.padding(vertical = 4.dp).width(52.dp)
            .clickable { onClick() },
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
                        items(history.reversed()) { item ->
                            val domain = getDomainName(item.url)
                            LaunchedEffect(item.url) { loadFavicon(domain) }
                            val fav = faviconBitmaps[domain]
                            Surface(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                color = ITEM_BG
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
//PART 10 END

//PART 11 START
fun hashPattern(pattern: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(pattern.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}

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

                Divider(color = DIVIDER_COLOR, thickness = 1.dp)

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

                Divider(color = DIVIDER_COLOR, thickness = 1.dp)

                Spacer(Modifier.height(24.dp))

                if (toggleChecked) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                if (hasPattern) {
                                    onChangePattern()
                                } else {
                                    onToggleChange(true)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
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

    val spacingPx = remember { with(density) { dotSpacing.toPx() } }
    val sizePx = remember { with(density) { dotSize.toPx() } }

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

    val shakeOffset by animateFloatAsState(
        targetValue = if (showError) 10f else 0f,
        animationSpec = if (showError) {
            repeatable(iterations = 3, animation = tween(50), repeatMode = RepeatMode.Reverse)
        } else {
            tween(0)
        }
    )

    LaunchedEffect(showError) {
        if (showError) {
            delay(600)
            showError = false
            selectedDots.clear()
        }
    }

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

    fun handleComplete() {
        val patternStr = selectedDots.joinToString(",")
        val dotCount = selectedDots.size
        val hash = hashPattern(patternStr)

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

                Text(
                    promptText,
                    color = if (errorState) DELETE_BG else MUTED,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(24.dp))

                if (mode != "unlock") {
                    Button(
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
                        colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
                    ) {
                        Text("Reset")
                    }
                }

                Spacer(Modifier.weight(0.3f))
            }
        }
    }
}
//PART 11 END

//PART 12 START
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
                    Button(
                        onClick = { showGuide = true },
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("?", fontSize = 14.sp, color = WHITE)
                    }
                }

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
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                color = ITEM_BG
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

                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    Button(
                        onClick = onAddScript,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
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
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    placeholder = { Text("Script name", color = WHITE.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = WHITE, fontSize = 14.sp),
                    shape = RectangleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FIELD_BG,
                        unfocusedContainerColor = FIELD_BG,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = WHITE
                    )
                )

                Spacer(Modifier.height(12.dp))

                Text("Code", color = MUTED, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))

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
                        focusedContainerColor = FIELD_BG,
                        unfocusedContainerColor = FIELD_BG,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
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
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
                ) {
                    Text("Cancel", color = WHITE)
                }
                Button(
                    onClick = { onSave(title, code) },
                    modifier = Modifier.weight(1f),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
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

                Divider(color = DIVIDER_COLOR, thickness = 1.dp)

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
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(guideText))
                            },
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
                        ) {
                            Text("Copy Guide", color = WHITE)
                        }
                    }
                }
            }
        }
    }
}
//PART 12 END

//PART 13 START
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

                Divider(color = DIVIDER_COLOR, thickness = 1.dp)

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

                Divider(color = DIVIDER_COLOR, thickness = 1.dp)

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
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                color = ITEM_BG
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

                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    Button(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
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
            } catch (e: Exception) { }
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
                        focusedContainerColor = FIELD_BG,
                        unfocusedContainerColor = FIELD_BG,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = WHITE
                    )
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { filePickerLauncher.launch("text/plain") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
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
//PART 13 END

//PART 14 START
@Composable
fun ElementRulesScreen(
    rules: List<CustomHideRule>,
    onDismiss: () -> Unit,
    onToggleRule: (String) -> Unit,
    onDeleteRule: (String) -> Unit,
    onAddRule: (String, String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var ruleToDelete by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showDeleteConfirm && ruleToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; ruleToDelete = null },
            title = { Text("Delete Rule?", color = WHITE, fontSize = 18.sp) },
            text = { Text("This cannot be undone.", color = MUTED, fontSize = 14.sp) },
            confirmButton = {
                TextButton({
                    onDeleteRule(ruleToDelete!!)
                    showDeleteConfirm = false
                    ruleToDelete = null
                }) { Text("Delete", color = WHITE) }
            },
            dismissButton = {
                TextButton({
                    showDeleteConfirm = false
                    ruleToDelete = null
                }) { Text("Cancel", color = WHITE) }
            },
            containerColor = SURFACE,
            titleContentColor = WHITE,
            textContentColor = WHITE,
            shape = RectangleShape,
            tonalElevation = 0.dp
        )
    }

    if (showAddDialog) {
        var newDomain by remember { mutableStateOf("") }
        var newSelector by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Rule", color = WHITE, fontSize = 18.sp) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDomain,
                        onValueChange = { newDomain = it },
                        singleLine = true,
                        placeholder = { Text("Domain (e.g. example.com or *)", color = WHITE.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = WHITE, fontSize = 14.sp),
                        shape = RectangleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FIELD_BG,
                            unfocusedContainerColor = FIELD_BG,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = WHITE
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSelector,
                        onValueChange = { newSelector = it },
                        singleLine = true,
                        placeholder = { Text("CSS Selector", color = WHITE.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = WHITE, fontSize = 14.sp),
                        shape = RectangleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FIELD_BG,
                            unfocusedContainerColor = FIELD_BG,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = WHITE
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newDomain.isNotBlank() && newSelector.isNotBlank()) {
                            onAddRule(newDomain.trim(), newSelector.trim())
                            showAddDialog = false
                        }
                    },
                    enabled = newDomain.isNotBlank() && newSelector.isNotBlank()
                ) {
                    Text("Add", color = if (newDomain.isNotBlank() && newSelector.isNotBlank()) WHITE else WHITE.copy(alpha = 0.3f))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
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
                    Text("Element Rules", color = WHITE, fontSize = 18.sp)
                    if (rules.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Text("(${rules.size})", color = MUTED, fontSize = 14.sp)
                    }
                }

                if (rules.isEmpty()) {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No rules", color = MUTED, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("Use Hide Element or Add Rule", color = MUTED.copy(alpha = 0.7f), fontSize = 14.sp)
                        }
                    }
                } else {
                    val groupedRules = rules.groupBy { it.domain }
                    val sortedDomains = groupedRules.keys.sortedByDescending { d ->
                        groupedRules[d]?.maxOfOrNull { it.timestamp } ?: 0L
                    }

                    LazyColumn(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        for (domain in sortedDomains) {
                            val domainRules = groupedRules[domain]?.sortedByDescending { it.timestamp } ?: emptyList()

                            item(key = domain) {
                                Text(
                                    domain,
                                    color = MUTED,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                )
                            }

                            items(domainRules, key = { it.id }) { rule ->
                                Surface(
                                    Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    color = ITEM_BG
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Switch(
                                            checked = rule.enabled,
                                            onCheckedChange = { onToggleRule(rule.id) },
                                            modifier = Modifier.padding(end = 4.dp),
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = WHITE,
                                                checkedTrackColor = WHITE.copy(alpha = 0.3f),
                                                uncheckedThumbColor = WHITE.copy(alpha = 0.5f),
                                                uncheckedTrackColor = Color(0xFF444444)
                                            )
                                        )
                                        Text(
                                            rule.selector,
                                            color = if (rule.enabled) WHITE else MUTED,
                                            fontSize = 13.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton({
                                            ruleToDelete = rule.id
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

                Surface(
                    Modifier.fillMaxWidth().navigationBarsPadding(),
                    color = SURFACE
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = ELEVATED_BG, contentColor = WHITE)
                    ) {
                        Icon(Icons.Default.Add, null, tint = WHITE)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Rule", color = WHITE)
                    }
                }
            }
        }
    }
}
//PART 14 END
