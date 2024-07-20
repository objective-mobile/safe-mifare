package com.objmobile.safemifare

import com.objmobile.safemifare.tag.TestSafeTag
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class NfcDeviceTest {
    private companion object {
        val testData =
            """MIFARE Classic family of tags is being used in short range (up to 10 centimeters) 
                |RFID applications where higher security and fast data reading systems are required. 
                |This family of tags have fast contactless communication speed (106 Kbit/s) between 
                |the card and the reader and uses CRYPTO1, a proprietary encryption algorithm created 
                |by NXP Semiconductors. It also supports anti-collision feature so that multiple cards
                | in the field can work. MIFARE® Ultralight differs from MIFARE® Classic family. 
                | The contactless communication is not encrypted. However, it is perfectly designed
                |  for single or limited-use tickets in public transport, event ticketing (i.e.
                |   stadiums, exhibitions) and loyalty applications.""".trimMargin()
    }

    @Test
    fun `Read test`() {
        val sectors = 16
        val blocks = 4
        val blockSize = 16
        val data = testData.toByteArray()
        var startIndex = 0
        val store: Array<ByteArray> = Array(sectors * blocks, ({ blockIndex ->
            if (blockIndex in listOf(0,1,2,3) || (blockIndex + 1) % blocks == 0) {
                ByteArray(blockSize)
            } else {
                val block = data.copyOfRange(startIndex, startIndex + blockSize)
                startIndex += blockSize
                block
            }
        }))
        val tag = TestSafeTag(store = store)
        val device = BaseNfcDevice(tag)
        runBlocking {
            assertArrayEquals(data, device.readTagData())
        }
    }

    @Test
    fun `Write test`() {
        val sectors = 16
        val blocks = 4
        val blockSize = 16
        val data = testData.toByteArray()
        var startIndex = 0
        val store: Array<ByteArray> = Array(sectors * blocks, ({ blockIndex ->
            if (blockIndex in listOf(0,1,2,3) || (blockIndex + 1) % blocks == 0) {
                ByteArray(blockSize)
            } else {
                val block = data.copyOfRange(startIndex, startIndex + blockSize)
                startIndex += blockSize
                block
            }
        }))
        val tag = TestSafeTag()
        val device = BaseNfcDevice(tag)
        runBlocking {
            device.writeTagData(data)
            assertArrayEquals(store, tag.store)
        }
    }

    @Test
    fun `Write and read test`() {
        val device = BaseNfcDevice(TestSafeTag())
        val data = testData.toByteArray()
        runBlocking {
            device.writeTagData(data)
            assertArrayEquals(data, device.readTagData().copyOfRange(0, data.size))
            println(String(device.readTagData().copyOfRange(0, data.size)))
        }
    }

    @Test
    fun `Authenticate correct at first place test`() {
        val correctKey = "key"
        val keys = listOf(
            correctKey.toByteArray(),
            "key2".toByteArray(),
            "key3".toByteArray(),
            "key4".toByteArray()
        )
        val device = BaseNfcDevice(TestSafeTag(correctKey = correctKey.toByteArray(), keys = keys))
        val data = testData.toByteArray()
        runBlocking {
            device.writeTagData(data)
            assertArrayEquals(data, device.readTagData().copyOfRange(0, data.size))
            println(String(device.readTagData().copyOfRange(0, data.size)))
        }
    }
}