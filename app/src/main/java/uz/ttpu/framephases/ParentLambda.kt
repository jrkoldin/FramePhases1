package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ParentLambda() {
    var count by remember { mutableIntStateOf(0) }
    SideEffect { Log.d("Skip", "ParentLambda: composition") }
    Column {
        Button(onClick = { count++ }) { Text("Add") }
        ChildLambda(count = { count })
        SiblingLambda()
    }
}

@Composable
fun ChildLambda(count: () -> Int) {
    SideEffect { Log.d("Skip", "ChildLambda: composition") }
    Text("Count: ${count()}")
}

@Composable
fun SiblingLambda() {
    SideEffect { Log.d("Skip", "SiblingLambda: composition") }
    Text("I never change")
}
