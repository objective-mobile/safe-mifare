package com.objmobile.safemifare

import android.nfc.tech.MifareClassic
import android.util.Log
import com.google.common.io.BaseEncoding

class BaseNfcDevice(private val tag: SafeTag) : NfcDevice {
    override suspend fun readTagData(): ByteArray {
        tag.connect()
        val numSectors = tag.sectorCount()
        val mfcArray = ArrayList<Byte>()
        val firstBlocks = tag.getBlockCountInSector(0)
        var blockIndexLast = firstBlocks

        for (sectorIndex in (1 until numSectors)) {
            tag.authenticateSectorWithKeyA(sectorIndex)
            val numBlocks = tag.getBlockCountInSector(sectorIndex)
            for (blockIndex in (0 until numBlocks - 1)) {
                val readBlock = blockIndexLast + blockIndex
                mfcArray.addAll(
                    tag.readBlock(
                        readBlock
                    ).toTypedArray()
                )
            }
            blockIndexLast += numBlocks
        }
        tag.close()
        return mfcArray.toByteArray()
    }

    override suspend fun writeTagData(data: ByteArray) {
        tag.connect()
        val numSectors = tag.sectorCount()
        val blockSize = 16
        tag.authenticateSectorWithKeyA(0)
        val firstBlocks = tag.getBlockCountInSector(0)
        val mfcArray = ByteArray((tag.blockCount() * blockSize) - (firstBlocks * blockSize))
        data.copyInto(mfcArray)
        var dataIndex = 0
        var blockIndexLast = firstBlocks
        for (sectorIndex in (1 until numSectors)) {
            tag.authenticateSectorWithKeyA(sectorIndex)
            val numBlocks = tag.getBlockCountInSector(sectorIndex)
            for (blockIndex in (0 until numBlocks - 1)) {
                val writeBlock = blockIndexLast + blockIndex
                tag.writeBlock(
                    writeBlock, mfcArray.copyOfRange(
                        dataIndex, dataIndex + 16
                    )
                )
                dataIndex += 16
            }
            blockIndexLast += numBlocks
        }
        try {
            tag.close()
        } catch (e: Exception) {
        }
    }

    override suspend fun readTagId() = BaseEncoding.base16().encode(tag.id())
}