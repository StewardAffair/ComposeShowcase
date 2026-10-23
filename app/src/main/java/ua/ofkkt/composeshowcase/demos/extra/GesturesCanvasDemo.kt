package ua.ofkkt.composeshowcase.demos.extra

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ua.ofkkt.composeshowcase.ui.common.CodeSnippet
import ua.ofkkt.composeshowcase.ui.common.DemoColumn
import ua.ofkkt.composeshowcase.ui.common.DemoSection
import ua.ofkkt.composeshowcase.ui.common.Tip
import ua.ofkkt.composeshowcase.ui.common.TipKind
import kotlin.math.roundToInt

/* Доповнення: жести (pointerInput, transformable) і малювання (Canvas, drawBehind). */
@Composable
fun GesturesCanvasDemo() {
    DemoColumn {
        TapSection()
        DragSection()
        TransformSection()
        PaintSection()
        ProgressRingSection()
    }
}

@Composable
private fun TapSection() {
    var last by remember { mutableStateOf("—") }
    DemoSection("1. detectTapGestures", note = "Звичайний clickable знає лише tap. pointerInput дає доступ до double tap, long press і координат.") {
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { last = "tap у (${it.x.roundToInt()}, ${it.y.roundToInt()})" },
                        onDoubleTap = { last = "double tap" },
                        onLongPress = { last = "long press" },
                    )
                },
            contentAlignment = Alignment.Center,
        ) { Text("Остання дія: $last", color = MaterialTheme.colorScheme.onSecondaryContainer) }
    }
}

@Composable
private fun DragSection() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    DemoSection(
        "2. Перетягування",
        note = "Modifier.offset { } з лямбдою читає стан лише у фазі layout — без recomposition на кожен кадр (Лекція 10: відкладене читання).",
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Box(
                Modifier
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offset += dragAmount
                        }
                    },
            )
        }
        OutlinedButton(onClick = { offset = Offset.Zero }) { Text("Повернути на місце") }
        CodeSnippet(
            """
            Modifier
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, drag -> change.consume(); offset += drag }
                }
            """,
        )
    }
}

@Composable
private fun TransformSection() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        rotation += rotationChange
        pan += panChange
    }
    DemoSection("3. transformable: zoom, rotate, pan", note = "Двома пальцями: щипок — масштаб, обертання — поворот.") {
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .transformable(state),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        rotationZ = rotation
                        translationX = pan.x
                        translationY = pan.y
                    }
                    .size(90.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center,
            ) { Text("🖼", style = MaterialTheme.typography.headlineMedium) }
        }
        Text("scale ${"%.2f".format(scale)} · rotation ${rotation.roundToInt()}°", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun PaintSection() {
    val lines = remember { mutableStateListOf<Pair<Offset, Offset>>() }
    val ink = MaterialTheme.colorScheme.primary
    DemoSection("4. Canvas: малювання пальцем", note = "Кожен рух пальця додає відрізок у список стану; Canvas перемальовує всі відрізки.") {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        lines.add((change.position - dragAmount) to change.position)
                    }
                },
        ) {
            lines.forEach { (start, end) ->
                drawLine(color = ink, start = start, end = end, strokeWidth = 8f, cap = StrokeCap.Round)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { lines.clear() }) { Text("Очистити") }
            Text("Відрізків: ${lines.size}", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ProgressRingSection() {
    var progress by remember { mutableFloatStateOf(0.65f) }
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val arc = MaterialTheme.colorScheme.primary
    val highlight = MaterialTheme.colorScheme.tertiaryContainer
    DemoSection("5. Canvas: власний індикатор і drawBehind") {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(120.dp)) {
                    val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    val inset = stroke.width / 2
                    val arcSize = androidx.compose.ui.geometry.Size(size.width - stroke.width, size.height - stroke.width)
                    drawArc(track, startAngle = 0f, sweepAngle = 360f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = stroke)
                    drawArc(arc, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = stroke)
                }
                Text("${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                "drawBehind — фон під текстом",
                modifier = Modifier
                    .drawBehind { drawRoundRect(highlight, cornerRadius = CornerRadius(8.dp.toPx())) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
        Slider(value = progress, onValueChange = { progress = it })
        Tip("У Canvas координати в пікселях: dp переводимо через toPx(). Кольори з теми беремо ДО блоку малювання.", kind = TipKind.Info)
    }
}
