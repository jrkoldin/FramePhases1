# Practice 7 - Jetpack Compose Phases

Answers in my own words. For the hands-on questions the "expected" lines are what I predicted before running; I check them in Logcat and fix anything that is different.

## Question 1 - The three phases of a frame

**(a)** Order: Composition, then Layout, then Drawing.
- Composition decides what to show. Its output is the UI tree (a tree of nodes with their modifiers and data).
- Layout decides where to put it. Its output is the size and position of every node.
- Drawing decides how it looks. Its output is the real drawing on the canvas.

**(b)** Layout has two steps: measure and place.

**(c)** A LazyColumn composes only the items that are visible. How many items are visible depends on how much space the parent gives it, and that is known only after the layout phase of the parent. So the children are composed after the parent's layout. Example: a chat screen with 1000 messages. On a phone only about 8 are visible, so only about 8 are composed. On a tablet or after rotation more fit, so more are composed. BoxWithConstraints is the same idea: it picks its content from the available width (for example a phone layout or a tablet layout).

**(d)** Compose is fast because it skips work. It remembers which state was read in which phase, so when a state changes it only reruns the code that read it. Composables whose inputs did not change are skipped, and phases that have nothing to do are skipped too.

## Question 2 - Walk the layout algorithm

Tree: Row contains Image (48x48) and Column; Column contains Text A (100x20) and Text B (80x20).

**(a)** Order of events:
1. measure Row
2. measure Image (48x48)
3. measure Column
4. measure Text A (100x20)
5. measure Text B (80x20)
6. Column decides its size, Row decides its size
7. place Row
8. place Image
9. place Column
10. place Text A
11. place Text B

**(b)** Sizes:
- Column: width = max(100, 80) = 100, height = 20 + 20 = 40, so 100x40.
- Row: width = 48 + 100 = 148, height = max(48, 40) = 48, so 148x48.

Positions relative to the top-left corner of the Row:
- Row: (0, 0)
- Image: (0, 0)
- Column: (48, 0)
- Text A: (48, 0)
- Text B: (48, 20)

**(c)** Every node is measured once and placed once. The lecture stresses this because the time is then linear: more nodes means proportionally more time. If each node was measured twice on every level, the work would double on each level of the tree, so for a tree with depth d it would be about 2^d. That is exponential and gets very slow for deep trees.

**(d)** The Composition phase produced this tree. Each node holds what it is (Row, Image, Text), its modifiers, its data (for example the text string) and its children.

## Question 3 - State reads and restart scopes

**(a)** Yes, they are equivalent. The `by` delegate is just a shorter way to write `.value`. In both cases the read happens when the state is used (when `padding` is passed into `Modifier.padding(...)`), not when the state is created. It happens in the body of the composable, so it is a composition read.

**(b)** Text("A"): offsetX is read inside the `offset { }` lambda, which runs in the layout phase (placement). When offsetX changes only layout (placement) runs again, there is no recomposition.
Text("B"): offsetX is read in the body of the composable (the `offsetPx` line), so it is read in the composition phase. When offsetX changes the function is recomposed, and then layout and drawing run again.

**(c)** A restart scope is a block of code that Compose can run again on its own when a state that it read changes. For a read made directly in the body of a composable function, the restart scope is that composable function itself.

## Question 4 - the first frame

Expected Logcat order (tag:Phases):
1. `LectureTree: composition`
2. measure: `Row`, `Image`, `Column`, `Text A`, `Text B` (all `layout/measure`)
3. place: `Row`, `Image`, `Column`, `Text A`, `Text B` (all `layout/place`)
4. draw: `Row`, `Image`, `Column`, `Text A`, `Text B` (all `draw`)

The measure order is the same as in the lecture trace (Row, Image, Column, Text A, Text B), the only difference is that the Image is 40dp instead of 48 px. After rotation everything runs again: composition, layout and drawing. The reason is that rotation is a configuration change, so the Activity is destroyed and created again, and `setContent` builds the whole screen from the start.

## Question 5 - state read in composition

Prediction after one tap: `PaddingDemo: composition`, `padding demo: layout/measure`, `padding demo: layout/place`, `padding demo: draw`. All four lines, because `padding` is read while the modifier is built, which is the composition phase. The composable runs again, then the size changes so layout runs, then it is drawn again. The same happens on every tap.

## Question 6 - state read in layout

Prediction after tap on Move: no composition line and no `layout/measure`. Only `offset demo: layout/place` appears, and most likely `offset demo: draw` too because the position changed. The `offset { }` lambda is read in the placement step, and measure and placement have separate restart scopes, so measure is not rerun.

Experiment (read moved above the Text): now `OffsetDemoHoisted: composition` appears, because `offsetPx` is read in the body of the composable. The lambda is created again with a new Int, so the modifier changes, and then `layout/measure`, `layout/place` and `draw` appear as well. So the same state costs much more when it is read earlier.

## Question 7 - state read in drawing

With Canvas: after a tap only `color demo: draw` appears. `color` is read inside the draw lambda, so only the drawing phase runs. No composition, no measure, no place.

With `Box(...background(color))`: now `ColorDemoBackground: composition` appears and then `color demo: draw`. Layout is normally skipped because the size did not change. The `background(Color)` overload gets the value when the modifier is built, so it is a composition read.

What this tells me: it is not important where the state is stored (it is the same `remember` in both demos). What matters is where it is read. The read decides which phases run.

## Question 8 - which phase re-runs?

| | Read in | Composition | Layout | Drawing |
|---|---|---|---|---|
| (a) `padding(p)` | composition | runs | only if size or position changed (it does, padding changes size) | runs after layout changed |
| (b) `offset { IntOffset(p, 0) }` | layout (placement) | skipped | runs (placement) | only if position changed (it does) |
| (c) `offset(p)` Dp overload | composition | runs | only if size or position changed (position changes) | runs |
| (d) `Canvas { drawRect(c) }` | drawing | skipped | skipped | runs |
| (e) `Layout { ... s ... }` | layout (measure) | skipped | runs | only if size or position changed |
| (f) `drawWithContent { c }` | drawing | skipped | skipped | runs |

For a value that changes on every frame of an animation I would use (d) or (f), reading it in the draw phase, if the value is only something visual (color, alpha). If the value is a position I would use (b). The reason is that they skip composition (and layout for d and f), so the least work is done on every frame.

## Question 9 - Parallax

Slow version: the scroll offset is read while the modifier is built, so on every scroll event it triggers composition, layout and drawing again.
Fast version: the read is inside `offset { }`, so on every scroll event only layout (placement) and drawing run.

Expected numbers: `slow: composition #...` grows into the hundreds during 3 seconds of scrolling, `fast: composition #1` stays (almost) at the start value.

## Question 10 - derivedStateOf

Naive: `listState.firstVisibleItemIndex` is read in composition, so the function is recomposed every time the first visible item changes. Scrolling through about 30 items gives about 30 compositions.
Derived: the composition count stays at about 2.

The difference is so large because the button changes only twice (hidden to shown and back), but the naive version is invalidated by every change of the index, even when the result of `index > 0` stays the same. `derivedStateOf` watches the index but only tells the reader when the Boolean result really changes.

The Top button works with `rememberCoroutineScope` and `listState.animateScrollToItem(0)`.

## Question 11 - Skipping and deferred reads

1. Three taps on Add: `Parent: composition` 3 times and `Child: composition` 3 times. `Sibling` 0 times, because its inputs did not change, so it is skipped.
2. With the lambda: `Parent` 0 times, `Child` 3 times, `Sibling` 0 times.
3. The state is now read in the body of Child, so the restart scope that recorded the read is Child, not Parent. When `count` changes only the Child scope is invalidated and rerun, and Parent does not run again.

## Question 12 - Two frames for one layout

Image height is 360 px.

Frame 1:
- composition reads `imageHeightPx = 0`, so the top padding of the text is 0
- in the layout phase the image is measured and `onSizeChanged` writes `imageHeightPx = 360`
- a recomposition is scheduled, because that state was already read in composition
- the frame is drawn with the text at the top, on top of the image (wrong picture)

Frame 2:
- composition reads `imageHeightPx = 360`, so the top padding is 360 px converted to dp
- in layout the size is still 360, `onSizeChanged` does not fire again, so nothing is scheduled
- the text is drawn below the image (correct picture)

**(a)** `toDp()` is a warning sign because it takes a number that was measured in the layout phase and uses it as an input of the composition. The data goes backwards, from a later phase to an earlier one.
**(b)** The pattern: read a state in an early phase, then write that state in a later phase, which makes the early phase run again.
**(c)** It breaks the unidirectional data flow principle: the phases must go one way, Composition, Layout, Drawing.

## Question 13 - Reproduce the loop

CyclicDemo expected log (tag:Cycle):
```
composition #1: imageHeightPx=0
onSizeChanged: height=<120dp in pixels>
composition #2: imageHeightPx=<120dp in pixels>
```
So there are 2 compositions, and the first picture is wrong.

CyclicFixed: only `fixed: composition #1`. One composition, no wrong frame.

The single source of truth in the fixed version is the Column. It measures the box, learns its height and places the Text right below it, all in one layout pass, without any state.

## Question 14 - StackBelow

Log with two children (tag:StackBelow):
```
measure child
measure child
place children
```
Each child is measured exactly once and only then the children are placed. That is the one pass rule: measure all, decide the size, place all, and nobody is measured twice.

With a third child: 3 `measure child` lines and then 1 `place children`. The number of measure lines is the number of children, so the cost grows linearly.
