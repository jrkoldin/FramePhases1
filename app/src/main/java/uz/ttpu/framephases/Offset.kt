package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun Offset() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "Offset: composition") }
    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        Text(
            text = "Offset",
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToPx(), 0) }
                .logPhases("offset"),
        )
    }
}

@Composable
fun OffsetHoisted() {
    var offsetX by remember { mutableStateOf(0.dp) }
    SideEffect { Log.d("Phases", "OffsetDemoHoisted: composition") }
    val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }
    Column {
        Button(onClick = { offsetX = if (offsetX == 0.dp) 80.dp else 0.dp }) {
            Text("Move")
        }
        Text(
            text = "Offset demo",
            modifier = Modifier
                .offset { IntOffset(offsetPx, 0) }
                .logPhases("offset demo"),
        )
    }
}
