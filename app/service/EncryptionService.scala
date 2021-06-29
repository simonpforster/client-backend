package service

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.spec.{GCMParameterSpec, SecretKeySpec}
import javax.crypto.{Cipher, SecretKey}
import javax.inject.{Inject, Singleton}

@Singleton
class EncryptionService @Inject()(configuration: play.api.Configuration)() {
  val AES_KEY_SIZE = 256
  val GCM_IV_LENGTH = 12
  val GCM_TAG_LENGTH: Int = 16

  def getKey: SecretKeySpec = {
    val appKeyString = configuration.underlying.getString("play.http.secret.key")
    val decodedString = Base64.getDecoder.decode(appKeyString)
    val originalKey = new SecretKeySpec(decodedString, 0, decodedString.length, "AES")
    originalKey
  }

  def getNonce: Array[Byte] = {
    val nonce = BigInt(GCM_IV_LENGTH).toByteArray
    val random = new SecureRandom()
    random.nextBytes(nonce)
    nonce
  }

  def encrypt(plainText: Array[Byte], key: SecretKey, nonce: Array[Byte]): Array[Byte] = {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val keySpec = new SecretKeySpec(key.getEncoded, "AES")
    val gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec)
    val cipherText = cipher.doFinal(plainText)
    cipherText
  }

  def decrypt(cipherText: Array[Byte], key: SecretKey, nonce: Array[Byte]): String = {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val keySpec = new SecretKeySpec(key.getEncoded, "AES")
    val gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce)
    cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)
    val decrypted = cipher.doFinal(cipherText)
    val decryptedString = decrypted.map(_.toChar).mkString
    decryptedString
  }
}
