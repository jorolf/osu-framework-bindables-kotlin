package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.Bindable
import osu.framework.bindables.BindableDouble
import kotlin.test.assertEquals

class BindableDoubleTest {
    @Test
    fun testSets() {
        fun testSet(value: Double) {
            val bindable = BindableDouble().apply { this.value = value }
            assertEquals(value, bindable.value)
        }

        testSet(0.0)
        testSet(-0.0)
        testSet(1.0)
        testSet(-105.123)
        testSet(105.123)
        testSet(Double.MIN_VALUE)
        testSet(Double.MAX_VALUE)
    }

    @Test
    fun testParsingStrings() {
        fun testParsingString(value: String, expected: Double) {
            val bindable = BindableDouble()
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingString("0", 0.0)
        testParsingString("1", 1.0)
        testParsingString("-0", -0.0)
        testParsingString("-105.123", -105.123)
        testParsingString("105.123", 105.123)
    }

    @Test
    fun testParsingStringsWithRange() {
        fun testParsingStringWithRange(value: String, minValue: Double, maxValue: Double, expected: Double) {
            val bindable = BindableDouble().apply { this.minValue = minValue; this.maxValue = maxValue }
            bindable.parse(value)

            assertEquals(expected, bindable.value)
        }

        testParsingStringWithRange("0", -10.0, 10.0, 0.0)
        testParsingStringWithRange("1", -10.0, 10.0, 1.0)
        testParsingStringWithRange("-0", -10.0, 10.0, -0.0)
        testParsingStringWithRange("-105.123", -10.0, 10.0, -10.0)
        testParsingStringWithRange("105.123", -10.0, 10.0, 10.0)
    }

    @Test
    fun testParsingDoubles() {
        fun testParsingDouble(value: Double) {
            val bindable = BindableDouble()
            bindable.parse(value)

            assertEquals(value, bindable.value)
        }

        testParsingDouble(0.0)
        testParsingDouble(-0.0)
        testParsingDouble(1.0)
        testParsingDouble(-105.123)
        testParsingDouble(105.123)
        testParsingDouble(Double.MIN_VALUE)
        testParsingDouble(Double.MAX_VALUE)
    }

    @Test
    fun testPropagationToPlainBindable() {
        val number = BindableDouble(1000.0)
        val bindable = Bindable(0.0, Double::class.javaObjectType)

        bindable.bindTo(number)

        number.precision = 0.5
        number.minValue = 0.0
        number.maxValue = 10.0
    }
}