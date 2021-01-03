package no.jergan.puertorico

import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import no.jergan.puertorico.model.{Input, World}
import org.http4s.dsl.io.{->, GET, POST, Root}
import org.http4s.headers.`Content-Type`
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, MediaType, Response, Status, UrlForm}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext
import scalatags.Text.all._

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
      case GET -> Root => Sync[F].pure {
        println("get")
        Response[F](Status.Ok)
          .withEntity(HTML.toHtml(worlds.toList).render)
          .withContentType(`Content-Type`(MediaType.text.html))
      }
      case req @ POST -> Root =>
        req.decode[UrlForm] { data =>
          Sync[F].pure{
            println("post")
            val input = data.getFirst(HTML.inputName)
            val world = worlds.head
            input.foreach(indexAsString => {
              world.inputs().flatten
                .find(_.index == Integer.parseInt(indexAsString))
                .foreach(input => worlds.addOne(worlds.head.next(input)))
            })
            //               println(data.getFirst(HTML.inputName))
            //               worlds.addOne(worlds.head.next(Input(0, "hei")))
            Response[F](Status.Ok)
              .withEntity(HTML.toHtml(worlds.toList).render)
              .withContentType(`Content-Type`(MediaType.text.html))
          }
        }
    }
    val routes = Router(
      "/"-> service,
    )
    HttpServer[F](configuration.port, configuration.bindAddress, executionContext, routes)
  }

}
