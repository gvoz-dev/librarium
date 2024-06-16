package itcube

import zio.schema.*

package object services:

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

end services
