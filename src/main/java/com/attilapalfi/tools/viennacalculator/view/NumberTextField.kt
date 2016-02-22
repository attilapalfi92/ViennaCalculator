package com.attilapalfi.tools.viennacalculator.view

import javafx.scene.control.TextField

/**
 * Created by palfi on 2016-02-21.
 */
class NumberTextField : TextField {

    private val validatorRegex = Regex("[0-9]*")

    constructor() : super()

    constructor(p0: String?) : super(p0)

    override fun replaceText(start: Int, end: Int, text: String) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }


    override fun replaceSelection(text: String) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private fun validate(text: String): Boolean = text.matches(validatorRegex);
}