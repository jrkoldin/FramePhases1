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
    Demo("Q4 LectureTree") { LectureTree() },
    Demo("Q5 PaddingDemo") { PaddingDemo() },
    Demo("Q6 OffsetDemo") { OffsetDemo() },
    Demo("Q6 OffsetDemoHoisted") { OffsetDemoHoisted() },
    Demo("Q7 ColorDemo") { ColorDemo() },
    Demo("Q7 ColorDemoBackground") { ColorDemoBackground() },
    Demo("Q9 ParallaxSlow") { ParallaxSlow() },
    Demo("Q9 ParallaxFast") { ParallaxFast() },
    Demo("Q10 ScrollToTopNaive") { ScrollToTopNaive() },
    Demo("Q10 ScrollToTopDerived") { ScrollToTopDerived() },
    Demo("Q11 Parent") { Parent() },
    Demo("Q11 ParentLambda") { ParentLambda() },
    Demo("Q13 CyclicDemo") { CyclicDemo() },
    Demo("Q13 CyclicFixed") { CyclicFixed() },
    Demo("Q14 StackBelow") { StackBelowDemo() },
    Demo("Q14 StackBelow 3 children") { StackBelowDemo3() },
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
