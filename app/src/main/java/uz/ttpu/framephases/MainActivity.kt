package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.framephases.ui.theme.FramePhasesTheme

private class Demo(val title: String, val content: @Composable () -> Unit)

private val demos = listOf(
    Demo("4") { LectureTree() },
    Demo("5") { PaddingDemo() },
    Demo("6") { OffsetDemo() },
    Demo("6") { OffsetDemoHoisted() },
    Demo("7") { Color() },
    Demo("7") { ColorDemoBackground() },
    Demo("9") { ParallaxSlow() },
    Demo("9 ") { ParallaxFast() },
    Demo("10") { ScrollToTopNaive() },
    Demo("10") { ScrollToTopDerived() },
    Demo("11") { Parent() },
    Demo("11") { ParentLambda() },
    Demo("13") { Cyclic() },
    Demo("13") { CyclicFixed() },
    Demo("14") { StackBelowDemo() },
    Demo("14") { StackBelowDemo3() },
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FramePhasesTheme {
                var selected by rememberSaveable { mutableIntStateOf(-1) }
                BackHandler(enabled = selected >= 0) { selected = -1 }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        if (selected < 0) {
                            DemoMenu(onSelect = { selected = it })
                        } else {
                            demos[selected].content()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoMenu(onSelect: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(demos) { index, demo ->
            Text(
                text = demo.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(index) }
                    .padding(16.dp),
            )
            HorizontalDivider()
        }
    }
}
