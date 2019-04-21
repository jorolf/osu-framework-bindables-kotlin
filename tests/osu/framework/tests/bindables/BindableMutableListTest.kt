package osu.framework.tests.bindables

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import osu.framework.bindables.BindableMutableList
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class BindableMutableListTest {
    private lateinit var bindableStringList: BindableMutableList<String>

    @BeforeEach
    fun setUp() {
        bindableStringList = BindableMutableList()
    }

    @Test
    fun testConstructorDoesNotAddItemsByDefault() {
        assertTrue(bindableStringList.isEmpty())
    }

    @Test
    fun testConstructorWithItemsAddsItemsInternally() {
        val list = listOf("ok", "nope", "random", "")

        val bindableMutableList = BindableMutableList(list)

        assertAll({
            assertAll(list.map { str -> { assertTrue(bindableMutableList.contains(str)) } })
        }, {
            assertEquals(list.size, bindableMutableList.size)
        })
    }

    @Test
    fun testGetRetrievesObjectAtIndex() {
        bindableStringList.add("0")
        bindableStringList.add("1")
        bindableStringList.add("2")

        assertEquals("1", bindableStringList[1])
    }

    @Test
    fun testSetMutatesObjectAtIndex() {
        bindableStringList.add("0")
        bindableStringList.add("1")
        bindableStringList[1] = "2"

        assertEquals("2", bindableStringList[1])
    }

    @Test
    fun testGetWhileDisabledDoesNotThrowUnsupportedOperationException() {
        bindableStringList.add("0")
        bindableStringList.disabled = true

        assertEquals("0", bindableStringList[0])
    }

    @Test
    fun testSetWhileDisabledThrowsUnsupportedOperationException() {
        bindableStringList.add("0")
        bindableStringList.disabled = true

        assertThrows<UnsupportedOperationException> { bindableStringList[0] = "1" }
    }

    @Test
    fun testSetNotifiesSubscribers() {
        bindableStringList.add("0")

        lateinit var addedItem: String
        lateinit var removedItem: String

        bindableStringList.itemAdded += { addedItem = it }
        bindableStringList.itemRemoved += { removedItem = it }

        bindableStringList[0] = "1"

        assertEquals("0", removedItem)
        assertEquals("1", addedItem)
    }

    @Test
    fun testSetNotifiesBoundLists() {
        bindableStringList.add("0")

        lateinit var addedItem: String
        lateinit var removedItem: String

        val list = BindableMutableList<String>()
        list.bindTo(bindableStringList)
        bindableStringList.itemAdded += { addedItem = it }
        bindableStringList.itemRemoved += { removedItem = it }

        bindableStringList[0] = "1"

        assertEquals("0", removedItem)
        assertEquals("1", addedItem)
    }

    @Test
    fun testAddInsertsItemAtIndex() {
        bindableStringList.add("0")
        bindableStringList.add("2")

        bindableStringList.add(1, "1")

        assertAll(
            { assertEquals("0", bindableStringList[0]) },
            { assertEquals("1", bindableStringList[1]) },
            { assertEquals("2", bindableStringList[2]) }
        )
    }

    @Test
    fun testAddNotifiesSubscribers() {
        bindableStringList.add("0")
        bindableStringList.add("2")

        var wasAdded = false
        bindableStringList.itemAdded += { wasAdded = true }
        bindableStringList.add(1, "1")

        assertTrue(wasAdded)
    }

    @Test
    fun testAddNotifiesBoundLists() {
        bindableStringList.add("0")
        bindableStringList.add("2")

        var wasAdded = false

        val list = BindableMutableList<String>()
        list.bindTo(bindableStringList)
        list.itemAdded += { wasAdded = true }

        bindableStringList.add(1, "1")

        assertTrue(wasAdded)
    }

    @Test
    fun testAddInsertsItemAtIndexInBoundList() {
        bindableStringList.add("0")
        bindableStringList.add("2")

        val list = BindableMutableList<String>()
        list.bindTo(bindableStringList)

        bindableStringList.add(1, "1")

        assertAll(
            { assertEquals("0", list[0]) },
            { assertEquals("1", list[1]) },
            { assertEquals("2", list[2]) }
        )
    }

    @Test
    fun testRemoveAtRemovesItemAtIndex() {
        bindableStringList.add("0")
        bindableStringList.add("1")
        bindableStringList.add("2")

        bindableStringList.removeAt(1)

        assertEquals("0", bindableStringList[0])
        assertEquals("2", bindableStringList[1])
    }

    @Test
    fun testRemoveAtWithDisabledListThrowsUnsupportedOperationException() {
        bindableStringList.add("abc")
        bindableStringList.disabled = true

        assertThrows<UnsupportedOperationException> { bindableStringList.removeAt(0) }
    }

    @Test
    fun testRemoveAtNotifiesSubscribers() {
        var wasRemoved = false

        bindableStringList.add("abc")
        bindableStringList.itemRemoved += { wasRemoved = true }

        bindableStringList.removeAt(0)

        assertTrue(wasRemoved)
    }

    @Test
    fun testRemoveAtNotifiesBoundLists() {
        bindableStringList.add("abc")

        var wasRemoved = false

        val list = BindableMutableList<String>()
        list.bindTo(bindableStringList)
        list.itemRemoved += { wasRemoved = true }

        bindableStringList.removeAt(0)

        assertTrue(wasRemoved)
    }

    @Test
    fun testDisabledWhenSetToTrueNotifiesSubscriber() {
        var isDisabled: Boolean? = null
        bindableStringList.disabledChanged += { isDisabled = it }

        bindableStringList.disabled = true
        assertTrue(isDisabled ?: false)
    }

    @Test
    fun testDisabledWhenSetToTrueNotifiesSubscribers() {
        var isDisabledA: Boolean? = null
        var isDisabledB: Boolean? = null
        var isDisabledC: Boolean? = null
        bindableStringList.disabledChanged += { isDisabledA = it }
        bindableStringList.disabledChanged += { isDisabledB = it }
        bindableStringList.disabledChanged += { isDisabledC = it }

        bindableStringList.disabled = true
        assertAll(
            { assertTrue(isDisabledA ?: false) },
            { assertTrue(isDisabledB ?: false) },
            { assertTrue(isDisabledC ?: false) }
        )
    }

    @Test
    fun testDisabledWhenSetToCurrentValueDoesNotNotifySubscriber() {
        bindableStringList.disabledChanged += { fail() }
        bindableStringList.disabled = bindableStringList.disabled
    }

    @Test
    fun testDisabledWhenSetTOCurrentValueDoesNotNotifySubscribers() {
        bindableStringList.disabledChanged += { fail() }
        bindableStringList.disabledChanged += { fail() }
        bindableStringList.disabledChanged += { fail() }
        bindableStringList.disabled = bindableStringList.disabled
    }

    @Test
    fun testDisabledNotifiesBoundLists() {
        val list = BindableMutableList<String>()
        list.bindTo(bindableStringList)

        bindableStringList.disabled = true

        assertTrue(list.disabled)
    }

    @Test
    fun testBoundCopyWithAdd() {
        val boundCopy = bindableStringList.getBoundCopy()
        var boundCopyItemAdded = false
        boundCopy.itemAdded += { boundCopyItemAdded = true }

        bindableStringList.add("test")

        assertTrue(boundCopyItemAdded)
    }
}