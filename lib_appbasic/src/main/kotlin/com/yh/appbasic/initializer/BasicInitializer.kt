package com.yh.appbasic.initializer

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class BasicInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        AppBasicShare.install(this)
        return true
    }
    
    @Deprecated("", ReplaceWith(""))
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null
    
    @Deprecated("", ReplaceWith(""))
    override fun getType(uri: Uri): String? = null
    
    @Deprecated("", ReplaceWith(""))
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    
    @Deprecated("", ReplaceWith(""))
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
    
    @Deprecated("", ReplaceWith(""))
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}
