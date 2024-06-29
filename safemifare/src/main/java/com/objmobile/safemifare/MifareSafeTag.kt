package com.objmobile.safemifare

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import java.lang.Exception

class MifareSafeTag(private val tag: Tag, private val keys: List<ByteArray> = listOf(
    MifareClassic.KEY_DEFAULT, MifareClassic.KEY_NFC_FORUM, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY
)) : SafeTag {
    private val mfc = MifareClassic.get(tag)
    override fun connect() {
        mfc.connect()
    }

    override fun sectorCount(): Int = mfc.sectorCount
    override fun blockCount(): Int = mfc.blockCount
    override fun getBlockCountInSector(sectorIndex: Int): Int =
        mfc.getBlockCountInSector(sectorIndex)

    override fun authenticateSectorWithKeyA(sectorIndex: Int){
        keys.forEach {
            if (mfc.authenticateSectorWithKeyA(sectorIndex, it))
                return
        }
        throw Exception("Error authenticateSectorWithKeyA $sectorIndex \n keys: $keys")
    }

    override fun readBlock(blockIndex: Int): ByteArray =
        mfc.readBlock(blockIndex)

    override fun writeBlock(blockIndex: Int, data: ByteArray) =
        mfc.writeBlock(blockIndex, data)

    override fun close() =
        mfc.close()

    override fun id(): ByteArray =
        tag.id
    override fun keys(): List<ByteArray> =
        listOf(MifareClassic.KEY_DEFAULT, MifareClassic.KEY_NFC_FORUM, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)
}