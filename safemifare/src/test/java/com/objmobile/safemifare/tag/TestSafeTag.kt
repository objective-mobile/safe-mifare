package com.objmobile.safemifare.tag

import com.objmobile.safemifare.FirstBlockException
import com.objmobile.safemifare.MifareAuthenticateException
import com.objmobile.safemifare.SafeTag

class TestSafeTag(
    private val id: ByteArray = "id".toByteArray(),
    private val correctKey: ByteArray = "key".toByteArray(),
    private val keys: List<ByteArray> = listOf(correctKey),
    private val sectors: Int = 16,
    private val blocks: Int = 4,
    private val blockSize: Int = 16,
    val store: Array<ByteArray> = Array(sectors * blocks, ({
        ByteArray(blockSize)
    }))
) : SafeTag {
    override fun connect() = Unit
    override fun sectorCount(): Int = sectors
    override fun blockCount(): Int = sectors * blocks
    override fun getBlockCountInSector(sectorIndex: Int): Int = blocks
    override fun authenticateSectorWithKeyA(
        sectorIndex: Int
    ) {
        if (!keys.map { it.toList() }.any { it == correctKey.toList() })
            throw MifareAuthenticateException(sectorIndex, keys)
    }
    override fun readBlock(blockIndex: Int): ByteArray {
        return store[blockIndex]
    }
    override fun writeBlock(blockIndex: Int, data: ByteArray) {
        if (blockIndex == 0 || (blockIndex+1) % blocks == 0)
            throw FirstBlockException(blockIndex)
        store.set(blockIndex, data)
    }
    override fun close() = Unit
    override fun id(): ByteArray = id
    override fun keys(): List<ByteArray> = keys
}