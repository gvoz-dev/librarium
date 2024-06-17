package itcube.utils

import itcube.config.SecurityConfig
import zio.*

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.util.Try

/** Функции для обеспечения безопасности. */
object Security:

  /** Получить секретный ключ. */
  def secret: URIO[SecurityConfig, String] =
    ZIO.serviceWithZIO[SecurityConfig](config => ZIO.succeed(config.secret))

  /** Захешировать пароль.
    *
    * @param password
    *   пароль
    */
  def hashPassword(password: String): Task[String] =
    ZIO.fromTry(
      Try {
        val data: Array[Byte] = password.getBytes(StandardCharsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val encodedHash = messageDigest.digest(data)
        bytesToHexString(encodedHash)
      }
    )
  end hashPassword

  /** Преобразовать массив байт в HEX-строку.
    *
    * @param bytes
    *   массив байт
    */
  private def bytesToHexString(bytes: Array[Byte]): String =
    bytes.foldLeft("")((acc, b) => acc + String.format("%02x", Byte.box(b)))

  /** Произвести валидацию пароля.
    *
    * @param password
    *   пароль
    * @param hash
    *   сохранённый хэш
    */
  def validatePassword(password: String, hash: String): UIO[Boolean] =
    hashPassword(password).map(_ == hash).orElse(ZIO.succeed(false))

end Security
