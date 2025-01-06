package com.kkkk.presentation.main.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.kkkk.stempo.presentation.R

val PretendardSemiBold = FontFamily(Font(R.font.pretendard_semibold, FontWeight.SemiBold))
val PretendardMedium = FontFamily(Font(R.font.pretendard_medium, FontWeight.Medium))

@Stable
class StempoTypography internal constructor(
    head1: TextStyle,
    head2: TextStyle,
    head3: TextStyle,
    head4: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    body3: TextStyle,
    caption1: TextStyle,
) {
    var head1: TextStyle by mutableStateOf(head1)
        private set
    var head2: TextStyle by mutableStateOf(head2)
        private set
    var head3: TextStyle by mutableStateOf(head3)
        private set
    var head4: TextStyle by mutableStateOf(head4)
        private set
    var body1: TextStyle by mutableStateOf(body1)
        private set
    var body2: TextStyle by mutableStateOf(body2)
        private set
    var body3: TextStyle by mutableStateOf(body3)
        private set
    var caption1: TextStyle by mutableStateOf(caption1)
        private set

    fun copy(
        head0: TextStyle = this.head1,
        head1: TextStyle = this.head2,
        head2: TextStyle = this.head3,
        head3: TextStyle = this.head4,
        body1: TextStyle = this.body1,
        body2: TextStyle = this.body2,
        body3: TextStyle = this.body3,
        caption1: TextStyle = this.caption1,
    ): StempoTypography = StempoTypography(
        head0,
        head1,
        head2,
        head3,
        body1,
        body2,
        body3,
        caption1,
    )

    fun update(other: StempoTypography) {
        head1 = other.head1
        head2 = other.head2
        head3 = other.head3
        head4 = other.head4
        body1 = other.body1
        body2 = other.body2
        body3 = other.body3
        caption1 = other.caption1
    }
}

fun stempoTextStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit = 0.sp,
): TextStyle = TextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

@Composable
fun stempoTypography(): StempoTypography {
    return StempoTypography(
        head1 = stempoTextStyle(
            fontFamily = PretendardSemiBold,
            fontSize = 26.sp,
            lineHeight = 44.sp
        ),
        head2 = stempoTextStyle(
            fontFamily = PretendardSemiBold,
            fontSize = 24.sp,
            lineHeight = 38.sp
        ),
        head3 = stempoTextStyle(
            fontFamily = PretendardSemiBold,
            fontSize = 20.sp,
            lineHeight = 28.sp
        ),
        head4 = stempoTextStyle(
            fontFamily = PretendardSemiBold,
            fontSize = 18.sp,
            lineHeight = 26.sp,
        ),
        body1 = stempoTextStyle(
            fontFamily = PretendardMedium,
            fontSize = 18.sp,
            lineHeight = 30.sp
        ),
        body2 = stempoTextStyle(
            fontFamily = PretendardMedium,
            fontSize = 16.sp,
            lineHeight = 26.sp
        ),
        body3 = stempoTextStyle(
            fontFamily = PretendardSemiBold,
            fontSize = 14.sp,
            lineHeight = 22.sp
        ),
        caption1 = stempoTextStyle(
            fontFamily = PretendardMedium,
            fontSize = 12.sp,
            lineHeight = 22.sp
        )
    )
}
