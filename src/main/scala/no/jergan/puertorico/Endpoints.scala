package no.jergan.puertorico

import cats.effect.{ConcurrentEffect, Resource, Sync, Timer}
import no.jergan.puertorico.model.World
import org.http4s.dsl.io.{->, GET, Root}
import org.http4s.headers.`Content-Type`
import org.http4s.server.{Router, Server}
import org.http4s.{HttpRoutes, MediaType, Response, Status}

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
           worlds.addOne(worlds.iterator.next())
           Response[F](Status.Ok)
             .withEntity(html(head(), body(h1("i am simple " + worlds.size))).render)
             .withContentType(`Content-Type`(MediaType.text.html))
         }
      }
      val routes = Router(
         "/"-> service,
      )

      HttpServer[F](configuration.port, configuration.bindAddress, executionContext, routes)
   }

}
