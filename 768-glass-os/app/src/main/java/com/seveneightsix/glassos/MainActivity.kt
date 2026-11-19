package com.seveneightsix.glassos

import android.content.Intent
import android.content.ComponentName
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class AppEntry(val packageName:String, val label:String, val icon:Drawable, val launchActivity:String = "")

// 768 Glass OS — ruby-glass luxury interface.
private val Void = Color(0xFF090607)
private val Panel = Color(0xFF1B1013)
private val Glass = Color(0xFF251317)
private val GlassEdge = Color(0xFFFFC3D0)
private val GlassGlow = Color(0xFFB83B5A)
private val White = Color(0xFFFFF4F3)
private val Silver = Color(0xFFE0BFC2)
private val Dim = Color(0xFF9A7B80)
private val Hairline = Color(0xFF48242D)
private val Ruby = Color(0xFFE11D48)
private val RubyDeep = Color(0xFF7F102B)
private val RoseGold = Color(0xFFD9A0A7)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  enableEdgeToEdge()
  setContent { GlassOS() }
 }
}

@Composable
fun GlassOS() {
 var page by remember { mutableStateOf("Home") }
 val ctx = androidx.compose.ui.platform.LocalContext.current
 var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
 LaunchedEffect(Unit) {
  apps = withContext(Dispatchers.IO) {
   val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
   @Suppress("DEPRECATION")
   ctx.packageManager.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
    .asSequence()
    .filter { it.activityInfo.packageName != ctx.packageName }
    .distinctBy { it.activityInfo.packageName }
    .map { info ->
     AppEntry(
      packageName = info.activityInfo.packageName,
      label = info.loadLabel(ctx.packageManager).toString(),
      icon = info.loadIcon(ctx.packageManager),
      launchActivity = info.activityInfo.name
     )
    }
    .sortedBy { it.label.lowercase() }
    .toList()
  }
 }
 val time = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()) }
 val date = remember { java.text.SimpleDateFormat("EEE  ·  dd MMM", java.util.Locale.getDefault()).format(java.util.Date()).uppercase() }

 MaterialTheme(colorScheme = darkColorScheme(primary=Ruby, secondary=RoseGold, background=Void, surface=Panel)) {
  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF16090D), Void, Color(0xFF090607))))) {
   // Android safe drawing insets protect the content around punch-hole cameras and notches.
   Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal=22.dp, vertical=14.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
     Row(verticalAlignment=Alignment.CenterVertically) {
      Box(Modifier.size(8.dp).background(Brush.linearGradient(listOf(Color(0xFFFFA3B5), Ruby, RubyDeep)), CircleShape).border(1.dp, Color(0xFFFFC4CE).copy(alpha=.55f), CircleShape))
      Spacer(Modifier.width(9.dp))
      Text("768 / GLASS", color=White, fontSize=10.sp, fontWeight=FontWeight.SemiBold, letterSpacing=2.4.sp)
     }
     Text("A17 / RUBY EDITION", color=Dim, fontSize=9.sp, letterSpacing=1.7.sp)
    }
    Spacer(Modifier.height(38.dp))
    when(page) {
     "Apps" -> {
      Text("APP INDEX", color=Dim, fontSize=9.sp, letterSpacing=2.4.sp)
      Spacer(Modifier.height(7.dp))
      Text("Library", color=White, fontSize=34.sp, fontWeight=FontWeight.Light)
      Text("${apps.size} INSTALLED", color=Dim, fontSize=9.sp, letterSpacing=1.5.sp)
      Spacer(Modifier.height(18.dp))
      LazyVerticalGrid(columns=GridCells.Adaptive(92.dp), verticalArrangement=Arrangement.spacedBy(10.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
       items(apps, key = { it.packageName }) { app ->
        Column(
         Modifier.fillMaxWidth()
          .background(Brush.verticalGradient(listOf(Color(0x66FFFFFF), Color(0x332F101B), Color(0x66200912))), RoundedCornerShape(20.dp))
          .border(1.dp, Brush.linearGradient(listOf(GlassEdge.copy(alpha=.62f), Color(0x44FFFFFF), GlassGlow.copy(alpha=.35f))), RoundedCornerShape(20.dp))
          .clickable {
           val launch = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(ComponentName(app.packageName, app.launchActivity))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           runCatching { ctx.startActivity(launch) }
          }
          .padding(vertical=14.dp,horizontal=7.dp),
         horizontalAlignment=Alignment.CenterHorizontally
        ) {
         AndroidView(
          factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.FIT_CENTER; setPadding(2,2,2,2) } },
          update = { it.setImageDrawable(app.icon) },
          modifier = Modifier.size(42.dp)
         )
         Spacer(Modifier.height(8.dp))
         Text(app.label,color=White.copy(alpha=.94f),fontSize=10.sp,maxLines=2)
        }
       }
      }
     }
     "Device" -> DevicePanel()
     "Settings" -> {
      Text("SYSTEM / STYLE",color=Dim,fontSize=9.sp,letterSpacing=2.4.sp)
      Spacer(Modifier.height(7.dp))
      Text("Appearance",color=White,fontSize=34.sp,fontWeight=FontWeight.Light)
      Spacer(Modifier.height(20.dp))
      DetailRow("01","Ruby glass","Translucent crimson · rose gold · obsidian")
      DetailRow("02","Display safe area","Camera cutout-aware layout")
      DetailRow("03","Motion language","Quiet spring transitions")
      DetailRow("04","Surface finish","Frosted layers · reflective glass edges")
     }
     else -> {
      Text(date,color=Dim,fontSize=9.sp,letterSpacing=2.1.sp)
      Spacer(Modifier.height(10.dp))
      Text(time,color=White,fontSize=68.sp,fontWeight=FontWeight.ExtraLight,letterSpacing=(-3).sp)
      Spacer(Modifier.height(5.dp))
      Text("A richer digital atmosphere.",color=Silver,fontSize=15.sp,fontWeight=FontWeight.Light)
      Spacer(Modifier.height(32.dp))
      Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
      Spacer(Modifier.height(16.dp))
      Text("QUICK ACCESS  /  RUBY GLASS",color=Dim,fontSize=9.sp,letterSpacing=2.2.sp)
      Spacer(Modifier.height(12.dp))
      QuickRow("Applications","Open app index",Icons.Default.GridView) { page="Apps" }
      QuickRow("Device status","Storage overview",Icons.Default.Memory) { page="Device" }
      QuickRow("Personalize","Interface preferences",Icons.Default.Tune) { page="Settings" }
      Spacer(Modifier.weight(1f))
      Text("DESIGNED FOR CLARITY  /  01",color=Dim,fontSize=8.sp,letterSpacing=1.8.sp)
     }
    }
    if(page!="Home") Spacer(Modifier.weight(1f))
    Row(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0x66FFFFFF),Color(0x552F1820),Color(0x9911080D))),RoundedCornerShape(26.dp)).border(1.dp,Color(0x99FFFFFF),RoundedCornerShape(26.dp)).padding(6.dp),horizontalArrangement=Arrangement.SpaceAround) {
     listOf("Home","Apps","Device","Settings").forEach { item ->
      val selected=page==item
      val scale by animateFloatAsState(if(selected) 1f else .96f,animationSpec=spring(stiffness=500f),label="navScale")
      val icon=when(item){"Home"->Icons.Default.Home;"Apps"->Icons.Default.GridView;"Device"->Icons.Default.Memory;else->Icons.Default.Tune}
      Column(Modifier.scale(scale).clickable{page=item}.background(if(selected) Color(0x667A1B36) else Color.Transparent,RoundedCornerShape(20.dp)).padding(horizontal=13.dp,vertical=9.dp),horizontalAlignment=Alignment.CenterHorizontally) {
       Icon(icon,null,tint=if(selected) White else Dim,modifier=Modifier.size(18.dp))
       Text(item,color=if(selected) White else Dim,fontSize=9.sp)
      }
     }
    }
   }
  }
 }
}

@Composable
private fun QuickRow(title:String,subtitle:String,icon:androidx.compose.ui.graphics.vector.ImageVector,onClick:()->Unit) {
 Row(Modifier.fillMaxWidth().padding(bottom=9.dp).background(Brush.verticalGradient(listOf(Color(0x55FFFFFF),Color(0x443A1A25),Color(0xAA13090E))),RoundedCornerShape(17.dp)).border(1.dp,Color(0x88FFFFFF),RoundedCornerShape(17.dp)).clickable(onClick=onClick).padding(horizontal=15.dp,vertical=15.dp),verticalAlignment=Alignment.CenterVertically) {
  Icon(icon,null,tint=Silver,modifier=Modifier.size(20.dp))
  Spacer(Modifier.width(13.dp))
  Column(Modifier.weight(1f)) {
   Text(title,color=White,fontSize=13.sp,fontWeight=FontWeight.Medium)
   Text(subtitle,color=Dim,fontSize=10.sp)
  }
  Icon(Icons.Default.ArrowOutward,null,tint=Dim,modifier=Modifier.size(15.dp))
 }
}

@Composable
private fun DetailRow(number:String,title:String,description:String) {
 Column(Modifier.fillMaxWidth().padding(vertical=15.dp)) {
  Row(verticalAlignment=Alignment.CenterVertically) {
   Text(number,color=Dim,fontSize=9.sp,letterSpacing=1.sp)
   Spacer(Modifier.width(15.dp))
   Column {
    Text(title,color=White,fontSize=14.sp)
    Text(description,color=Dim,fontSize=10.sp)
   }
  }
  Spacer(Modifier.height(14.dp))
  Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
 }
}

@Composable
private fun DevicePanel() {
 val stat=remember{StatFs(Environment.getDataDirectory().path)}
 val total=stat.totalBytes/(1024.0*1024*1024)
 val free=stat.availableBytes/(1024.0*1024*1024)
 Column {
  Text("DEVICE / 01",color=Dim,fontSize=9.sp,letterSpacing=2.4.sp)
  Spacer(Modifier.height(7.dp))
  Text("Overview",color=White,fontSize=34.sp,fontWeight=FontWeight.Light)
  Spacer(Modifier.height(22.dp))
  Text("INTERNAL STORAGE",color=Dim,fontSize=9.sp,letterSpacing=1.8.sp)
  Spacer(Modifier.height(8.dp))
  Text("%.1f GB".format(free),color=White,fontSize=42.sp,fontWeight=FontWeight.Light)
  Text("AVAILABLE OF %.1f GB".format(total),color=Silver,fontSize=9.sp,letterSpacing=1.4.sp)
  Spacer(Modifier.height(20.dp))
  Box(Modifier.fillMaxWidth().height(1.dp).background(Hairline))
  Spacer(Modifier.height(14.dp))
  Text("Read-only device information. Android retains control of protected app data and system cleanup.",color=Dim,fontSize=11.sp)
 }
}
