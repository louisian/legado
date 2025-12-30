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
        const val DIALOGUE_COLOR_DAY1 = 123
        const val DIALOGUE_COLOR_DAY2 = 124
        const val DIALOGUE_COLOR_NIGHT1 = 125
        const val DIALOGUE_COLOR_NIGHT2 = 126
        const val DIALOGUE_COLOR_EINK1 = 127
        const val DIALOGUE_COLOR_EINK2 = 128
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
        swEnableDialogueColor.isChecked = config.dialogueColorEnabled
        etDialoguePattern.setText(config.dialoguePattern)
        updateAllColorButtons()
        llDialogueColor.visibility = if (config.dialogueColorEnabled) View.VISIBLE else View.GONE
    }

    private fun initEvent() = binding.run {
        swEnableDialogueColor.setOnCheckedChangeListener { _, isChecked ->
            ReadBookConfig.durConfig.dialogueColorEnabled = isChecked
            llDialogueColor.visibility = if (isChecked) View.VISIBLE else View.GONE
            applyConfig()
        }

        // 白天模式颜色按钮
        btnDialogueColorDay1.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColor))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_DAY1)
                .show(requireActivity())
        }

        btnDialogueColorDay2.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColor2))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_DAY2)
                .show(requireActivity())
        }

        // 夜间模式颜色按钮
        btnDialogueColorNight1.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColorNight))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_NIGHT1)
                .show(requireActivity())
        }

        btnDialogueColorNight2.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColorNight2))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_NIGHT2)
                .show(requireActivity())
        }

        // 墨水屏模式颜色按钮
        btnDialogueColorEink1.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColorEInk))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_EINK1)
                .show(requireActivity())
        }

        btnDialogueColorEink2.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setColor(android.graphics.Color.parseColor(ReadBookConfig.durConfig.dialogueColorEInk2))
                .setShowAlphaSlider(false)
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setDialogId(DIALOGUE_COLOR_EINK2)
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

    private fun updateAllColorButtons() = binding.run {
        // 白天模式
        updateColorButton(btnDialogueColorDay1, ReadBookConfig.durConfig.dialogueColor)
        updateColorButton(btnDialogueColorDay2, ReadBookConfig.durConfig.dialogueColor2)
        // 夜间模式
        updateColorButton(btnDialogueColorNight1, ReadBookConfig.durConfig.dialogueColorNight)
        updateColorButton(btnDialogueColorNight2, ReadBookConfig.durConfig.dialogueColorNight2)
        // 墨水屏模式
        updateColorButton(btnDialogueColorEink1, ReadBookConfig.durConfig.dialogueColorEInk)
        updateColorButton(btnDialogueColorEink2, ReadBookConfig.durConfig.dialogueColorEInk2)
    }

    private fun updateColorButton(button: io.legado.app.lib.theme.view.ThemeButton, colorStr: String) {
        val color = android.graphics.Color.parseColor(colorStr)
        button.setBackgroundColor(color)
        val textColor = if (ColorUtils.isColorLight(color)) {
            android.graphics.Color.BLACK
        } else {
            android.graphics.Color.WHITE
        }
        button.setTextColor(textColor)
    }

    fun onColorSelected(dialogId: Int, color: Int) {
        val config = ReadBookConfig.durConfig
        when (dialogId) {
            DIALOGUE_COLOR_DAY1 -> config.setDialogueColor1(color, 0)
            DIALOGUE_COLOR_DAY2 -> config.setDialogueColor2(color, 0)
            DIALOGUE_COLOR_NIGHT1 -> config.setDialogueColor1(color, 1)
            DIALOGUE_COLOR_NIGHT2 -> config.setDialogueColor2(color, 1)
            DIALOGUE_COLOR_EINK1 -> config.setDialogueColor1(color, 2)
            DIALOGUE_COLOR_EINK2 -> config.setDialogueColor2(color, 2)
        }
        updateAllColorButtons()
        applyConfig()
    }

    private fun applyConfig() {
        ReadBook.clearTextChapter()
        postEvent(EventBus.UP_CONFIG, arrayListOf(2, 5))
    }
}
