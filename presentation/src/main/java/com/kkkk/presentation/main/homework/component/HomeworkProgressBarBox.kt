package com.kkkk.presentation.main.homework.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.presentation.main.homework.HomeworkState
import com.kkkk.presentation.main.theme.Dark
import com.kkkk.presentation.main.theme.Gray600
import com.kkkk.presentation.main.theme.Sky50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HomeworkProgressBarBox(
    homeworkState: HomeworkState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Dark, RoundedCornerShape(12.dp))
    ) {
        if (!homeworkState.homeworkList.isEmpty()) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.study_tv_progress),
                    style = StempoTheme.typography.head4,
                    color = White
                )
                BoxWithConstraints(
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                ) {
                    HomeworkProgressBar(
                        homeworkState = homeworkState,
                        maxWidth = maxWidth
                    )
                }
            }
        } else {
            HomeworkEmptyBox()
        }
    }
}

@Composable
fun BoxScope.HomeworkProgressBar(
    homeworkState: HomeworkState,
    maxWidth: Dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = homeworkState.progress,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .align(Alignment.Center),
        color = Sky50,
        trackColor = Gray600,
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = ((maxWidth - 18.dp) * animatedProgress).roundToPx(),
                    y = 0
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Sky50)
        )
    }
}

@Composable
fun HomeworkEmptyBox(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.study_tv_empty),
            style = StempoTheme.typography.head4,
            color = White
        )
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo_illustration),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeworkProgressBarPreview() {
    StempoTheme {
        HomeworkProgressBarBox(
            homeworkState = HomeworkState(
                homeworkList = persistentListOf(
                    StudyModel.StudyItemModel(0, "description", true),
                    StudyModel.StudyItemModel(1, "description", false)
                ),
            )
        )
    }
}