package com.objmobile.safemifare

class TestSafeTag : SafeTag {
    private companion object {
        const val sectors = 16
        const val blocks = 4
        const val blockSize = 16
    }

    private val store: Array<ByteArray> = Array(sectors * blocks, ({
        ByteArray(blockSize)
    }))

    override fun connect() {
    }

    override fun sectorCount(): Int = sectors
    override fun blockCount(): Int = sectors * blocks
    override fun getBlockCountInSector(sectorIndex: Int): Int = blocks
    override fun authenticateSectorWithKeyA(
        sectorIndex: Int
    ) {

    }

    override fun readBlock(blockIndex: Int): ByteArray {
        return store[blockIndex]
    }

    override fun writeBlock(blockIndex: Int, data: ByteArray) {
        //TODO check block is first
        store.set(blockIndex, data)
    }

    override fun close() {
    }

    override fun id(): ByteArray {
        return "test".toByteArray()
    }

    override fun keys(): List<ByteArray> = listOf("key".toByteArray())
}