package osu.framework.tests.bindables

import org.junit.jupiter.api.*
import osu.framework.bindables.Bindable
import osu.framework.bindables.LeasedBindable
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BindableLeasingTest {
    private lateinit var original: Bindable<Int>

    @BeforeEach
    fun setUp() {
        original = Bindable(1, Int::class.javaObjectType)
    }

    @Test
    fun testLeaseAndReturns() {
        fun testLeaseAndReturn(revert: Boolean) {
            val leased = original.beginLease(revert)

            assertEquals(original.value, leased.value)

            leased.value = 2

            assertEquals(2, original.value)
            assertEquals(original.value, leased.value)

            leased.returnBindable()

            assertEquals(if (revert) 1 else 2, original.value)
        }

        testLeaseAndReturn(false)
        setUp()
        testLeaseAndReturn(true)
    }

    @Test
    fun testLeaseAndReturnedOnUnbindAll() {
        fun testLeaseAndReturnedOnUnbindAll(revert: Boolean) {
            val leased = original.beginLease(revert)

            assertEquals(original.value, leased.value)

            leased.value = 2

            assertEquals(2, original.value)
            assertEquals(original.value, leased.value)

            original.unbindAll()

            assertEquals(if (revert) 1 else 2, original.value)
        }

        testLeaseAndReturnedOnUnbindAll(false)
        setUp()
        testLeaseAndReturnedOnUnbindAll(true)
    }

    @Test
    fun testConsecutiveLeases() {
        val leased1 = original.beginLease(false)
        leased1.returnBindable()
        val leased2 = original.beginLease(false)
        leased2.returnBindable()
    }

    @Test
    fun testModifyReturnFail() {
        val leased1 = original.beginLease(false)
        leased1.returnBindable()

        assertThrows<UnsupportedOperationException> { leased1.value = 2 }
        assertThrows<UnsupportedOperationException> { leased1.disabled = true }
        assertThrows<UnsupportedOperationException> { leased1.returnBindable() }
    }

    @Test
    fun testDoubleLeaseFails() {
        original.beginLease(false)
        assertThrows<UnsupportedOperationException> { original.beginLease(false) }
    }

    @Test
    fun testIncorrectEndLease() {
        assertThrows<UnsupportedOperationException> { original.endLease(original) }

        original.beginLease(false)
        assertThrows<UnsupportedOperationException> { original.endLease(original) }
    }

    @Test
    fun testDisabledStateDuringLease() {
        assertFalse(original.disabled)

        val leased = original.beginLease(true)

        assertTrue(original.disabled)
        assertTrue(leased.disabled)

        assertThrows<UnsupportedOperationException> { original.disabled = false }

        leased.disabled = false

        assertFalse(leased.disabled)
        assertFalse(original.disabled)

        original.value = 2

        assertThrows<UnsupportedOperationException> { original.disabled = true }
        assertFalse(original.disabled)
        assertFalse(leased.disabled)

        leased.disabled = true

        assertTrue(original.disabled)
        assertTrue(leased.disabled)

        leased.returnBindable()

        assertFalse(original.disabled)
    }

    @Test
    fun testDisabledChangeViaBindings() {
        original.beginLease(true)

        val bound = original.getBoundCopy()

        assertThrows<UnsupportedOperationException> { bound.disabled = false }
        assertTrue(original.disabled)
    }

    @Test
    fun testDisabledChangeViaBindingsToLeased() {
        var changedState: Boolean? = null
        original.disabledChanged += { changedState = it }

        val leased = original.beginLease(true)

        val bound = leased.getBoundCopy()

        bound.disabled = false

        assertEquals(changedState, false)
        assertFalse(original.disabled)
    }

    @Test
    fun testValueChangeViaBindings() {
        original.beginLease(true)

        val bound = original.getBoundCopy()

        assertThrows<UnsupportedOperationException> { bound.value = 2 }
        assertEquals(1, original.value)
    }

    @Test
    fun testDisabledRevertedAfterLease() {
        fun testDisabledRevertedAfterLease(revert: Boolean) {
            var changedState: Boolean? = null

            original.disabled = true
            original.disabledChanged += { changedState = it }

            val leased = original.beginLease(revert)
            leased.returnBindable()

            assertTrue(original.disabled)
            assertFalse(changedState != null)
        }

        testDisabledRevertedAfterLease(false)
        setUp()
        testDisabledRevertedAfterLease(true)
    }

    @Test
    fun testLeaseFromBoundBindable() {
        val copy = original.getBoundCopy()

        val leased = copy.beginLease(true)

        assertThrows<UnsupportedOperationException> { original.beginLease(false) }

        assertThrows<UnsupportedOperationException> { copy.beginLease(false) }

        leased.value = 2

        assertEquals(2, original.value)
        assertEquals(original.value, leased.value)
        assertEquals(original.value, copy.value)

        val leasedCopy = leased.getBoundCopy()
        leasedCopy.value = 3

        assertEquals(3, original.value)
        assertEquals(original.value, leased.value)
        assertEquals(original.value, copy.value)

        leasedCopy.disabled = false
        leasedCopy.disabled = true

        leased.returnBindable()

        original.value = 1

        assertEquals(1, original.value)
        assertEquals(original.value, copy.value)
        assertFalse(original.disabled)
    }

    @Test
    fun `testCan'tLeaseFromLease`() {
        val leased = original.beginLease(false)
        assertThrows<UnsupportedOperationException> { leased.beginLease(false) }
    }

    @Test
    fun `testCan'tLeaseFromBindingChain`() {
        val leased = original.beginLease(true)

        val copy = leased.getBoundCopy()

        assertThrows<UnsupportedOperationException> { (copy as LeasedBindable<Int>).returnBindable() }
    }

    @Test
    fun testUnbindAllReturnsLease() {
        val leased = original.beginLease(true)
        leased.unbindAll()
        leased.unbindAll()
    }
}