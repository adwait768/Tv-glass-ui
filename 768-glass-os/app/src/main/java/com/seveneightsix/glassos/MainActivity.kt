package com.seveneightsix.glassos

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Restrained graphite / silver palette. Accent is deliberately low-saturation.
private val Black = Color(0xFF08090B)
private val Graphite = Color(0xFF17191D)
private val Silver = Color(0xFFD8DCE1)
private val Muted = Color(0xFF9299A3)
private val Glass = Color(0x16FFFFFF)
private val Edge = Color(0x24FFFFFF)

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
 val apps = remember {
  val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
  @Suppress("DEPRECATION")
  ctx.packageManager.queryIntentActivities(i, PackageManager.MATCH_ALL)
   .sortedBy { it.loadLabel(ctx.packageManager).toString().lowercase() }
 }
 MaterialTheme(colorScheme = darkColorScheme(primary=Silver, background=Black, surface=Graphite)) {
  Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF25282D),Black,Color(0xFF111316))))) {
   // safeDrawingPadding respects status/navigation bars and display cutouts:
   // centred punch-holes, corner cutouts, and traditional notches.
   Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal=20.dp, vertical=14.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
     Column {
      Text("768", color=Silver, fontSize=13.sp, fontWeight=FontWeight.Bold, letterSpacing=5.sp)
      Text("GLASS OS", color=Muted, fontSize=9.sp, letterSpacing=3.sp)
     }
     Text("DESIGNED TO FEEL LIGHT", color=Muted, fontSize=8.sp, letterSpacing=1.5.sp)
    }
    Spacer(Modifier.height(34.dp))
    when(page) {
     "Apps" -> {
      Text("Library", color=Silver, fontSize=30.sp, fontWeight=FontWeight.Light)
      Text("${apps.size} applications", color=Muted, fontSize=12.sp)
      Spacer(Modifier.height(18.dp))
      LazyVerticalGrid(columns=GridCells.Adaptive(92.dp), verticalArrangement=Arrangement.spacedBy(12.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
       items(apps) { app ->
        val label=app.loadLabel(ctx.packageManager).toString()
        Column(Modifier.background(Glass,RoundedCornerShape(22.dp)).clickable {
         ctx.packageManager.getLaunchIntentForPackage(app.activityInfo.packageName)?.let { ctx.startActivity(it) }
        }.padding(vertical=17.dp, horizontal=8.dp), horizontalAlignment=Alignment.CenterHorizontally) {
         Text(label.take(1).uppercase(), color=Silver, fontSize=23.sp, fontWeight=FontWeight.Light)
         Spacer(Modifier.height(6.dp))
         Text(label, color=Silver.copy(alpha=.86f), fontSize=10.sp, maxLines=2)
        }
       }
      }
     }
     "Device" -> DevicePanel()
     "Settings" -> {
      Text("Appearance", color=Silver, fontSize=30.sp, fontWeight=FontWeight.Light)
      Spacer(Modifier.height(12.dp))
      GlassTile("Monochrome finish","Graphite · silver · soft translucency",Icons.Default.Contrast)
      GlassTile("Display adaptation","Uses Android safe insets for your camera cutout",Icons.Default.Smartphone)
      GlassTile("Motion","Subtle spring transitions",Icons.Default.Animation)
     }
     else -> {
      Text("THURSDAY  /  19 NOV", color=Muted, fontSize=10.sp, letterSpacing=2.sp)
      Spacer(Modifier.height(12.dp))
      Text("Less noise.\nMore focus.", color=Silver, fontSize=39.sp, fontWeight=FontWeight.ExtraLight, lineHeight=43.sp)
      Spacer(Modifier.height(28.dp))
      GlassTile("Application library","All apps, one quiet space",Icons.Default.GridView) { page="Apps" }
      GlassTile("Device overview","Storage and device information",Icons.Default.Memory) { page="Device" }
      GlassTile("Personalize","Finish, layout and motion",Icons.Default.Tune) { page="Settings" }
      Spacer(Modifier.height(18.dp))
      Text("768  /  A NEW INTERFACE", color=Muted, fontSize=9.sp, letterSpacing=2.sp)
     }
    }
    Spacer(Modifier.weight(1f))
    Row(Modifier.fillMaxWidth().background(Color(0xCC17191D),RoundedCornerShape(30.dp)).padding(7.dp), horizontalArrangement=Arrangement.SpaceAround) {
     listOf("Home","Apps","Device","Settings").forEach { item ->
      val selected = page==item
      val scale by animateFloatAsState(if(selected) 1f else .94f, animationSpec=spring(), label="navScale")
      val icon=when(item){"Home"->Icons.Default.Home;"Apps"->Icons.Default.GridView;"Device"->Icons.Default.Memory;else->Icons.Default.Tune}
      Column(Modifier.scale(scale).clickable{page=item}.background(if(selected) Color(0x22FFFFFF) else Color.Transparent,RoundedCornerShape(22.dp)).padding(horizontal=12.dp,vertical=8.dp),horizontalAlignment=Alignment.CenterHorizontally) {
       Icon(icon,null,tint=if(selected) Silver else Muted,modifier=Modifier.size(19.dp))
       Text(item,color=if(selected) Silver else Muted,fontSize=9.sp)
      }
     }
    }
   }
  }
 }
}

@Composable
private fun GlassTile(title:String, subtitle:String, icon:androidx.compose.ui.graphics.vector.ImageVector, onClick:()->Unit = {}) {
 Row(Modifier.fillMaxWidth().padding(bottom=10.dp).background(Glass,RoundedCornerShape(22.dp)).clickable(onClick=onClick).padding(17.dp),verticalAlignment=Alignment.CenterVertically) {
  Icon(icon,null,tint=Silver.copy(alpha=.9f),modifier=Modifier.size(23.dp))
  Spacer(Modifier.width(14.dp))
  Column(Modifier.weight(1f)) {
   Text(title,color=Silver,fontSize=14.sp,fontWeight=FontWeight.Medium)
   Text(subtitle,color=Muted,fontSize=10.sp)
  }
  Icon(Icons.Default.ChevronRight,null,tint=Muted)
 }
}

@Composable
private fun DevicePanel() {
 val s=remember{StatFs(Environment.getDataDirectory().path)}
 val total=s.totalBytes/(1024.0*1024*1024)
 val free=s.availableBytes/(1024.0*1024*1024)
 Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Text("Device",color=Silver,fontSize=30.sp,fontWeight=FontWeight.Light)
  GlassTile("Internal storage","%.1f GB available of %.1f GB".format(free,total),Icons.Default.Storage)
  Text("Read-only information. Android retains control of protected system cleanup and app data.",color=Muted,fontSize=11.sp)
 }
}
