package com.dstwrtv.app.core.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object CryptoHelper {
    private const val SECRET_KEY = "MySecretKeyForIPTVChannels4000!!"
    private const val IV = "16BytesLongIV!!!"

    fun encryptString(plainText: String): String? {
        return try {
            val key = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun decryptString(encryptedBase64: String): String? {
        return try {
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val key = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            // Return null if it is not valid AES, indicating we should fall back to plain text
            null
        }
    }

    fun decrypt(encryptedBase64: String): String? {
        return try {
            val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)
            val key = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            val ivSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
            
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            
            // GZIP decompression
            val bais = ByteArrayInputStream(decryptedBytes)
            val gzis = GZIPInputStream(bais)
            val buffer = ByteArray(1024)
            val baos = ByteArrayOutputStream()
            var len: Int
            while (gzis.read(buffer).also { len = it } != -1) {
                baos.write(buffer, 0, len)
            }
            baos.toString(Charsets.UTF_8.name())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
