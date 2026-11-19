package com.seveneightsix.glassos

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class AppEntry(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable
)

private val Ruby = Color(0xFFE52B55)
private val Rose = Color(0xFFFFB8C7)
private val White = Color(0xFFFFF7F8)
private val Background = Color(0xFF090609)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { RubyGlassLauncher() }
    }
}

@Composable
private fun RubyGlassLauncher() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }

    // Resolve app labels and icons away from the UI thread to keep scrolling responsive.
    LaunchedEffect(Unit) {
        apps = withContext(Dispatchers.IO) {
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0
            ).asSequence()
                .filter { it.activityInfo.packageName != context.packageName }
                .distinctBy { it.activityInfo.packageName }
                .map { info ->
                    AppEntry(
                        packageName = info.activityInfo.packageName,
                        activityName = info.activityInfo.name,
                        label = info.loadLabel(context.packageManager).toString(),
                        icon = info.loadIcon(context.packageManager)
                    )
                }
                .sortedBy { it.label.lowercase() }
                .toList()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(primary = Ruby, background = Background)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0xFF32101C), Color(0xFF130A10), Background, Color(0xFF1B0911))
                )
            )
        ) {
            // Subtle ambient ruby light behind the translucent surfaces.
            Box(
                Modifier.align(Alignment.TopEnd).padding(top = 90.dp, end = 8.dp)
                    .size(190.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0x557F1B3B), Color.Transparent)),
                        RoundedCornerShape(100.dp)
                    )
            )
            Column(
                Modifier.fillMaxSize().safeDrawingPadding()
                    .padding(start = 17.dp, end = 17.dp, top = 12.dp, bottom = 10.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(8.dp).clip(RoundedCornerShape(50))
                                .background(Brush.linearGradient(listOf(Rose, Ruby, Color(0xFF790D2D))))
                        )
                        Spacer(Modifier.width(9.dp))
                        Text(
                            "768 / GLASS", color = White, fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold, letterSpacing = 2.2.sp
                        )
                    }
                    Text("RUBY CRYSTAL", color = Rose.copy(alpha = .78f), fontSize = 9.sp, letterSpacing = 1.8.sp)
                }

                Spacer(Modifier.height(15.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 76.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 1.dp, end = 1.dp, top = 5.dp, bottom = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        GlassAppTile(app = app) {
                            val launch = Intent(Intent.ACTION_MAIN)
                                .addCategory(Intent.CATEGORY_LAUNCHER)
                                .setComponent(ComponentName(app.packageName, app.activityName))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val options = ActivityOptions.makeCustomAnimation(
                                context, android.R.anim.fade_in, android.R.anim.fade_out
                            ).toBundle()
                            runCatching { context.startActivity(launch, options) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassAppTile(app: AppEntry, onLaunch: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) .94f else 1f,
        animationSpec = spring(dampingRatio = .62f, stiffness = 650f),
        label = "glassTilePress"
    )
    Column(
        modifier = Modifier.fillMaxWidth().scale(scale)
            .clip(RoundedCornerShape(21.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0x66FFFFFF), Color(0x33F5B9C8),
                        Color(0x553E1424), Color(0xAA170912)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color(0xCCFFFFFF), Color(0x66FFB7C9), Color(0x447C2440), Color(0x88FFFFFF))
                ),
                shape = RoundedCornerShape(21.dp)
            )
            .clickable {
                pressed = true
                onLaunch()
            }
            .padding(horizontal = 4.dp, vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Real installed app artwork, not generated letter placeholders.
        AndroidView(
            factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.FIT_CENTER } },
            update = { image -> image.setImageDrawable(app.icon) },
            modifier = Modifier.size(43.dp)
        )
        Spacer(Modifier.height(7.dp))
        Text(
            app.label, color = White.copy(alpha = .96f),
            fontSize = 10.sp, lineHeight = 12.sp, maxLines = 2,
            fontWeight = FontWeight.Medium
        )
    }
}
