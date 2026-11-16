package com.glass768.tv

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Night = Color(0xFF07111F)
private val Glass = Color(0xB91A2B40)
private val Edge = Color(0x66FFFFFF)
private val Ice = Color(0xFF8BE9FF)

private data class TvApp(val name: String, val packageName: String)
private data class Scene(val title: String, val group: String, val image: String)

private val scenes = listOf(
    Scene("Alpine dusk", "Nature", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1920&q=85"),
    Scene("Ocean blue", "Ocean", "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=1920&q=85"),
    Scene("Night sky", "Space", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=1920&q=85"),
    Scene("Modern lines", "Architecture", "https://images.unsplash.com/photo-1487958449943-2429e8be8625?w=1920&q=85"),
    Scene("Forest light", "Nature", "https://images.unsplash.com/photo-1448375240586-882707db888b?w=1920&q=85"),
    Scene("Blue abstract", "Abstract", "https://images.unsplash.com/photo-1557683316-973673baf926?w=1920&q=85"),
    Scene("Performance", "Cars", "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=1920&q=85")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent { GlassLauncher() }
    }
}

@Composable
private fun GlassLauncher() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var tab by remember { mutableStateOf("Home") }
    var sceneIndex by remember { mutableIntStateOf(0) }
    var rotate by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("All") }
    var assistant by remember { mutableStateOf(false) }
    var command by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Hello! I'm 768 AI. Try “next wallpaper” or “open YouTube”.") }
    var apps by remember { mutableStateOf(emptyList<TvApp>()) }
    var time by remember { mutableStateOf("") }

    fun refreshApps() {
        val pm = context.packageManager
        val leanback = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val normal = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = (pm.queryIntentActivities(leanback, PackageManager.MATCH_ALL) +
            pm.queryIntentActivities(normal, PackageManager.MATCH_ALL))
            .distinctBy { it.activityInfo.packageName }
            .filter { it.activityInfo.packageName != context.packageName }
            .map { TvApp(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
    }

    LaunchedEffect(Unit) {
        refreshApps()
        while (true) {
            time = SimpleDateFormat("EEE, d MMM  •  h:mm a", Locale.getDefault()).format(Date())
            delay(30_000)
        }
    }
    LaunchedEffect(rotate) {
        if (rotate) while (true) {
            delay(60_000)
            sceneIndex = (sceneIndex + 1) % scenes.size
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Night, surface = Glass, primary = Ice,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(Night)) {
            AsyncImage(
                model = scenes[sceneIndex].image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0x66030A14), Color(0xB807111F), Color(0xF007111F)))
            ))
            Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 26.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("768 GLASS", color = Ice, fontSize = 15.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
                        Text("Your screen. Your space.", color = Color.White.copy(.7f), fontSize = 12.sp)
                    }
                    Text(time, color = Color.White.copy(.9f), fontSize = 15.sp)
                    Spacer(Modifier.width(18.dp))
                    Surface(color = Glass, shape = RoundedCornerShape(40.dp), border = BorderStroke(1.dp, Edge)) {
                        Row(Modifier.padding(horizontal = 14.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Ice, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(7.dp))
                            Text("Powered by 768 AI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf("Home", "Apps", "Wallpapers", "Customize").forEach { name ->
                        GlassTab(name, tab == name) { tab = name }
                    }
                    Spacer(Modifier.weight(1f))
                    GlassTab("768 AI", false) { assistant = true }
                }
                Spacer(Modifier.height(24.dp))
                when (tab) {
                    "Apps" -> AppsPage(apps, { refreshApps() }) { app ->
                        val launch = context.packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
                            ?: context.packageManager.getLaunchIntentForPackage(app.packageName)
                        if (launch != null) runCatching { context.startActivity(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
                    }
                    "Wallpapers" -> WallpaperPage(category, { category = it }, sceneIndex, { sceneIndex = it }, rotate, { rotate = it })
                    "Customize" -> Column(Modifier.widthIn(max = 650.dp)) {
                        Text("Make it yours", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(22.dp))
                        Text("Automatic wallpaper rotation")
                        Switch(checked = rotate, onCheckedChange = { rotate = it })
                        Text("Glass styling is enabled throughout the interface.", color = Color.White.copy(.65f))
                    }
                    else -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Text("A little more cinematic.", color = Color.White.copy(.75f), fontSize = 16.sp)
                        Text("Make yourself at home.", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            FeatureCard("Your apps", "Launch installed apps", Icons.Default.Apps) { tab = "Apps" }
                            FeatureCard("Wallpapers", "Choose your atmosphere", Icons.Default.Wallpaper) { tab = "Wallpapers" }
                            FeatureCard("768 AI", "Open local assistant", Icons.Default.AutoAwesome) { assistant = true }
                            FeatureCard("Next scene", "Refresh background", Icons.Default.Refresh) { sceneIndex = (sceneIndex + 1) % scenes.size }
                        }
                        Spacer(Modifier.height(25.dp))
                        Text("QUICK LAUNCH", color = Ice, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(apps.take(9), key = { it.packageName }) { app ->
                                GlassTile(app.name) { openApp(context, app.packageName) }
                            }
                        }
                    }
                }
                Spacer(Modifier.weight(1f, fill = false))
                Text("768 GLASS TV  •  ANDROID TV", color = Color.White.copy(.45f), fontSize = 10.sp, letterSpacing = 1.5.sp)
            }
            if (assistant) {
                Box(Modifier.fillMaxSize().background(Color(0x99000000)), contentAlignment = Alignment.Center) {
                    Surface(Modifier.width(520.dp), color = Color(0xF0182B40), shape = RoundedCornerShape(26.dp), border = BorderStroke(1.dp, Edge)) {
                        Column(Modifier.padding(22.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Ice)
                                Spacer(Modifier.width(8.dp))
                                Text("768 AI", fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                IconButton(onClick = { assistant = false }) { Icon(Icons.Default.Close, null) }
                            }
                            Text("Your TV, at your command.", color = Color.White.copy(.65f))
                            Spacer(Modifier.height(15.dp))
                            Text(response, Modifier.fillMaxWidth().background(Night, RoundedCornerShape(12.dp)).padding(14.dp))
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(command, { command = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Try: next wallpaper") })
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(onClick = {
                                    val q = command.trim().lowercase()
                                    response = when {
                                        "next wallpaper" in q -> { sceneIndex = (sceneIndex + 1) % scenes.size; "Changed to ${scenes[sceneIndex].title}." }
                                        "wallpaper" in q -> { tab = "Wallpapers"; "Opening wallpaper studio." }
                                        "apps" in q -> { tab = "Apps"; "Showing installed apps." }
                                        q.startsWith("open ") -> {
                                            val match = apps.firstOrNull { it.name.lowercase().contains(q.removePrefix("open ")) }
                                            if (match == null) "I couldn't find that app." else { openApp(context, match.packageName); "Opening ${match.name}." }
                                        }
                                        else -> "I can open apps and control wallpapers. Cloud AI is not connected yet."
                                    }
                                    command = ""
                                }, enabled = command.isNotBlank()) { Text("Send") }
                            }
                            Text("Local assistant prototype • Cloud AI not connected", color = Color.White.copy(.45f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun openApp(context: android.content.Context, packageName: String) {
    val intent = context.packageManager.getLeanbackLaunchIntentForPackage(packageName)
        ?: context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}

@Composable
private fun GlassTab(label: String, selected: Boolean, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.05f else 1f, spring(), label = "tab")
    Surface(
        Modifier.scale(scale).onFocusChanged { focused = it.isFocused }.clickable { onClick() }.focusable(),
        color = if (selected || focused) Color(0xDD29445E) else Color(0x77101E30),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, if (selected || focused) Ice else Edge)
    ) {
        Text(label, Modifier.padding(horizontal = 18.dp, vertical = 10.dp), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FeatureCard(title: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.045f else 1f, spring(), label = "feature")
    Surface(
        Modifier.width(190.dp).height(116.dp).scale(scale).onFocusChanged { focused = it.isFocused }.clickable { onClick() }.focusable(),
        color = if (focused) Color(0xDD29445E) else Glass,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (focused) Ice else Edge),
        shadowElevation = if (focused) 16.dp else 5.dp
    ) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = Ice, modifier = Modifier.size(23.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(sub, fontSize = 10.sp, color = Color.White.copy(.65f))
            }
        }
    }
}

@Composable
private fun GlassTile(label: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Surface(
        Modifier.size(112.dp, 76.dp).onFocusChanged { focused = it.isFocused }.clickable { onClick() }.focusable(),
        color = if (focused) Color(0xDD29445E) else Glass,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (focused) Ice else Edge)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, Modifier.padding(8.dp), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun AppsPage(apps: List<TvApp>, refresh: () -> Unit, open: (TvApp) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Your apps", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("${apps.size} installed apps discovered", color = Color.White.copy(.65f))
            }
            Button(onClick = refresh) { Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(5.dp)); Text("Refresh") }
        }
        Spacer(Modifier.height(20.dp))
        androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps, key = { it.packageName }) { app ->
                Surface(Modifier.fillMaxWidth().clickable { open(app) }.focusable(), color = Glass, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Edge)) {
                    Text(app.name, Modifier.padding(14.dp), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun WallpaperPage(category: String, setCategory: (String) -> Unit, selected: Int, select: (Int) -> Unit, rotate: Boolean, setRotate: (Boolean) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Wallpaper studio", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Choose the atmosphere for your screen", color = Color.White.copy(.65f))
            }
            Text("Auto rotate", fontSize = 12.sp)
            Switch(checked = rotate, onCheckedChange = setRotate)
        }
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("All", "Nature", "Ocean", "Space", "Cars", "Architecture", "Abstract")) { c ->
                GlassTab(c, category == c) { setCategory(c) }
            }
        }
        Spacer(Modifier.height(18.dp))
        val choices = scenes.withIndex().filter { category == "All" || it.value.group == category }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(choices, key = { it.value.title }) { item ->
                Column(Modifier.width(220.dp).clickable { select(item.index) }.focusable()) {
                    AsyncImage(item.value.image, item.value.title, Modifier.fillMaxWidth().height(125.dp), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
                    Text(item.value.title, Modifier.padding(top = 7.dp), fontWeight = if (selected == item.index) FontWeight.Bold else FontWeight.Normal)
                    Text(item.value.group, fontSize = 11.sp, color = Ice)
                }
            }
        }
        Text("Remote images require an internet connection. Persistent offline wallpaper storage is not included yet.", color = Color.White.copy(.5f), fontSize = 11.sp)
    }
}
