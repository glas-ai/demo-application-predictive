package com.autoencoder.glasdemoapp.shared.utils.extensions

import android.content.Context
import java.io.File

fun ByteArray?.saveToTemporaryFile(context: Context?): String? {
    var returnPath: String? = null
    this?.let {
        val mFolder = File("${(context ?: return returnPath).filesDir}/Profile")
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
        val tmpFile = File(mFolder.absolutePath, "Glas_Profile")
        tmpFile.writeBytes(this)
        returnPath = tmpFile.absolutePath
    }
    return returnPath
}
