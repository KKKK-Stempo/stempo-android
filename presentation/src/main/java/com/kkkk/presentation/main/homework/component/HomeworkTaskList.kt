package com.kkkk.presentation.main.homework.component

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.presentation.main.component.clickableWithoutRipple
import com.kkkk.presentation.main.homework.HomeworkState
import com.kkkk.presentation.main.homework.model.HomeworkMode
import com.kkkk.presentation.main.theme.Gray100
import com.kkkk.presentation.main.theme.Gray200
import com.kkkk.presentation.main.theme.Purple10
import com.kkkk.presentation.main.theme.Purple50
import com.kkkk.presentation.main.theme.StempoTheme
import com.kkkk.presentation.main.theme.White
import com.kkkk.stempo.presentation.R
import kotlinx.collections.immutable.persistentListOf

@Composable
fun HomeworkTaskList(
    homeworkState: HomeworkState,
    modifier: Modifier = Modifier,
    onCheckedBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
    onDeleteBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
    onAddBtnClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Gray100)
    ) {
        Text(
            text = stringResource(
                id = if (homeworkState.selectedMode == HomeworkMode.MYSELF) {
                    R.string.study_tv_my
                } else {
                    R.string.study_tv_teacher
                }
            ),
            style = StempoTheme.typography.head4,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(8.dp))
                .border(1.dp, Gray200, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            if (!homeworkState.isListEmpty) {
                LazyColumn {
                    items(homeworkState.homeworkList, key = { item -> item.id }) { item ->
                        HomeworkListItem(
                            modifier = Modifier.animateItem(
                                tween(
                                    durationMillis = 500,
                                    easing = LinearOutSlowInEasing
                                )
                            ),
                            homeworkState = homeworkState,
                            studyItem = item,
                            onCheckedBtnClick = onCheckedBtnClick,
                            onDeleteBtnClick = onDeleteBtnClick
                        )
                    }
                }
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.img_study_empty),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            }

            if (homeworkState.selectedMode == HomeworkMode.TEACHER) {
                Button(
                    onClick = onAddBtnClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple10,
                        contentColor = Purple50
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.study_btn_add),
                        style = StempoTheme.typography.body3,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeworkListItem(
    homeworkState: HomeworkState,
    studyItem: StudyModel.StudyItemModel,
    modifier: Modifier = Modifier,
    onCheckedBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
    onDeleteBtnClick: (StudyModel.StudyItemModel) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            imageVector = ImageVector.vectorResource(
                id = if (studyItem.completed) {
                    R.drawable.ic_checkbox_checked
                } else {
                    R.drawable.ic_checkbox_unchecked
                }
            ),
            contentDescription = null,
            modifier = Modifier.clickableWithoutRipple { onCheckedBtnClick(studyItem) }
        )

        Text(
            text = studyItem.description,
            style = StempoTheme.typography.head4,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )

        if (homeworkState.selectedMode == HomeworkMode.TEACHER) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                modifier = Modifier.clickableWithoutRipple { onDeleteBtnClick(studyItem) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeworkTaskListPreview() {
    StempoTheme {
        HomeworkTaskList(
            homeworkState = HomeworkState(
                selectedMode = HomeworkMode.TEACHER,
                homeworkList = persistentListOf(
                    StudyModel.StudyItemModel(0, "description1", true),
                    StudyModel.StudyItemModel(1, "description2", false)
                )
            )
        )
    }
}