package no.jergan.puertorico

import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import no.jergan.puertorico.model.{World}
import org.http4s.dsl.io.{->, GET, POST, Root}
import org.http4s.headers.`Content-Type`
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, MediaType, Response, Status, UrlForm}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

/**
 * HTTP endpoints definitions.
 *
 * @author <a href="mailto:oyvind@jergan.no">Oyvind Jergan</a>
 */
object Endpoints {

  def create[F[_]: ConcurrentEffect: Timer](configuration: Configuration,
                                            executionContext: ExecutionContext,
                                            worlds: ListBuffer[World]
                                           ): Resource[F, Server[F]] = {
    val service: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root => Sync[F].pure(response(worlds.toList))
      case req @ POST -> Root =>
        req.decode[UrlForm] { data =>
          Sync[F].pure{
            data.getFirst(HTML.inputName)
            .foreach(indexAsString => {
              HTML.move(worlds.head, indexAsString)
                .foreach(move => worlds.addOne(worlds.head.next(move)))
            })
            response(worlds.toList)
          }
        }
    }
    val routes = Router(
      "/"-> service,
    )
    HttpServer[F](configuration.port, configuration.bindAddress, executionContext, routes)
  }

  def response[F[_]](worlds: List[World]): Response[F] = {
    Response[F](Status.Ok)
      .withEntity(HTML.toHtml(worlds).render)
      .withContentType(`Content-Type`(MediaType.text.html))
  }

}
