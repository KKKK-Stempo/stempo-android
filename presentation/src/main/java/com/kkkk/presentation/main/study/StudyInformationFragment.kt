package com.kkkk.presentation.main.study

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.kkkk.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyInformationBinding

@AndroidEntryPoint
class StudyInformationFragment :
    BaseFragment<FragmentStudyInformationBinding>(R.layout.fragment_study_information) {
    private val viewModel by viewModels<StudyViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
    }

    private fun setObserver() {
        viewModel.articleState.flowWithLifecycle(lifecycle).onEach { state ->
            with(binding) {
                if (state.hasPrevious) {
                    btnStudyInformationBefore.visibility = View.VISIBLE
                } else {
                    btnStudyInformationBefore.visibility = View.INVISIBLE
                }

                if (state.hasNext) {
                    btnStudyInformationNext.visibility = View.VISIBLE
                } else {
                    btnStudyInformationNext.visibility = View.INVISIBLE
                }

                invisibleItems(state.items.size)

                val items = listOf(
                    itemStudyInformation1,
                    itemStudyInformation2,
                    itemStudyInformation3
                )

                state.items.take(3).forEachIndexed { index, item ->
                    with(items[index]) {
                        tvStudyInformationTitle.text = item.title
                        tvStudyExerciseContent.text = item.content
                        ivStudyInformationThumbnail.load(item.thumbnailUrl ?: R.drawable.img_default_content)
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun invisibleItems(size: Int) {
        with(binding) {
            val items = listOf(
                itemStudyInformation1,
                itemStudyInformation2,
                itemStudyInformation3
            )

            items.forEachIndexed { index, item ->
                item.root.visibility = if (index < size) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}