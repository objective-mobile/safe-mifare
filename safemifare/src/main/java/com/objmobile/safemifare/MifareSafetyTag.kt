package com.objmobile.safemifare

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import com.google.common.io.BaseEncoding

class MifareSafetyTag(private val tag: Tag) : SafetyTag {
    override fun readTagData(): ByteArray {
        val mfc = MifareClassic.get(tag)
        mfc.connect()
        val numSectors = mfc.sectorCount
        Log.d("MifareSafetyTag","readTagData: num sectors "+numSectors)
        val mfcArray = ArrayList<Byte>()

        val firstBlocks = mfc.getBlockCountInSector(0)
        var blockIndexLast = firstBlocks

        for (sectorIndex in (1 until numSectors)) {
            var authSector = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT)
            if (!authSector)
                authSector =  mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_NFC_FORUM)
            if (!authSector)
                authSector = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)

            if (!authSector)
                throw Exception("Error auth sector ${sectorIndex}")


            val numBlocks = mfc.getBlockCountInSector(sectorIndex)
            Log.d("MifareSafetyTag","readTagData: num blocks ${sectorIndex} ${numBlocks}")

            for (blockIndex in (0 until numBlocks - 1)) {
                val readBlock = blockIndexLast + blockIndex
                mfcArray.addAll(
                    mfc.readBlock(
                        readBlock
                    ).toTypedArray()
                )
            }
            blockIndexLast += numBlocks

        }
        mfc.close()
        return mfcArray.toByteArray()
    }
    override fun writeTagData(data: ByteArray) {
        val mfc = MifareClassic.get(tag)
        mfc.connect()
        val numSectors = mfc.sectorCount
        Log.d("MifareSafetyTag","writeTagData: num blocks "+numSectors)
        val blockSize = 16
        val authZeroSector = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT)
        if (!authZeroSector){
            Log.d("D_MifareNfcController","writeTagData: sector 0");
            throw Exception("Error auth sector 0")
        }
        val firstBlocks = mfc.getBlockCountInSector(0)
        val mfcArray = ByteArray((mfc.blockCount * blockSize) - (firstBlocks * blockSize))
        data.copyInto(mfcArray)
        var dataIndex = 0
        var blockIndexLast = firstBlocks
        for (sectorIndex in (1 until numSectors)) {
            val authSector = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT)
            if (!authSector) {
                Log.d("D_MifareNfcController","writeTagData: sector ${sectorIndex}");
                throw Exception("Error auth sector ${sectorIndex}")
            }
            val numBlocks = mfc.getBlockCountInSector(sectorIndex)
            Log.d("MifareSafetyTag","writeTagData: num blocks "+numBlocks)
            for (blockIndex in (0 until numBlocks - 1)) {
                val writeBlock = blockIndexLast + blockIndex
                Log.d("MifareNfcController", "writeTagData: ${sectorIndex} ${writeBlock}");
                mfc.writeBlock(
                    writeBlock, mfcArray.copyOfRange(
                        dataIndex,
                        dataIndex + 16
                    )
                )
                dataIndex += 16
            }
            blockIndexLast += numBlocks
        }
        try {
            mfc.close()
        } catch (e: Exception) {
            Log.d("MifareNfcController", "writeTagData: ${e.message}");
        }
    }
    override fun readTagId() = BaseEncoding.base16().encode(tag.id)
}