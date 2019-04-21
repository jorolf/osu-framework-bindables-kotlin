package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.BindableFloat
import kotlin.test.assertEquals

class BindableFloatTest {
    @Test
    fun testSets() {
        fun testSet(value: Float) {
            val bindable = BindableFloat().apply { this.value = value }
            assertEquals(value, bindable.value)
        }

        testSet(0.0f)
        testSet(-0.0f)
        testSet(1.0f)
        testSet(-105.123f)
        testSet(105.123f)
        testSet(Float.MIN_VALUE)
        testSet(Float.MAX_VALUE)
    }

    @Test
    fun testParsingStrings() {
        fun testParsingString(value: String, expected: Float) {
            val bindable = BindableFloat()
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingString("0", 0.0f)
        testParsingString("1", 1.0f)
        testParsingString("-0", -0.0f)
        testParsingString("-105.123", -105.123f)
        testParsingString("105.123", 105.123f)
    }

    @Test
    fun testParsingStringsWithRange() {
        fun testParsingStringWithRange(value: String, minValue: Float, maxValue: Float, expected: Float) {
            val bindable = BindableFloat().apply { this.minValue = minValue; this.maxValue = maxValue }
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingStringWithRange("0", -10.0f, 10.0f, 0.0f)
        testParsingStringWithRange("1", -10.0f, 10.0f, 1.0f)
        testParsingStringWithRange("-0", -10.0f, 10.0f, -0.0f)
        testParsingStringWithRange("-105.123", -10.0f, 10.0f, -10.0f)
        testParsingStringWithRange("105.123", -10.0f, 10.0f, 10.0f)
    }

    @Test
    fun testParsingFloats() {
        fun testParsingFloat(value: Float) {
            val bindable = BindableFloat()
            bindable.parse(value)

            assertEquals(value, bindable.value)
        }

        testParsingFloat(0.0f)
        testParsingFloat(-0.0f)
        testParsingFloat(1.0f)
        testParsingFloat(-105.123f)
        testParsingFloat(105.123f)
        testParsingFloat(Float.MIN_VALUE)
        testParsingFloat(Float.MAX_VALUE)
    }
}