package com.objmobile.safemifare

interface NfcDevice {

    suspend fun readTagId(): String

    suspend fun readTagData(): ByteArray

    suspend fun writeTagData(data: ByteArray)

}