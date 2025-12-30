package io.legado.app.lib.theme.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import io.legado.app.lib.theme.accentColor
import io.legado.app.utils.applyTint

class ThemeButton(context: Context, attrs: AttributeSet) : AppCompatButton(context, attrs) {

    init {
        if (!isInEditMode) {
            applyTint(context.accentColor)
        }
    }
}

