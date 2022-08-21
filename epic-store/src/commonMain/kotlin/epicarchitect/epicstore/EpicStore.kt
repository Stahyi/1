package epicarchitect.epicstore

class EpicStore(
    private val isClearNeeded: () -> Boolean = { false },
    private val doBeforeClear: (key: Any?, value: Any?) -> Unit = { _, _ -> },
    private val doAfterClear: () -> Unit = { },
) {

    private val map = mutableMapOf<Any?, Any?>()

    fun clearIfNeeded() {
        map.values.forEach {
            if (it is EpicStore) {
                it.clearIfNeeded()
            }
        }

        if (isClearNeeded()) {
            map.forEach(doBeforeClear)
            map.clear()
            doAfterClear()
        }
    }

    operator fun get(key: Any?) = map[key]

    operator fun set(key: Any?, value: Any?) = map.set(key, value)

}

inline fun <reified T> EpicStore.getOrSet(
    key: Any? = T::class, provide: () -> T
) = (get(key) ?: provide().also {
    set(key, it)
}) as T