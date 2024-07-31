package com.kkkk.presentation.main.study

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.kkkk.core.base.BaseFragment
import com.kkkk.domain.entity.response.StudyModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyInformationBinding

@AndroidEntryPoint
class StudyInformationFragment :
    BaseFragment<FragmentStudyInformationBinding>(R.layout.fragment_study_information) {
    private val viewModel by viewModels<StudyViewModel>()

    private val items by lazy {
        listOf(
            binding.itemStudyInformation1,
            binding.itemStudyInformation2,
            binding.itemStudyInformation3
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        setButtonClickListeners()
    }

    private fun setObserver() {
        viewModel.articleState.flowWithLifecycle(lifecycle).onEach { state ->
            with(binding) {
                btnStudyInformationBefore.visibility =
                    if (state.hasPrevious) View.VISIBLE else View.INVISIBLE

                btnStudyInformationNext.visibility =
                    if (state.hasNext) View.VISIBLE else View.INVISIBLE

                updateItems(state.items)
            }
        }.launchIn(lifecycleScope)
    }

    private fun updateItems(stateItems: List<StudyModel.StudyItemModel>) {
        items.forEachIndexed { index, item ->
            if (index < stateItems.size) {
                item.root.isVisible = true
                with(item) {
                    tvStudyInformationTitle.text = stateItems[index].title
                    tvStudyExerciseContent.text = stateItems[index].content
                    ivStudyInformationThumbnail.load(
                        stateItems[index].thumbnailUrl ?: R.drawable.img_default_content
                    )
                }
            } else {
                item.root.visibility = View.INVISIBLE
            }
        }
    }

    private fun setButtonClickListeners() {
        binding.btnStudyInformationBefore.setOnClickListener {
            viewModel.getArticles(-1)
        }
        binding.btnStudyInformationNext.setOnClickListener {
            viewModel.getArticles(1)
        }
    }
}