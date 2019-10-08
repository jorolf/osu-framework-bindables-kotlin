package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.BindableInt
import kotlin.test.assertEquals

class BindableIntTest {
    @Test
    fun testSets() {
        fun testSet(value: Int) {
            val bindable = BindableInt().apply { this.value = value }
            assertEquals(value, bindable.value)
        }

        testSet(0)
        testSet(-0)
        testSet(1)
        testSet(-105)
        testSet(105)
        testSet(Int.MIN_VALUE)
        testSet(Int.MAX_VALUE)
    }

    @Test
    fun testParsingStrings() {
        fun testParsingString(value: String, expected: Int) {
            val bindable = BindableInt()
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingString("0", 0)
        testParsingString("1", 1)
        testParsingString("-0", -0)
        testParsingString("-105", -105)
        testParsingString("105", 105)
    }

    @Test
    fun testParsingStringsWithRange() {
        fun testParsingStringWithRange(value: String, minValue: Int, maxValue: Int, expected: Int) {
            val bindable = BindableInt().apply { this.minValue = minValue; this.maxValue = maxValue }
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingStringWithRange("0", -10, 10, 0)
        testParsingStringWithRange("1", -10, 10, 1)
        testParsingStringWithRange("-0", -10, 10, -0)
        testParsingStringWithRange("-105", -10, 10, -10)
        testParsingStringWithRange("105", -10, 10, 10)
    }

    @Test
    fun testParsingInts() {
        fun testParsingInt(value: Int) {
            val bindable = BindableInt()
            bindable.parse(value)

            assertEquals(value, bindable.value)
        }

        testParsingInt(0)
        testParsingInt(-0)
        testParsingInt(1)
        testParsingInt(-105)
        testParsingInt(105)
        testParsingInt(Int.MIN_VALUE)
        testParsingInt(Int.MAX_VALUE)
    }
}