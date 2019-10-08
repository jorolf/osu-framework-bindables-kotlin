package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.BindableLong
import kotlin.test.assertEquals

class BindableLongTest {
    @Test
    fun testSets() {
        fun testSet(value: Long) {
            val bindable = BindableLong().apply { this.value = value }
            assertEquals(value, bindable.value)
        }

        testSet(0)
        testSet(-0)
        testSet(1)
        testSet(-105)
        testSet(105)
        testSet(Long.MIN_VALUE)
        testSet(Long.MAX_VALUE)
    }

    @Test
    fun testParsingStrings() {
        fun testParsingString(value: String, expected: Long) {
            val bindable = BindableLong()
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
        fun testParsingStringWithRange(value: String, minValue: Long, maxValue: Long, expected: Long) {
            val bindable = BindableLong().apply { this.minValue = minValue; this.maxValue = maxValue }
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
    fun testParsingLongs() {
        fun testParsingLong(value: Long) {
            val bindable = BindableLong()
            bindable.parse(value)

            assertEquals(value, bindable.value)
        }

        testParsingLong(0)
        testParsingLong(-0)
        testParsingLong(1)
        testParsingLong(-105)
        testParsingLong(105)
        testParsingLong(Long.MIN_VALUE)
        testParsingLong(Long.MAX_VALUE)
    }
}