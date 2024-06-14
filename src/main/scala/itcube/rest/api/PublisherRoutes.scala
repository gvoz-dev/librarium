package itcube.rest.api

import itcube.entities.Publisher
import itcube.repositories.publisher.PublisherRepository
import itcube.services.publisher.PublisherService
import zio.*
import zio.http.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

/** API издателей. */
object PublisherRoutes {
  def apply(): Routes[PublisherRepository, Response] = {
    Routes(
      // GET /publishers
      // GET /publishers?name=:name
      Method.GET / "publishers" -> handler { (request: Request) =>
        {
          if (request.url.queryParams.isEmpty) {
            PublisherService.all
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                publishers => Response(body = Body.from(publishers))
              )
          } else {
            val names: Chunk[String] = request.url.queryParams("name")
            if (names.nonEmpty) {
              val name = names(0)
              PublisherService
                .findByName(name)
                .mapBoth(
                  error => Response.internalServerError(error.getMessage),
                  {
                    case Some(publisher) =>
                      Response(body = Body.from(publisher))
                    case None =>
                      Response.notFound(s"Publisher $name not found!")
                  }
                )
            } else {
              ZIO.fail(Response.badRequest("No name query param"))
            }
          }
        }
      },

      // GET /publishers/:id
      Method.GET / "publishers" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            PublisherService
              .findById(id)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                {
                  case Some(publisher) =>
                    Response(body = Body.from(publisher))
                  case None =>
                    Response.notFound(s"Publisher $id not found!")
                }
              )
          }
      },

      // POST /publishers
      Method.POST / "publishers" -> handler { (request: Request) =>
        for {
          publisher <- request.body
            .to[Publisher]
            .orElseFail(Response.badRequest)
          response <- PublisherService
            .create(publisher)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // PATCH /publishers
      Method.PATCH / "publishers" -> handler { (request: Request) =>
        for {
          publisher <- request.body
            .to[Publisher]
            .orElseFail(Response.badRequest)
          response <- PublisherService
            .update(publisher)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // DELETE /publishers/:id
      Method.DELETE / "publishers" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            PublisherService
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
