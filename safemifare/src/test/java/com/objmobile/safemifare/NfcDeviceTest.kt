package com.objmobile.safemifare

import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class NfcDeviceTest {
    @Test
    fun `read and write test`() {
        val device = BaseNfcDevice(TestSafeTag())
        val testData = "test data".toByteArray()
        runBlocking {
            device.writeTagData(testData)
            assertArrayEquals(testData, device.readTagData().copyOfRange(0,testData.size))
            println(String( device.readTagData().copyOfRange(0,testData.size)))
        }
    }
}