package ua.ofkkt.composeshowcase.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Лекція 13, слайд «Shapes: поглиблено» — п'ять рівнів форм визначаються один раз у темі.
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // чекбокси, малі чіпи
    small = RoundedCornerShape(8.dp),        // кнопки-чіпи, текстові поля
    medium = RoundedCornerShape(12.dp),      // картки
    large = RoundedCornerShape(16.dp),       // великі картки, sheet-и
    extraLarge = RoundedCornerShape(28.dp),  // діалоги
)
