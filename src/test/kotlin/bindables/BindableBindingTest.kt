package osu.framework.tests.bindables

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import osu.framework.bindables.Bindable
import osu.framework.bindables.BindableInt
import osu.framework.bindables.ValueChangedEvent
import kotlin.test.*

class BindableBindingTest {
    @Test
    fun testPropagation() {
        val bindable1 = Bindable("default", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        assertEquals("default", bindable1.value)
        assertEquals(bindable2.value, bindable1.value)

        bindable1.value = "new value"

        assertEquals("new value", bindable1.value)
        assertEquals(bindable2.value, bindable1.value)
    }

    @Test
    fun testDisabled() {
        val bindable1 = Bindable("default", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        bindable1.disabled = true

        assertThrows<UnsupportedOperationException> { bindable1.value = "new value" }
        assertThrows<UnsupportedOperationException> { bindable2.value = "new value" }

        bindable1.disabled = false
        bindable1.value = "new value"

        assertEquals("new value", bindable1.value)
        assertEquals("new value", bindable2.value)

        bindable2.value = "new value 2"

        assertEquals("new value 2", bindable1.value)
        assertEquals("new value 2", bindable2.value)
    }

    @Test
    fun testValueChanged() {
        val bindable1 = Bindable("default", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        var changed1 = 0
        var changed2 = 0

        bindable1.valueChanged += { changed1++ }
        bindable2.valueChanged += { changed2++ }

        bindable1.value = "new value"

        assertEquals(1, changed1)
        assertEquals(1, changed2)

        bindable1.value = "new value 2"

        assertEquals(2, changed1)
        assertEquals(2, changed2)

        bindable1.value = "new value 2"

        assertEquals(2, changed1)
        assertEquals(2, changed2)
    }

    @Test
    fun testValueChangedWithUpstreamRejection() {
        val bindable1 = Bindable("won't change", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        var changed1 = 0
        var changed2 = 0

        bindable1.valueChanged += { changed1++ }
        bindable2.valueChanged += {
            bindable2.value = "won't change"
            changed2++
        }

        bindable1.value = "new value"

        assertEquals("won't change", bindable1.value)
        assertEquals(bindable1.value, bindable2.value)

        // bindable1 should only receive the final value changed, skipping the intermediary (overidden) one.
        assertEquals(1, changed1)
        assertEquals(2, changed2)
    }

    @Test
    fun testDisabledChanged() {
        val bindable1 = Bindable("default", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        var changed1 = false
        var changed2 = false

        bindable1.disabledChanged += { changed1 = it }
        bindable2.disabledChanged += { changed2 = it }

        bindable1.disabled = true

        assertTrue(changed1)
        assertTrue(changed2)

        bindable1.disabled = false

        assertFalse(changed1)
        assertFalse(changed2)
    }

    @Test
    fun testDisabledChangedWithUpstreamRejection() {
        val bindable1 = Bindable("won't change", String::class.java)
        val bindable2 = bindable1.getBoundCopy()

        var changed1 = 0
        var changed2 = 0

        bindable1.disabledChanged += { changed1++ }
        bindable2.disabledChanged += {
            bindable2.disabled = false
            changed2++
        }

        bindable1.disabled = true

        assertFalse(bindable1.disabled)
        assertFalse(bindable2.disabled)

        // bindable1 should only receive the final disabled changed, skipping the intermediary (overidden) one.
        assertEquals(1, changed1)
        assertEquals(2, changed2)
    }

    @Test
    fun testMinValueChanged() {
        val bindable1 = BindableInt()
        val bindable2 = BindableInt()
        bindable2.bindTo(bindable1)

        var minValue1 = 0
        var minValue2 = 0

        bindable1.minValueChanged += { minValue1 = it }
        bindable2.minValueChanged += { minValue2 = it }

        bindable1.minValue = 1

        assertEquals(1, minValue1)
        assertEquals(1, minValue2)

        bindable1.minValue = 2

        assertEquals(2, minValue1)
        assertEquals(2, minValue2)
    }

    @Test
    fun testMinValueChangedWithUpstreamRejection() {
        val bindable1 = BindableInt()
        val bindable2 = BindableInt()
        bindable2.bindTo(bindable1)

        var changed1 = 0
        var changed2 = 0

        bindable1.minValueChanged += { changed1++ }
        bindable2.minValueChanged += {
            bindable2.minValue = 1337
            changed2++
        }

        bindable1.minValue = 2

        assertEquals(1337, bindable1.minValue)
        assertEquals(bindable1.minValue, bindable2.minValue)

        assertEquals(1, changed1)
        assertEquals(2, changed2)
    }

    @Test
    fun testMaxValueChanged() {
        val bindable1 = BindableInt()
        val bindable2 = BindableInt()
        bindable2.bindTo(bindable1)

        var maxValue1 = 0
        var maxValue2 = 0

        bindable1.maxValueChanged += { maxValue1 = it }
        bindable2.maxValueChanged += { maxValue2 = it }

        bindable1.maxValue = 1

        assertEquals(1, maxValue1)
        assertEquals(1, maxValue2)

        bindable1.maxValue = 2

        assertEquals(2, maxValue1)
        assertEquals(2, maxValue2)
    }

    @Test
    fun testMaxValueChangedWithUpstreamRejection() {
        val bindable1 = BindableInt()
        val bindable2 = BindableInt()
        bindable2.bindTo(bindable1)

        var changed1 = 0
        var changed2 = 0

        bindable1.maxValueChanged += { changed1++ }
        bindable2.maxValueChanged += {
            bindable2.maxValue = 1337
            changed2++
        }

        bindable1.maxValue = 2

        assertEquals(1337, bindable1.maxValue)
        assertEquals(bindable1.maxValue, bindable2.maxValue)

        assertEquals(1, changed1)
        assertEquals(2, changed2)
    }

    @Test
    fun testUnbindFrom() {
        val bindable1 = Bindable(5, Int::class.java)
        val bindable2 = Bindable(0, Int::class.java)
        bindable2.bindTo(bindable1)

        assertEquals(bindable1.value, bindable2.value)

        bindable2.unbindFrom(bindable1)
        bindable1.value = 10

        assertNotEquals(bindable1.value, bindable2.value)
    }

    @Test
    fun testEventArgs() {
        val bindable1 = Bindable(0, Int::class.java)
        val bindable2 = Bindable(0, Int::class.java)
        bindable2.bindTo(bindable1)

        lateinit var event1: ValueChangedEvent<Int>
        lateinit var event2: ValueChangedEvent<Int>

        bindable1.bindValueChanged { event1 = it }
        bindable2.bindValueChanged { event2 = it }

        bindable1.value = 1

        assertEquals(0, event1.oldValue)
        assertEquals(1, event1.newValue)
        assertEquals(0, event2.oldValue)
        assertEquals(1, event2.newValue)

        bindable1.value = 2

        assertEquals(1, event1.oldValue)
        assertEquals(2, event1.newValue)
        assertEquals(1, event2.oldValue)
        assertEquals(2, event2.newValue)
    }
}