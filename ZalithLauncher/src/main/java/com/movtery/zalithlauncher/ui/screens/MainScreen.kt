import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun VoxelosRamMonitor() {
    val context = LocalContext.current
    var ramText by remember { mutableStateOf("جاري فحص الذاكرة...") }

    // تحديث قراءة الذاكرة كل ثانيتين
    LaunchedEffect(Unit) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        while (true) {
            activityManager.getMemoryInfo(memoryInfo)
            val availableMegs = memoryInfo.availMem / (1024 * 1024)
            val totalMegs = memoryInfo.totalMem / (1024 * 1024)
            val usedMegs = totalMegs - availableMegs
            ramText = "RAM: $usedMegs MB / $totalMegs MB"
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .background(Color(0xFF161B22), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = ramText,
            color = Color(0xFF00E5FF), // لون Voxelos السماوي
            style = MaterialTheme.typography.labelMedium
        )
    }
}
