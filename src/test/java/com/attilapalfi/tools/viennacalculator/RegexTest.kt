package com.attilapalfi.tools.viennacalculator

import org.junit.Test

/**
 * Created by palfi on 2016-02-21.
 */
class RegexTest {

    @Test
    fun testRegex() {
        val matches1 = validate("32423")
        val matches2 = validate("alma")

    }

    private fun validate(text: String): Boolean {
        val reg = Regex("[0-9]*")
        val matches = text.matches(reg);
        return matches
    }
}