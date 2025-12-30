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
import io.legado.app.lib.theme.bottomBackground
import io.legado.app.lib.theme.getPrimaryTextColor
import io.legado.app.model.ReadBook
import io.legado.app.ui.book.read.ReadBookActivity
import io.legado.app.ui.book.read.page.provider.ChapterProvider
import io.legado.app.utils.ColorUtils
import io.legado.app.utils.postEvent
import io.legado.app.utils.setLayout
import io.legado.app.utils.viewbindingdelegate.viewBinding

class DialogueColorConfigDialog : BaseDialogFragment(R.layout.dialog_dialogue_color_config) {

    companion object {
        const val DIALOGUE_COLOR = 123
    }

    private val binding by viewBinding(DialogDialogueColorConfigBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.run {
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setBackgroundDrawableResource(R.color.background)
            decorView.setPadding(0, 0, 0, 0)
            val attr = attributes
            attr.dimAmount = 0.0f
            attr.gravity = Gravity.BOTTOM
            attributes = attr
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        (activity as? ReadBookActivity)?.let {
            it.bottomDialog++
        }
        initView()
        initEvent()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        ReadBookConfig.save()
        (activity as? ReadBookActivity)?.let {
            it.bottomDialog--
        }
    }

    private fun initView() = binding.run {
        val config = ReadBookConfig.durConfig
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
        postEvent(EventBus.UP_CONFIG, true)
        ChapterProvider.upLayout()
        ReadBook.loadContent(resetPageOffset = false)
    }
}
