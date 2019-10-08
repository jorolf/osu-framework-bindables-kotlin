package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.Bindable
import kotlin.test.assertEquals

class BindableStringTest {
    @Test
    fun testSet() {
        testSetValue("")
        testSetValue<String?>(null)
        testSetValue("this is a string")
    }

    private inline fun <reified T: String?> testSetValue(value: T) {
        val bindable = Bindable(value, T::class.java)
        assertEquals(value, bindable.value)
    }

    @Test
    fun testParse() {
        testParseValue("")
        testParseValue("null")
        testParseValue("this is a string")
    }

    private fun testParseValue(value: String) {
        val bindable = Bindable(value, String::class.java).apply { parse(value) }
        assertEquals(value, bindable.value)
    }
}
