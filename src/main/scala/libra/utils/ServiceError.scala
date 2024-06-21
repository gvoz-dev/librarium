package libra.utils

import zio.schema.*

/** ADT ошибок сервиса. */
sealed trait ServiceError

object ServiceError:

  /** HTTP 500. */
  final case class InternalServerError(
      message: String = "Internal Server Error"
  ) extends ServiceError

  /** HTTP 404. */
  final case class NotFound(
      message: String = "Not Found"
  ) extends ServiceError

  /** HTTP 401. */
  final case class Unauthorized(
      message: String = "Unauthorized"
  ) extends ServiceError

  /** HTTP 400. */
  final case class BadRequest(
      message: String = "Bad Request"
  ) extends ServiceError

  /** Тип-сумма ошибок репозитория. */
  type RepositoryError = InternalServerError | NotFound

  /** Тип-сумма ошибок клиента. */
  type ClientError = NotFound | Unauthorized | BadRequest

  /** Гивены ZIO-схем ошибок сервиса. */
  given Schema[ServiceError] = DeriveSchema.gen
  given Schema[ServiceError.InternalServerError] = DeriveSchema.gen
  given Schema[ServiceError.NotFound] = DeriveSchema.gen
  given Schema[ServiceError.Unauthorized] = DeriveSchema.gen
  given Schema[ServiceError.BadRequest] = DeriveSchema.gen

end ServiceError
