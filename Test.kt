import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.io.File

fun main() {
    val SECRET_KEY = "MySecretKeyForIPTVChannels4000!!"
    val IV = "16BytesLongIV!!!"
    val encryptedBase64 = File("system_config.dat").readText().trim()
    
    try {
        val encryptedBytes = Base64.getDecoder().decode(encryptedBase64)
        val key = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
        val ivSpec = IvParameterSpec(IV.toByteArray(Charsets.UTF_8))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        println(String(decryptedBytes, Charsets.UTF_8).take(200))
    } catch(e: Exception) {
        e.printStackTrace()
    }
}
