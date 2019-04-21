package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import osu.framework.bindables.Bindable
import kotlin.test.*

class BindableEnumTest {
    @Test
    fun testSet() {
        val bindable = Bindable(TestEnum.Value1, TestEnum::class.java)
        assertEquals(TestEnum.Value1, bindable.value)
        bindable.value = TestEnum.Value2
        assertEquals(TestEnum.Value2, bindable.value)
    }

    @Test
    fun testParse() {
        val bindable = Bindable(TestEnum.Value1, TestEnum::class.java)
        bindable.parse(TestEnum.Value2)
        assertEquals(TestEnum.Value2, bindable.value)
        bindable.parse("Value1")
        assertEquals(TestEnum.Value1, bindable.value)
    }

    enum class TestEnum {
        Value1, Value2
    }
}