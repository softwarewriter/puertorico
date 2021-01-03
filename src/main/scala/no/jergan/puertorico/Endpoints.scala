package no.jergan.puertorico

import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import no.jergan.puertorico.model.{Frame, Move, Player, World}
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
                                            frames: ListBuffer[Frame]
                                           ): Resource[F, Server[F]] = {
    val service: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root => Sync[F].pure(response(frames.toList))
      case req @ POST -> Root =>
        req.decode[UrlForm] { data =>
          Sync[F].pure{
            val world = frames.head.world
            data.getFirst(HTML.inputName)
            .foreach(indexAsString => {
              HTML.move(world, indexAsString)
                .foreach(move => frames.addOne(Frame(Some(move), Some(world.player()), world.next(move))))
            })
            response(frames.toList)
          }
        }
    }
    val routes = Router(
      "/"-> service,
    )
    HttpServer[F](configuration.port, configuration.bindAddress, executionContext, routes)
  }

  def response[F[_]](frames: List[Frame]): Response[F] = {
    Response[F](Status.Ok)
      .withEntity(HTML.toHtml(frames).render)
      .withContentType(`Content-Type`(MediaType.text.html))
  }

}
