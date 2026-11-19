package com.seveneightsix.glassos

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF07131F)
private val Aqua = Color(0xFF8BE9F2)
private val Glass = Color(0x2EFFFFFF)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
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
  ctx.packageManager.queryIntentActivities(i, PackageManager.MATCH_ALL).sortedBy { it.loadLabel(ctx.packageManager).toString().lowercase() }
 }
 MaterialTheme(colorScheme = darkColorScheme(primary=Aqua, background=Night)) {
  Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF15364C),Night,Color(0xFF202746))))) {
   Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) {
     Column { Text("768 GLASS", color=Color.White, fontWeight=FontWeight.Bold, letterSpacing=3.sp); Text("A calmer way to use your phone", color=Aqua, fontSize=11.sp) }
     Text("GLASS OS", color=Color.White.copy(alpha=.8f), fontSize=12.sp)
    }
    Spacer(Modifier.height(28.dp))
    when(page) {
     "Apps" -> {
      Text("Your apps", color=Color.White, fontSize=28.sp, fontWeight=FontWeight.SemiBold)
      Spacer(Modifier.height(12.dp))
      LazyVerticalGrid(columns=GridCells.Adaptive(100.dp), verticalArrangement=Arrangement.spacedBy(10.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
       items(apps) { app ->
        val label=app.loadLabel(ctx.packageManager).toString()
        Column(Modifier.background(Glass,RoundedCornerShape(20.dp)).clickable {
         ctx.packageManager.getLaunchIntentForPackage(app.activityInfo.packageName)?.let { ctx.startActivity(it) }
        }.padding(14.dp), horizontalAlignment=Alignment.CenterHorizontally) {
         Text(label.take(1).uppercase(), color=Aqua, fontSize=25.sp, fontWeight=FontWeight.Bold)
         Text(label, color=Color.White, fontSize=11.sp, maxLines=2)
        }
       }
      }
     }
     "Device" -> DevicePanel()
     "Settings" -> {
      Text("Glass settings", color=Color.White, fontSize=28.sp)
      Text("Next: wallpapers, icon scale, glass strength and animation controls.", color=Color.White.copy(alpha=.75f), modifier=Modifier.padding(top=12.dp))
     }
     else -> {
      Text("Good morning.", color=Color.White.copy(alpha=.8f), fontSize=16.sp)
      Text("Make space for what matters.", color=Color.White, fontSize=31.sp, fontWeight=FontWeight.SemiBold, lineHeight=37.sp)
      Spacer(Modifier.height(24.dp))
      Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
       Tile("Apps","Launch your apps",Icons.Default.Apps,Modifier.weight(1f)){page="Apps"}
       Tile("Device","Storage overview",Icons.Default.Storage,Modifier.weight(1f)){page="Device"}
      }
      Spacer(Modifier.height(12.dp))
      Tile("Personalize","Wallpaper and appearance",Icons.Default.Palette,Modifier.fillMaxWidth()){page="Settings"}
     }
    }
    Spacer(Modifier.weight(1f))
    Row(Modifier.fillMaxWidth().background(Color(0x553D6378),RoundedCornerShape(28.dp)).padding(8.dp), horizontalArrangement=Arrangement.SpaceAround) {
     listOf("Home","Apps","Device","Settings").forEach { item ->
      val icon=when(item){"Home"->Icons.Default.Home;"Apps"->Icons.Default.Apps;"Device"->Icons.Default.Memory;else->Icons.Default.Tune}
      Column(Modifier.clickable{page=item}.padding(horizontal=8.dp,vertical=6.dp),horizontalAlignment=Alignment.CenterHorizontally) {
       Icon(icon,null,tint=if(page==item) Aqua else Color.White.copy(alpha=.7f))
       Text(item,color=if(page==item) Aqua else Color.White.copy(alpha=.7f),fontSize=10.sp)
      }
     }
    }
   }
  }
 }
}

@Composable
private fun Tile(title:String, subtitle:String, icon:androidx.compose.ui.graphics.vector.ImageVector, modifier:Modifier=Modifier, onClick:()->Unit) {
 Row(modifier.background(Glass,RoundedCornerShape(23.dp)).clickable(onClick=onClick).padding(15.dp),verticalAlignment=Alignment.CenterVertically) {
  Icon(icon,null,tint=Aqua,modifier=Modifier.size(27.dp)); Spacer(Modifier.width(10.dp))
  Column { Text(title,color=Color.White,fontWeight=FontWeight.SemiBold); Text(subtitle,color=Color.White.copy(alpha=.7f),fontSize=10.sp) }
 }
}

@Composable
private fun DevicePanel() {
 val s=remember{StatFs(Environment.getDataDirectory().path)}
 val total=s.totalBytes/(1024.0*1024*1024)
 val free=s.availableBytes/(1024.0*1024*1024)
 Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
  Text("Device overview",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.SemiBold)
  Tile("Internal storage","%.1f GB free of %.1f GB".format(free,total),Icons.Default.Storage,Modifier.fillMaxWidth()){}
  Text("Read-only overview. Android controls protected app-cache and system cleanup.",color=Color.White.copy(alpha=.7f),fontSize=12.sp)
 }
}
