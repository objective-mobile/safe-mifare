package com.objmobile.safemifare

interface SafeTag {
    fun connect()
    fun sectorCount(): Int
    fun blockCount(): Int
    fun getBlockCountInSector(sectorIndex: Int): Int
    fun authenticateSectorWithKeyA(sectorIndex: Int)
    fun readBlock(blockIndex: Int): ByteArray
    fun writeBlock(blockIndex: Int, data: ByteArray)
    fun close();
    fun id(): ByteArray
    fun keys(): List<ByteArray>
}