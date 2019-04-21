## osu.Framework.Bindables in Kotlin

This is a translation of the bindables from the [osu-framework](https://github.com/ppy/osu-framework) for kotlin.  

Join our [discord server](https://discord.gg/mvwxGPR) if you have questions related to this translation
 or the osu-framework in general.  
 For Examples see the ``examples`` folder.

Most thing are as you would expect them to be translated, there are however some notable changes:

### General

Every method and every property is now lowercase (e.g. ``Value`` -> ``value``, ``Disabled`` -> ``disabled``).  

There is a special ``Event<T>`` class that represents c# events which kotlin does not have.

There are no interfaces. Interfaces like ``IBindable`` or ``IParseable`` have been removed.
If you need them create a issue and state your use case.

``LockedWeakList<T>`` is now ``SynchronizedWeakList<T>``.  

``BindableList<T>`` is now ``BindableMutableList<T>``.

``Cached<T>`` has been renamed to ``TypedCached<T>`` because of limitations of the JVM. ``Cached`` stays the same.  

``internal`` packages should be treated as *internal*. Their purpose is to circumvent certain limitations of kotlin.

### Bindables

The constructor signature of a ``Bindable<T>`` is ``Bindable<T>(initialValue: T, clazz: Class<T>)``.
The first value is the initial value this ``Bindable<T>`` has
and the second value is the class that this ``Bindable<T>`` saves.
The reason for that is that the generic type can't be queried at runtime because of the generic type erasure of the JVM.

A special inline method was added to assist this problem: ``Bindable.new<T>(value: T)``.

There are also special versions of this method for nullable bindables. Their ``default`` value is initialized to ``null``.

The ``parse(input: Any)`` has also changed a bit. It does not allow null values to be passed
and it allows custom ``String`` parsers now.
Whenever a ``String`` is passed to this method it tries to find a static ``valueOf(str: String)`` method on the generic
type.
Classes like ``Boolean`` or ``Long`` have these already implemented.
(Note: When using primitive types like ``Int`` or ``Long`` then the constructor expects the object types)

``bindValueChanged`` and ``bindDisabledChanged`` have had their parameters swapped.
This allows them to be used more easily in kotlin.

The description properties were also removed.

#### Leased Bindables

``LeasedBindable<T>`` stayed mostly the same. The primary constructor is private now.

### Bindable Numbers

The ``getBoundCopy()`` methods of ``BindableNumber<T>`` have been renamed to ``getBoundNumberCopy()`` so that they don't
overshadow their "original" method.

``isInteger`` checks the precision instead of the class now. This change was made to allow for custom number classes.
(Like ``BigDecimal``)

The default values of bindable numbers are ``0``

#### ``BindableNumber<T>`` only changes

The constructor now takes in the default values for the precision, the min value and the max value.
It also required functions for converting a number into ``T`` and adding ``T``.

### Bindable Lists

The ``itemAdded`` and ``itemRemoved`` events fire for each item and not for the whole "batch".

``BindableMutableList<T>`` is fully compatible with ``java.util.List<T>`` or ``kotlin.collections.MutableList<T>``.