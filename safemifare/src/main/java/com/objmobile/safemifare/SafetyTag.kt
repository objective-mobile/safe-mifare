package com.objmobile.safemifare

import android.nfc.Tag

interface SafetyTag {

    fun readTagId(): String

    fun readTagData(): ByteArray

    fun writeTagData(data: ByteArray)

}