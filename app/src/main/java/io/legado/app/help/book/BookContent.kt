package io.legado.app.help.book

import io.legado.app.data.entities.ReplaceRule

data class BookContent(
    val sameTitleRemoved: Boolean,
    val textList: List<String>,
    //起效的替换规则
    val effectiveReplaceRules: List<ReplaceRule>?
) {
    
    // 对话范围列表
    val dialogueRanges: MutableList<DialogueRange> = mutableListOf()

    override fun toString(): String {
        return textList.joinToString("\n")
    }
    
    data class DialogueRange(
        val paragraphIndex: Int,  // 段落索引
        val start: Int,            // 对话开始位置
        val end: Int               // 对话结束位置
    )

}
