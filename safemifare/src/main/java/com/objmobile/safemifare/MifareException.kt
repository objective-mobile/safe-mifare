package com.objmobile.safemifare

abstract class MifareException(mifareMessage: String) : Exception(mifareMessage)
class MifareAuthenticateException(
    sectorIndex: Int, keys: List<ByteArray>
) : MifareException("Error authenticateSectorWithKeyA $sectorIndex \n keys: $keys")

class FirstBlockException(
    blockIndex: Int
) : MifareException("Attempt to write to the first block of the sector. Block index - $blockIndex.")