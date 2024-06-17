package itcube.rest.api

import itcube.entities.User
import itcube.repositories.user.UserRepository
import itcube.services.user.UserService
import zio.*
import zio.http.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

/** API пользователей. */
@deprecated
object UserRoutes {
  def apply(): Routes[UserRepository, Response] = {
    Routes(
      // GET /users
      // GET /users?name=:name
      Method.GET / "users" -> handler { (request: Request) =>
        {
          if (request.url.queryParams.isEmpty) {
            UserService.all
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                users => Response(body = Body.from(users))
              )
          } else {
            val names: Chunk[String] = request.url.queryParams("name")
            if (names.nonEmpty) {
              val name = names(0)
              UserService
                .findByName(name)
                .mapBoth(
                  error => Response.internalServerError(error.getMessage),
                  {
                    case Some(user) =>
                      Response(body = Body.from(user))
                    case None =>
                      Response.notFound(s"User $name not found!")
                  }
                )
            } else {
              ZIO.fail(Response.badRequest("No name query param"))
            }
          }
        }
      },

      // GET /users/:id
      Method.GET / "users" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            UserService
              .findById(id)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                {
                  case Some(user) =>
                    Response(body = Body.from(user))
                  case None =>
                    Response.notFound(s"User $id not found!")
                }
              )
          }
      },

      // POST /users
      Method.POST / "users" -> handler { (request: Request) =>
        for {
          user <- request.body.to[User].orElseFail(Response.badRequest)
          response <- UserService
            .create(user)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // PATCH /users
      Method.PATCH / "users" -> handler { (request: Request) =>
        for {
          _ <- ZIO.logInfo("User patched!")
          user <- request.body.to[User].orElseFail(Response.badRequest)
          response <- UserService
            .update(user)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // DELETE /users/:id
      Method.DELETE / "users" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            UserService
              .delete(id)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                _ => Response.ok
              )
          }
      }
    )
  }
}
