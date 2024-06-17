package itcube.utils

import zio.schema.*

/** Типы ошибок приложения. */
object Errors:

  /** Ошибка сервиса. */
  sealed trait ServiceError

  /** Ошибка запроса к СУБД. */
  case class InvalidQueryError(message: String) extends ServiceError

  object InvalidQueryError:
    given schema: Schema[InvalidQueryError] = DeriveSchema.gen

  /** Ошибка отсутствия результатов запроса. */
  case class NotFoundError(message: String) extends ServiceError

  object NotFoundError:
    given schema: Schema[NotFoundError] = DeriveSchema.gen

  /** Ошибка HTTP. */
  sealed trait HttpError

  /** Ошибка аутентификации пользователя. */
  case class AuthenticationError(message: String) extends HttpError

  object AuthenticationError:
    given schema: Schema[AuthenticationError] = DeriveSchema.gen

  /** Некорректный запрос. */
  case class BadRequestError(message: String) extends HttpError

  object BadRequestError:
    given schema: Schema[BadRequestError] = DeriveSchema.gen

end Errors
