package com.kkkk.presentation.main.study

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyBinding
import java.lang.ref.WeakReference

@AndroidEntryPoint
class StudyFragment : BaseFragment<FragmentStudyBinding>(R.layout.fragment_study),
    OnItemClickListener {
    private val viewModel by activityViewModels<StudyViewModel>()

    private var homeworkDialog: WeakReference<Dialog>? = null

    private var _studyStudentAdapter: StudyAdapter? = null
    private val studyStudentAdapter
        get() = requireNotNull(_studyStudentAdapter) { getString(R.string.error_msg) }

    private var _studyTeacherAdapter: StudyAdapter? = null
    private val studyTeacherAdapter
        get() = requireNotNull(_studyTeacherAdapter) { getString(R.string.error_msg) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(R.color.gray_100)

        setAdapter()
        observeTypeIsMe()
        observeStudyList()
        observeToast()
        setToggleClickListener()
        setAddHomeworkButtonClickListener()
    }

    private fun setAdapter() {
        _studyTeacherAdapter = StudyAdapter(requireContext(), this, false)
        binding.rvTeacherExercise.adapter = studyTeacherAdapter

        _studyStudentAdapter = StudyAdapter(requireContext(), this, true)
        binding.rvStudentExercise.adapter = studyStudentAdapter
    }


    private fun observeTypeIsMe() {
        viewModel.typeIsMe.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { isMe ->
            setToggleState(isMe)
        }.launchIn(lifecycleScope)
    }

    private fun observeStudyList() {
        viewModel.studyList.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { studyList ->
                studyStudentAdapter.submitList(studyList)
                studyTeacherAdapter.submitList(studyList)
                setToggleState(viewModel.typeIsMe.value)

                with(binding) {
                    if (studyList.isEmpty()) {
                        layoutHomeworkEmpty.visibility = View.VISIBLE
                        layoutHomeworkValid.visibility = View.INVISIBLE
                    } else {
                        layoutHomeworkEmpty.visibility = View.INVISIBLE
                        layoutHomeworkValid.visibility = View.VISIBLE

                        progressBarHomework.max = studyList.size
                        progressBarHomework.progress = studyList.count { it.completed }
                        ivSeekbarThumb.x =
                            progressBarHomework.width * progressBarHomework.progress / progressBarHomework.max.toFloat()
                    }
                }
            }.launchIn(lifecycleScope)
    }

    private fun observeToast() {
        viewModel.toast.flowWithLifecycle(lifecycle).onEach {
            toast(it)
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ResourceAsColor")
    private fun setToggleClickListener() {
        binding.itemToggle.tvStudent.setOnClickListener {
            viewModel.setTypeIsMe(true)
        }
        binding.itemToggle.tvTeacher.setOnClickListener {
            viewModel.setTypeIsMe(false)
        }
    }

    private fun setAddHomeworkButtonClickListener() {
        binding.btnTeacherAddHomework.setOnClickListener {
            showHomeworkDialog()
        }
    }

    private fun setToggleState(isMe: Boolean) {
        setToggleAppearance(isMe)
        setLayoutVisibility(isMe)
        updateExerciseVisibility(isMe)
    }

    private fun setToggleAppearance(isMe: Boolean) {
        with(binding.itemToggle) {
            val (selectedView, unselectedView) = if (isMe) {
                tvStudent to tvTeacher
            } else {
                tvTeacher to tvStudent
            }

            selectedView.apply {
                setBackgroundResource(R.drawable.shape_toggle_selected)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            unselectedView.apply {
                background = null
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
    }

    private fun setLayoutVisibility(isMe: Boolean) {
        with(binding) {
            layoutMyExercise.isVisible = isMe
            layoutTeacherExercise.isVisible = !isMe
        }
    }

    private fun updateExerciseVisibility(isMe: Boolean) {
        with(binding) {
            val (emptyView, exerciseView) = if (isMe) {
                ivMyExerciseEmpty to layoutMyExerciseValid
            } else {
                ivTeacherExerciseEmpty to rvTeacherExercise
            }

            val isEmpty = viewModel.studyList.value.isEmpty()

            emptyView.isVisible = isEmpty
            exerciseView.isVisible = !isEmpty
        }
    }

    private fun showHomeworkDialog() {
        val dialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_add_homework)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
            setCancelable(true)

            this.findViewById<TextView>(R.id.btn_study_dialog_cancel).setOnClickListener {
                dismiss()
            }

            this.findViewById<TextView>(R.id.btn_study_dialog_save).setOnClickListener {
                viewModel.addHomework(
                    this.findViewById<TextView>(R.id.et_study_dialog).text.toString()
                )
                dismiss()
            }

            show()
        }

        homeworkDialog = WeakReference(dialog)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _studyStudentAdapter = null
        _studyTeacherAdapter = null
        homeworkDialog?.get()?.dismiss()
        homeworkDialog = null
    }

    override fun onCheckboxClick(itemId: Int, description: String, completed: Boolean) {
        viewModel.updateHomework(itemId, description, completed)
    }

    override fun onDeleteButtonClick(itemId: Int) {
        viewModel.deleteHomework(itemId)
    }
}
