package libra.utils

import zio.schema.*

/** ADT ошибок сервиса. */
sealed trait ServiceError

object ServiceError:

  /** Ошибка базы данных. */
  final case class DatabaseError(message: String = "Database error")
      extends ServiceError

  /** Данные не найдены. */
  final case class NotFoundError(message: String = "Not found")
      extends ServiceError

  /** Ошибка репозитория. */
  type RepositoryError = DatabaseError | NotFoundError

  /** Ошибка аутентификации. */
  final case class AuthenticationError(message: String = "Authentication error")
      extends ServiceError

  /** Гивены ZIO-схем ошибок сервиса. */
  given Schema[ServiceError.DatabaseError] = DeriveSchema.gen
  given Schema[ServiceError.NotFoundError] = DeriveSchema.gen
  given Schema[ServiceError.AuthenticationError] = DeriveSchema.gen

end ServiceError
