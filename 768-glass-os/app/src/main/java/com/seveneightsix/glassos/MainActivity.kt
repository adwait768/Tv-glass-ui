package com.seveneightsix.glassos

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class AppEntry(val packageName:String,val activityName:String,val label:String,val icon:Drawable)
private data class Palette(val name:String,val base:Color,val accent:Color,val glow:Color,val text:Color)
private val themes=listOf(
 Palette("Ruby",Color(0xFF170912),Color(0xFFE52B55),Color(0xFFFFB8C7),Color(0xFFFFF7F8)),
 Palette("Sapphire",Color(0xFF071321),Color(0xFF398BDF),Color(0xFFB6DDFF),Color(0xFFF2F8FF)),
 Palette("Emerald",Color(0xFF071711),Color(0xFF28A879),Color(0xFFB1F2D8),Color(0xFFF1FFF8)),
 Palette("Amethyst",Color(0xFF140C20),Color(0xFF9861D9),Color(0xFFE0C5FF),Color(0xFFFBF5FF)),
 Palette("Champagne",Color(0xFF1B140B),Color(0xFFC49A55),Color(0xFFFFE4B2),Color(0xFFFFFAF0)),
 Palette("Graphite",Color(0xFF0B0D11),Color(0xFF8995A5),Color(0xFFE0E7F0),Color(0xFFF7F9FC))
)
class MainActivity:ComponentActivity(){
 override fun onCreate(savedInstanceState:Bundle?){
  super.onCreate(savedInstanceState);enableEdgeToEdge()
  window.attributes=window.attributes.apply{preferredRefreshRate=120f}
  setContent{GlassLauncher()}
 }
}
@Composable private fun GlassLauncher(){
 val context=LocalContext.current
 var apps by remember{mutableStateOf<List<AppEntry>>(emptyList())}
 var query by remember{mutableStateOf("")}
 var vertical by remember{mutableStateOf(true)}
 var themeIndex by remember{mutableIntStateOf(0)}
 var themesOpen by remember{mutableStateOf(false)}
 val p=themes[themeIndex]
 LaunchedEffect(Unit){apps=withContext(Dispatchers.IO){
  @Suppress("DEPRECATION")
  context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),0)
   .asSequence().filter{it.activityInfo.packageName!=context.packageName}
   .distinctBy{it.activityInfo.packageName}
   .map{a->AppEntry(a.activityInfo.packageName,a.activityInfo.name,a.loadLabel(context.packageManager).toString(),a.loadIcon(context.packageManager))}
   .sortedBy{it.label.lowercase()}.toList()
 }}
 val shown=apps.filter{it.label.contains(query,true)}
 MaterialTheme(colorScheme=darkColorScheme(primary=p.accent,background=p.base)){
  Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(p.accent.copy(alpha=.23f),p.base,Color.Black)))){
   Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal=16.dp,vertical=10.dp)){
    Row(Modifier.fillMaxWidth().padding(vertical=8.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
     Text("768 / GLASS",color=p.text,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
     Text(apps.size.toString()+" APPS",color=p.glow,fontSize=9.sp,letterSpacing=1.5.sp)
    }
    Row(Modifier.fillMaxWidth().padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically){
     Text("Apps",color=p.text,fontSize=29.sp,fontWeight=FontWeight.Light,modifier=Modifier.weight(1f))
     Pill(if(vertical)"Vertical" else "Horizontal",p){vertical=!vertical}
     Spacer(Modifier.width(7.dp))
     Pill(if(themesOpen)"Done" else "Themes",p){themesOpen=!themesOpen}
    }
    if(themesOpen){
     LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp),contentPadding=PaddingValues(vertical=5.dp)){
      items(themes){t->
       Column(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha=.08f)).border(1.dp,if(t.name==p.name)t.glow else Color.White.copy(alpha=.2f),RoundedCornerShape(14.dp)).clickable{themeIndex=themes.indexOf(t)}.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){
        Box(Modifier.size(23.dp).background(Brush.linearGradient(listOf(t.glow,t.accent,t.base)),CircleShape))
        Spacer(Modifier.height(5.dp));Text(t.name,color=p.text,fontSize=9.sp)
       }
      }
     }
    }
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha=.08f)).border(1.dp,Color.White.copy(alpha=.22f),RoundedCornerShape(16.dp)).padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically){
     Text("⌕",color=p.glow,fontSize=22.sp)
     TextField(value=query,onValueChange={query=it},placeholder={Text("Search apps",color=p.glow.copy(alpha=.7f),fontSize=13.sp)},singleLine=true,modifier=Modifier.weight(1f),colors=TextFieldDefaults.colors(focusedTextColor=p.text,unfocusedTextColor=p.text,focusedContainerColor=Color.Transparent,unfocusedContainerColor=Color.Transparent,focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent,cursorColor=p.glow))
    }
    Spacer(Modifier.height(10.dp))
    AnimatedContent(targetState=vertical,label="layoutTransition",transitionSpec={fadeIn(spring(stiffness=400f)) togetherWith fadeOut(spring(stiffness=450f))}){isVertical->
     if(isVertical){
      LazyColumn(Modifier.fillMaxSize(),verticalArrangement=Arrangement.spacedBy(7.dp),contentPadding=PaddingValues(bottom=20.dp)){
       items(shown,key={it.packageName}){app->AppRow(app,p){openApp(context,app)}}
      }
     }else{
      LazyRow(Modifier.fillMaxSize(),horizontalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=20.dp)){
       items(shown,key={it.packageName}){app->AppTile(app,p){openApp(context,app)}}
      }
     }
    }
   }
  }
 }
}
private fun openApp(c:android.content.Context,a:AppEntry){
 val i=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(ComponentName(a.packageName,a.activityName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
 val opts=ActivityOptions.makeCustomAnimation(c,android.R.anim.fade_in,android.R.anim.fade_out).toBundle()
 runCatching{c.startActivity(i,opts)}
}
@Composable private fun Pill(label:String,p:Palette,onClick:()->Unit){
 Text(label,color=p.text,fontSize=10.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.clip(RoundedCornerShape(30.dp)).background(Color.White.copy(alpha=.10f)).border(1.dp,p.glow.copy(alpha=.45f),RoundedCornerShape(30.dp)).clickable(onClick=onClick).padding(horizontal=12.dp,vertical=9.dp))
}
@Composable private fun AppRow(a:AppEntry,p:Palette,onClick:()->Unit){
 Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(Brush.horizontalGradient(listOf(Color.White.copy(alpha=.16f),p.accent.copy(alpha=.10f),Color.White.copy(alpha=.04f)))).border(1.dp,Color.White.copy(alpha=.24f),RoundedCornerShape(17.dp)).clickable(onClick=onClick).padding(horizontal=12.dp,vertical=9.dp),verticalAlignment=Alignment.CenterVertically){
  AndroidView(factory={ImageView(it).apply{scaleType=ImageView.ScaleType.FIT_CENTER}},update={it.setImageDrawable(a.icon)},modifier=Modifier.size(39.dp))
  Spacer(Modifier.width(13.dp));Text(a.label,color=p.text,fontSize=13.sp,fontWeight=FontWeight.Medium,modifier=Modifier.weight(1f))
  Text("›",color=p.glow,fontSize=22.sp)
 }
}
@Composable private fun AppTile(a:AppEntry,p:Palette,onClick:()->Unit){
 Column(Modifier.width(92.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color.White.copy(alpha=.20f),p.accent.copy(alpha=.15f),p.base.copy(alpha=.65f)))).border(1.dp,Brush.linearGradient(listOf(Color.White.copy(alpha=.75f),p.glow.copy(alpha=.4f),Color.White.copy(alpha=.15f))),RoundedCornerShape(20.dp)).clickable(onClick=onClick).padding(horizontal=5.dp,vertical=15.dp),horizontalAlignment=Alignment.CenterHorizontally){
  AndroidView(factory={ImageView(it).apply{scaleType=ImageView.ScaleType.FIT_CENTER}},update={it.setImageDrawable(a.icon)},modifier=Modifier.size(45.dp))
  Spacer(Modifier.height(8.dp));Text(a.label,color=p.text,fontSize=10.sp,maxLines=2)
 }
}
