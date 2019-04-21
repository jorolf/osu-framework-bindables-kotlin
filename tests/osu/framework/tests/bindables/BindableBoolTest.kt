package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.BindableBool
import kotlin.test.assertEquals

class BindableBoolTest {
    @Test
    fun testSet() {
        var bindable = BindableBool().apply { value = false }
        assertEquals(false, bindable.value)

        bindable = BindableBool().apply { value = true }
        assertEquals(true, bindable.value)
    }

    @Test
    fun testParsingStrings() {
        testParsingString("True", true)
        testParsingString("true", true)
        testParsingString("False", false)
        testParsingString("false", false)
        testParsingString("1", true)
        testParsingString("0", false)
    }

    private fun testParsingString(value: String, expected: Boolean) {
        val bindable = BindableBool()
        bindable.parse(value)

        assertEquals(expected, bindable.value)
    }

    @Test
    fun testParsingBooleans() {
        var bindable = BindableBool().apply { parse(false) }
        assertEquals(false, bindable.value)

        bindable = BindableBool().apply { parse(true) }
        assertEquals(true, bindable.value)
    }
}