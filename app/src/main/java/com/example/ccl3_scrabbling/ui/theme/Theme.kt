package com.example.ccl3_scrabbling.ui.theme


import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.ccl3_scrabbling.data.FontRepository


private val DarkColorScheme = darkColorScheme(
    primary = BrownDark,
    onPrimary = BrownLight,
    secondary = GreyDark,
    onSecondary = Color.Black,
    tertiary = RedLight,
    onTertiary = Color.Black,
    background = Color.Black,
    onBackground = BrownLight,
    surface = BrownLight,
    onSurface = Color.Black,
)


private val LightColorScheme = lightColorScheme(
    primary = BrownLight,
    onPrimary = BrownDark,
    secondary = GreyLight,
    onSecondary = Color.Black,
    tertiary = RedLight,
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = BrownDark,
    surface = BrownDark,
    onSurface = Color.Black,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


@Composable
fun Ccl3scrabblingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    // Add typography parameter with default value
    typography: Typography = Typography,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    // Observe font changes
    val currentFont by remember { FontRepository.currentFontIndex }


    // Create dynamic typography based on selected font
    val currentTypography = remember(currentFont) {
        Typography(
            displayLarge = typography.displayLarge.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            displayMedium = typography.displayMedium.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            displaySmall = typography.displaySmall.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            headlineLarge = typography.headlineLarge.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            headlineMedium = typography.headlineMedium.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            headlineSmall = typography.headlineSmall.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            titleLarge = typography.titleLarge.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            titleMedium = typography.titleMedium.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            titleSmall = typography.titleSmall.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            bodyLarge = typography.bodyLarge.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            bodyMedium = typography.bodyMedium.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            bodySmall = typography.bodySmall.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            labelLarge = typography.labelLarge.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            labelMedium = typography.labelMedium.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            ),
            labelSmall = typography.labelSmall.copy(
                fontFamily = FontRepository.getCurrentFont().fontFamily
            )
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = currentTypography,
        content = content
    )
}
