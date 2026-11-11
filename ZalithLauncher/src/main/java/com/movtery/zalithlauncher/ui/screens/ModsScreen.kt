package com.movtery.zalithlauncher.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.File
import java.net.URL

@Composable
fun InstallFabricApiButton(
    minecraftVersion: String,
    modsDirectory: File
) {
    val coroutineScope = rememberCoroutineScope()
    var statusText by remember { mutableStateOf("تثبيت Fabric API تلقائياً") }
    var isDownloading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!isDownloading) {
                isDownloading = true
                statusText = "جاري البحث والتحميل..."
                coroutineScope.launch {
                    val success = downloadFabricApi(minecraftVersion, modsDirectory)
                    statusText = if (success) "تم تثبيت Fabric API بنجاح ✅" else "فشل التحميل أو الإصدار غير مدعوم ❌"
                    isDownloading = false
                }
            }
        },
        enabled = !isDownloading,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = statusText, color = Color.Black)
    }
}

private suspend fun downloadFabricApi(mcVersion: String, modsDir: File): Boolean = withContext(Dispatchers.IO) {
    try {
        if (!modsDir.exists()) modsDir.mkdirs()

        val apiUrl = "https://modrinth.com[\"$mcVersion\"]&loaders=[\"fabric\"]"
        val jsonResponse = URL(apiUrl).readText()
        val versionsArray = JSONArray(jsonResponse)

        if (versionsArray.length() == 0) return@withContext false

        val latestVersion = versionsArray.getJSONObject(0)
        val filesArray = latestVersion.getJSONArray("files")
        val primaryFile = filesArray.getJSONObject(0)
        val downloadUrl = primaryFile.getString("url")
        val fileName = primaryFile.getString("filename")

        val targetFile = File(modsDir, fileName)
        URL(downloadUrl).openStream().use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
