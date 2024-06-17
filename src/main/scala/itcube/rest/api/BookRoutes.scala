package itcube.rest.api

import itcube.entities.{Book, Comment}
import itcube.repositories.book.BookRepository
import itcube.repositories.comment.CommentRepository
import itcube.services.book.BookService
import itcube.services.comment.CommentService
import zio.*
import zio.http.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

/** API книг. */
@deprecated
object BookRoutes {
  def apply(): Routes[BookRepository & CommentRepository, Response] = {
    Routes(
      // GET /books
      // GET /books?title=:title
      Method.GET / "books" -> handler { (request: Request) =>
        {
          if (request.url.queryParams.isEmpty) {
            BookService.all
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                books => Response(body = Body.from(books))
              )
          } else {
            val titles: Chunk[String] = request.url.queryParams("title")
            if (titles.nonEmpty) {
              val title = titles(0)
              BookService
                .findByTitle(title)
                .mapBoth(
                  error => Response.internalServerError(error.getMessage),
                  {
                    case Some(book) =>
                      Response(body = Body.from(book))
                    case None =>
                      Response.notFound(s"Book $title not found!")
                  }
                )
            } else {
              ZIO.fail(Response.badRequest("No name query param"))
            }
          }
        }
      },

      // GET /books/:id
      Method.GET / "books" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            BookService
              .findById(id)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                {
                  case Some(book) =>
                    Response(body = Body.from(book))
                  case None =>
                    Response.notFound(s"Book $id not found!")
                }
              )
          }
      },

      // POST /books
      Method.POST / "books" -> handler { (request: Request) =>
        for {
          book <- request.body.to[Book].orElseFail(Response.badRequest)
          response <- BookService
            .create(book)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // PATCH /books
      Method.PATCH / "books" -> handler { (request: Request) =>
        for {
          book <- request.body.to[Book].orElseFail(Response.badRequest)
          response <- BookService
            .update(book)
            .mapBoth(
              error => Response.internalServerError(error.getMessage),
              success => Response(body = Body.from(success))
            )
        } yield response
      },

      // DELETE /books/:id
      Method.DELETE / "books" / string("id") -> handler {
        (id: String, _: Request) =>
          {
            BookService
              .delete(id)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                _ => Response.ok
              )
          }
      },

      // POST /comments/:bookId/:userId/
      Method.POST / "comments" / string("bookId") / string(
        "userId"
      ) -> handler { (bookId: String, userId: String, request: Request) =>
        {
          for {
            comment <- request.body.to[Comment].orElseFail(Response.badRequest)
            response <- CommentService
              .create(comment, userId, bookId)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                _ => Response.ok
              )
          } yield response
        }
      },

      // GET /comments/:bookId/:userId/
      Method.GET / "comments" / string("bookId") / string("userId") -> handler {
        (bookId: String, userId: String, request: Request) =>
          {
            CommentService
              .findByUserAndBook(userId, bookId)
              .mapBoth(
                error => Response.internalServerError(error.getMessage),
                comments => Response(body = Body.from(comments))
              )
          }
      }
    )
  }
}
