package com.kkkk.presentation.main.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

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

private val LocalStempoTypography = staticCompositionLocalOf<StempoTypography> {
    error("No StempoTypography provided")
}

/* StempoTheme
*
* Typo를 변경하고 싶다면 StempoTheme.typography.head1으로 접근하시면 됩니다.
* ex) Text(text = "Stempo Example Typo", style = StempoTheme.typography.head1)
*/
object StempoTheme {
    val typography: StempoTypography
        @Composable get() = LocalStempoTypography.current
}

@Composable
private fun ProvideStempoTypography(typography: StempoTypography, content: @Composable () -> Unit) {
    val provideTypography = remember { typography.copy() }
    provideTypography.update(typography)

    CompositionLocalProvider(
        LocalStempoTypography provides provideTypography,
        content = content
    )
}

@Composable
fun StempoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    // darkTheme을 지원하지 않기에 현재는 LightColorScheme만 사용합니다.
    val colorScheme = LightColorScheme

    SetSystemBarsColor(
        statusBarColor = Color.White,
        navigationBarColor = Color.White,
        darkIcons = true
    )

    val typography = stempoTypography()

    ProvideStempoTypography(typography) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

@Composable
fun SetSystemBarsColor(
    statusBarColor: Color = Color.White,
    navigationBarColor: Color = Color.White,
    darkIcons: Boolean = true
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = darkIcons
        )

        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = darkIcons
        )
    }
}