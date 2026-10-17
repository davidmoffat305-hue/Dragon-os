package com.dragonso.launcher

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.delay

data class AppEntry(val label: String, val intent: Intent, val icon: ImageBitmap)

private val Ember = Color(0xFFFF5A2C)

class MainActivity : ComponentActivity() {
    private var apps by mutableStateOf(emptyList<AppEntry>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackHandler {} // A home screen never exits on Back.
            DragonHome(apps) { startActivity(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        apps = loadApps()
    }

    private fun loadApps(): List<AppEntry> {
        val pm = packageManager
        val query = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        val installed = pm.queryIntentActivities(query, 0)
            .filter { it.activityInfo.packageName != packageName }
            .map { ri ->
                val launch = Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                    .setClassName(ri.activityInfo.packageName, ri.activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                AppEntry(
                    ri.loadLabel(pm).toString(),
                    launch,
                    ri.loadIcon(pm).toBitmap(128, 128).asImageBitmap()
                )
            }
            .sortedBy { it.label.lowercase() }

        val settings = Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val settingsEntry = pm.resolveActivity(settings, 0)?.let {
            AppEntry("Settings", settings, it.loadIcon(pm).toBitmap(128, 128).asImageBitmap())
        }
        return installed + listOfNotNull(settingsEntry)
    }
}

@Composable
fun DragonHome(apps: List<AppEntry>, onOpen: (Intent) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF2B1322), Color(0xFF0D0B12))))
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dragon SO", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Clock()
        }
        Spacer(Modifier.weight(1f))
        Text("Your apps", color = Color(0xFFC9C3D6), fontSize = 20.sp)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp)
        ) {
            items(apps) { Tile(it, onOpen) }
        }
    }
}

@Composable
fun Tile(app: AppEntry, onOpen: (Intent) -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.1f else 1f, label = "tileScale")
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier
            .width(168.dp)
            .scale(scale)
            .background(Color(0x22FFFFFF), shape)
            .border(if (focused) 3.dp else 0.dp, Ember, shape)
            .onFocusChanged { focused = it.isFocused }
            .clickable { onOpen(app.intent) }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(app.icon, contentDescription = null, modifier = Modifier.size(72.dp))
        Text(app.label, color = Color.White, fontSize = 16.sp, maxLines = 1)
    }
}

@Composable
fun Clock() {
    var text by remember { mutableStateOf(now()) }
    LaunchedEffect(Unit) {
        while (true) {
            text = now()
            delay(15_000)
        }
    }
    Text(text, color = Color.White, fontSize = 22.sp)
}

private fun now(): String = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
