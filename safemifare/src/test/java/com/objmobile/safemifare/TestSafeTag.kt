package com.objmobile.safemifare

class TestSafeTag(
    private val id: ByteArray,
    private val correctKey: ByteArray,
    private val keys: List<ByteArray>,
    private val sectors: Int = 16,
    private val blocks: Int = 4,
    private val blockSize: Int = 16) : SafeTag {

    private val store: Array<ByteArray> = Array(sectors * blocks, ({
        ByteArray(blockSize)
    }))
    override fun connect() = Unit
    override fun sectorCount(): Int = sectors
    override fun blockCount(): Int = sectors * blocks
    override fun getBlockCountInSector(sectorIndex: Int): Int = blocks
    override fun authenticateSectorWithKeyA(
        sectorIndex: Int
    ) {
        if (!keys.contains(correctKey))
            throw MifareAuthenticateException(sectorIndex, keys)
    }
    override fun readBlock(blockIndex: Int): ByteArray {
        return store[blockIndex]
    }
    override fun writeBlock(blockIndex: Int, data: ByteArray) {
        //TODO check block is first
        if (blockIndex == 0)
            throw FirstBlockException(blockIndex)

        store.set(blockIndex, data)
    }
    override fun close() {
    }
    override fun id(): ByteArray = id
    override fun keys(): List<ByteArray> = keys
}