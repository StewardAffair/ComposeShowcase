package ua.ofkkt.composeshowcase.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/*
 * Лекція 13, слайд «Типографіка: кастомні шрифти».
 *
 * Щоб підключити власний шрифт:
 *   1. покладіть файли у app/src/main/res/font/ (brand_regular.ttf, brand_bold.ttf)
 *   2. val BrandFamily = FontFamily(
 *          Font(R.font.brand_regular, FontWeight.Normal),
 *          Font(R.font.brand_bold, FontWeight.Bold),
 *      )
 *   3. замініть BrandDisplayFamily нижче на BrandFamily.
 *
 * Тут, щоб проєкт не залежав від файлів шрифтів, «брендом» виступає системний Serif
 * для заголовків — різницю з body (SansSerif) добре видно на екрані «Типографіка».
 */
val BrandDisplayFamily: FontFamily = FontFamily.Serif

private val Base = Typography()

val AppTypography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = BrandDisplayFamily),
    displayMedium = Base.displayMedium.copy(fontFamily = BrandDisplayFamily),
    displaySmall = Base.displaySmall.copy(fontFamily = BrandDisplayFamily),
    headlineLarge = Base.headlineLarge.copy(fontFamily = BrandDisplayFamily, fontWeight = FontWeight.SemiBold),
    headlineMedium = Base.headlineMedium.copy(fontFamily = BrandDisplayFamily, fontWeight = FontWeight.SemiBold),
    headlineSmall = Base.headlineSmall.copy(fontFamily = BrandDisplayFamily, fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall,
    bodyLarge = Base.bodyLarge,
    bodyMedium = Base.bodyMedium,
    bodySmall = Base.bodySmall,
    labelLarge = Base.labelLarge,
    labelMedium = Base.labelMedium,
    labelSmall = Base.labelSmall,
)
