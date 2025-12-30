package io.legado.app.ui.book.read.config

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import io.legado.app.R
import io.legado.app.base.BaseDialogFragment
import io.legado.app.constant.AppLog
import io.legado.app.constant.EventBus
import io.legado.app.databinding.DialogDialogueColorConfigBinding
import io.legado.app.help.config.ReadBookConfig
import io.legado.app.model.ReadBook
import io.legado.app.ui.book.read.ReadBookActivity
import io.legado.app.utils.ColorUtils
import io.legado.app.utils.postEvent
import io.legado.app.utils.viewbindingdelegate.viewBinding

class DialogueColorConfigDialog : BaseDialogFragment(R.layout.dialog_dialogue_color_config) {

    companion object {
        const val DIALOGUE_COLOR = 123
    }

    private val binding by viewBinding(DialogDialogueColorConfigBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawableResource(R.color.background)
            it.decorView.setPadding(0, 0, 0, 0)
            val attr = it.attributes
            attr.dimAmount = 0.0f
            attr.gravity = Gravity.BOTTOM
            it.attributes = attr
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? ReadBookActivity)?.bottomDialog++
        initView()
        initEvent()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        ReadBookConfig.save()
        (activity as? ReadBookActivity)?.bottomDialog--
    }

    private fun initView() = binding.run {
        val config = ReadBookConfig.durConfig
        AppLog.put("对话颜色对话框初始化 - 启用:${config.dialogueColorEnabled}, 颜色:${config.curDialogueColor()}, 正则:${config.dialoguePattern}")
        swEnableDialogueColor.isChecked = config.dialogueColorEnabled
        etDialoguePattern.setText(config.dialoguePattern)
        updateDialogueColorButton()
        llDialogueColor.visibility = if (config.dialogueColorEnabled) View.VISIBLE else View.GONE
    }

    private fun initEvent() = binding.run {
        swEnableDialogueColor.setOnCheckedChangeListener { _, isChecked ->
            ReadBookConfig.durConfig.dialogueColorEnabled = isChecked
            llDialogueColor.visibility = if (isChecked) View.VISIBLE else View.GONE
            applyConfig()
        }

        btnDialogueColor.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(ReadBookConfig.durConfig.curDialogueColor())
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR)
                .show(requireActivity())
        }

        etDialoguePattern.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val pattern = etDialoguePattern.text.toString()
                if (pattern.isNotEmpty()) {
                    ReadBookConfig.durConfig.dialoguePattern = pattern
                    applyConfig()
                }
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        btnConfirm.setOnClickListener {
            val pattern = etDialoguePattern.text.toString()
            if (pattern.isNotEmpty()) {
                ReadBookConfig.durConfig.dialoguePattern = pattern
            }
            applyConfig()
            dismiss()
        }
    }

    private fun updateDialogueColorButton() {
        val color = ReadBookConfig.durConfig.curDialogueColor()
        binding.btnDialogueColor.setBackgroundColor(color)
        val textColor = if (ColorUtils.isColorLight(color)) {
            android.graphics.Color.BLACK
        } else {
            android.graphics.Color.WHITE
        }
        binding.btnDialogueColor.setTextColor(textColor)
    }

    fun onColorSelected(color: Int) {
        ReadBookConfig.durConfig.setCurDialogueColor(color)
        updateDialogueColorButton()
        applyConfig()
    }

    private fun applyConfig() {
        AppLog.put("应用对话颜色配置 - 启用:${ReadBookConfig.durConfig.dialogueColorEnabled}, 颜色:${ReadBookConfig.durConfig.curDialogueColor()}")
        // 清除所有章节缓存，因为对话识别范围可能改变
        ReadBook.clearTextChapter()
        AppLog.put("已清除章节缓存")
        // 发送配置更新事件并重新加载内容
        // 2: upStyle - 更新文字样式（包括颜色）
        // 5: loadContent - 重新加载内容
        postEvent(EventBus.UP_CONFIG, arrayListOf(2, 5))
        AppLog.put("已发送 UP_CONFIG 事件: [2, 5]")
    }
}
