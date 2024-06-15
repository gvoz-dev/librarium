package itcube

import zio.schema.*

package object services:

  /** Ошибка сервиса. */
  sealed trait ServiceError

  /** Ошибка запроса к СУБД. */
  case class QueryError(message: String) extends ServiceError

  object QueryError:
    given schema: Schema[QueryError] = DeriveSchema.gen

  /** Ошибка отсутствия результатов запроса. */
  case class NotFoundError(message: String) extends ServiceError

  object NotFoundError:
    given schema: Schema[NotFoundError] = DeriveSchema.gen

end services
