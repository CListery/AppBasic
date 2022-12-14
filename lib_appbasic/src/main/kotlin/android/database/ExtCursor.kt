package android.database


/**
 * Created by CYH on 2019-06-17 11:17
 */
@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
fun <T : Any> Cursor.get(columnIndex: Int, defaultVal: T): T {
    if (-1 == columnIndex || isNull(columnIndex)) {
        return defaultVal
    }
    return when (defaultVal) {
        is Long -> getLong(columnIndex)
        is Int -> getInt(columnIndex)
        is String -> getString(columnIndex)
        is Double -> getDouble(columnIndex)
        is Float -> getFloat(columnIndex)
        is ByteArray -> getBlob(columnIndex)
        is Boolean -> getInt(columnIndex) == 1
        else -> defaultVal
    } as T
}
