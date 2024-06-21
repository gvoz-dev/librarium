package libra.utils

import libra.config.SecurityConfig
import zio.*

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/** Функции для обеспечения безопасности. */
object Security:

  /** Получить секретный ключ. */
  def secret: URIO[SecurityConfig, String] =
    ZIO.serviceWithZIO[SecurityConfig](config => ZIO.succeed(config.secret))

  /** Захешировать пароль.
    *
    * Для хеширования используется алгоритм SHA-256.
    *
    * @param password
    *   пароль
    */
  def hashPassword(password: String): UIO[String] =
    ZIO.succeed {
      val data: Array[Byte] = password.getBytes(StandardCharsets.UTF_8)
      val messageDigest = MessageDigest.getInstance("SHA-256")
      val encodedHash = messageDigest.digest(data)
      bytesToHexString(encodedHash)
    }

  /** Преобразовать массив байт в HEX-строку.
    *
    * @param bytes
    *   массив байт
    */
  private def bytesToHexString(bytes: Array[Byte]): String =
    bytes.foldLeft("")((acc, b) => acc + String.format("%02x", b))

  /** Произвести валидацию пароля.
    *
    * @param password
    *   пароль
    * @param hash
    *   сохранённый хэш
    */
  def validatePassword(password: String, hash: String): UIO[Boolean] =
    hashPassword(password).map(_ == hash)

  /** Произвести валидацию адреса электронной почты.
    *
    * @param email
    *   адрес электронной почты
    */
  def validateEmail(email: String): UIO[Boolean] =
    ZIO.succeed {
      // TODO: написать нормальную реализацию с использованием регулярок
      email.contains("@")
    }

end Security
